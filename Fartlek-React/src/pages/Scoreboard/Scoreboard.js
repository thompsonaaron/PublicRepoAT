import React, { Component } from "react";
import { connect } from "react-redux";
import { Link, withRouter } from "react-router-dom";

import axios from "../../axios";
import Spinner from "../../components/UI/Spinner/Spinner";
import withErrorHandler from "../../containers/HOC/withErrorHandler";
import { ReactComponent as ForwardSvg } from "../../img/SVG/forward.svg";
import classes from "./Scoreboard.module.css";

class Scoreboard extends Component {
  state = {
    teams: [],
    week: 0,
    loading: false,
  };

  componentDidUpdate(prevProps) {
    if (this.props.location !== prevProps.location) {
      const { search } = this.props.history.location;
      const params = new URLSearchParams(search);
      let week = params.get("week");
      if (!week) {
        week = "0";
      }
      if (this.props.idToken) {
        this.getScoreboardCaller(week);
      }
    }
  }

  async componentDidMount() {
    const { search } = this.props.history.location;
    const params = new URLSearchParams(search);
    let week = params.get("week");

    if (!week) {
      week = "0";
    }

    if (this.props.idToken) {
      this.getScoreboardCaller(week);
    } else {
      // wait until state has time to update
      const sleep = m => new Promise(r => setTimeout(r, m));
      await sleep(50);
      if (this.props.idToken) {
        this.getScoreboardCaller(this.state.week);
      }
    }
  }

  getScoreboardCaller = week => {
    try {
      this.setState({ week });
      this.getScoreboardData(week);
    } catch (e) {
      this.setState({ error: e.message }, console.log(e));
      return e;
    }
  };

  inputChangedHandler = (event, inputName) => {
    const { value } = event.target;
    this.setState({ week: value });
    this.props.history.push(`/scoreboard?week=${value}`);
  };

  getScoreboardData = async value => {
    const headers = {
      headers: {
        Authorization: this.props.idToken,
      },
    };

    const url = `/scoreboard?week=${value}`;
    this.setState({ loading: true });
    try {
      const response = await axios.get(url, headers);
      this.convertScoreboardData(response.data);
    } catch (error) {
      console.error(error);
    }
    this.setState({ loading: false });
  };

  convertScoreboardData = teams => {
    // [[MoundsView, [{User}...]], [WhiteBear, [{User}]]]
    const finalOutput = [];
    for (let i = 0; i < teams.length; i++) {
      const team = [];
      const { teamName } = teams[i];
      const { users } = teams[i];

      for (let j = 0; j < users.length; j++) {
        const { user } = users[j];
        const { username } = user;
        const fullName = `${user.firstName} ${user.lastName}`;
        const { mileage } = users[j];

        const flattenedUser = {};
        flattenedUser.username = username;
        flattenedUser.fullName = fullName;
        flattenedUser.mileage = mileage;
        flattenedUser.key = `${teamName}And${username}Key`;
        team.push(flattenedUser);
      }
      const teamObject = { teamName, team };
      teamObject.key = `teamObject${teamName}`;
      finalOutput.push(teamObject);
    }
    this.setState({ teams: finalOutput, loading: false });
  };

  render() {
    const mileageTables = [];
    for (let i = 0; i < this.state.teams.length; i++) {
      const { team } = this.state.teams[i];
      const details = team.map(user => (
        <div key={`mileageTable${user.username}${this.state.week}`} className={classes.ItemsBodyContent}>
          <span className={classes.span}>
            <Link
              to={{
                pathname: "/trainingLog",
                search: `?username=${user.username}&week=${this.state.week}`,
              }}
            >
              {user.fullName}
            </Link>
          </span>
          <span className={classes.span}>{user.mileage > 0 ? user.mileage.toFixed(2) : 0}</span>
        </div>
      ));

      const output = (
        <div key={`outputDiv${this.state.teams[i].teamName}`} className={classes.teamContainer}>
          <div className={classes.Items}>
            <div className={classes.ItemsHead}>
              <Link to={`/teams/${this.state.teams[i].teamName}`}>
                {this.state.teams[i].teamName}
                <ForwardSvg
                  width="15px"
                  height="15px"
                  style={{ cursor: "pointer", fill: "#0B5AA2", marginLeft: "10px" }}
                  title="Edit Workout"
                />
              </Link>
              <hr />
            </div>

            <div className={classes.ItemsBody}>{details}</div>
          </div>
        </div>
      );
      mileageTables.push(output);
    }

    return (
      <div className={classes.main}>
        {this.props.idToken && (
          <div className={classes.Scoreboard}>
            <form>
              <label className={classes.Label}>Scoreboard for</label>
              <select
                value={this.state.week}
                id="scoreboardSelect"
                className={classes.Select}
                onChange={this.inputChangedHandler}
              >
                <option value="0" key="0">
                  This Week
                </option>
                <option value="1" key="1">
                  Last Week
                </option>
                <option value="2" key="2">
                  2 Weeks Ago
                </option>
                options.push(
                <option value="3" key="3">
                  3 Weeks Ago
                </option>
              </select>
            </form>
          </div>
        )}
        <div className={classes.mileageContainer}>
          {this.state.loading && <Spinner />}
          {mileageTables}
        </div>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  idToken: state.idToken,
});

export default withRouter(connect(mapStateToProps, null)(withErrorHandler(Scoreboard, axios)));

//export default withErrorHandler(Scoreboard, axios);

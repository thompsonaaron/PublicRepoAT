import React, { Component } from "react";
import { connect } from "react-redux";

import axios from "../../axios";
import Button from "../../components/UI/Button/Button";
import withErrorHandler from "../../containers/HOC/withErrorHandler";
import * as actions from "../../store/actions/auth";
import classes from "./JoinTeam.module.css";

class JoinTeam extends Component {
  state = {
    allTeams: [],
  };

  async componentDidMount() {
    const teams = await this.getAllTeams();
    if (teams) {
      this.setState({ allTeams: teams });
    }
  }

  getAllTeams = async () => {
    const { idToken } = this.props;
    const headers = {
      headers: {
        Authorization: idToken,
        // "Access-Control-Allow-Origin": "*",
        "Content-Type": "application/json",
      },
    };
    try {
      const response = await axios.get("/viewTeams", headers);
      return response.data;
    } catch (error) {
      console.error(error);
    }
  };

  leaveTeam = async teamId => {
    try {
      await this.props.onLeaveClicked(this.props.userId, teamId, this.props.idToken);
    } catch (error) {
      console.error(error);
    }
  };

  joinTeam = async teamId => {
    try {
      await this.props.onJoinClicked(this.props.username, teamId, this.props.idToken);
    } catch (error) {
      console.error(error);
    }
  };

  render() {
    const userTeamNames = this.props.teams.map(team => team.name);

    return (
      <div className={classes.containerDiv}>
        <h1>Join A Team</h1>
        {this.props.teams &&
          this.state.allTeams.map(anyTeam =>
            userTeamNames.includes(anyTeam.name) ? (
              <div key={`leaveTeamDiv${anyTeam.teamId}`} className={classes.buttonContainer}>
                <span>{anyTeam.name}</span>
                <Button
                  key={`leaveTeamButton${anyTeam.teamId}`}
                  clicked={() => this.leaveTeam(anyTeam.teamId)}
                  config={{ width: "30%" }}
                >
                  Leave Team
                </Button>
              </div>
            ) : (
              <div key={`joinTeamDiv${anyTeam.teamId}`} className={classes.buttonContainer}>
                <span>{anyTeam.name}</span>
                <Button
                  key={`joinTeamButton${anyTeam.teamId}`}
                  clicked={() => this.joinTeam(anyTeam.teamId)}
                  config={{ width: "30%" }}
                >
                  Join Team
                </Button>
              </div>
            )
          )}
      </div>
    );
  }
}

const mapStateToProps = state => ({
  teams: state.teams,
  userId: state.userId,
  username: state.username,
  idToken: state.idToken,
});

const mapDispatchToProps = dispatch => ({
  onJoinClicked: (username, teamId, idToken) => dispatch(actions.joinTeamOnServer(username, teamId, idToken)),
  onLeaveClicked: (userId, teamId, idToken) => dispatch(actions.leaveTeamOnServer(userId, teamId, idToken)),
});

export default connect(mapStateToProps, mapDispatchToProps)(withErrorHandler(JoinTeam, axios));

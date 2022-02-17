import React, { Component } from "react";
import { connect } from "react-redux";
import { NavLink } from "react-router-dom";

import Dropdown from "../../../UI/Dropdown/Dropdown";
import classes from "./TeamsDropdown.module.css";

class TeamsDropdown extends Component {
  state = {
    options: [
      <li key="optionsList">
        <NavLink onClick={this.props.closeMenuHandler} key="joinTeam" className={classes.listItem} to="/teams/joinTeam">
          Join a Team
        </NavLink>
      </li>,
    ],
  };

  componentDidUpdate(prevProps) {
    if (this.props.teams != prevProps.teams) {
      const options = [
        <li key="optionsList">
          <NavLink
            onClick={this.props.closeMenuHandler}
            key="joinTeam"
            className={classes.listItem}
            to="/teams/joinTeam"
          >
            Join a Team
          </NavLink>
        </li>,
      ];

      if (this.props.teams) {
        const teams = this.props.teams.map(team => (
          <NavLink
            key={team.name}
            onClick={this.props.closeMenuHandler}
            className={classes.listItem}
            to={`/teams/${team.name}`}
          >
            {team.name}
          </NavLink>
        ));

        if (teams.length > 0) {
          const finalTeams = options.concat(teams);
          this.setState({ options: finalTeams });
        }
      }
    }
  }

  render() {
    return (
      <Dropdown
        openDropdownHandler={this.props.openTeamMenuHandler}
        header="Teams"
        options={this.state.options}
        showMenuHandler={this.props.showTeamMenu}
      />
    );
  }
}

const mapStateToProps = state => ({
  teams: state.teams,
});

export default connect(mapStateToProps)(TeamsDropdown);

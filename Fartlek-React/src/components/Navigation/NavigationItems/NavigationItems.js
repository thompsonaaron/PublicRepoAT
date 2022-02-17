import React from "react";
import { connect } from "react-redux";

import CoachesDropdown from "./CoachesDropdown/CoachesDropdown";
import NavigationItem from "./NavigationItem/NavigationItem";
import classes from "./NavigationItems.module.css";
import TeamsDropdown from "./TeamsDropdown/TeamsDropdown";

const NavigationItems = props => (
  <div className={classes.Navbar}>
    {props.idToken && (
      <ul className={classes.leftNav}>
        <NavigationItem link="/trainingLog" key="TrainingLogButton" clicked={props.closeMenuHandler}>
          Training Log
        </NavigationItem>
        <NavigationItem link="/addWorkout" key="AddWorkoutButton" clicked={props.closeMenuHandler}>
          Add Workout
        </NavigationItem>
        <TeamsDropdown
          showTeamMenu={props.showTeamMenu}
          openTeamMenuHandler={props.openTeamMenuHandler}
          closeMenuHandler={props.closeMenuHandler}
        />
        <CoachesDropdown
          showCoachesMenu={props.showCoachesMenu}
          openCoachesMenuHandler={props.openCoachesMenuHandler}
          closeMenuHandler={props.closeMenuHandler}
        />
      </ul>
    )}
  </div>
);

const mapStateToProps = state => {
  return {
    idToken: state.idToken,
  };
};

export default connect(mapStateToProps)(NavigationItems);

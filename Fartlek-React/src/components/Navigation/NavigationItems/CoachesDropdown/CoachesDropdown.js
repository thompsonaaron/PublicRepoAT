import React from "react";
import { connect } from "react-redux";
import { NavLink } from "react-router-dom";

import Dropdown from "../../../UI/Dropdown/Dropdown";
import classes from "./CoachesDropdown.module.css";

const CoachesDropdown = props => {
  let coach = null;
  if (props.roles && props.roles.length > 0) {
    coach = !props.roles.map(item => item.roleId).includes(2) ? (
      <NavLink
        key="registerAsCoach"
        onClick={props.closeMenuHandler}
        className={classes.listItem}
        to="/coaches/registerAsCoach"
      >
        Register
      </NavLink>
    ) : null;
  }

  const options = [
    <li key="coachesOptions">
      {coach}
      {props.roles && props.roles.length > 0 && !props.roles.map(item => item.roleId).includes(2) && (
        <NavLink
          key="registerAsCoach"
          onClick={props.closeMenuHandler}
          className={classes.listItem}
          to="/coaches/registerAsCoach"
        >
          Register
        </NavLink>
      )}
      {/* <NavLink
				key="findACoach"
				onClick={props.closeMenuHandler}
				className={classes.listItem}
				style={{ color: "black" }}
				to="/coaches/findACoach">
				Find a Coach
			</NavLink> */}
      <NavLink
        key="createATeam"
        onClick={props.closeMenuHandler}
        className={classes.listItem}
        to="/coaches/createATeam"
      >
        Create a Team
      </NavLink>
    </li>,
  ];

  return (
    <Dropdown
      openDropdownHandler={props.openCoachesMenuHandler}
      header="Coaches"
      options={options}
      showMenuHandler={props.showCoachesMenu}
    />
  );
};

const mapStateToProps = state => ({
  roles: state.roles,
});

export default connect(mapStateToProps)(CoachesDropdown);

import React from "react";
import { NavLink } from "react-router-dom";

import classes from "./NavigationItem.module.css";

const navigationItem = props => (
  <li className={classes.NavItem}>
    <NavLink to={props.link} activeClassName={classes.active} onClick={props.clicked}>
      {props.children}
    </NavLink>
  </li>
);

export default navigationItem;

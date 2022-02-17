import React from "react";

import NavigationItem from "../NavigationItems/NavigationItem/NavigationItem";
import classes from "./RightNavigationItems.module.css";

const rightNavigationItems = props => {
  const items = props.username ? (
    <>
      <NavigationItem link="/myAccount" clicked={props.closeMenuHandler}>
        My Account
      </NavigationItem>
      <NavigationItem link="/logout" clicked={props.closeMenuHandler}>
        Logout
      </NavigationItem>
    </>
  ) : (
    <>
      <NavigationItem link="/createAccount" clicked={props.closeMenuHandler}>
        Create Account
      </NavigationItem>
      <NavigationItem link="/login" clicked={props.closeMenuHandler}>
        Login
      </NavigationItem>
    </>
  );

  return <ul className={classes.list}>{items}</ul>;
};

export default rightNavigationItems;

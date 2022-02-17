import React, { Fragment } from "react";

import classes from "./DrawerToggle.module.css";

const drawerToggle = props => {
  let toggler = null;

  // if (props.showTopDrawer) {
  toggler = (
    <div onClick={props.clicked} className={classes.DrawerToggle}>
      <div />
      <div />
      <div />
    </div>
  );
  // };

  return <>{toggler}</>;
};

export default drawerToggle;

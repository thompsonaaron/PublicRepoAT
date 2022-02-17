import React, { Fragment } from "react";

import NavigationItems from "../NavigationItems/NavigationItems";
import RightNavigationItems from "../RightNavigationItems/RightNavigationItems";
import classes from "./TopDrawer.module.css";

const topDrawer = props => {
  let attachedClasses = [classes.TopDrawer, classes.Close];
  if (props.showTopDrawer) {
    attachedClasses = [classes.TopDrawer, classes.Open];
  }

  return (
    <>
      <nav className={attachedClasses.join(" ")}>
        <NavigationItems
          closeMenuHandler={props.clicked}
          showTeamMenu={props.showTeamMenu}
          openTeamMenuHandler={props.openTeamMenuHandler}
          openCoachesMenuHandler={props.openCoachesMenuHandler}
          showCoachesMenu={props.showCoachesMenu}
        />
        <RightNavigationItems closeMenuHandler={props.clicked} username={props.username} />
      </nav>
    </>
  );
};

export default topDrawer;

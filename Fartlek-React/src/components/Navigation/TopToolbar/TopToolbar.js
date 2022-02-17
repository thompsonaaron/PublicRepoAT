import React from "react";
import { connect } from "react-redux";
import { NavLink } from "react-router-dom";

// import TopDrawer from '../TopDrawer/TopDrawer';
import NavigationItems from "../NavigationItems/NavigationItems";
import RightNavigationItems from "../RightNavigationItems/RightNavigationItems";
import DrawerToggle from "./DrawerToggle/DrawerToggle";
import classes from "./TopToolbar.module.css";

const TopToolbar = props => {
  return (
    <header>
      <nav className={classes.TopToolbar}>
        <DrawerToggle clicked={props.clicked} />
        <div className={classes.leftNav}>
          <NavLink to="/" className={classes.Logo} onClick={props.closeMenuHandler}>
            Fartlek
          </NavLink>
          <div className={classes.flexLeft}>
            {props.idToken && (
              <NavigationItems
                className={classes.DesktopOnly}
                showTeamMenu={props.showTeamMenu}
                openTeamMenuHandler={props.openTeamMenuHandler}
                showCoachesMenu={props.showCoachesMenu}
                openCoachesMenuHandler={props.openCoachesMenuHandler}
                closeMenuHandler={props.closeMenuHandler}
              />
            )}
          </div>
        </div>
        <div className={classes.flexRight}>
          <RightNavigationItems
            username={props.username}
            className={classes.DesktopOnly}
            logout={props.logout}
            closeMenuHandler={props.closeMenuHandler}
          />
        </div>
      </nav>
    </header>
  );
};

const mapStateToProps = state => ({
  idToken: state.idToken,
});

export default connect(mapStateToProps)(TopToolbar);

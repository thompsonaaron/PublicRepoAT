import React, { Component } from "react";
import { connect } from "react-redux";

import TopDrawer from "../../components/Navigation/TopDrawer/TopDrawer";
import TopToolbar from "../../components/Navigation/TopToolbar/TopToolbar";
import * as actions from "../../store/actions/auth";
import classes from "./Layout.module.css";

class Layout extends Component {
  state = {
    showTopDrawerToggle: true,
    showTopDrawer: false,
    showTeamMenu: false,
    showCoachesMenu: false,
  };

  topDrawerToggleHandler = () => {
    const currentState = this.state.showTopDrawer;
    const newState = !currentState;
    this.setState({ showTopDrawer: newState });
  };

  closeTopDrawer = () => {
    this.setState({ showTopDrawer: false });
  };

  closeMenuHandler = () => {
    if (this.state.showTeamMenu || this.state.showCoachesMenu) {
      this.setState({
        showTeamMenu: false,
        showCoachesMenu: false,
      });
      console.log("toolbar menus closed");
    }
  };

  openTeamMenuHandler = () => {
    this.setState({
      showTeamMenu: !this.state.showTeamMenu,
      showCoachesMenu: false,
    });
  };

  openCoachesMenuHandler = () => {
    const showCoaches = this.state.showCoachesMenu;
    const changed = !showCoaches;
    this.setState({
      showCoachesMenu: changed,
      showTeamMenu: false,
    });
  };

  closeMenuAndTopDrawer = () => {
    this.closeTopDrawer();
    this.closeMenuHandler();
  };

  render() {
    return (
      <div className={classes.container}>
        <TopToolbar
          onLogout={this.props.onLogout}
          username={this.props.username}
          clicked={this.topDrawerToggleHandler}
          showTeamMenu={this.state.showTeamMenu}
          showCoachesMenu={this.state.showCoachesMenu}
          openTeamMenuHandler={this.openTeamMenuHandler}
          openCoachesMenuHandler={this.openCoachesMenuHandler}
          closeMenuHandler={this.closeMenuAndTopDrawer}
        />
        <TopDrawer
          showTopDrawer={this.state.showTopDrawer}
          clicked={this.closeMenuAndTopDrawer}
          username={this.props.username}
          openTeamMenuHandler={this.openTeamMenuHandler}
          showTeamMenu={this.state.showTeamMenu}
          openCoachesMenuHandler={this.openCoachesMenuHandler}
          showCoachesMenu={this.state.showCoachesMenu}
        />
        <main className={classes.mainContent} onClick={this.closeMenuAndTopDrawer}>
          {this.props.children}
        </main>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  username: state.username,
});

const mapDispatchToProps = dispatch => ({
  onLogout: () => dispatch(actions.logout()),
});

export default connect(mapStateToProps, mapDispatchToProps)(Layout);

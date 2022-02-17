import React, { Component } from "react";
import { connect } from "react-redux";

import Spinner from "../../components/UI/Spinner/Spinner";
import * as actions from "../../store/actions/auth";

class Logout extends Component {
  async componentDidMount() {
    if (this.props.location.pathname === "/logout") {
      try {
        await this.props.onLogout();
        this.props.history.push("/");
      } catch (error) {
        console.error(error);
      }
    }
  }

  render() {
    return <Spinner />;
  }
}

const mapDispatchToProps = dispatch => ({
  onLogout: () => dispatch(actions.logout()),
});

export default connect(null, mapDispatchToProps)(Logout);

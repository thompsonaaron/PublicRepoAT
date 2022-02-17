import React, { Component } from "react";

import Backdrop from "../Backdrop/Backdrop";
import classes from "./Modal.module.css";

class Modal extends Component {
  // enhances performance since this is rarely rendered
  // Does not need pure component since this.props.modalClosed does not change
  shouldComponentUpdate(nextProps, nextState) {
    return nextProps.show !== this.props.show || nextProps.children !== this.props.children;
  }

  render() {
    return (
      <>
        <Backdrop show={this.props.show} clicked={this.props.modalClosed} />
        <div
          className={classes.modal}
          style={{
            transform: this.props.show ? "translateY(0)" : "translateY(-100vh)", // vh is viewport height and slides off the screen
            opacity: this.props.show ? "1.0" : "0.0",
          }}
        >
          {this.props.children}
        </div>
      </>
    );
  }
}

export default Modal;

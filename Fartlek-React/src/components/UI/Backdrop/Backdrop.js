import React from "react";

import classes from "./Backdrop.module.css";

const backdrop = props =>
  // let styleClass = classes.backdrop;
  // if (props.splitModal){
  //     styleClass = classes.splitModal;
  // }

  props.show ? (
    <div className={classes.backdrop} style={{ opacity: props.opacity || "50%" }} onClick={props.clicked} />
  ) : null;
export default backdrop;

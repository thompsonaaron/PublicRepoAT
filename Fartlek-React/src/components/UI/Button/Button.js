import React from "react";

import classes from "./Button.module.css";

const Button = props => {
  const classList = [];
  classList.push(classes.Button);

  if (props.primary) {
    classList.push(classes.Primary);
  }

  if (props.secondary) {
    classList.push(classes.Secondary);
  }

  return (
    <button
      className={classList.join(" ")}
      onClick={props.clicked}
      disabled={props.disabled}
      style={props.config}
      type={props.type}
      title={props.title}
    >
      {props.children}
    </button>
  );
};

export default Button;

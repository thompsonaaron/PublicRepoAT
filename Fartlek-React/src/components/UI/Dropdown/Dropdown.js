import { faCaretDown } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React from "react";

import classes from "./Dropdown.module.css";

const Dropdown = props => {
  let styles = classes.dropdown;
  if (props.showMenuHandler) {
    styles += ` ${classes.show}`;
  } else {
    styles += ` ${classes.hide}`;
  }

  return (
    <div className={classes.inline}>
      <div className={classes.link}>
        <span className={classes.span} onClick={props.openDropdownHandler}>
          {props.header}
          <FontAwesomeIcon className={classes.icon} icon={faCaretDown} />
        </span>
      </div>
      <div className={styles}>
        <ul className={classes.list}>{props.options}</ul>
      </div>
    </div>
  );
};

export default Dropdown;

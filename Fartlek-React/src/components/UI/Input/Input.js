import React from "react";

import Button from "../Button/Button";
import classes from "./Input.module.css";

// takes elementType, elementConfig, value, and changed props
const Input = props => {
  let inputElement = null;
  const inputClasses = [classes.InputElement];
  if (!props.valid && props.started) {
    inputClasses.push(classes.Invalid);
  }

  switch (props.elementType) {
    case "input":
      inputElement = (
        <input
          className={inputClasses.join(" ")}
          {...props.elementConfig}
          value={props.value}
          onChange={props.changed}
          style={{ display: props.display, ...props.style }}
          ref={props.reference}
        />
      );
      break;
    case "textarea":
      inputElement = (
        <textarea
          className={inputClasses.join(" ")}
          {...props.elementConfig}
          style={props.style}
          value={props.value}
          onChange={props.changed}
        />
      );
      break;
    case "select":
      inputElement = (
        <select className={inputClasses.join(" ")} value={props.value} onChange={props.changed} style={props.style}>
          {props.elementConfig.options.map(option => (
            <option key={option.value} value={option.value}>
              {option.displayValue}
            </option>
          ))}
        </select>
      );
      break;
    case "button":
      inputElement = (
        <Button
          clicked={props.clicked}
          type="button"
          style={{ display: props.display, width: props.divWidth, ...props.style }}
        >
          {props.value}
        </Button>
      );
      break;
    default:
      inputElement = (
        <input
          className={inputClasses.join(" ")}
          {...props.elementConfig}
          value={props.value}
          onChange={props.changed}
          ref={props.ref}
        />
      );
  }

  let label = null;
  if (props.elementConfig && props.elementConfig.label && props.elementConfig.label !== "") {
    label = (
      <label className={classes.Label} style={{ display: props.display }}>
        {props.elementConfig.label}
      </label>
    );
  }

  return (
    <div className={classes.Input} style={{ width: props.divWidth, display: props.display }}>
      {label}
      {inputElement}
    </div>
  );
};

export default Input;

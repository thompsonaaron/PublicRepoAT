import React, { useCallback, useEffect, useRef, useState } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";

import classes from "../../App.module.css";
import axios from "../../axios";
import Button from "../../components/UI/Button/Button";
import Input from "../../components/UI/Input/Input";
import uniqueStyles from "../CreateTeam/CreateTeam.module.css";

const RegisterCoach = props => {
  const [formFields, setFormFields] = useState({
    coachName: {
      elementType: "input",
      elementConfig: {
        label: "Coach Name",
      },
      value: "",
      validation: {
        required: true,
      },
      valid: false,
      started: false,
    },
    background: {
      elementType: "textarea",
      elementConfig: {
        label: "Background",
        placeholder: "insert your coaching philosophy, background, etc.",
      },
      value: "",
      validation: {
        required: false,
      },
      valid: true,
    },
  });
  const stateRef = useRef();
  stateRef.current = formFields;

  useEffect(() => {
    console.log("entered use effect");
  }, [formFields]);

  const checkValidity = useCallback((value, validationRules) => {
    let isValid = true;
    let updatedValue = value;

    if (validationRules.required) {
      if (typeof value !== "string") {
        updatedValue = value.toString();
      }
      isValid = updatedValue.trim() !== "" && isValid;
    }

    if (validationRules.minLength) {
      isValid = updatedValue.length >= validationRules.minLength && isValid;
    }

    return isValid;
  }, []);

  const onSubmitHandler = useCallback(async event => {
    event.preventDefault();

    const validityArray = [];
    for (const key in stateRef.current) {
      validityArray.push(checkValidity(stateRef.current[key].value, stateRef.current[key].validation));
    }

    if (validityArray.includes(false)) {
      return;
    }

    await registerCoach();
    props.history.push("/");
  }, []);

  const registerCoach = async () => {
    const headers = {
      headers: {
        // "Access-Control-Allow-Origin": "*",
        Authorization: props.idToken,
        "Content-Type": "application/json",
      },
    };

    const formData = {
      userId: props.userId,
      coachName: stateRef.current.coachName.value,
      biography: stateRef.current.background.value,
    };

    return axios.post("/registerCoach", formData, headers);
  };

  const inputChangedHandler = useCallback((event, inputName) => {
    const { value } = event.target;

    const updatedFormFields = {
      ...stateRef.current,
      [inputName]: {
        ...stateRef.current[inputName],
        value,
        valid: checkValidity(value, formFields[inputName].validation),
        started: true,
      },
    };
    setFormFields(updatedFormFields);
  }, []);

  const inputFields = [];
  const currentFormFields = formFields;
  const button = (
    <Button type="submit" config={{ width: "50%" }}>
      Register
    </Button>
  );
  const header = <h1>Coach Registration</h1>;

  for (const key in currentFormFields) {
    inputFields.push({
      id: key,
      config: currentFormFields[key],
    });
  }

  const form = inputFields.map(input => (
    <Input
      key={input.id}
      elementType={input.config.elementType}
      elementConfig={input.config.elementConfig}
      required={input.config.validation.required}
      value={input.config.value}
      started={input.config.started}
      valid={input.config.valid}
      changed={event => inputChangedHandler(event, input.id)}
    />
  ));

  return (
    <div className={classes.container}>
      {header}
      <form onSubmit={onSubmitHandler}>
        {form}
        <div className={uniqueStyles.buttonDiv}>{button}</div>
      </form>
    </div>
  );
};

const mapStateToProps = state => ({
  idToken: state.idToken,
  userId: state.userId,
});

export default withRouter(connect(mapStateToProps)(RegisterCoach));

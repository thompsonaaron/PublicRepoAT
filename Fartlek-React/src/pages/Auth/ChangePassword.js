import { Auth } from "aws-amplify";
import React, { Component } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";

import Button from "../../components/UI/Button/Button";
import Input from "../../components/UI/Input/Input";
import Modal from "../../components/UI/Modal/Modal";
import * as actions from "../../store/actions/auth";
import classes from "./auth.module.css";

class ChangePassword extends Component {
  state = {
    formFields: {
      code: {
        elementType: "input",
        elementConfig: {
          label: "Code",
        },
        value: "",
        validation: {
          required: true,
        },
        valid: true,
        started: false,
      },
      newPassword: {
        elementType: "password",
        elementConfig: {
          label: "New Password",
          type: "password",
        },
        value: "",
        validation: {
          required: true,
        },
        valid: true,
        started: false,
      },
      repeatPassword: {
        elementType: "password",
        elementConfig: {
          label: "Confirm Password",
          type: "password",
        },
        value: "",
        validation: {
          required: true,
        },
        valid: false,
        started: false,
      },
    },
    error: null,
  };

  errorConfirmedHandler = () => {
    this.setState({ error: null });
  };

  inputChangedHandler = (event, inputName) => {
    const updatedFormFields = {
      ...this.state.formFields,
      [inputName]: {
        ...this.state.formFields[inputName],
        value: event.target.value,
        valid: this.checkValidity(event.target.value, this.state.formFields[inputName].validation),
        started: true,
      },
    };
    this.setState({ formFields: updatedFormFields });
  };

  checkValidity = (value, validationRules) => {
    let isValid = true;

    if (validationRules.required) {
      isValid = value.trim() !== "" && isValid;
    }

    return isValid;
  };

  onSubmitHandler = async event => {
    event.preventDefault();

    if (
      this.state.formFields.newPassword.value.trim() === "" ||
      this.state.formFields.repeatPassword.value.trim() === ""
    ) {
      alert("Passwords cannot be blank.");
      return;
    }

    if (this.state.formFields.newPassword.value !== this.state.formFields.repeatPassword.value) {
      alert("Passwords must match. Please try again");
      return;
    }

    const validityArray = [];
    for (const key in this.state.formFields) {
      validityArray.push(this.state.formFields[key].valid);
    }

    if (validityArray.includes(false)) {
      return;
    }

    // Collect confirmation code and new password
    const code = this.state.formFields.code.value;
    const new_password = this.state.formFields.newPassword.value;
    try {
      await Auth.forgotPasswordSubmit(this.props.match.params.username, code, new_password);
      this.props.history.push("/login");
    } catch (e) {
      this.setState({ error: e.message });
      console.log("error changing password", e);
    }
  };

  render() {
    const inputFields = [];
    for (const key in this.state.formFields) {
      inputFields.push({
        id: key,
        config: this.state.formFields[key],
      });
    }

    const buttonConfig = {
      width: "100%",
    };

    return (
      <>
        <Modal show={!!this.state.error} modalClosed={this.errorConfirmedHandler}>
          {this.state.error}
        </Modal>
        <div className={classes.Auth}>
          <p style={{ textAlign: "left", padding: "0px 15px" }}>
            We have sent a password reset code by email to {this.props.match.params.email}. Enter the code to reset your
            password.
          </p>
          <form onSubmit={this.onSubmitHandler}>
            {inputFields.map(input => (
              <Input
                key={input.id}
                elementType={input.config.elementType}
                elementConfig={input.config.elementConfig}
                required={input.config.validation.required}
                value={input.config.value}
                started={input.config.started}
                valid={input.config.valid}
                changed={event => this.inputChangedHandler(event, input.id)}
              />
            ))}
            <div style={{ padding: "20px 0px" }}>
              <Button type="submit" config={buttonConfig}>
                Reset My Password
              </Button>
            </div>
          </form>
        </div>
      </>
    );
  }
}

const mapDispatchToProps = dispatch => ({
  onSignIn: (username, password) => dispatch(actions.auth(username, password)),
});

export default withRouter(connect(null, mapDispatchToProps)(ChangePassword));

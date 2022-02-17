import { Auth } from "aws-amplify";
import React, { Component } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";

import Button from "../../components/UI/Button/Button";
import Input from "../../components/UI/Input/Input";
import Modal from "../../components/UI/Modal/Modal";
import * as actions from "../../store/actions/auth";
import classes from "./auth.module.css";

// import { withAuthenticator, AmplifySignOut } from '@aws-amplify/ui-react';

class ConfirmSignup extends Component {
  state = {
    formFields: {
      code: {
        elementType: "input",
        elementConfig: {
          label: "Verification Code",
          placeholder: "xxxxxx",
        },
        value: "",
        validation: {
          required: true,
          length: 6,
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
    const updatedState = {
      code: {
        ...this.state.formFields.code,
        value: event.target.value,
        valid: this.checkValidity(event.target.value, this.state.formFields.code.validation),
        started: true,
      },
    };
    this.setState({ formFields: updatedState });
  };

  checkValidity = (value, validationRules) => {
    let isValid = true;

    if (validationRules.required) {
      isValid = value.trim() !== "" && isValid;
    }

    if (validationRules.length) {
      isValid = value.length === validationRules.length && isValid;
    }

    return isValid;
  };

  onSubmitHandler = async event => {
    event.preventDefault();

    if (!this.state.formFields.code.valid) {
      console.log("tried to confirm sign up without 6 digit code");
      return;
    }

    // Collect confirmation code and new password
    const { username } = this.props.match.params;
    const code = this.state.formFields.code.value;
    try {
      await Auth.confirmSignUp(username, code);
      this.props.history.push("/login");
    } catch (e) {
      this.setState({ error: e.message });
      console.log("error confirming sign up", e);
    }
  };

  render() {
    const modal = (
      <Modal show={this.state.error} modalClosed={this.errorConfirmedHandler}>
        {this.state.error}
      </Modal>
    );

    const inputFields = [];
    for (const key in this.state.formFields) {
      inputFields.push({
        id: key,
        config: this.state.formFields[key],
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
        changed={event => this.inputChangedHandler(event, input.id)}
      />
    ));

    const buttonConfig = {
      width: "100%",
    };

    return (
      <>
        {modal}
        <div className={classes.Auth}>
          <p style={{ textAlign: "left" }}>
            We have sent a code to
            {this.props.match.params.email}. Enter it below to confirm your account. Your account will not be active
            until confirmed.
          </p>
          <form onSubmit={this.onSubmitHandler}>
            {form}
            <div style={{ padding: "20px 0px" }}>
              <Button type="submit" config={buttonConfig}>
                Confirm My Account
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

export default withRouter(connect(null, mapDispatchToProps)(ConfirmSignup));

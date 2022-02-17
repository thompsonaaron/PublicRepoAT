import React, { Component } from "react";
import { connect } from "react-redux";
import { Link, withRouter } from "react-router-dom";

import Button from "../../components/UI/Button/Button";
import Input from "../../components/UI/Input/Input";
import Modal from "../../components/UI/Modal/Modal";
import Spinner from "../../components/UI/Spinner/Spinner";
import * as actions from "../../store/actions/auth";
import classes from "./auth.module.css";

// import { withAuthenticator, AmplifySignOut } from '@aws-amplify/ui-react';
// import {auth} from 'aws-amplify';

class Login extends Component {
  state = {
    formFields: {
      username: {
        elementType: "input",
        elementConfig: {
          label: "Username",
          placeholder: "Username",
        },
        value: "",
        validation: {
          required: true,
        },
        valid: true,
        started: false,
      },
      password: {
        elementType: "input",
        elementConfig: {
          label: "Password",
          placeholder: "Password",
          type: "password",
        },
        value: "",
        validation: {
          required: true,
          minLength: 6,
        },
        valid: true,
        started: false,
      },
    },
    loading: false,
    error: null,
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

    if (validationRules.minLength) {
      isValid = value.length >= 6 && isValid;
    }
    return isValid;
  };

  errorConfirmedHandler = () => {
    this.setState({ error: null });
  };

  onSubmitHandler = async event => {
    event.preventDefault();
    this.setState({ loading: true });
    try {
      await this.props.onSignIn(this.state.formFields.username.value, this.state.formFields.password.value);
      this.props.history.push("/");
    } catch (e) {
      this.setState({ error: e.message });
      console.log("error signing up", e.message);
    } finally {
      this.setState({ loading: false });
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
        <Modal show={this.state.error} modalClosed={this.errorConfirmedHandler}>
          {this.state.error}
        </Modal>
        {this.state.loading && <Spinner />}
        <div className={classes.Auth}>
          <h3 className={classes.header}>Sign in with your username and password</h3>
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
            <div>
              <Link className={classes.anchor} to="/forgotPassword">
                Forgot your password?
              </Link>
            </div>
            <div style={{ padding: "20px 0px" }}>
              <Button type="submit" config={buttonConfig}>
                Sign In
              </Button>
            </div>
            <div>
              Need an account?&nbsp;
              <Link className={classes.InlineLink} to="/createAccount">
                Sign Up
              </Link>
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

export default withRouter(connect(null, mapDispatchToProps)(Login));

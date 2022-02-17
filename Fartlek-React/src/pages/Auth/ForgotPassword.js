import { Auth } from "aws-amplify";
import React, { Component } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";

import Button from "../../components/UI/Button/Button";
import Input from "../../components/UI/Input/Input";
import * as actions from "../../store/actions/auth";
import classes from "./auth.module.css";

class ForgotPassword extends Component {
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
    },
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
    const username = this.state.formFields.username.value;
    // check that this exists in the database before trying. If not, tell user!
    // Send confirmation code to user's email
    try {
      const response = await Auth.forgotPassword(username);
      const url = `/changePassword/${username}/${response.CodeDeliveryDetails.Destination}`;
      this.props.history.push(url);
    } catch (e) {
      alert(`Error. ${e.message}`);
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
      <div className={classes.Auth}>
        <h1>Forgot your password?</h1>
        {/* <p style={{ textAlign: "left" }}>
					Enter your Username below and we will send a message to reset your
					password
				</p> */}
        <form onSubmit={this.onSubmitHandler}>
          {form}
          <div style={{ padding: "20px 0px" }}>
            <Button type="submit" config={buttonConfig}>
              Reset My Password
            </Button>
          </div>
        </form>
      </div>
    );
  }
}

const mapDispatchToProps = dispatch => ({
  onSignIn: (username, password) => dispatch(actions.auth(username, password)),
});

export default withRouter(connect(null, mapDispatchToProps)(ForgotPassword));

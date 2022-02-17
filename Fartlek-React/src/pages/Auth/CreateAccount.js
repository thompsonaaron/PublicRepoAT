import { Auth } from "aws-amplify";
import React, { Component } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";

import axios from "../../axios";
import Button from "../../components/UI/Button/Button";
import Input from "../../components/UI/Input/Input";
import Modal from "../../components/UI/Modal/Modal";
import withErrorHandler from "../../containers/HOC/withErrorHandler";
import classes from "./auth.module.css";

class CreateAccount extends Component {
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
        valid: false,
        started: false,
      },
      email: {
        elementType: "email",
        elementConfig: {
          label: "Email",
          placeholder: "Email",
          type: "email",
        },
        value: "",
        validation: {
          required: true,
        },
        valid: false,
        started: false,
      },
      password: {
        elementType: "password",
        elementConfig: {
          label: "Password",
          type: "password",
          placeholder: "********",
        },
        value: "",
        validation: {
          required: true,
          minLength: 8,
        },
        valid: false,
        started: false,
      },
    },
    error: null,
  };

  inputChangedHandler = (event, inputName) => {
    let { value } = event.target;
    if (inputName === "phoneNumber") {
      // const reg = new RegExp("(|)|-|s+");
      // value = value.replace(reg, "");
      value = value.replaceAll(" ", "");
      value = value.replaceAll("(", "");
      value = value.replaceAll(")", "");
      value = value.replaceAll("-", "");
      const { length } = value;
      if (length >= 4 && length <= 6) {
        const areaCode = value.substring(0, 3);
        value = `(${areaCode}) ${value.substring(3)}`;
      }
      if (length > 6 && length <= 10) {
        const areaCode = value.substring(0, 3);
        value = `(${areaCode}) ${value.substring(3, 6)}-${value.substring(6)}`;
      }
    }
    const updatedFormFields = {
      ...this.state.formFields,
      [inputName]: {
        ...this.state.formFields[inputName],
        value,
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
      isValid = value.length >= validationRules.minLength && isValid;
    }

    return isValid;
  };

  onSubmitHandler = async event => {
    event.preventDefault();

    const validityArray = [];
    for (const key in this.state.formFields) {
      validityArray.push(this.checkValidity(this.state.formFields[key].value, this.state.formFields[key].validation));
    }

    if (validityArray.includes(false)) {
      return;
    }

    // Submit some information to AWS cognito
    const username = this.state.formFields.username.value;
    const password = this.state.formFields.password.value;
    const email = this.state.formFields.email.value;
    try {
      const { user } = await Auth.signUp({
        username,
        password,
        attributes: {
          email,
        },
      });
      console.log(user);
      this.props.history.push(`/confirmSignup/${username}/${email}`);
    } catch (error) {
      console.log("error signing up:", error);
      this.setState({ error: error.message });
    }
  };

  errorConfirmedHandler = () => {
    this.setState({ error: null });
  };

  // updateAccount = async (email, username) => {
  // 	const formData = {
  // 		email,
  // 		username,
  // 		firstName: this.state.formFields.firstName.value,
  // 		lastName: this.state.formFields.lastName.value,
  // 		phoneNumber: this.state.formFields.phoneNumber.value,
  // 		privacyId: this.state.formFields.privacy.value,
  // 		city: this.state.formFields.city.value,
  // 		state: this.state.formFields.state.value,
  // 		country: "United States",
  // 	};
  // 	const token = this.props.idToken;

  // 	const headers = {
  // 		"Access-Control-Allow-Origin": "*",
  // 		Authorization: `Bearer ${token}`,
  // 	};
  // 	try {
  // 		return await axios.post("/updateAccount", formData, headers);
  // 	} catch (e) {
  // 		// this.setState({ error: e }, console.log(e));
  // 		// return e;
  // 	}
  // };

  render() {
    const inputFields = [];
    for (const key in this.state.formFields) {
      inputFields.push({
        id: key,
        config: this.state.formFields[key],
      });
    }

    return (
      <>
        <Modal show={this.state.error} modalClosed={this.errorConfirmedHandler}>
          {this.state.error}
        </Modal>
        <div className={classes.Auth}>
          <h3 className={classes.header}>Create An Account</h3>
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
                label={input.config.label}
                changed={event => this.inputChangedHandler(event, input.id)}
              />
            ))}
            <div className={classes.buttonContainer}>
              <Button type="submit">Create Account</Button>
            </div>
          </form>
        </div>
      </>
    );
  }
}

const mapStateToProps = state => ({
  idToken: state.idToken,
});

// const mapDispatchToProps = dispatch => {
//     return {
//         signin: (username, password) => dispatch(actions.auth(username, password))
//     }
// }

export default connect(mapStateToProps, null)(withRouter(withErrorHandler(CreateAccount, axios)));

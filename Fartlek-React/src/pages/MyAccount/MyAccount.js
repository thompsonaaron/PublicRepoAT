import React, { Component } from "react";
import { connect } from "react-redux";
// import * as actions from '../../store/actions/auth';
import { withRouter } from "react-router-dom";

import appContainer from "../../App.module.css";
import axios from "../../axios";
import Button from "../../components/UI/Button/Button";
import Input from "../../components/UI/Input/Input";
import Modal from "../../components/UI/Modal/Modal";
import withErrorHandler from "../../containers/HOC/withErrorHandler";
import classes from "./MyAccount.module.css";

// import { Auth } from 'aws-amplify';

class MyAccount extends Component {
  state = {
    formFields: {
      firstName: {
        elementType: "input",
        elementConfig: {
          label: "First Name",
        },
        value: "",
        validation: {
          required: true,
        },
        valid: false,
        started: false,
      },
      lastName: {
        elementType: "input",
        elementConfig: {
          label: "Last Name",
        },
        value: "",
        validation: {
          required: true,
        },
        valid: false,
        started: false,
      },
      phoneNumber: {
        elementType: "input",
        elementConfig: {
          label: "Phone number",
          placeholder: "(xxx) xxx-xxxx",
        },
        value: "",
        validation: {
          required: false,
          phoneNumber: true,
        },
        valid: false,
        started: false,
      },
      privacy: {
        elementType: "select",
        elementConfig: {
          label: "Privacy",
          options: [
            {
              value: "1",
              displayValue: "Public",
            },
            {
              value: "2",
              displayValue: "Private",
            },
          ],
        },
        validation: {
          required: true,
        },
        value: "1",
        valid: true,
        started: false,
      },
      city: {
        elementType: "input",
        elementConfig: {
          label: "City",
        },
        value: "",
        validation: {
          required: true,
        },
        valid: false,
        started: false,
      },
      state: {
        elementType: "select",
        elementConfig: {
          label: "State",
          options: [
            {
              value: "AZ",
              displayValue: "Arizona",
            },
            {
              value: "AL",
              displayValue: "Alabama",
            },
            {
              value: "AK",
              displayValue: "Alaska",
            },
            {
              value: "AR",
              displayValue: "Arkansas",
            },
            {
              value: "CA",
              displayValue: "California",
            },
            {
              value: "CO",
              displayValue: "Colorado",
            },
            {
              value: "CT",
              displayValue: "Connecticut",
            },
            {
              value: "DE",
              displayValue: "Delaware",
            },
            {
              value: "FL",
              displayValue: "Florida",
            },
            {
              value: "GA",
              displayValue: "Georgia",
            },
            {
              value: "HI",
              displayValue: "Hawaii",
            },
            {
              value: "ID",
              displayValue: "Idaho",
            },
            {
              value: "IL",
              displayValue: "Illinois",
            },
            {
              value: "IN",
              displayValue: "Indiana",
            },
            {
              value: "IA",
              displayValue: "Iowa",
            },
            {
              value: "KS",
              displayValue: "Kansas",
            },
            {
              value: "KY",
              displayValue: "Kentucky",
            },
            {
              value: "LA",
              displayValue: "Louisiana",
            },
            {
              value: "ME",
              displayValue: "Maine",
            },
            {
              value: "MD",
              displayValue: "Maryland",
            },
            {
              value: "MA",
              displayValue: "Massachusetts",
            },
            {
              value: "MI",
              displayValue: "Michigan",
            },
            {
              value: "MN",
              displayValue: "Minnesota",
            },
            {
              value: "MS",
              displayValue: "Mississippi",
            },
            {
              value: "MO",
              displayValue: "Missouri",
            },
            {
              value: "MT",
              displayValue: "Montana",
            },
            {
              value: "NE",
              displayValue: "Nebraska",
            },
            {
              value: "NV",
              displayValue: "Nevada",
            },
            {
              value: "NH",
              displayValue: "New Hampshire",
            },
            {
              value: "NJ",
              displayValue: "New Jersey",
            },
            {
              value: "NM",
              displayValue: "New Mexico",
            },
            {
              value: "NY",
              displayValue: "New York",
            },
            {
              value: "NC",
              displayValue: "North Carolina",
            },
            {
              value: "ND",
              displayValue: "North Dakota",
            },
            {
              value: "OH",
              displayValue: "Ohio",
            },
            {
              value: "OK",
              displayValue: "Oklahoma",
            },
            {
              value: "OR",
              displayValue: "Oregon",
            },
            {
              value: "PA",
              displayValue: "Pennsylvania",
            },
            {
              value: "RI",
              displayValue: "Rhode Island",
            },
            {
              value: "SC",
              displayValue: "South Carolina",
            },
            {
              value: "SD",
              displayValue: "South Dakota",
            },
            {
              value: "TN",
              displayValue: "Tennessee",
            },
            {
              value: "TX",
              displayValue: "Texas",
            },
            {
              value: "UT",
              displayValue: "Utah",
            },
            {
              value: "VT",
              displayValue: "Vermont",
            },
            {
              value: "VA",
              displayValue: "Virginia",
            },
            {
              value: "WA",
              displayValue: "Washington",
            },
            {
              value: "WV",
              displayValue: "West Virginia",
            },
            {
              value: "Wisconsin",
              displayValue: "Wisconsin",
            },
            {
              value: "WY",
              displayValue: "Wyoming",
            },
          ],
        },
        validation: {
          required: true,
        },
        value: "AZ",
        valid: true,
        started: false,
      },
    },
    username: "",
    email: "",
    error: null,
  };

  async componentDidMount() {
    const headers = {
      headers: {
        Authorization: this.props.idToken,
      },
    };
    if (this.props.idToken) {
      try {
        const returnedUser = await axios.get("/getUser", headers);

        if (returnedUser) {
          const newState = {
            ...this.state.formFields,
            formFields: {
              firstName: {
                ...this.state.formFields.firstName,
                value: returnedUser.data.firstName,
              },
              lastName: {
                ...this.state.formFields.lastName,
                value: returnedUser.data.lastName,
              },
              phoneNumber: {
                ...this.state.formFields.phoneNumber,
                value: returnedUser.data.phoneNumber,
              },
              privacy: {
                ...this.state.formFields.privacy,
                value: returnedUser.data.privacyId,
              },
              city: {
                ...this.state.formFields.city,
                value: returnedUser.data.city,
              },
              state: {
                ...this.state.formFields.state,
                value: returnedUser.data.state,
              },
            },
            username: returnedUser.data.username,
            email: returnedUser.data.email,
          };
          this.setState(newState);
        }
      } catch (error) {
        console.error(error);
      }
    }
  }

  inputChangedHandler = (event, inputName) => {
    let { value } = event.target;
    if (inputName === "phoneNumber") {
      const reg = /\(|\)|-|\s+|\D/gi;
      value = value.replaceAll(reg, "");
      const { length } = value;

      if (length >= 4 && length <= 6) {
        const areaCode = value.substring(0, 3);
        value = `${areaCode}-${value.substring(3)}`;
      }
      if (length > 6) {
        const areaCode = value.substring(0, 3);
        value = `${areaCode}-${value.substring(3, 6)}-${value.substring(6, 10)}`;
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

    if (!value) return false;

    if (validationRules.required) {
      if (typeof value !== "string") {
        value = value.toString();
      }
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
      this.setState({ error: "One or more required fields are blank" });
      return;
    }
    try {
      await this.updateAccount();
      this.props.history.push("/");
    } catch (error) {
      console.log("Error thrown in updateAccount of MyAccount.js");
    }
  };

  errorConfirmedHandler = () => {
    this.setState({ error: null });
  };

  updateAccount = async () => {
    const formData = {
      email: this.state.email,
      username: this.state.username,
      firstName: this.state.formFields.firstName.value,
      lastName: this.state.formFields.lastName.value,
      phoneNumber: this.state.formFields.phoneNumber.value,
      privacyId: this.state.formFields.privacy.value,
      city: this.state.formFields.city.value,
      state: this.state.formFields.state.value,
      country: "United States",
    };

    const headers = {
      headers: {
        // "Access-Control-Allow-Origin": "*",
        Authorization: this.props.idToken,
      },
    };
    const validationResponse = await axios.post("/createAccount", formData, headers);
    return validationResponse;
  };

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
        <div className={appContainer.container}>
          <form onSubmit={this.onSubmitHandler} className={classes.form}>
            <h1>Update Your Account</h1>
            <Input
              value={this.state.username}
              elementConfig={{
                label: "Username",
                readOnly: true,
                title: "This field is read only",
              }}
            />
            <Input
              value={this.state.email}
              elementConfig={{
                label: "Email",
                readOnly: true,
                title: "This field is read only",
              }}
            />
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
            <div className={classes.buttonContainer}>
              <Button type="submit">Update Account</Button>
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

export default connect(mapStateToProps, null)(withRouter(withErrorHandler(MyAccount, axios)));

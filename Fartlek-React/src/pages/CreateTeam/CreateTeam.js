import React, { useEffect, useState } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";

import appContainer from "../../App.module.css";
import axios from "../../axios";
import Button from "../../components/UI/Button/Button";
import Input from "../../components/UI/Input/Input";
import { stateOptions, usCities } from "../../constants/constants";
import withErrorHandler from "../../containers/HOC/withErrorHandler";
import * as actions from "../../store/actions/auth";
import classes from "./CreateTeam.module.css";

const CreateTeam = props => {
  const [teamName, setTeamName] = useState("");
  const [address, setAddress] = useState("");
  const [city, setCity] = useState("Apache Junction");
  const [teamState, setTeamState] = useState("AZ");
  const [zipcode, setZipcode] = useState("");
  const [privacyId, setPrivacyId] = useState(1);

  useEffect(() => {
    const { teamId } = props.match.params;
    if (teamId) {
      const currentTeam = props.teams.find(t => t.teamId == teamId);
      setTeamName(currentTeam.name);
      setAddress(currentTeam.address);
      setCity(currentTeam.city);
      setTeamState(currentTeam.state);
      setZipcode(currentTeam.zipcode);
      setPrivacyId(currentTeam.teamPrivacyId);
    }
  }, [props.match.params, props.teams]);

  // const checkValidity = (value, validationRules) => {
  //   let isValid = true;

  //   if (validationRules.required) {
  //     if (typeof value !== "string") {
  //       value = value.toString();
  //     }
  //     isValid = value.trim() !== "" && isValid;
  //   }

  //   if (validationRules.minLength) {
  //     isValid = value.length >= validationRules.minLength && isValid;
  //   }

  //   return isValid;
  // };

  const onSubmitHandler = async event => {
    event.preventDefault();

    try {
      if (props.match.params.teamId) {
        await editTeam();
      } else {
        await addCoachAndTeam();
      }
    } catch (error) {
      console.error(error);
    }
  };

  const editTeam = async () => {
    const headers = {
      headers: {
        Authorization: props.idToken,
        "Content-Type": "application/json",
      },
    };

    const team = {
      name: teamName,
      teamId: props.match.params.teamId,
      address: address,
      city: city,
      state: teamState,
      zipcode: zipcode,
      teamPrivacyId: privacyId,
    };

    await axios.post("/editTeam", team, headers);
    props.history.push("/scoreboard");
  };

  const addCoachAndTeam = async () => {
    const headers = {
      headers: {
        Authorization: props.idToken,
        "Content-Type": "application/json",
      },
    };

    const team = {
      name: teamName,
      address: address,
      city: city,
      state: teamState,
      zipcode: zipcode,
      teamPrivacyId: privacyId,
    };

    const { data: teamName } = await axios.post("/addCoachToNewTeam", team, headers);
    await props.updateReduxUser(props.idToken);
    props.history.push(`/teams/${teamName}`);
  };

  const selectedState = stateOptions.find(s => s.value === teamState);
  const cities = usCities.filter(o => o.state === selectedState?.displayValue);

  return (
    <div className={appContainer.container}>
      <h1>{props.match.params.teamId ? "Edit" : "Create New"} Team</h1>
      <form onSubmit={onSubmitHandler}>
        <Input
          key="teamName"
          elementType="input"
          elementConfig={{
            label: "Team Name",
          }}
          required={true}
          value={teamName}
          changed={e => setTeamName(e.target.value)}
        />
        <Input
          key="address"
          elementType="input"
          elementConfig={{
            label: "Street",
          }}
          required={false}
          value={address}
          changed={e => setAddress(e.target.value)}
        />
        <Input
          key="city"
          elementType="select"
          elementConfig={{
            label: "City",
            options: cities.map(c => ({ value: c.city, displayValue: c.city })),
          }}
          required={true}
          value={city}
          changed={e => setCity(e.target.value)}
        />
        <Input
          key="state"
          elementType="select"
          elementConfig={{
            label: "State",
            options: stateOptions,
          }}
          required={true}
          value={teamState}
          changed={e => setTeamState(e.target.value)}
        />
        <Input
          key="zipcdoe"
          elementType="input"
          elementConfig={{
            label: "Zipcode",
            maxLength: 5,
          }}
          required={true}
          value={zipcode}
          changed={e => setZipcode(e.target.value)}
        />
        <Input
          key="privacyId"
          elementType="select"
          elementConfig={{
            label: "Privacy",
            options: [
              {
                value: 1,
                displayValue: "Public",
              },
              {
                value: 2,
                displayValue: "Private",
              },
            ],
          }}
          required={true}
          value={privacyId}
          changed={e => setPrivacyId(+e.target.value)}
        />
        <div className={classes.buttonDiv}>
          <Button type="submit" config={{ width: "50%" }}>
            {props.match.params.teamId ? "Edit" : "Create"}
          </Button>
        </div>
      </form>
    </div>
  );
};

const mapStateToProps = state => ({
  idToken: state.idToken,
  roles: state.roles,
  teams: state.teams,
});

const mapDispatchToProps = dispatch => ({
  updateReduxUser: token => dispatch(actions.getUser(token)),
});

export default connect(mapStateToProps, mapDispatchToProps)(withRouter(withErrorHandler(CreateTeam, axios)));

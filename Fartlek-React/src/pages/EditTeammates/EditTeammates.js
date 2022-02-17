import React, { useCallback, useEffect, useState } from "react";
import { connect } from "react-redux";
import { useParams } from "react-router-dom";

import axios from "../../axios";
import Button from "../../components/UI/Button/Button";
import Input from "../../components/UI/Input/Input";
import Label from "../../components/UI/Label/Label";
import Spinner from "../../components/UI/Spinner/Spinner";
import withErrorHandler from "../../containers/HOC/withErrorHandler";
import classes from "./EditTeammates.module.css";

const EditTeammates = props => {
  let { teamId } = useParams();
  const [isLoading, setIsLoading] = useState(false);
  const [addedTeammate, setAddedTeammate] = useState("");
  const [removedTeammateId, setRemovedTeammateId] = useState("");
  const [addedCoach, setAddedCoach] = useState("");
  const [preferredName, setPreferredName] = useState("");
  const [removedCoach, setRemovedCoach] = useState("");
  const [teammates, setTeammates] = useState([]);
  const [coaches, setCoaches] = useState([]);

  const getTeammates = useCallback(async () => {
    const headers = {
      headers: {
        Authorization: props.idToken,
      },
    };
    const { data } = await axios.get(`/getTeammates?teamId=${teamId}`, {}, headers);
    return data;
  }, [axios, props.idToken, teamId]);

  const getCoaches = useCallback(async () => {
    const headers = {
      headers: {
        Authorization: props.idToken,
      },
    };
    const { data } = await axios.get(`/getTeam?teamId=${teamId}`, {}, headers);
    return [data.headCoach, ...data.assistantCoaches];
  }, [axios, props.idToken, teamId]);

  useEffect(
    () =>
      (async () => {
        const teammates = await getTeammates(teamId);
        setTeammates(teammates);
      })(),
    [getTeammates]
  );

  useEffect(
    () =>
      (async () => {
        const coaches = await getCoaches(teamId);
        setCoaches(coaches);
      })(),
    [getCoaches]
  );

  const addTeammateHandler = async () => {
    try {
      setIsLoading(true);
      const headers = {
        headers: {
          Authorization: props.idToken,
        },
      };
      await axios.post(`/addTeammate?username=${addedTeammate}&teamId=${teamId}`, {}, headers);
      setTeammates(await getTeammates(teamId));
      setAddedTeammate("");
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  const removeTeammateHandler = async () => {
    try {
      setIsLoading(true);
      const headers = {
        headers: {
          Authorization: props.idToken,
        },
      };
      await axios.post(`/removeTeammate?userId=${removedTeammateId}&teamId=${teamId}`, {}, headers);
      setTeammates(await getTeammates());
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  const addCoachHandler = async () => {
    try {
      setIsLoading(true);
      const headers = {
        headers: {
          Authorization: props.idToken,
        },
      };
      await axios.post(
        `/addAssistantCoach?userId=${addedCoach}&teamId=${teamId}&preferredName=${preferredName}`,
        {},
        headers
      );
      setCoaches(await getCoaches());
      setAddedCoach("");
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  const removeCoachHandler = async () => {
    setIsLoading(true);
    try {
      const headers = {
        headers: {
          Authorization: props.idToken,
        },
      };
      await axios.post(`/removeCoach?userId=${removedCoach}&teamId=${teamId}`, {}, headers);
      setCoaches(await getCoaches());
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <div className={classes.container}>
        <div className={classes.inputDiv}>
          <Label className={classes.label}>Add Teammate</Label>
          <Input elementType="input" value={addedTeammate} changed={e => setAddedTeammate(e.target.value)} />
          <Button primary={true} config={{ width: "50%" }} clicked={addTeammateHandler}>
            Add
          </Button>
        </div>
        <div className={classes.inputDiv}>
          <Label>Remove Teammate</Label>
          <Input
            elementType="select"
            elementConfig={{
              options: teammates.map(mate => ({
                value: mate.userId,
                displayValue: `${mate.firstName} ${mate.lastName} (${mate.username})`,
                key: mate.userId,
              })),
            }}
            style={{ rowDirection: "row" }}
            changed={e => setRemovedTeammateId(e.target.value)}
          />
          <Button secondary={true} config={{ width: "50%" }} clicked={removeTeammateHandler}>
            Remove
          </Button>
        </div>
        <div className={classes.inputDiv}>
          <Label>Add Coach</Label>
          <Input
            elementType="select"
            elementConfig={{
              options: teammates.map(mate => ({
                value: mate.userId,
                displayValue: `${mate.firstName} ${mate.lastName} (${mate.username})`,
                key: mate.userId,
              })),
            }}
            changed={e => setAddedCoach(e.target.value)}
          />
          <Input
            elementType="input"
            value={preferredName}
            elementConfig={{ placeholder: "Preferred Name" }}
            changed={e => setPreferredName(e.target.value)}
          ></Input>
          <Button primary={true} config={{ width: "50%" }} clicked={addCoachHandler}>
            Add
          </Button>
        </div>
        <div className={classes.inputDiv}>
          <Label>Remove Coach</Label>
          <Input
            elementType="select"
            elementConfig={{
              options: coaches.map(coach => ({
                value: coach.userId,
                displayValue: `${coach.coachName}`,
                key: coach.userId,
              })),
            }}
            changed={e => setRemovedCoach(e.target.value)}
          />
          <Button secondary={true} config={{ width: "50%" }} clicked={removeCoachHandler}>
            Remove
          </Button>
        </div>
      </div>
      {isLoading && <Spinner />}
    </>
  );
};

const mapStateToProps = state => ({
  idToken: state.idToken,
});

export default connect(mapStateToProps, null)(withErrorHandler(EditTeammates, axios));

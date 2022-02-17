import moment from "moment";
import React, { Component } from "react";
import { isSafari } from "react-device-detect";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";

import axios from "../../axios";
import Button from "../../components/UI/Button/Button";
import inputStyles from "../../components/UI/Input/Input.module.css";
import Modal from "../../components/UI/Modal/Modal";
import withErrorHandler from "../../containers/HOC/withErrorHandler";
import { convertDurationToFormattedString } from "../../util";
import classes from "./AddWorkout.module.css";

class AddWorkout extends Component {
  state = {
    formFields: {
      exercise: {
        validation: {
          required: false,
        },
        value: "1",
      },
      workoutDate: {
        value: "",
        validation: {
          required: true,
        },
        valid: false,
        started: false,
      },
      timeOfDay: {
        validation: {
          required: true,
        },
        value: "1",
      },
      location: {
        value: "",
        valid: true,
        validation: {
          required: false,
        },
      },
      distance: {
        value: "",
        validation: {
          required: false,
        },
        valid: true,
        started: false,
      },
      hours: {
        value: "",
        validation: {
          required: false,
        },
        valid: true,
        started: false,
      },
      minutes: {
        value: "",
        validation: {
          required: true,
        },
        valid: false,
        started: false,
      },
      seconds: {
        value: "",
        validation: {
          required: true,
        },
        valid: false,
        started: false,
      },
      heartRate: {
        value: "0",
        validation: {
          required: false,
        },
        valid: false,
        started: false,
      },
      cadence: {
        value: "0",
        validation: {
          required: false,
        },
        valid: false,
        started: false,
      },
      comments: {
        value: "",
        validation: {
          required: false,
        },
        valid: false,
        started: false,
      },
    },
    error: null,
    splitCounter: 0,
    workoutId: null,
  };

  async componentDidMount() {
    const { search } = this.props.history.location;
    const params = new URLSearchParams(search);
    const workoutId = params.get("workoutId");
    if (workoutId) {
      await this.getWorkoutById(workoutId);
    } else {
      this.insertCurrentDate();
    }
  }

  getWorkoutById = async workoutId => {
    const headers = {
      headers: {
        // "Access-Control-Allow-Origin": "*",
        Authorization: this.props.idToken,
      },
    };

    try {
      let workout = await axios.get(`/getWorkout?workoutId=${workoutId}`, headers);
      workout = workout.data;
      const currentState = this.state.formFields;
      currentState.exercise.value = workout.workoutType.workoutId;
      // const isSafari =
      //   /constructor/i.test(window.HTMLElement) ||
      //   (function (p) {
      //     return p.toString() === "[object SafariRemoteNotification]";
      //   })(!window["safari"]);

      const wDate = new Date(workout.workoutDate);
      const finalDate = isSafari ? moment(wDate).format("YYYY/MM/DD") : moment(wDate).format("YYYY-MM-DD");
      currentState.workoutDate.value = finalDate;
      // const workoutDate =
      //   typeof Date.prototype.toISOString === "function"
      //     ? wDate.toISOString().split("T")[0]
      //     : getIsoStandardDate(wDate);
      // currentState.workoutDate.value = workoutDate;
      // currentState.workoutDate.value = getIsoStandardDate(new Date(workout.workoutDate));
      currentState.timeOfDay.value = workout.workoutTime.timeOfDayId;
      currentState.location.value = workout.location;
      currentState.distance.value = workout.distance;
      currentState.heartRate.value = workout.averageHR;
      currentState.cadence.value = workout.cadence;
      currentState.comments.value = workout.comments;
      const duration = convertDurationToFormattedString(workout.duration);
      const hours = parseInt(duration.substring(0, 2)) || 0;
      const minutes = parseInt(duration.substring(3, 5)) || 0;
      const seconds = parseInt(duration.substring(6, 8)) || 0;
      currentState.hours.value = hours;
      currentState.minutes.value = minutes;
      currentState.seconds.value = seconds;
      const workoutSplits = workout.splits;
      for (const key in workoutSplits) {
        const splitMinutes = workoutSplits[key].splitTime.substring(0, 2);
        const splitSeconds = workoutSplits[key].splitTime.substring(3, 5);
        this.addSplitHandler(splitMinutes, splitSeconds);
      }

      this.setState({ formFields: currentState, workoutId: workout.workoutId });
    } catch (error) {
      console.error(error);
    }
  };

  insertCurrentDate = () => {
    const local = new Date();
    local.setMinutes(local.getMinutes() - local.getTimezoneOffset());
    const currentDate = local.toJSON().slice(0, 10);
    const updatedFormFields = {
      ...this.state.formFields,
      workoutDate: {
        ...this.state.formFields.workoutDate,
        value: currentDate,
      },
    };
    this.setState({ formFields: updatedFormFields });
  };

  inputChangedHandler = (event, inputName) => {
    const { value } = event.target;
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

    const fields = this.state.formFields;
    try {
      for (const key in fields) {
        const isValid = this.checkValidity(this.state.formFields[key].value, this.state.formFields[key].validation);
        if (!isValid) {
          throw new Error(`${key} cannot be blank`);
        }
      }

      await this.addWorkout();
      this.props.history.push("/");
    } catch (error) {
      this.setState({ error: error.message });
      console.log("error caught while adding/editing workout");
    }
  };

  errorConfirmedHandler = () => {
    this.setState({ error: null });
  };

  addWorkout = async () => {
    let hours = this.state.formFields.hours.value;
    const minutes = this.state.formFields.minutes.value;
    const seconds = this.state.formFields.seconds.value;
    if (hours === "") {
      hours = 0;
    }

    const duration = `PT${hours}H${minutes}M${seconds}S`;
    const splitArray = [];
    for (let i = 1; i <= this.state.splitCounter; i++) {
      const minute = this.state.formFields[`splitMinute${i}`].value;
      const second = this.state.formFields[`splitSecond${i}`].value;
      const split = `${minute}:${second}`;
      splitArray.push(split);
    }

    const formData = {
      workoutId: this.state.workoutId,
      //workoutType: this.state.formFields.exercise.value,
      workoutDate: this.state.formFields.workoutDate.value,
      //workoutTime: this.state.formFields.timeOfDay.value,
      location: this.state.formFields.location.value,
      distance: this.state.formFields.distance.value,
      duration,
      splits: splitArray,
      comments: this.state.formFields.comments.value,
      cadence: this.state.formFields.cadence.value,
      averageHR: this.state.formFields.heartRate.value,
    };

    const headers = {
      headers: {
        Authorization: this.props.idToken,
      },
    };
    if (this.state.workoutId) {
      await axios.post(
        `/editWorkout?workoutTypeId=${this.state.formFields.exercise.value}&workoutTimeId=${this.state.formFields.timeOfDay.value}`,
        formData,
        headers
      );
    } else {
      await axios.post(
        `/addWorkout?workoutTypeId=${this.state.formFields.exercise.value}&workoutTimeId=${this.state.formFields.timeOfDay.value}`,
        formData,
        headers
      );
    }
  };

  addSplitHandler = (minutes, seconds) => {
    // console.log("started adding a split");
    const { splitCounter } = this.state;
    const newSplitNum = splitCounter + 1;
    const currentFormFields = this.state.formFields;
    const { comments } = this.state.formFields;
    const minuteField = {
      value: minutes || "",
      validation: {
        required: false,
      },
      valid: false,
      started: false,
    };
    const secondField = {
      value: seconds || "",
      validation: {
        required: false,
      },
      valid: false,
      started: false,
    };
    delete currentFormFields.comments;
    currentFormFields[`splitMinute${newSplitNum}`] = minuteField;
    currentFormFields[`splitSecond${newSplitNum}`] = secondField;
    currentFormFields.comments = comments;
    this.setState({ formFields: currentFormFields, splitCounter: newSplitNum });
  };

  removeSplitHandler = () => {
    console.log("started removing a split");
    const { splitCounter } = this.state;
    const newSplitNum = splitCounter - 1;
    const currentFormFields = this.state.formFields;
    const { comments } = this.state.formFields;
    delete currentFormFields.comments;
    delete currentFormFields[`splitMinute${splitCounter}`];
    delete currentFormFields[`splitSecond${splitCounter}`];
    currentFormFields.comments = comments;
    this.setState({ formFields: currentFormFields, splitCounter: newSplitNum });
  };

  render() {
    const inputFields = [];
    for (const key in this.state.formFields) {
      inputFields.push({
        id: key,
        config: this.state.formFields[key],
      });
    }

    // const exercise = (
    //   <div className={inputStyles.Input}>
    //     <label className={inputStyles.Label}>Exercise</label>
    //     <select
    //       key="exercise"
    //       value={this.state.formFields.exercise.value}
    //       className={inputStyles.InputElement}
    //       onChange={event => this.inputChangedHandler(event, "exercise")}
    //     >
    //       <option value="1">Running</option>
    //       <option value="2">Biking</option>
    //       <option value="3">Swimming</option>
    //       <option value="4">Aqua Jogging</option>
    //       <option value="5">Nordic Skiing</option>
    //     </select>
    //   </div>
    // );

    // const workoutDate = (
    //   <div className={inputStyles.Input}>
    //     <label className={inputStyles.Label}>Date</label>
    //     <input
    //       key="workoutDate"
    //       value={this.state.formFields.workoutDate.value}
    //       className={inputStyles.InputElement}
    //       type="date"
    //       onChange={event => this.inputChangedHandler(event, "workoutDate")}
    //     />
    //   </div>
    // );

    // make this flex-direction column on mobile
    const duration = (
      <div className={classes.outerDurationDiv}>
        <label className={classes.Label}>Duration*</label>
        <div className={classes.durationDiv}>
          <input
            key="hours"
            type="number"
            placeholder="hours"
            value={this.state.formFields.hours.value}
            className={classes.durationInput}
            onChange={event => this.inputChangedHandler(event, "hours")}
          />
          <input
            key="minutes"
            type="number"
            placeholder="minutes"
            value={this.state.formFields.minutes.value}
            className={classes.durationInput}
            onChange={event => this.inputChangedHandler(event, "minutes")}
          />
          <input
            key="seconds"
            type="number"
            placeholder="seconds"
            value={this.state.formFields.seconds.value}
            className={classes.durationInput}
            onChange={event => this.inputChangedHandler(event, "seconds")}
          />
        </div>
      </div>
    );

    const timeOfDay = (
      <div className={inputStyles.Input}>
        <label className={inputStyles.Label}>Time of Day</label>
        <select
          key="timeOfDay"
          value={this.state.formFields.timeOfDay.value}
          className={inputStyles.InputElement}
          onChange={event => this.inputChangedHandler(event, "timeOfDay")}
        >
          <option value="1">Morning</option>
          <option value="2">Afternoon</option>
          <option value="3">Evening</option>
          <option value="4">Night</option>
        </select>
      </div>
    );

    const location = (
      <div className={inputStyles.Input}>
        <label className={inputStyles.Label}>Location</label>
        <input
          key="location"
          value={this.state.formFields.location.value}
          className={inputStyles.InputElement}
          onChange={event => this.inputChangedHandler(event, "location")}
        />
      </div>
    );

    const distance = (
      <div className={inputStyles.Input}>
        <label className={inputStyles.Label}>Distance</label>
        <input
          key="distance"
          type="number"
          step="0.01"
          value={this.state.formFields.distance.value}
          className={inputStyles.InputElement}
          onChange={event => this.inputChangedHandler(event, "distance")}
        />
      </div>
    );

    const heartRate = (
      <div className={inputStyles.Input}>
        <label className={inputStyles.Label}>Avg Heart Rate</label>
        <input
          key="heartRate"
          value={this.state.formFields.heartRate.value}
          className={inputStyles.InputElement}
          type="number"
          onChange={event => this.inputChangedHandler(event, "heartRate")}
        />
      </div>
    );

    const cadence = (
      <div className={inputStyles.Input}>
        <label className={inputStyles.Label}>Cadence</label>
        <input
          key="cadence"
          value={this.state.formFields.cadence.value}
          className={inputStyles.InputElement}
          type="number"
          onChange={event => this.inputChangedHandler(event, "cadence")}
        />
      </div>
    );

    const buttons = (
      <div className={inputStyles.Input}>
        <label className={classes.Label}>Splits</label>
        <div className={classes.buttonDiv}>
          <Button config={{ width: "50%" }} clicked={() => this.addSplitHandler(0, 0)} type="button">
            Add Split
          </Button>
          <Button config={{ width: "50%" }} clicked={this.removeSplitHandler} type="button">
            Remove Split
          </Button>
        </div>
      </div>
    );

    const comments = (
      <div className={inputStyles.Input}>
        <label className={inputStyles.Label}>Comments</label>
        <textarea
          key="comments"
          value={this.state.formFields.comments.value}
          className={inputStyles.InputElement}
          onChange={event => this.inputChangedHandler(event, "comments")}
        />
      </div>
    );

    const splitCount = this.state.splitCounter;
    const splits = [];
    for (let i = 1; i <= splitCount; i++) {
      const split = (
        <div className={classes.outerDurationDiv} key={`inputDiv${i}`}>
          <label className={classes.Label}>{i}</label>
          <div className={classes.durationDiv}>
            <input
              key={`splitMinute${i}`}
              placeholder="minutes"
              value={this.state.formFields[`splitMinute${i}`].value}
              className={classes.durationInput}
              onChange={event => this.inputChangedHandler(event, `splitMinute${i}`)}
            />
            <input
              key={`splitSecond${i}`}
              placeholder="seconds"
              value={this.state.formFields[`splitSecond${i}`].value}
              className={classes.durationInput}
              onChange={event => this.inputChangedHandler(event, `splitSecond${i}`)}
            />
          </div>
        </div>
      );
      splits[i] = split;
    }

    const buttonConfig = {
      width: "50%",
    };

    return (
      <>
        <Modal show={!!this.state.error} modalClosed={this.errorConfirmedHandler}>
          {this.state.error}
        </Modal>
        <div className={classes.Workout}>
          <form onSubmit={this.onSubmitHandler} className={classes.form}>
            <h1>{this.state.workoutId ? "Edit Workout" : "Add Workout"}</h1>
            <div className={inputStyles.Input} key="exerciseDiv">
              <label className={inputStyles.Label}>Exercise</label>
              <select
                key="exercise"
                value={this.state.formFields.exercise.value}
                className={inputStyles.InputElement}
                onChange={event => this.inputChangedHandler(event, "exercise")}
              >
                <option value="1">Running</option>
                <option value="2">Biking</option>
                <option value="3">Swimming</option>
                <option value="4">Aqua Jogging</option>
                <option value="5">Nordic Skiing</option>
              </select>
            </div>
            <div className={inputStyles.Input} key="workoutDateDiv">
              <label className={inputStyles.Label}>Date</label>
              <input
                key="workoutDate"
                value={this.state.formFields.workoutDate.value}
                className={inputStyles.InputElement}
                type="date"
                onChange={event => this.inputChangedHandler(event, "workoutDate")}
              />
            </div>
            {timeOfDay}
            {location}
            {distance}
            {duration}
            {heartRate}
            {cadence}
            {buttons}
            {splits}
            {comments}
            <div className={classes.Button}>
              <Button type="submit" config={buttonConfig}>
                {this.state.workoutId ? "Edit Workout" : "Add Workout"}
              </Button>
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

export default connect(mapStateToProps)(withRouter(withErrorHandler(AddWorkout, axios)));

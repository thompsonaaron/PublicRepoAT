import moment from "moment";
import React, { Component } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";

import axios from "../../axios";
import CoachingCommentModal from "../../components/UI/CoachingCommentModal/CoachingCommentModal";
import Paginator from "../../components/UI/Paginator/Paginator";
import SplitModal from "../../components/UI/SplitModal/SplitModal";
import withErrorHandler from "../../containers/HOC/withErrorHandler";
import { convertDurationToMinutes } from "../../util";
import classes from "./TrainingLog.module.css";
import TrainingLogCard from "./TrainingLogCard";
import TrainingLogTable from "./TrainingLogTable";

class TrainingLog extends Component {
  state = {
    week: "0",
    initialWorkout: "",
    logger: null,
    workouts: [],
    workout: null,
    relationship: null,
    screenWidth: null,
    showSplitModal: false,
    showCoachingCommentsModal: false,
    summaryData: [],
  };

  async componentDidMount() {
    window.addEventListener("resize", this.updateWindowDimensions);
    const { logger, week } = this.getLoggerUsernameAndWeek();
    try {
      let [{ allWorkouts, relationship }, { data }] = await Promise.all([
        this.getTrainingLog(logger, week),
        axios.get(`/getUserByUsername?username=${logger}`, {
          headers: {
            Authorization: this.props.idToken,
          },
        }),
      ]);

      const summaryData = this.createTrainingLogSummary(allWorkouts);
      this.setState({
        workouts: allWorkouts,
        relationship: relationship,
        screenWidth: window.innerWidth,
        week,
        logger: data,
        summaryData: summaryData,
      });
    } catch (error) {
      console.log(`Error thrown in getTrainingLog from within TrainingLog.js. Error is ${error.Message}`);
    }
  }

  async componentDidUpdate(prevProps, prevState) {
    this.mounted = true;

    const { logger, week } = this.getLoggerUsernameAndWeek();
    if (week != this.state.week) {
      this.setState({ week });
    }

    if (this.state.logger && logger != this.state.logger.username) {
      const loggerInfo = await axios.get("/", {
        headers: {
          // "Access-Control-Allow-Origin": "*",
          Authorization: this.props.idToken,
        },
      });
      this.setState({ logger: loggerInfo });
    }

    if (this.mounted && (prevState.week != this.state.week || prevState.logger != this.state.logger)) {
      const { allWorkouts } = await this.getTrainingLog(logger, week);
      if (allWorkouts != prevState.workouts) {
        const summaryData = this.createTrainingLogSummary(allWorkouts);
        this.setState({
          week,
          workouts: allWorkouts,
          summaryData: summaryData,
        });
      }
    }
  }

  getLoggerUsernameAndWeek = () => {
    const { search } = this.props.location;
    const params = new URLSearchParams(search);
    let week = params.get("week");
    let logger = params.get("username");

    if (week === null || week === "") {
      week = this.state.week;
    }

    if (logger === null) {
      logger = this.props.username;
    }

    return { logger, week };
  };

  componentWillUnmount() {
    window.removeEventListener("resize", this.updateWindowDimensions);
    this.mounted = false;
  }

  updateWindowDimensions = () => {
    console.log(`screen width is ${window.innerWidth}`);
    this.setState({ screenWidth: window.innerWidth });
  };

  getTrainingLog = async (username, week) => {
    const headers = {
      headers: {
        Authorization: this.props.idToken,
      },
    };

    if (!week) {
      week = this.state.week;
    }

    if (!username) {
      username = this.props.username;
    }
    const url = `/trainingLog?username=${username}&week=${week}`;
    const response = await axios.get(url, headers);
    return response.data;
  };

  createTrainingLogSummary = allWorkouts => {
    const exerciseTypes = ["Running", "Cycling", "Swimming", "Aqua Jogging", "Nordic Skiing"];

    const summaryData = [];
    for (let i = 0; i < 5; i++) {
      const exerciseWorkouts = allWorkouts.filter(workout => workout.workoutType.workoutId == i + 1);
      const summaryRow = this.inputTrainingSummaryFields(exerciseWorkouts, exerciseTypes[i]);
      if (summaryRow) {
        summaryData.push(summaryRow);
      }
    }
    return summaryData;
  };

  inputTrainingSummaryFields = (allWorkouts, exerciseType) => {
    if (allWorkouts.length > 0) {
      const workoutLength = allWorkouts.length;
      let totalDistance = 0;
      let totalSeconds = 0;
      let cadenceCounter = 0;
      let totalCadence = 0;
      let heartRateCounter = 0;
      let totalHeartRate = 0;
      // let distanceCounter = 0;
      // let durationCounter = 0;
      for (let i = 0; i < workoutLength; i++) {
        if (allWorkouts[i].distance != 0) {
          totalDistance += allWorkouts[i].distance;
          // distanceCounter++;
        }

        if (moment.duration(allWorkouts[i].duration).asSeconds() != 0) {
          totalSeconds += moment.duration(allWorkouts[i].duration).asSeconds();
          // durationCounter++;
        }

        if (allWorkouts[i].cadence != 0) {
          totalCadence += allWorkouts[i].cadence;
          cadenceCounter++;
        }
        if (allWorkouts[i].averageHR != 0) {
          totalHeartRate += allWorkouts[i].averageHR;
          heartRateCounter++;
        }
      }

      totalDistance = totalDistance.toFixed(2);
      let totalSecPerMile = 0;
      let totalMinPerMile;
      if (totalDistance != 0) {
        totalSecPerMile = (totalSeconds * 1000) / totalDistance;
      }
      totalMinPerMile = Math.floor(totalSecPerMile / 60) || 0;
      totalSecPerMile = Math.floor(totalSecPerMile - totalMinPerMile * 60) || 0;

      if (totalSecPerMile < 10) {
        totalSecPerMile = `0${totalSecPerMile}`;
      }
      const totalAvgPace = `${totalMinPerMile}:${totalSecPerMile}`;
      const totalTime = convertDurationToMinutes(totalSeconds);
      const avgCadence = Math.floor(totalCadence / cadenceCounter) || 0;
      const avgHR = Math.floor(totalHeartRate / heartRateCounter) || 0;
      return {
        exerciseType,
        avgHR,
        avgCadence,
        totalDuration: totalTime,
        avgPace: totalAvgPace,
        totalDistance,
      };
    }
  };

  saveCoachingComments = async (event, workoutId) => {
    event.preventDefault();
    const headers = {
      headers: {
        // "Access-Control-Allow-Origin": "*",
        Authorization: this.props.idToken,
        "Content-Type": "application/json",
      },
    };
    const url = `/addCoachingComments?workoutId=${workoutId}`;
    const initialWorkouts = this.state.workouts;

    let coachingComments = null;
    for (const w of initialWorkouts) {
      if (w.workoutId == workoutId) {
        coachingComments = w.coachingComments;
        w.ccTimestamp = +new Date();
        w.ccUserId = this.props.userId;
      }
    }
    const formData = {
      coachingComments,
      ccUserId: this.props.userId,
    };

    try {
      await axios.post(url, formData, headers);
      const { allWorkouts } = await this.getTrainingLog(this.state.logger.username, this.state.week);
      this.setState({
        showCoachingCommentsModal: false,
        workouts: allWorkouts,
      });
    } catch (error) {
      console.error(error);
    }
  };

  inputChangedHandler = (event, workoutId) => {
    const { value } = event.target;
    const { workouts } = this.state;
    const workoutIndex = workouts.findIndex(workout => workout.workoutId == workoutId);
    workouts[workoutIndex].coachingComments = value;
    this.setState({ workouts });
  };

  deleteWorkoutHandler = async workoutId => {
    const headers = {
      headers: {
        // "Access-Control-Allow-Origin": "*",
        Authorization: this.props.idToken,
      },
    };
    try {
      await axios.post("/deleteWorkout", { workoutId }, headers);
      const allWorkouts = this.state.workouts;
      const newWorkouts = allWorkouts.filter(workout => workout.workoutId != workoutId);
      this.setState({ workouts: newWorkouts });
    } catch (error) {
      console.error("Error thrown in deleteWorkout from within TrainingLog.js");
    }
  };

  redirectTrainingLogWeek = (username, week) => {
    this.props.history.push(`/trainingLog?username=${username}&week=${week}`);
  };

  resetCoachingComments = () => {
    let allWorkouts = [...this.state.workouts];
    const arrayPosition = allWorkouts.findIndex(workout => workout.workoutId === this.state.initialWorkout.workoutId);
    if (arrayPosition !== -1) {
      allWorkouts[arrayPosition].coachingComments = this.state.initialWorkout.coachingComments;
    }

    this.setState({
      workouts: allWorkouts,
      initialWorkout: "",
      showCoachingCommentsModal: false,
    });
  };

  toggleCoachingComments = workout => {
    const currentStatus = this.state.showCoachingCommentsModal;
    this.setState({
      showCoachingCommentsModal: !currentStatus,
      workout: workout,
      initialWorkout: { ...workout },
    });
  };

  showSplitModal = workout => {
    this.setState({ showSplitModal: true, workout });
  };

  render() {
    return (
      <div className={classes.container}>
        <div style={{ fontWeight: "bold" }}>
          {this.state.logger && `${this.state.logger.firstName}'s Training Log `}
        </div>
        {this.state.screenWidth < 900 && (
          <Paginator
            baseURL="/trainingLog"
            pageParam="week"
            pageParamValue={this.state.week}
            username={this.props.username}
            logger={this.state.logger && this.state.logger.username}
            setPage={this.redirectTrainingLogWeek}
          />
        )}
        {this.state.workouts && this.state.relationship && this.state.screenWidth < 900 && (
          <TrainingLogCard
            workouts={this.state.workouts}
            relationship={this.state.relationship}
            summaryData={this.state.summaryData}
            toggleCoachingComments={this.toggleCoachingComments}
            deleteWorkoutHandler={this.deleteWorkoutHandler}
            logger={this.state.logger}
          />
        )}
        {this.state.workouts && this.state.relationship && this.state.screenWidth >= 900 && (
          <TrainingLogTable
            workouts={this.state.workouts}
            relationship={this.state.relationship}
            summaryData={this.state.summaryData}
            showSplitModal={this.showSplitModal}
            toggleCoachingComments={this.toggleCoachingComments}
            deleteWorkoutHandler={this.deleteWorkoutHandler}
          />
        )}
        {this.state.screenWidth >= 900 && (
          <Paginator
            baseURL="/trainingLog"
            pageParam="week"
            pageParamValue={this.state.week}
            username={this.props.username}
            logger={this.state.logger && this.state.logger.username}
            setPage={this.redirectTrainingLogWeek}
          />
        )}

        {this.state.showSplitModal && (
          <SplitModal
            workout={this.state.workout}
            showSplitModal={this.state.showSplitModal}
            onCloseModal={() => this.setState({ showSplitModal: false })}
          />
        )}
        {this.state.showCoachingCommentsModal && (
          <CoachingCommentModal
            show={this.state.showCoachingCommentsModal}
            onCloseModal={() => this.setState({ showCoachingCommentsModal: false })}
            workout={this.state.workout}
            inputChangedHandler={this.inputChangedHandler}
            onSubmit={this.saveCoachingComments}
            reset={() => this.resetCoachingComments()}
          />
        )}
      </div>
    );
  }
}

const mapStateToProps = state => ({
  idToken: state.idToken,
  username: state.username,
  userId: state.userId,
});

export default withRouter(connect(mapStateToProps)(withErrorHandler(TrainingLog, axios)));

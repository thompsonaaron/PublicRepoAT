import moment from "moment";
import React from "react";
import { withRouter } from "react-router-dom";

import { ReactComponent as EditSvg } from "../../img/SVG/edit.svg";
import { ReactComponent as DeleteSvg } from "../../img/SVG/trash.svg";
import { convertDurationToFormattedString, getLongFormDate } from "../../util";
import classes from "./TrainingLogCard.module.css";

const Card = props => {
  const { workouts, relationship, toggleCoachingComments, deleteWorkoutHandler, summaryData } = props;

  return (
    <div className={classes.container}>
      {summaryData && summaryData.length > 0 && (
        <div className={classes.cardContainer}>
          <div style={{ borderTop: "10px solid #0b5aa2" }}>
            <strong>Week Summary:</strong>
            {summaryData.map(exSummary => (
              <div style={{ textAlign: "left", paddingBottom: "15px" }}>
                <span>
                  <strong>{exSummary.exerciseType}</strong>
                </span>
                <div style={{ display: "flex", flexDirection: "column" }}>
                  <span>Distance: {exSummary.totalDistance} miles</span>
                  <span>Duration: {exSummary.totalDuration} minutes</span>
                  <span>Average Pace: {exSummary.avgPace}</span>
                  {!!exSummary.avgHR && <span>Average HR: {exSummary.avgHR}</span>}
                  {!!exSummary.avgCadence && <span>Average Cadence: {exSummary.avgCadence}</span>}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
      {workouts.map(workout => {
        return (
          <div className={classes.cardContainer} key={workout.workoutId}>
            <div className={classes.borderBox}>
              <span className={classes.noMarginSpan}>
                {workout.workoutDate && getLongFormDate(workout.workoutDate)}
              </span>
              <span>
                {relationship === "user" && (
                  <>
                    <EditSvg
                      width="15px"
                      height="15px"
                      style={{
                        cursor: "pointer",
                        fill: "#0B5AA2",
                        marginRight: "10px",
                      }}
                      onClick={() => props.history.push(`/addWorkout?workoutId=${workout.workoutId}`)}
                      title="Edit Workout"
                    />
                    <DeleteSvg
                      width="15px"
                      height="15px"
                      style={{ cursor: "pointer", fill: "#0B5AA2" }}
                      onClick={() => deleteWorkoutHandler(workout.workoutId)}
                      title="Delete Workout"
                    />
                  </>
                )}
              </span>
            </div>
            <div style={{ display: "flex" }}>{workout.workoutType.workoutName}</div>
            <div style={{ display: "flex" }}>{workout.location}</div>
            <div
              style={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-evenly",
                marginTop: "10px",
              }}
            >
              <div className={classes.flexColumn}>
                <span className={classes.noMarginSpan + " " + classes.label}>Duration</span>
                <span>{convertDurationToFormattedString(workout.duration)}</span>
              </div>
              <div className={classes.flexColumn}>
                <span className={classes.noMarginSpan + " " + classes.label}>Distance</span>
                <span>{workout.distance}</span>
              </div>
              <div className={classes.flexColumn}>
                <span className={classes.noMarginSpan + " " + classes.label}>Pace</span>
                <span>{workout.averagePace}</span>
              </div>
            </div>
            <div
              style={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-evenly",
                marginTop: "10px",
              }}
            >
              <div className={classes.flexColumn}>
                <span className={classes.noMarginSpan + " " + classes.label}>Cadence</span>
                <span>{workout.cadence}</span>
              </div>
              <div className={classes.flexColumn}>
                <span className={classes.noMarginSpan + " " + classes.label}>Average HR</span>
                <span>{workout.averageHR}</span>
              </div>
            </div>
            <div className={[classes.noMarginSpan, classes.label, classes.flexTopMargin].join(" ")}>Splits</div>
            <div
              style={{
                display: "flex",
                flexDirection: "column",
              }}
            >
              {workout &&
                workout.splits &&
                workout.splits.length > 0 &&
                workout.splits.map((split, index) => (
                  <span style={{ display: "flex", alignSelf: "flex-start" }} key={`splitIndex${index}`}>
                    {index + 1})&nbsp;{split.splitTime.substring(0, 5)}
                  </span>
                ))}
            </div>
            <div className={[classes.noMarginSpan, classes.label, classes.flexTopMargin].join(" ")}>Comments</div>
            <div style={{ display: "flex" }}>{workout.comments}</div>
            <div className={classes.flexTopMargin}>
              <span className={classes.noMarginSpan + " " + classes.label}>Coaching Comments</span>
              <span className={classes.noMarginSpan}>
                {relationship === "coach" && (
                  <EditSvg
                    width="15px"
                    height="15px"
                    style={{ cursor: "pointer", fill: "#0B5AA2" }}
                    onClick={() => toggleCoachingComments(workout)}
                    title="EditCoachingComments"
                  />
                )}
              </span>
            </div>
            <div
              style={{
                display: "flex",
                textAlign: "left",
              }}
            >
              {workout.coachingComments}
            </div>
            <div
              style={{
                display: "flex",
                textAlign: "left",
                fontSize: ".8rem",
                paddingBottom: "1rem",
              }}
            >
              {workout.ccCoachName && " - " + workout.ccCoachName}
              {workout.ccTimestamp && ", " + moment(workout.ccTimestamp).format("dddd, MMMM Do YYYY, h:mm:ss a")}
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default withRouter(Card);

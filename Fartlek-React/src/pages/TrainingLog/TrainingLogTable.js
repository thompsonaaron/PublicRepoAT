import moment from "moment";
import React, { useCallback, useEffect, useState } from "react";
import { withRouter } from "react-router-dom";

import Button from "../../components/UI/Button/Button";
import { ReactComponent as EditSvg } from "../../img/SVG/edit.svg";
import { ReactComponent as DeleteSvg } from "../../img/SVG/trash.svg";
import { convertDurationToFormattedString } from "../../util";
import classes from "./TrainingLog.module.css";

const TrainingLogTable = props => {
  const { workouts, relationship, summaryData, deleteWorkoutHandler, showSplitModal, toggleCoachingComments } = props;
  let [workoutJsx, setWorkoutJsx] = useState();

  const renderTrainingLogTable = useCallback(() => {
    let workoutRows = [];

    for (let i = 0; i < workouts.length; i++) {
      const workout = workouts[i];
      const { workoutDate } = workout;
      const workoutUtcString = `${workoutDate[0]}-${workoutDate[1]}-${workoutDate[2]}`;
      const workoutMoment = moment(workoutUtcString, "YYYY-MM-DD");
      const workoutDay = workoutMoment.format("dddd");
      const formattedWorkoutDate = workoutMoment.format("MMM DD, YYYY");

      let buttonColumns = null;
      switch (relationship) {
        case "user":
          buttonColumns = (
            <>
              <td>
                <EditSvg
                  width="15px"
                  height="15px"
                  style={{ cursor: "pointer", fill: "#0B5AA2" }}
                  onClick={() => props.history.push(`/addWorkout?workoutId=${workout.workoutId}`)}
                  title="Edit Workout"
                />
              </td>
              <td>
                <DeleteSvg
                  width="15px"
                  height="15px"
                  style={{ cursor: "pointer", fill: "#0B5AA2" }}
                  onClick={() => deleteWorkoutHandler(workout.workoutId)}
                  title="Delete Workout"
                />
              </td>
            </>
          );
          break;
        case "coach":
          buttonColumns = (
            <td colSpan="2">
              <Button clicked={() => toggleCoachingComments(workouts[i])}>Comment</Button>
            </td>
          );
          break;
        default:
          buttonColumns = (
            <>
              <td />
              <td />
            </>
          );
      }

      const formattedWorkout = (
        <tr key={`workout${i}`}>
          <td className={classes.row}>{workout.workoutType.workoutName}</td>
          <td>{workoutDay}</td>
          <td style={{ whiteSpace: "nowrap" }}>{formattedWorkoutDate}</td>
          <td>{workout.workoutTime.name}</td>
          <td>{workout.location}</td>
          <td>{workout.distance.toFixed(2)}</td>
          <td>{convertDurationToFormattedString(workout.duration)}</td>
          <td>{workout.averagePace.slice(0, 5)}</td>
          <td>{workout.averageHR}</td>
          <td>{workout.cadence}</td>
          <td>{workout.splits.length > 0 ? <Button clicked={() => showSplitModal(workout)}>Splits</Button> : null}</td>
          <td>{workout.comments}</td>
          {buttonColumns}
        </tr>
      );

      const coachingCommentsRow =
        workout.coachingComments != null ? (
          <tr key={`coachingComments${i}`} className={classes.coachComment}>
            <td colSpan="14">
              {workout.coachingComments + " - "}
              {workout.ccCoachName && workout.ccCoachName}
              {workout.ccTimestamp && " @ " + moment(workout.ccTimestamp).format("dddd, MMMM Do YYYY, h:mm:ss a")}
            </td>
          </tr>
        ) : null;

      workoutRows.push(formattedWorkout);
      if (coachingCommentsRow) {
        workoutRows.push(coachingCommentsRow);
      }
    }
    setWorkoutJsx(workoutRows);
  }, [workouts, deleteWorkoutHandler, props.history, relationship, showSplitModal, toggleCoachingComments]);

  useEffect(() => {
    if (workouts) renderTrainingLogTable();
    console.log("rendering table");
  }, [workouts, renderTrainingLogTable]);

  return (
    <>
      <table className={classes.table}>
        <thead className={classes.thead}>
          <tr className={classes.row}>
            <th className={classes.row}>Exercise</th>
            <th>Day</th>
            <th>Date</th>
            <th>Time</th>
            <th>Location</th>
            <th>Distance</th>
            <th>Duration</th>
            <th>Pace</th>
            <th>AvgHR</th>
            <th>Cadence</th>
            <th>Splits</th>
            <th>Comments</th>
            <th />
            <th />
            <th />
            <th />
          </tr>
        </thead>
        <tbody className={classes.tbody}>{workoutJsx}</tbody>
        <tfoot className={classes.tfooter} style={{ fontWeight: "bold" }}>
          {summaryData &&
            summaryData.length > 0 &&
            summaryData.map(exSummary => (
              <tr key={`summaryData ${exSummary.exerciseType}`}>
                <td className={classes.row}>{exSummary.exerciseType}</td>
                <td>Total</td>
                <td />
                <td />
                <td />
                <td>{exSummary.totalDistance}</td>
                <td>{exSummary.totalDuration}</td>
                <td>{exSummary.avgPace}</td>
                <td>{exSummary.avgHR}</td>
                <td>{exSummary.avgCadence}</td>
                <td />
                <td />
                <td />
                <td />
              </tr>
            ))}
        </tfoot>
      </table>
    </>
  );
};

export default withRouter(TrainingLogTable);

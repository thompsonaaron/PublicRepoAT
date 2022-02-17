import moment from "moment";
import React from "react";

import Backdrop from "../Backdrop/Backdrop";
import classes from "./SplitModal.module.css";

const SplitModal = props => {
  // takes workout, showModal, onCloseModal as prop

  let workoutDay = null;
  let formattedWorkoutDate = null;

  if (props.workout) {
    const { workoutDate } = props.workout;
    const workoutUtcString = `${workoutDate[0]}-${workoutDate[1]}-${workoutDate[2]}`;
    const workoutMoment = moment(workoutUtcString, "YYYY-MM-DD");
    workoutDay = workoutMoment.format("dddd");
    formattedWorkoutDate = workoutMoment.format("MMM DD, YYYY");
  }

  return (
    <>
      {props.workout && (
        <>
          <Backdrop opacity="20%" show={props.showSplitModal} clicked={props.onCloseModal} />
          <div
            className={classes.modal}
            style={{
              transform: props.showSplitModal ? "translateY(0)" : "translateY(-100vh)",
              opacity: props.showSplitModal ? "1.0" : "0.0",
              display: "flex",
              justifyContent: "center",
              flexDirection: "row",
              alignItems: "center",
            }}
          >
            <h3>
              {workoutDay} {formattedWorkoutDate}
            </h3>
            <ol
              style={{
                display: "flex",
                flexDirection: "column",
                paddingRight: "20px",
                margin: "0px 0px",
              }}
            >
              {props.workout.splits.map((split, index) => (
                <div>{`${index + 1})  ${split.splitTime.substring(0, 5)}`}</div>
              ))}
            </ol>
          </div>
        </>
      )}
    </>
  );
};

export default SplitModal;

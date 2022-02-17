import moment from "moment";
import React from "react";

import Backdrop from "../Backdrop/Backdrop";
import Button from "../Button/Button";
import Input from "../Input/Input";
import classes from "./CoachingCommentModal.module.css";

const CoachingCommentModal = props => {
  const { workout, show, onSubmit, inputChangedHandler, onCloseModal, reset } = props;
  let workoutDay = null;
  let formattedWorkoutDate = null;

  if (workout) {
    const { workoutDate } = workout;
    const workoutUtcString = `${workoutDate[0]}-${workoutDate[1]}-${workoutDate[2]}`;
    const workoutMoment = moment(workoutUtcString, "YYYY-MM-DD");
    workoutDay = workoutMoment.format("dddd");
    formattedWorkoutDate = workoutMoment.format("MMM DD, YYYY");
  }

  return (
    <>
      {workout && (
        <>
          <Backdrop show={show} clicked={onCloseModal} />
          <div
            className={classes.modal}
            style={{
              transform: show ? "translateY(0)" : "translateY(-100vh)",
              opacity: show ? "1.0" : "0.0",
            }}
          >
            <h3>Coaching Comments</h3>
            <span>
              {workoutDay} {formattedWorkoutDate}
            </span>
            <form onSubmit={event => onSubmit(event, workout.workoutId)}>
              <Input
                elementType="textarea"
                value={workout.coachingComments || ""}
                changed={event => inputChangedHandler(event, workout.workoutId)}
              />

              <Button type="reset" clicked={reset}>
                Cancel
              </Button>
              <Button type="submit">Save</Button>
            </form>
          </div>
        </>
      )}
    </>
  );
};

export default CoachingCommentModal;

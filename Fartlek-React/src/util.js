import moment from "moment";

export const convertDurationToFormattedString = duration => {
  const momDuration = moment.duration(duration * 1000);
  const totalSeconds = momDuration.asSeconds();
  let hours = Math.floor(totalSeconds / 3600);
  let minutes = Math.floor((totalSeconds - hours * 3600) / 60);
  let seconds = Math.floor(totalSeconds - hours * 3600 - minutes * 60);

  if (hours < 10) {
    hours = `0${hours}`;
  }
  if (minutes < 10) {
    minutes = `0${minutes}`;
  }
  if (seconds < 10) {
    seconds = `0${seconds}`;
  }

  return `${hours}:${minutes}:${seconds}`;
};

export const convertDurationToFormattedMinSec = duration => {
  // let momDuration = moment.duration(duration * 1000);
  // let totalSeconds = momDuration.asSeconds();

  const totalSeconds = moment.duration(duration, "seconds");
  let totalTime = parseInt(totalSeconds / 60, 10);
  let seconds = (totalSeconds % 60).toFixed(0);
  if (seconds < 10) {
    seconds = "0" + seconds;
  }
  totalTime += `:${seconds}`;

  return totalTime;
};

export const convertDurationToMinutes = duration => {
  const totalSeconds = moment.duration(duration, "seconds");
  return parseInt(totalSeconds / 60, 10);
};

export const getFormattedDate = date => {
  const year = date.getFullYear();

  let month = (1 + date.getMonth()).toString();
  month = month.length > 1 ? month : `0${month}`;

  let day = date.getDate().toString();
  day = day.length > 1 ? day : `0${day}`;

  return `${month}/${day}/${year}`;
};

export const getIsoStandardDate = date => {
  const year = date.getFullYear();

  let month = (1 + date.getMonth()).toString();
  month = month.length > 1 ? month : `0${month}`;

  let day = date.getDate().toString();
  day = day.length > 1 ? day : `0${day}`;

  return `${year}-${month}-${day}`;
};

export const getLongFormDate = date => {
  if (!date) {
    return "";
  }
  const workoutUtcString = `${date[0]}-${date[1]}-${date[2]}`;
  const workoutMoment = moment(workoutUtcString, "YYYY-MM-DD");
  return workoutMoment.format("MMM DD, YYYY");
};

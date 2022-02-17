import { Auth } from "aws-amplify";

import axios from "../../axios";
import * as actionTypes from "./actionTypes";

export const success = () => ({
  type: actionTypes.SUCCESS,
});
// export const authStart = () => ({
//   type: actionTypes.AUTH_START,
// });

export const authSuccess = (username, email, idToken, refreshToken) => ({
  type: actionTypes.AUTH_SUCCESS,
  username,
  idToken,
  email,
  refreshToken,
});

// export const logoutStart = () => ({
//   type: actionTypes.LOGOUT_START,
// });

export const logoutSuccess = () => ({
  type: actionTypes.LOGOUT_SUCCESS,
});

export const logout = () => async dispatch => {
  //dispatch(logoutStart());
  // try {
  await Auth.signOut();
  // } catch (error) {
  //   // eslint-disable-next-line no-console
  //   console.log("error signing out: ", error);
  // }
  dispatch(logoutSuccess());
};

// export const getUserStart = () => ({
//   type: actionTypes.GET_USER_START,
// });

export const getUserSuccess = (userId, userTeams, userRoles) => ({
  type: actionTypes.GET_USER_SUCCESS,
  userId: userId,
  userTeams: userTeams,
  userRoles: userRoles,
});

// export const getUserFailed = (error) => ({
//   type: actionTypes.GET_USER_FAILED,
//   error: error,
// });

const getUserFromServer = async idToken => {
  const headers = {
    headers: {
      Authorization: idToken,
    },
  };
  const url = "/getUser";
  return await axios.get(url, headers);
};

// authenticates a user at sign in; should rename logIn
export const auth = (username, password) => async dispatch => {
  //try {
  //dispatch(authStart());
  await Auth.signIn(username, password);
  const { email, idToken, refreshToken } = await checkAuthenticationStatus();
  dispatch(authSuccess(username, email, idToken, refreshToken));
  await dispatch(getUser(idToken));
  // } catch (e) {
  //   console.log(`failed to get user in auth with error: ${e}`);
  //   dispatch(getUserFailed(e.message));
  // }
};

const checkAuthenticationStatus = async () => {
  const currentSession = await Auth.currentSession();
  if (currentSession) {
    // const username = currentSession.idToken.payload["cognito:username"];
    const { email } = currentSession.idToken.payload;
    const idToken = currentSession.idToken.jwtToken;
    const refreshToken = currentSession.refreshToken.token;
    return { email, idToken, refreshToken };
  }
};

// looks in local storage for pre-existing token
export const checkAuth = () => async dispatch => {
  try {
    const currentSession = await Auth.currentSession();
    if (currentSession) {
      const username = currentSession.idToken.payload["cognito:username"];
      const { email } = currentSession.idToken.payload;
      const idToken = currentSession.idToken.jwtToken;
      const refreshToken = currentSession.refreshToken.token;
      dispatch(authSuccess(username, email, idToken, refreshToken));
      await dispatch(getUser(idToken));
      return idToken;
    }
  } catch (error) {
    console.error(error);
  }
};

export const getUser = idToken => async dispatch => {
  const response = await getUserFromServer(idToken);
  if (response) {
    const { userId, userTeams, userRoles } = response.data;
    dispatch(getUserSuccess(userId, userTeams, userRoles));
  }
};

export const joinTeamStart = () => ({
  type: actionTypes.JOIN_TEAM_START,
});

export const joinTeamFail = () => ({
  type: actionTypes.JOIN_TEAM_FAIL,
});

const joinTeam = async (username, teamId, idToken) => {
  const headers = {
    headers: {
      Authorization: idToken,
      //"Access-Control-Allow-Origin": "*",
    },
  };
  const url = `/addTeammate?username=${username}&teamId=${teamId}`;
  return axios.post(url, {}, headers);
};

export const joinTeamOnServer = (username, teamId, idToken) => async dispatch => {
  //dispatch(joinTeamStart());
  const response = await joinTeam(username, teamId, idToken);
  if (response) {
    await dispatch(getUser(idToken));
    // dispatch(actionTypes.JOIN_TEAM_SUCCESS);
  }
};

const leaveTeam = async (userId, teamId, idToken) => {
  const headers = {
    headers: {
      Authorization: idToken,
      // "Access-Control-Allow-Origin": "*",
      "Content-Type": "application/json",
    },
  };
  return axios.post(`/removeTeammate?userId=${userId}&teamId=${teamId}`, {}, headers);
};

// reusing start, success, fail methods
export const leaveTeamOnServer = (userId, teamId, idToken) => async dispatch => {
  dispatch(joinTeamStart());
  const response = await leaveTeam(userId, teamId, idToken);
  if (response) {
    dispatch(getUser(idToken));
  }
  // else {
  //   dispatch(joinTeamFail());
  // }
};

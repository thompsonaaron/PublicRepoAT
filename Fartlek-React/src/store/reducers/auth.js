import * as actionTypes from "../actions/actionTypes";

const initialState = {
  email: null,
  error: null,
  idToken: null,
  loading: false,
  refreshToken: null,
  roles: [],
  teams: [],
  userId: null,
  username: null,
};

const authReducer = (state = initialState, action) => {
  switch (action.type) {
    case actionTypes.SUCCESS:
      return {
        ...state,
        loading: false,
      };
    case actionTypes.AUTH_START:
      return {
        ...state,
        loading: true,
      };
    case actionTypes.AUTH_SUCCESS:
      return {
        ...state,
        idToken: action.idToken,
        refreshToken: action.refreshToken,
        loading: false,
        userId: action.userId,
        username: action.username,
        email: action.email,
        error: false,
      };
    case actionTypes.LOGOUT_START:
      return {
        ...state,
        loading: true,
      };
    case actionTypes.LOGOUT_SUCCESS:
      return {
        idToken: null,
        refreshToken: null,
        username: null,
        email: null,
        error: null,
        loading: false,
        teams: null,
      };
    case actionTypes.GET_USER_START:
      return {
        ...state,
        loading: true,
      };
    case actionTypes.GET_USER_SUCCESS:
      return {
        ...state,
        userId: action.userId,
        roles: action.userRoles,
        teams: action.userTeams,
        loading: false,
      };
    case actionTypes.GET_USER_FAILED:
      return {
        ...state,
        loading: false,
        error: action.error,
      };
    case actionTypes.JOIN_TEAM_START:
      return {
        ...state,
        loading: true,
      };
    case actionTypes.JOIN_TEAM_SUCCESS:
      return {
        ...state,
        teams: action.teams,
      };
    case actionTypes.JOIN_TEAM_FAIL:
      return {
        ...state,
        loading: false,
      };
    default:
      return state;
  }
};

export default authReducer;

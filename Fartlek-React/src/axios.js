import axios from "axios";

import config from "./config/config";
//import { connect } from "react-redux";
//import * as actions from "./store/actions/auth";

const instance = axios.create({
  //     // baseURL: 'https://server.fartlek.us',
  baseURL: config.axiosBaseURL,
  headers: {
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Headers":
      "Accept,Origin,Content-Type,X-LS-CORS-Template,X-LS-Auth-Token,X-LS-Auth-User-Token,Content-Type,X-LS-Sync-Result,X-LS-Sequence,token",
    "Access-Control-Allow-Credentials": "true",
    "Access-Control-Allow-Methods": "POST, GET, OPTIONS, DELETE",
  },
});

// const axiosInstance = (props) => {
//   const instance = axios.create({
//     // baseURL: 'https://server.fartlek.us',
//     baseURL: "http://localhost:5000",
//   });

//   // const instance = axios.create({
//   //   // baseURL: 'https://server.fartlek.us',
//   //   baseURL: "http://localhost:5000",
//   // });

//   instance.interceptors.request.use((req) => {
//     const headers = {
//       Authorization: props.idToken,
//     };
//     req.headers = {
//       ...headers,
//       ...req.headers,
//     };

//     return req;
//   });
//   return instance;
// };

// const mapStateToProps = (state) => ({
//   idToken: state.idToken,
// });

// const mapDispatchToProps = (dispatch) => ({
//   checkStorageForToken: () => dispatch(actions.checkAuth()),
// });

// export default connect(mapStateToProps, mapDispatchToProps)(axiosInstance);

export default instance;

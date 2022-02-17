const config = {
  s3: {
    REGION: "us-east-2",
    BUCKET: "harrier-non-static-pages", // for files?
  },
  cognito: {
    REGION: "us-east-2",
    USER_POOL_ID: "hidden",
    APP_CLIENT_ID: "hidden",
    IDENTITY_POOL_ID: "hidden",
  },
  redirectURL: "https://fartlek.us/",
  axiosBaseURL: "https://api.fartlek.us",
  //axiosBaseURL: "http://localhost:5000",
};

export default config;

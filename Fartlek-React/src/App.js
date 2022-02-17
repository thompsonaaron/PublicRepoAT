import "./App.css";

import React, { Component, lazy, Suspense } from "react";
import { connect } from "react-redux";
import { Redirect, Route, Switch } from "react-router-dom";

import Spinner from "./components/UI/Spinner/Spinner";
import Layout from "./containers/Layout/Layout";
import Scoreboard from "./pages/Scoreboard/Scoreboard";
import * as actions from "./store/actions/auth";
const AddWorkout = lazy(() => import("./pages/AddWorkout/AddWorkout"));
const Auth = lazy(() => import("./pages/Auth/Login"));
const ChangePassword = lazy(() => import("./pages/Auth/ChangePassword"));
const ConfirmSignup = lazy(() => import("./pages/Auth/ConfirmSignup"));
const CreateAccount = lazy(() => import("./pages/Auth/CreateAccount"));
const CreateTeam = lazy(() => import("./pages/CreateTeam/CreateTeam"));
const EditTeammates = lazy(() => import("./pages/EditTeammates/EditTeammates"));
const Logout = lazy(() => import("./pages/Auth/Logout"));
const JoinTeam = lazy(() => import("./pages/JoinTeam/JoinTeam"));
const MyAccount = lazy(() => import("./pages/MyAccount/MyAccount"));
const NewPost = lazy(() => import("./pages/NewPost/NewPost"));
const RegisterCoach = lazy(() => import("./pages/RegisterCoach/RegisterCoach"));
const ForgotPassword = lazy(() => import("./pages/Auth/ForgotPassword"));
const TeamHome = lazy(() => import("./pages/TeamHome/TeamHome"));
const TrainingLog = lazy(() => import("./pages/TrainingLog/TrainingLog"));

class App extends Component {
  constructor(props) {
    super(props);
    this.props.checkStorageForToken();
  }

  render() {
    if (!this.props.isAuthenticated) {
      this.props.checkStorageForToken();
    }

    let routes = (
      <Switch>
        <Route path="/login" exact component={Auth} />
        <Route path="/createAccount" exact component={CreateAccount} />
        <Route path="/forgotPassword" exact component={ForgotPassword} />
        <Route path="/changePassword/:username/:email" component={ChangePassword} />
        <Route path="/confirmSignup/:username/:email" component={ConfirmSignup} />
        <Redirect from="*" to="/login" />
      </Switch>
    );

    if (this.props.isAuthenticated) {
      routes = (
        <Switch>
          <Route path="/logout" exact component={Logout} />
          <Route path="/new-post/:teamName/:teamId/:listingId?" component={NewPost} />
          <Route path="/scoreboard/week" component={Scoreboard} />
          <Route path="/myAccount" component={MyAccount} />
          <Route path="/addWorkout" exact component={AddWorkout} />
          <Route path="/trainingLog" exact component={TrainingLog} />
          <Route path="/teams/joinTeam" exact component={JoinTeam} />
          <Route path="/teams/EditTeammates/:teamId" exact component={EditTeammates} />
          <Route path="/teams/edit-team-info/:teamId" exact component={CreateTeam} />
          <Route path="/teams/:teamName" component={TeamHome} />
          <Route path="/coaches/registerAsCoach" exact component={RegisterCoach} />
          <Route path="/coaches/createATeam" component={CreateTeam} />
          <Route path="/scoreboard" component={Scoreboard} />
          <Redirect from="*" to="/scoreboard" />
        </Switch>
      );
    }

    return (
      <div className="App">
        <Suspense
          fallback={
            <Layout>
              <Spinner />
            </Layout>
          }
        >
          <Layout>{routes}</Layout>
        </Suspense>
      </div>
    );
  }
}

// for nested routes with different output depending on path
// const MatchTrainingLog = (match) => (
//   <Switch>
//     <Route path={`${match.path}`} component={TrainingLog}/>
//     <Route path={`${match.path}/:username/:week`} component={MatchWeek}/>
//   </Switch>
// )

// let match = useRouteMatch('/trainingLog');
// console.log('Match path: ' + `${match.path}/:teamName`);
// return (
//   <Switch>
//     <Route path={`/trainingLog/:username/:week`}  component={TrainingLog} />
//   </Switch>
// )

// const MatchWeek = match => (
//   <Switch>
//     <Route path={`${match.path}/:week`} component={TrainingLog}/>
//   </Switch>
// )

const mapStateToProps = state => ({
  isAuthenticated: state.idToken !== null,
});

const mapDispatchToProps = dispatch => ({
  checkStorageForToken: () => dispatch(actions.checkAuth()),
});

export default connect(mapStateToProps, mapDispatchToProps)(App);

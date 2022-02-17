import React from "react";
import { Link } from "react-router-dom";

import classes from "./TeamHome.module.css";

const LeftSidebar = props => {
  const newPostURL = `/new-post/${props.teamName}/${props.teamId}`;
  return (
    <>
      <div className={classes.leftSidebarItem}>
        <Link to={newPostURL}>New Post</Link>
      </div>
      <div className={classes.leftSidebarItem}>
        <Link to={`/teams/EditTeammates/${props.teamId}`}>Edit Teammates</Link>
      </div>
      <div className={classes.leftSidebarItem}>
        <Link to={`/teams/edit-team-info/${props.teamId}`}>Edit Team</Link>
      </div>
    </>
  );
};

export default LeftSidebar;

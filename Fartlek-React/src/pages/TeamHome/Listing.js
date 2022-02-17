import React from "react";
import ReactHtmlParser from "react-html-parser";
import { connect } from "react-redux";
import { Link } from "react-router-dom";

import { getLongFormDate } from "../../util";
import classes from "./Listing.module.css";

const Listing = props => {
  const { listingDetails, isACoach, deleteListing } = props;
  const teamId = listingDetails.teamId;
  const listingId = listingDetails.listingId;

  const teamName = props.teams.find(team => team.teamId === teamId).name;

  return (
    <div className={classes.container}>
      {/* THE HEADER */}
      <div className={classes.header}>
        <div className={classes.subHeader}>
          <h4 style={{ marginBottom: "0" }}>{listingDetails.title}</h4>
          <span style={{ margin: "0" }}>{getLongFormDate(listingDetails.dateTime)}</span>
          {isACoach && (
            <div>
              <Link
                style={{ display: "inline", marginRight: "20px" }}
                to={`/new-post/${teamName}/${teamId}/${listingId}`}
              >
                EDIT
              </Link>
              <a onClick={deleteListing} style={{ display: "inline", cursor: "pointer" }}>
                DELETE
              </a>
            </div>
          )}
        </div>
        {/* THE BODY */}
        <div className={classes.body}>{ReactHtmlParser(listingDetails.content)}</div>
        {/* THE FOOTER */}
        <div className={classes.footer}>
          {listingDetails &&
            listingDetails.listingFiles &&
            listingDetails.listingFiles.map(file => (
              <a target="_blank" href={file.filePath} key={file.filePath} rel="noreferrer">
                {file.title}
              </a>
            ))}
        </div>
      </div>
    </div>
  );
};

const mapStateToProps = state => {
  return {
    teams: state.teams,
  };
};

export default connect(mapStateToProps, null)(Listing);

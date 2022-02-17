import React, { useCallback, useEffect, useRef, useState } from "react";
import { connect } from "react-redux";

import axios from "../../axios";
import Button from "../../components/UI/Button/Button";
import Input from "../../components/UI/Input/Input";
import Modal from "../../components/UI/Modal/Modal";
import withErrorHandler from "../../containers/HOC/withErrorHandler";
import LoadMore from "../../img/loadMore.png";
import * as actions from "../../store/actions/auth";
import LeftSidebar from "./LeftSidebar";
import Listing from "./Listing";
import classes from "./TeamHome.module.css";

const TeamHome = props => {
  const [teamName, setTeamName] = useState(props.match.params.teamName);
  const [team, setTeam] = useState(props.teams.find(team => team.name === "teamName"));
  const [listings, setListings] = useState([]);
  const [lastListingId, setLastListingId] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const resourcesToUpload = useRef(null);

  const getTeamListings = useCallback(async () => {
    if (team) {
      const url = `/getTeamListings?teamId=${team.teamId}&lastListingId=${lastListingId}`;
      const headers = {
        headers: {
          Authorization: props.idToken,
          // "Access-Control-Allow-Origin": "*",
        },
      };
      try {
        const response = await axios.get(url, headers);
        const returnedListings = response.data;

        // if team changed, then reset and only show returned listings
        const oldTeamId = listings.length > 0 ? listings.map(listing => listing.teamId)[0] : 0;

        // if team didn't change, then add to current output
        team.teamId === oldTeamId ? setListings([...listings, ...returnedListings]) : setListings(returnedListings);

        const lastListing =
          returnedListings.length > 0 ? returnedListings[returnedListings.length - 1].listingId : lastListingId;

        if (lastListing !== lastListingId) {
          setLastListingId(lastListing);
        } else {
          setLastListingId(0);
        }
      } catch (error) {
        console.error(error);
      }
    }
  }, [team, lastListingId, props.idToken]);

  useEffect(async () => {
    await getTeamListings();
  }, [team, teamName]);

  useEffect(async () => {
    const newTeamName = props.match.params.teamName;
    const newTeam = props.teams.find(team => team.name === newTeamName);
    if (!newTeam) {
      props.history.push("/");
    }
    if (newTeam !== team) {
      setTeam(newTeam);
      setTeamName(newTeam.name);
      setLastListingId(0);
    }
  }, [props]);

  const deleteListingHandler = async listingId => {
    try {
      const URL = `/deleteListing?listingId=${listingId}`;
      const headers = {
        headers: {
          Authorization: props.idToken,
          // "Access-Control-Allow-Origin": "*",
        },
      };
      await axios.post(URL, {}, headers);
      const editedListings = listings.filter(listing => listing.listingId !== listingId);
      setListings(editedListings);
    } catch (error) {
      console.log(error);
    }
  };

  const addResourcesHandler = async () => {
    const URL = `/uploadFile?teamId=${team.teamId}`;
    let formData = new FormData();

    const files = resourcesToUpload.current.files;
    for (let file of files) {
      formData.append("file", file);
    }

    const headers = {
      contentType: false,
      processData: false,
      headers: {
        Accept: "application/json",
        Enctype: "multipart/form-data",
        Authorization: props.idToken,
        // "Access-Control-Allow-Origin": "*",
      },
    };
    await axios.post(URL, formData, headers);
    const { data: newTeam } = await axios.get(`/getTeam?teamId=${team.teamId}`, {
      headers: { Authorization: props.idToken },
    });
    setTeam(newTeam);
    setIsModalOpen(false);
    props.getUserDetails(props.idToken);
  };

  const deleteResourceHandler = async resourceId => {
    const url = `/deleteResource?resourceId=${resourceId}`;
    const headers = {
      headers: {
        Authorization: props.idToken,
      },
    };
    try {
      await axios.post(url, {}, headers);
      const currentTeam = { ...team };
      const newResources = currentTeam.teamResources.filter(resource => resource.resourceId !== resourceId);
      currentTeam.teamResources = newResources;
      setTeam(currentTeam);
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <>
      {team && (
        <div className={classes.mainDiv}>
          <h2 className={classes.teamHeader}>{teamName}</h2>
          <div className={classes.container}>
            <div className={classes.leftSidebar}>
              {(team.headCoach.userId === props.userId ||
                team.assistantCoaches.map(coach => coach.userId).includes(props.userId)) && (
                <LeftSidebar teamName={teamName} teamId={team.teamId} />
              )}
            </div>
            <div className={classes.teamHome}>
              {listings &&
                listings.length > 0 &&
                listings.map((listing, index) => (
                  <Listing
                    listingDetails={listing}
                    key={listing.title + index}
                    isACoach={true}
                    deleteListing={() => deleteListingHandler(listing.listingId)}
                  />
                ))}

              <div
                style={{
                  display: "flex",
                  justifyContent: "center",
                  cursor: "pointer",
                }}
              >
                {listings.length > 0 && lastListingId > 0 && (
                  <img
                    style={{ pointerEvents: "all" }}
                    src={LoadMore}
                    height="150px"
                    width="150px"
                    onClick={() => getTeamListings()}
                  />
                )}
              </div>
            </div>
            <div className={classes.rightSidebar}>
              <strong>
                Resources
                <span onClick={() => setIsModalOpen(true)} className={classes.plusIcon}>
                  +
                </span>
              </strong>
              {team &&
                team.teamResources?.map(resource => (
                  <div
                    key={resource.url}
                    style={{
                      display: "flex",
                      flexDirection: "row",
                      cursor: "pointer",
                    }}
                  >
                    <span onClick={() => deleteResourceHandler(resource.resourceId)}>&nbsp;x&nbsp; </span>
                    <a href={resource.url} target="_blank" rel="noreferrer">
                      {resource.title}
                    </a>
                  </div>
                ))}
            </div>
          </div>
        </div>
      )}
      {/* Make a confirmation modal that can be re-used */}
      <Modal show={isModalOpen} modalClosed={() => setIsModalOpen(false)}>
        <Input elementType="input" elementConfig={{ type: "file", multiple: true }} reference={resourcesToUpload} />
        <Button primary={true} clicked={addResourcesHandler}>
          upload
        </Button>
        <Button clicked={() => setIsModalOpen(false)}>cancel</Button>
      </Modal>
    </>
  );
};

const mapStateToProps = state => {
  return {
    idToken: state.idToken,
    roles: state.roles,
    teams: state.teams,
    userId: state.userId,
  };
};

const mapDispatchToProps = dispatch => ({
  getUserDetails: token => dispatch(actions.getUser(token)),
});

export default withErrorHandler(connect(mapStateToProps, mapDispatchToProps)(TeamHome), axios);

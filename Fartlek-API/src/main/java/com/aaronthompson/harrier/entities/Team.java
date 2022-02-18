/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.entities;

import java.util.List;
import java.util.Map;

/**
 *
 * @author thomp
 */
public class Team {

    private Integer teamId;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zipcode;
    private Integer teamPrivacyId;

    public Coach getHeadCoach() {
        return headCoach;
    }

    public void setHeadCoach(Coach headCoach) {
        this.headCoach = headCoach;
    }

    public List<Coach> getAssistantCoaches() {
        return assistantCoaches;
    }

    public void setAssistantCoaches(List<Coach> assistantCoaches) {
        this.assistantCoaches = assistantCoaches;
    }

    private Coach headCoach;
    private List<Coach> assistantCoaches;
    private List<Resource> teamResources;

    // added 7.20.2020
//    private List<Listing> teamListings; // AS MUCH AS THIS MAKES TYPICAL SENSE, LISTINGS ARE ALWAYS RECEIVED SEPARATE FROM A TEAM ANYWAYS
    // IF I CHANGED THE RELATIONSHIP BETWEEN USERS AND TEAMS, THEN TEAMS WOULD BE THE CENTRAL FIGURE AND HOLD TEAMUSERS, TEAMLISTINGS, ETC. 
    // BUT THEN IT WOULD BE A LOT MORE TO QUERY TO LOAD THE MAIN PAGE. I COULD LEAVE IT HERE, BUT JUST NOT ASSIGN TO IT MOST OF THE TIME.

    public Integer getTeamPrivacyId() {
        return teamPrivacyId;
    }

    public void setTeamPrivacyId(Integer teamPrivacyId) {
        this.teamPrivacyId = teamPrivacyId;
    }

    public List<Resource> getTeamResources() {
        return teamResources;
    }

    public void setTeamResources(List<Resource> teamResources) {
        this.teamResources = teamResources;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

}

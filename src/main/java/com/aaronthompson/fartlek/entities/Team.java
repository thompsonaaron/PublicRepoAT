/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.fartlek.entities;

import java.util.List;
import java.util.Map;

/**
 *
 * @author thomp
 */
public class Team {

    public List<User> getTeamUsers() {
        return teamUsers;
    }

    public void setTeamUsers(List<User> teamUsers) {
        this.teamUsers = teamUsers;
    }

    private Integer teamId;
    private Integer privacyId;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zipcode;

    private List<Coach> teamCoaches;

    private List<User> teamUsers;

    public List<Coach> getTeamCoaches() {
        return teamCoaches;
    }

    public void setTeamCoaches(List<Coach> teamCoaches) {
        this.teamCoaches = teamCoaches;
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

    public int getPrivacyId() {
        return privacyId;
    }

    public void setPrivacyId(Integer privacyId) {
        this.privacyId = privacyId;
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

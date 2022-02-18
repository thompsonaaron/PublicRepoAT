/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.entities;

/**
 *
 * @author thomp
 */
public class Coach extends User {

    private Integer userId;
    //private Integer teamId;
    private String coachName;
    private String biography;
    //private int coachRoleId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

//    public Integer getTeamId() {
//        return teamId;
//    }
//
//    public void setTeamId(Integer teamId) {
//        this.teamId = teamId;
//    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
//
//    public int getCoachRoleId() {
//        return coachRoleId;
//    }
//
//    public void setCoachRoleId(int coachRoleId) {
//        this.coachRoleId = coachRoleId;
//    }

}

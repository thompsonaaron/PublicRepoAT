/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.fartlek.entities;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 *
 * @author thomp
 */
public class Workout {

    private Integer workoutId;
    private Integer userId;
    private WorkoutType workoutType;
    private LocalDateTime workoutDateTime;
    private String location;
    private double distance;
    private String duration;
    private String averagePace;
    private String comments;
    private String coachingComments;
    private List<Split> splits;

    public Integer getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(Integer workoutId) {
        this.workoutId = workoutId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    public LocalDateTime getWorkoutDateTime() {
        return workoutDateTime;
    }

    public void setWorkoutDateTime(LocalDateTime workoutDateTime) {
        this.workoutDateTime = workoutDateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAveragePace() {
        return averagePace;
    }

    public void setAveragePace(String averagePace) {
        this.averagePace = averagePace;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCoachingComments() {
        return coachingComments;
    }

    public void setCoachingComments(String coachingComments) {
        this.coachingComments = coachingComments;
    }

    public List<Split> getSplits() {
        return splits;
    }

    public void setSplits(List<Split> splits) {
        this.splits = splits;
    }

    public Workout addWorkout(Workout workout) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}

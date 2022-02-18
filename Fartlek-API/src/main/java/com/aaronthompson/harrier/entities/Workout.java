/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.entities;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotBlank;

/**
 *
 * @author thomp
 */
public class Workout {

    private Integer workoutId;
    private Integer userId;
    @NotBlank
    private WorkoutType workoutType;
    @NotBlank
    private LocalDate workoutDate;
    private WorkoutTime workoutTime;
    private String location;
    private double distance;
    @NotBlank
    private Duration duration;
    private String averagePace;
    private Integer cadence;
    private Integer averageHR;
    private String comments;
    private String coachingComments;
    private Integer ccUserId;

    public String getCcCoachName() {
        return ccCoachName;
    }

    public void setCcCoachName(String ccCoachName) {
        this.ccCoachName = ccCoachName;
    }

    private String ccCoachName;

    public Timestamp getCcTimestamp() {
        return ccTimestamp;
    }

    public void setCcTimestamp(Timestamp ccTimestamp) {
        this.ccTimestamp = ccTimestamp;
    }

    private Timestamp ccTimestamp;
    private List<Split> splits;

    public Integer getCcUserId() {
        return ccUserId;
    }

    public void setCcUserId(Integer ccUserId) {
        this.ccUserId = ccUserId;
    }

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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
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

    public Integer getCadence() {
        return cadence;
    }

    public void setCadence(Integer cadence) {
        this.cadence = cadence;
    }

    public Integer getAverageHR() {
        return averageHR;
    }

    public void setAverageHR(Integer averageHR) {
        this.averageHR = averageHR;
    }

    public LocalDate getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(LocalDate workoutDate) {
        this.workoutDate = workoutDate;
    }

    public WorkoutTime getWorkoutTime() {
        return workoutTime;
    }

    public void setWorkoutTime(WorkoutTime workoutTime) {
        this.workoutTime = workoutTime;
    }
}

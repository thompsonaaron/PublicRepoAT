/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.fartlek.entities;

import java.sql.Time;

/**
 *
 * @author thomp
 */
public class Split {
    
    private Integer splitId;
    private Integer workoutId;
    private String splitTime;

    public Integer getSplitId() {
        return splitId;
    }

    public void setSplitId(Integer splitId) {
        this.splitId = splitId;
    }

    public Integer getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(Integer workoutId) {
        this.workoutId = workoutId;
    }

    public String getSplitTime() {
        return splitTime;
    }

    public void setSplitTime(String splitTime) {
        this.splitTime = splitTime;
    }


    
}

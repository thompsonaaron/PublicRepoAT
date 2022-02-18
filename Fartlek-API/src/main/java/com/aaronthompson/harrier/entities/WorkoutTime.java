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
public class WorkoutTime {

    private int timeOfDayId;
    private String name;

    public int getTimeOfDayId() {
        return timeOfDayId;
    }

    public void setTimeOfDayId(int timeOfDayId) {
        this.timeOfDayId = timeOfDayId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

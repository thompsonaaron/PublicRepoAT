/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.fartlek.dao;

import com.aaronthompson.fartlek.entities.Workout;
import com.aaronthompson.fartlek.entities.WorkoutType;
import java.util.List;

/**
 *
 * @author thomp
 */
public interface WorkoutDao {

    public List<Workout> getAllWorkouts();

    public List<Workout> getAllWorkoutsByUserId(Integer userId);

    public WorkoutType getWorkoutType(Integer typeId);

    public Workout addWorkout(Workout workout);

    public void deleteWorkoutByUserID(Integer userId);
}

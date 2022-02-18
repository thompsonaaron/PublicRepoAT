/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.dao;

import com.aaronthompson.harrier.entities.Coach;
import com.aaronthompson.harrier.entities.User;
import com.aaronthompson.harrier.entities.Workout;
import com.aaronthompson.harrier.entities.WorkoutType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 *
 * @author thomp
 */
public interface WorkoutDao {

    public List<Workout> getAllWorkouts();

    public List<Workout> getAllWorkoutsByUserId(Integer userId);

    public WorkoutType getWorkoutType(Integer typeId);

    public Workout addWorkout(Workout workout);

    public Workout getWorkoutById(Integer workoutId) throws DaoException;

    public Workout editWorkout(Workout workout);

    public void deleteWorkoutById(Integer workoutId) throws DaoException;
    
    public List<Workout> getAllWorkoutsByWeek(Integer userId, Integer pageNum);

    public List<Workout> getAllWorkoutsByDateRange(Integer userId, LocalDate startingLd, LocalDate endingLd);

    public Map<User, Double> getRankedOrderOfMileageByTeammate(List<User> teammates, Integer selectedWeek);

    public void addUserAsCoach (Coach coach);
    }

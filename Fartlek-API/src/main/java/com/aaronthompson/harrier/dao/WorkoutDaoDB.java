/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.dao;

import com.aaronthompson.harrier.entities.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author thomp
 */
@Repository
public class WorkoutDaoDB implements WorkoutDao {

    @Autowired
    JdbcTemplate template;

    // this is not used, only for collecting data
    @Override
    public List<Workout> getAllWorkouts() {

        List<Workout> allWorkouts = template.query("SELECT * FROM Workouts", new workoutMapper());

        // assigns the list of splits for each workout
        allWorkouts.forEach((w) -> {
            List<Split> allSplits = template.query("SELECT * FROM Splits WHERE workoutId = ?", new splitMapper(), w.getWorkoutId());
            w.setSplits(allSplits);
            WorkoutTime workoutTime = template.queryForObject("SELECT * FROM WorkoutTimes Where timeOfDayId = ?", new workoutTimeMapper(), w.getWorkoutTime().getTimeOfDayId());
            w.setWorkoutTime(workoutTime);
        });

        return allWorkouts;
    }

    @Override
    public List<Workout> getAllWorkoutsByUserId(Integer userId) {
        List<Workout> allWorkouts = template.query("SELECT * FROM Workouts WHERE userId = ? ORDER BY date DESC", new workoutMapper(), userId);

        allWorkouts.forEach((w) -> {
            List<Split> allSplits = template.query("SELECT * FROM Splits WHERE workoutId = ?", new splitMapper(), w.getWorkoutId());
            w.setSplits(allSplits);
            WorkoutTime workoutTime = template.queryForObject("SELECT * FROM WorkoutTimes Where timeOfDayId = ?", new workoutTimeMapper(), w.getWorkoutTime().getTimeOfDayId());
            w.setWorkoutTime(workoutTime);
        });

        return allWorkouts;
    }

    @Override
    public List<Workout> getAllWorkoutsByWeek(Integer userId, Integer pageNum) {
        List<Workout> allWorkouts = template.query("SELECT * FROM Workouts WHERE userId = ? "
                + "AND YEARWEEK(Workouts.date, 1) = YEARWEEK(now() - INTERVAL ? WEEK, 1) ORDER BY date ASC", new workoutMapper(), userId, pageNum);

        allWorkouts.forEach((w) -> {
            List<Split> allSplits = template.query("SELECT * FROM Splits WHERE workoutId = ?", new splitMapper(), w.getWorkoutId());
            w.setSplits(allSplits);
            WorkoutTime workoutTime = template.queryForObject("SELECT * FROM WorkoutTimes Where timeOfDayId = ?", new workoutTimeMapper(), w.getWorkoutTime().getTimeOfDayId());
            w.setWorkoutTime(workoutTime);
        });

        return allWorkouts;
    }

    @Override
    @Transactional
    public Workout addWorkout(Workout workout) {

        KeyHolder kh = new GeneratedKeyHolder();
        int rowsAffected = template.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO Workouts (userId, typeId, `date`, location, distance, duration, averagePace, comments, cadence, averageHR, timeOfDayId) "
                            + "VALUES  (?,?,?,?,?,?,?,?,?,?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setInt(1, workout.getUserId());
                    ps.setInt(2, workout.getWorkoutType().getWorkoutId());
                    ps.setDate(3, Date.valueOf(workout.getWorkoutDate()));
                    ps.setString(4, workout.getLocation());
                    ps.setDouble(5, workout.getDistance());
                    ps.setInt(6, (int) workout.getDuration().toSeconds());
                    ps.setString(7, workout.getAveragePace());
                    ps.setString(8, workout.getComments());
                    ps.setInt(9, workout.getCadence());
                    ps.setInt(10, workout.getAverageHR());
                    ps.setInt(11, workout.getWorkoutTime().getTimeOfDayId());

                    return ps;
                },
                kh);
        int generatedId = kh.getKey().intValue();
        workout.setWorkoutId(generatedId);

        for (Split split : workout.getSplits()) {
            int affected = template.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                "INSERT INTO Splits (workoutId, `time`) VALUES (?,?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setInt(1, workout.getWorkoutId());
                        ps.setString(2, split.getSplitTime());

                        return ps;
                    },
                    kh);
        }
        return workout;
    }

    @Override
    @Transactional
    public Workout editWorkout(Workout workout) {

        KeyHolder kh = new GeneratedKeyHolder();

        int rowsAffected = template.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "Update Workouts set userId = ?, typeId = ?, `date` = ?, location = ?, "
                            + "distance = ?, duration = ?, averagePace = ?, cadence =?, averageHR = ?, comments = ?, " +
                                    "coachingComments = ?, ccUserId = ?, ccCoachName = ?, ccTimestamp = ?, timeOfDayId = ? WHERE workoutId = ?",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setInt(1, workout.getUserId());
                    ps.setInt(2, workout.getWorkoutType().getWorkoutId());
                    ps.setDate(3, Date.valueOf(workout.getWorkoutDate()));
                    ps.setString(4, workout.getLocation());
                    ps.setDouble(5, workout.getDistance());
                    ps.setInt(6, (int) workout.getDuration().toSeconds());
                    ps.setString(7, workout.getAveragePace());
                    ps.setInt(8, workout.getCadence());
                    ps.setInt(9, workout.getAverageHR());
                    ps.setString(10, workout.getComments());
                    try {
                        ps.setString(11, workout.getCoachingComments());
                        ps.setInt(12, workout.getCcUserId());
                        ps.setString(13, workout.getCcCoachName());
                        ps.setTimestamp(14, workout.getCcTimestamp());
                    } catch (NullPointerException ex) {
                        ps.setString(11, null);
                        ps.setInt(12, 0);
                        ps.setString(13, null);
                        ps.setTimestamp(14, null);
                    }

                    ps.setInt(15, workout.getWorkoutTime().getTimeOfDayId());
                    ps.setInt(16, workout.getWorkoutId());
                    return ps;
                },
                kh);

        template.update("DELETE FROM Splits where workoutId = ?", workout.getWorkoutId());

        for (Split split : workout.getSplits()) {
            int affected = template.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                "INSERT INTO Splits (workoutId, `time`) VALUES (?,?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setInt(1, workout.getWorkoutId());
                        ps.setString(2, split.getSplitTime());

                        return ps;
                    },
                    kh);
        }
        return workout;
    }

    @Override
    public WorkoutType getWorkoutType(Integer typeId) {
        return template.queryForObject("SELECT * FROM WorkoutTypes Where typeId = ?",
                new WorkoutTypeMapper(), typeId);
    }

    @Override
    public Workout getWorkoutById(Integer workoutId) throws DaoException {
        try {
            Workout foundWorkout = template.queryForObject("SELECT * FROM Workouts WHERE workoutId = ?", new workoutMapper(), workoutId);
            List<Split> allSplits = template.query("SELECT * FROM Splits WHERE workoutId = ?", new splitMapper(), workoutId);
            foundWorkout.setSplits(allSplits);
            WorkoutTime workoutTime = template.queryForObject("SELECT * FROM WorkoutTimes Where timeOfDayId = ?", new workoutTimeMapper(), foundWorkout.getWorkoutTime().getTimeOfDayId());
            foundWorkout.setWorkoutTime(workoutTime);
            return foundWorkout;
        } catch (EmptyResultDataAccessException ex) {
            throw new DaoException("No workout found with that Id.");
        }
    }

    @Override
    public void deleteWorkoutById(Integer workoutId) throws DaoException {
        KeyHolder key = new GeneratedKeyHolder();
        int rows = template.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "DELETE FROM Splits Where WorkoutId = ?",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setInt(1, workoutId);
                    return ps;
                },
                key);

        int rows2 = template.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "DELETE FROM WorkoutPhotos Where WorkoutId = ?",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setInt(1, workoutId);
                    return ps;
                },
                key);

        KeyHolder kh = new GeneratedKeyHolder();
        int rowsAffected = template.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "DELETE FROM Workouts Where WorkoutId = ?",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setInt(1, workoutId);
                    return ps;
                },
                kh);

        if (rowsAffected > 1) {
            throw new DaoException("More than one workout deleted!");
        }
    }

    @Override
    public List<Workout> getAllWorkoutsByDateRange(Integer userId, LocalDate startingLd, LocalDate endingLd) {
        List<Workout> allWorkouts = template.query("SELECT * FROM Workouts Where UserId = ? AND date >= ? AND date <= ? ORDER BY date DESC",
                new workoutMapper(), userId, startingLd, endingLd);

        allWorkouts.forEach((w) -> {
            List<Split> allSplits = template.query("SELECT * FROM Splits WHERE workoutId = ?", new splitMapper(), w.getWorkoutId());
            w.setSplits(allSplits);
            WorkoutTime workoutTime = template.queryForObject("SELECT * FROM WorkoutTimes Where timeOfDayId = ?", new workoutTimeMapper(), w.getWorkoutTime().getTimeOfDayId());
            w.setWorkoutTime(workoutTime);
        });

        return allWorkouts;
    }

    @Override
    public Map<User, Double> getRankedOrderOfMileageByTeammate(List<User> teammates, Integer selectedWeek) {

        Map<User, Double> mileageMap = new HashMap<>();
        ObjectMapper obj = new ObjectMapper();
        Long start = System.nanoTime();
        Long end;
        try {
            for (int i = 0; i < teammates.size(); i++) {
                int userId = teammates.get(i).getUserId();
                double mileage = template.queryForObject("SELECT SUM(distance) FROM Workouts WHERE userId = ? AND "
                        + "typeId = 1 AND YEARWEEK(Workouts.date, 1) = YEARWEEK(now() - INTERVAL " + selectedWeek + " WEEK, 1)",
                        new mileageMapper(), userId);
                mileageMap.put(teammates.get(i), (double) mileage);
            }
            end = System.nanoTime();
            System.out.println("total time for workoutDao getRankedOrderOfMileage was " + (end-start));
        } catch (NullPointerException ex) {
            return mileageMap;
        }
        return mileageMap;
    }

    @Override
    public void addUserAsCoach(Coach coach) {

        KeyHolder kh = new GeneratedKeyHolder();
        int rowsAffected = template.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO Coaches (userId, coachName, biography) "
                                    + "VALUES  (?,?,?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setInt(1, coach.getUserId());
                    ps.setString(2, coach.getCoachName());
                    ps.setString(3, coach.getBiography());
                    return ps;
                },
                kh);
        int generatedId = kh.getKey().intValue();
    }

    private static class mileageMapper implements RowMapper<Double> {

        @Override
        public Double mapRow(ResultSet rs, int arg1) throws SQLException {
            return rs.getDouble("SUM(DISTANCE)");
        }
    }

    private static class workoutTimeMapper implements RowMapper<WorkoutTime> {

        @Override
        public WorkoutTime mapRow(ResultSet rs, int arg1) throws SQLException {
            WorkoutTime wt = new WorkoutTime();
            wt.setTimeOfDayId(rs.getInt("timeOfDayId"));
            wt.setName(rs.getString("Name"));
            return wt;
        }
    }

    private class workoutMapper implements RowMapper<Workout> {

        @Override
        public Workout mapRow(ResultSet rs, int i) throws SQLException {

            Workout w = new Workout();

            w.setWorkoutId(rs.getInt("workoutId"));
            w.setUserId(rs.getInt("userId"));

            LocalDate date = rs.getDate("date").toLocalDate();
            w.setWorkoutDate(date);

            WorkoutTime wt = new WorkoutTime();
            wt.setTimeOfDayId(rs.getInt("timeOfDayId"));
            w.setWorkoutTime(wt);

            w.setLocation(rs.getString("location"));
            w.setDistance(rs.getDouble("distance"));

            Integer totalSeconds = rs.getInt("duration");
            w.setDuration(convertToDuration(totalSeconds));

            w.setAveragePace(rs.getString("averagePace"));

            w.setComments(rs.getString("comments"));
            w.setCoachingComments(rs.getString("coachingComments"));

            Integer typeId = rs.getInt("typeId");

            WorkoutType workoutType = getWorkoutType(typeId);
            w.setWorkoutType(workoutType);

            w.setAverageHR(rs.getInt("averageHR"));
            w.setCadence(rs.getInt("cadence"));

            w.setCcUserId(rs.getInt("ccUserId"));
            w.setCcCoachName(rs.getString("ccCoachName"));
            w.setCcTimestamp((rs.getTimestamp("ccTimestamp")));

            return w;
        }
    }

    private static class splitMapper implements RowMapper<Split> {

        @Override
        public Split mapRow(ResultSet rs, int i) throws SQLException {

            Split split = new Split();

            split.setSplitId(rs.getInt("splitId"));
            split.setWorkoutId(rs.getInt("workoutId"));
            split.setSplitTime(rs.getString("time"));

            return split;
        }
    }

    private static class WorkoutTypeMapper implements RowMapper<WorkoutType> {

        @Override
        public WorkoutType mapRow(ResultSet rs, int i) throws SQLException {
            WorkoutType wt = new WorkoutType();

            wt.setWorkoutId(rs.getInt("typeId"));
            wt.setWorkoutName(rs.getString("name"));

            return wt;
        }
    }

    // this has to be done in JavaScript, not here, b/c it is all about how it is displayed
    private Duration convertToDuration(Integer totalSeconds) {
        Integer hours = totalSeconds / 3600;
        Integer minutes = ((totalSeconds - (hours * 3600)) / 60);
        Integer seconds = totalSeconds - (hours * 3600) - (minutes * 60);

        String h = hours.toString();
        String m = minutes.toString();
        String s = seconds.toString();

        if (hours < 10) {
            h = "0" + hours;
        }
        if (minutes < 10) {
            m = "0" + minutes;
        }
        if (seconds < 10) {
            s = "0" + seconds;
        }
        //"PT0H0M0S"
        String durationString = "PT" + h + "H" + m + "M" + s + "S";

        try {
            return Duration.parse(durationString);
        } catch (DateTimeParseException ex) {
            return Duration.parse("PT0H0M0S");
        }
    }
}

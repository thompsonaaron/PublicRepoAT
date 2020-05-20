/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.fartlek.dao;

import com.aaronthompson.fartlek.entities.Split;
import com.aaronthompson.fartlek.entities.Workout;
import com.aaronthompson.fartlek.entities.WorkoutType;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

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
        for (Workout w : allWorkouts) {
            List<Split> allSplits = template.query("SELECT * FROM splits WHERE workoutId = ?", new splitMapper(), w.getWorkoutId());
            w.setSplits(allSplits);
        }

        return allWorkouts;
    }

    @Override
    public List<Workout> getAllWorkoutsByUserId(Integer userId) {
        List<Workout> allWorkouts = template.query("SELECT * FROM Workouts WHERE userId = ? ORDER BY dateTime DESC", new workoutMapper(), userId);

        for (Workout w : allWorkouts) {
            List<Split> allSplits = template.query("SELECT * FROM splits WHERE workoutId = ?", new splitMapper(), w.getWorkoutId());
            w.setSplits(allSplits);
        }

        return allWorkouts;

    }

    @Override
    public Workout addWorkout(Workout workout) {

        KeyHolder kh = new GeneratedKeyHolder();
        int rowsAffected = template.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO Workouts (userId, typeId, `datetime`, location, distance, duration, averagePace, comments) "
                                    + "VALUES  (?,?,?,?,?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setInt(1, workout.getUserId());
                    ps.setInt(2, workout.getWorkoutType().getWorkoutId());
                    ps.setTimestamp(3, Timestamp.valueOf(workout.getWorkoutDateTime()));
                    ps.setString(4, workout.getLocation());
                    ps.setDouble(5, workout.getDistance());

                    Duration d = Duration.parse(workout.getDuration());
                    ps.setInt(6, (int) d.toSeconds());
                    ps.setString(7, workout.getAveragePace());
                    ps.setString(8, workout.getComments());

                    return ps;
                },
                kh);
        int generatedId = kh.getKey().intValue();
        workout.setWorkoutId(generatedId);

        for (Split split : workout.getSplits()) {
            int affected = template.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                "INSERT INTO splits (workoutId, `time`) VALUES (?,?)",
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
    public void deleteWorkoutByUserID(Integer userId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public WorkoutType getWorkoutType(Integer typeId) {
        return template.queryForObject("SELECT * FROM WorkoutTypes Where typeId = ?",
                new WorkoutTypeMapper(), typeId);
    }

    private class workoutMapper implements RowMapper<Workout> {

        @Override
        public Workout mapRow(ResultSet rs, int i) throws SQLException {

            Workout w = new Workout();

            w.setWorkoutId(rs.getInt("workoutId"));
            w.setUserId(rs.getInt("userId"));

            LocalDateTime dateTime = rs.getTimestamp("dateTime").toLocalDateTime();
            w.setWorkoutDateTime(dateTime);

            w.setLocation(rs.getString("location"));
            w.setDistance(rs.getDouble("distance"));

            Integer totalSeconds = rs.getInt("duration");
            String timeString = convertToTimeString(totalSeconds);
            w.setDuration(timeString);

            w.setAveragePace(rs.getTime("averagePace").toString());
            w.setComments(rs.getString("comments"));
            w.setCoachingComments(rs.getString("coachingComments"));

            Integer typeId = rs.getInt("typeId");

            WorkoutType workoutType = getWorkoutType(typeId);
            w.setWorkoutType(workoutType);

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
    private String convertToTimeString(Integer totalSeconds) {
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
        return h + ':' + m + ':' + s;
    }
}

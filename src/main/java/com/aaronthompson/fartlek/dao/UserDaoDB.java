/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.fartlek.dao;

import com.aaronthompson.fartlek.entities.Role;
import com.aaronthompson.fartlek.entities.Team;
import com.aaronthompson.fartlek.entities.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.aaronthompson.fartlek.entities.Privacy;

/**
 *
 * @author thomp
 */
@Repository
public class UserDaoDB implements UserDao {

    @Autowired
    JdbcTemplate template;

    @Override
    public User createAccount(User toAdd) throws DaoException {

        KeyHolder kh = new GeneratedKeyHolder();

        try {
            int rowsAffected = template.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                "INSERT INTO USERS (privacyId, username, `password`, firstName, lastName, email, phoneNumber, city, state, country) VALUES(?,?,?,?,?,?,?,?,?,?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setInt(1, toAdd.getPrivacyId());
                        ps.setString(2, toAdd.getUsername());
                        ps.setString(3, toAdd.getPassword());
                        ps.setString(4, toAdd.getFirstName());
                        ps.setString(5, toAdd.getLastName());
                        ps.setString(6, toAdd.getEmail());
                        ps.setString(7, toAdd.getPhoneNumber());
                        ps.setString(8, toAdd.getCity());
                        ps.setString(9, toAdd.getState());
                        ps.setString(10, toAdd.getCountry());

                        return ps;
                    },
                    kh);
        } catch (DataIntegrityViolationException ex) {
            throw new DaoException("Cannot add an incomplete user. All requires fields must have a non-null value.");
        }

        int generatedId = kh.getKey().intValue();

        toAdd.setUserId(generatedId);

        // insert into bridge table
        KeyHolder key = new GeneratedKeyHolder();
        try {
            int rowsAffected = template.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                "INSERT INTO UserRoles (UserId, RoleId) VALUES (?,?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setInt(1, generatedId);
                        ps.setInt(2, 1);

                        return ps;
                    },
                    key);
        } catch (DataIntegrityViolationException ex) {
            throw new DaoException("Cannot add user role.");
        }

        return toAdd;
    }

    @Override
    public User getUserById(Integer userId) {

        User foundUser = template.queryForObject("SELECT * FROM users WHERE userId = ?", new userMapper(), userId);

        List<Role> allRoles = template.query("SELECT * FROM userRoles ur JOIN roles r on r.roleId = ur.roleId WHERE userId = ?", new roleMapper(), userId);
        foundUser.setUserRoles(allRoles);

        List<Team> userTeams = template.query("SELECT t.* FROM teams t JOIN userTeams ut "
                + "on ut.teamId = t.teamId WHERE ut.userId = ?", new TeamDaoDB.TeamMapper(), userId);
        foundUser.setUserTeams(userTeams);

        return foundUser;
    }

    @Override
    public User getUserByUsername(String username) {
        try {
            User foundUser = template.queryForObject("SELECT * FROM users WHERE username = ?", new userMapper(), username);
            List<Role> allRoles = template.query("SELECT * FROM userRoles ur JOIN roles r on r.roleId = ur.roleId WHERE userId = ?", new roleMapper(), foundUser.getUserId());
            foundUser.setUserRoles(allRoles);

            List<Team> userTeams = template.query("SELECT t.* FROM teams t JOIN userTeams ut "
                    + "on ut.teamId = t.teamId WHERE ut.userId = ?", new TeamDaoDB.TeamMapper(), foundUser.getUserId());
            foundUser.setUserTeams(userTeams);

            return foundUser;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<User> getUsersByTeamId(Integer orgId) {
        List<User> allUsers = template.query("SELECT * FROM USERS u JOIN userTeams uo on uo.userId = u.userID "
                + "WHERE uo.teamId = ?", new userMapper(), orgId);

        // assign roles
        for (User u : allUsers) {
            List<Role> allRoles = template.query("SELECT * FROM userRoles ur "
                    + "JOIN roles r on r.roleId = ur.roleId WHERE userId = ?",
                    new roleMapper(), u.getUserId());
            u.setUserRoles(allRoles);
        }

        // assign teams HERE
        return allUsers;
    }

    @Override
    public void addUserToTeam(Integer teamId, Integer userId) throws DaoException {

        KeyHolder kh = new GeneratedKeyHolder();

        try {
            int rowsAffected = template.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                "INSERT INTO UserTeams (teamId, userId) VALUES (?,?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setInt(1, teamId);
                        ps.setInt(2, userId);

                        return ps;
                    },
                    kh);
        } catch (DataIntegrityViolationException ex) {
            throw new DaoException("Cannot add an incomplete user. All requires fields must have a non-null value.");
        }
    }

    @Override
    public List<User> getAllUsers() {

        List<User> allUsers = template.query("SELECT * FROM users", new userMapper());

        for (User u : allUsers) {
            List<Role> allRoles = template.query("SELECT * FROM userRoles ur JOIN roles r on r.roleId = ur.roleId WHERE userId = ?", new roleMapper(), u.getUserId());
            u.setUserRoles(allRoles);

            List<Team> userTeams = template.query("SELECT t.* FROM teams t JOIN userTeams ut "
                    + "on ut.teamId = t.teamId WHERE ut.userId = ?", new TeamDaoDB.TeamMapper(), u.getUserId());
            u.setUserTeams(userTeams);
        }

        return allUsers;
    }

    public static class userMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int i) throws SQLException {

            User u = new User();

            u.setUserId(rs.getInt("userId"));
            u.setUsername(rs.getString("username"));
            u.setPrivacyId(rs.getInt("privacyId"));
            u.setPassword(rs.getString("password"));
            u.setFirstName(rs.getString("firstName"));
            u.setLastName(rs.getString("lastName"));
            u.setEmail(rs.getString("email"));
            u.setPhoneNumber(rs.getString("phoneNumber"));
            u.setCity(rs.getString("city"));
            u.setState(rs.getString("state"));
            u.setCountry(rs.getString("country"));

            return u;
        }
    }

    public static class roleMapper implements RowMapper<Role> {

        @Override
        public Role mapRow(ResultSet rs, int arg1) throws SQLException {

            Role r = new Role();

            r.setRoleId(rs.getInt("roleId"));
            r.setRole(rs.getString("name"));

            return r;
        }
    }

    // only used in test at the moment 4/27/2020
    public static class PrivacyMapper implements RowMapper<Privacy> {

        @Override
        public Privacy mapRow(ResultSet rs, int arg1) throws SQLException {

            Privacy pr = new Privacy();

            pr.setPrivacyId(rs.getInt("privacyId"));
            pr.setPrivacyName(rs.getString("name"));

            return pr;
        }
    }
}

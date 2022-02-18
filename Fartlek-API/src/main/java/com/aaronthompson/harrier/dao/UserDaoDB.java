/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.dao;

import com.aaronthompson.harrier.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author thomp
 */
@Repository
public class UserDaoDB implements UserDao {

    @Autowired
    JdbcTemplate template;

    @Autowired
    TeamDaoDB teamDao;

    @Override
    public User getUserById(Integer userId) {

        User foundUser = template.queryForObject("SELECT * FROM Users WHERE userId = ?", new userMapper(), userId);

        List<Role> allRoles = template.query("SELECT * FROM UserRoles ur JOIN Roles r on r.roleId = ur.roleId WHERE userId = ?", new roleMapper(), userId);
        foundUser.setUserRoles(allRoles);

        List<Integer> userTeamIds = template.query("SELECT teamId FROM UserTeams Where userId = ?", new IntMapper(), foundUser.getUserId());
        List<Team> userTeams = new ArrayList<>();
        for (Integer teamId : userTeamIds) {
            userTeams.add(teamDao.getTeamById(teamId));
        }
        foundUser.setUserTeams(userTeams);
        return foundUser;
    }

    @Override
    public User getUserByUsername(String username) {
        try {
            User foundUser = template.queryForObject("SELECT * FROM Users WHERE username = ?", new userMapper(), username);
            List<Role> allRoles = template.query("SELECT * FROM UserRoles ur JOIN Roles r on r.roleId = ur.roleId WHERE userId = ?", new roleMapper(), foundUser.getUserId());
            foundUser.setUserRoles(allRoles);

            List<Integer> userTeamIds = template.query("SELECT teamId FROM UserTeams Where userId = ?", new IntMapper(), foundUser.getUserId());

            List<Team> userTeams = new ArrayList<>();
            for (Integer teamId : userTeamIds) {
                userTeams.add(teamDao.getTeamById(teamId));
            }
            foundUser.setUserTeams(userTeams);
            return foundUser;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (IncorrectResultSizeDataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<User> getUsersByTeamId(Integer orgId) {
        List<String> usernames = template.query("SELECT username FROM Users u JOIN UserTeams uo on uo.userId = u.userID "
                + "WHERE uo.teamId = ?", new StringMapper(), orgId);

        List<User> teamUsers = new ArrayList<>();
        for (int i = 0; i < usernames.size(); i++) {
            // for each user, find userTeams
            teamUsers.add(getUserByUsername(usernames.get(i)));
        }
        try {
            Collections.sort(teamUsers);
        } catch (NullPointerException ex) { // if a first name is null in the list of users, create a new list that does not include null users and sort this


            for (Iterator<User> iter = teamUsers.iterator(); iter.hasNext(); ) {
                try {
                    if (iter.next().getFirstName().equals(null)) {
                        // throws exception if null
                    }
                } catch (NullPointerException e) {
                    iter.remove();
                }
            }
        }
        Collections.sort(teamUsers);
        return teamUsers;
    }
//        return teamUsers;
//}

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
            throw new DaoException("Cannot add an incomplete user; one that is already on this team or for a team that does not exist.");
        }
    }

    @Override
    public List<User> getAllUsers() {

        List<User> allUsers = template.query("SELECT * FROM Users", new userMapper());

        for (User u : allUsers) {
            List<Role> allRoles = template.query("SELECT * FROM UserRoles ur JOIN Roles r on r.roleId = ur.roleId WHERE userId = ?", new roleMapper(), u.getUserId());
            u.setUserRoles(allRoles);

            List<Team> userTeams = template.query("SELECT t.* FROM Teams t JOIN UserTeams ut "
                    + "on ut.teamId = t.teamId WHERE ut.userId = ?", new TeamDaoDB.TeamMapper(), u.getUserId());
            u.setUserTeams(userTeams);
        }

        return allUsers;
    }

    @Override
    @Transactional
    public void editAccount(User editedUser) throws DaoException {

        KeyHolder kh = new GeneratedKeyHolder();

        try {
            int rowsAffected = template.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                "UPDATE Users set privacyId = ?, username = ?, firstName = ?, "
                                        + "lastName = ?, email = ?, phoneNumber = ?, "
                                        + "city = ?, state = ?, country = ? WHERE userId = ?",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setInt(1, editedUser.getPrivacyId());
                        ps.setString(2, editedUser.getUsername());
                        ps.setString(3, editedUser.getFirstName());
                        ps.setString(4, editedUser.getLastName());
                        ps.setString(5, editedUser.getEmail());
                        ps.setString(6, editedUser.getPhoneNumber());
                        ps.setString(7, editedUser.getCity());
                        ps.setString(8, editedUser.getState());
                        ps.setString(9, editedUser.getCountry());
                        ps.setInt(10, editedUser.getUserId());

                        return ps;
                    },
                    kh);
        } catch (DataIntegrityViolationException ex) {
            throw new DaoException("Unable to edit user.");
        }

        int rows = template.update("DELETE FROM UserRoles WHERE UserId = ?", editedUser.getUserId());

        // insert into bridge table
        for (Role ro : editedUser.getUserRoles()) {
            KeyHolder key = new GeneratedKeyHolder();
            try {
                int rowsAffected = template.update(
                        connection -> {
                            PreparedStatement ps = connection.prepareStatement(
                                    "INSERT INTO UserRoles (UserId, RoleId) VALUES (?,?)",
                                    Statement.RETURN_GENERATED_KEYS
                            );
                            ps.setInt(1, editedUser.getUserId());
                            ps.setInt(2, ro.getRoleId());

                            return ps;
                        },
                        key);
            } catch (DataIntegrityViolationException ex) {
                throw new DaoException("Cannot add user role.");
            }
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            User foundUser = template.queryForObject("SELECT * FROM Users WHERE email = ?", new userMapper(), email);
            List<Role> allRoles = template.query("SELECT * FROM UserRoles ur JOIN Roles r on r.roleId = ur.roleId WHERE userId = ?", new roleMapper(), foundUser.getUserId());
            foundUser.setUserRoles(allRoles);

            //can I reuse method from teamDao here?
            List<Team> userTeams = template.query("SELECT t.* FROM Teams t JOIN UserTeams ut "
                    + "on ut.teamId = t.teamId WHERE ut.userId = ?", new TeamDaoDB.TeamMapper(), foundUser.getUserId());
            foundUser.setUserTeams(userTeams);

            return foundUser;
        } catch (EmptyResultDataAccessException ex) {
            // this means the email does not exist... 
            return null;
        }
    }

    @Override
    public Coach getCoachByUserId(Integer userId) {
        String query = "SELECT * FROM Users u INNER JOIN Coaches c on c.userId = u.userId Where u.userId = ?";
        return template.queryForObject(query, new coachMapper(), userId);
    }

    @Override
    public void addAssistantCoachToTeam(Integer teamId, Coach coach) {
        //insert into teamCoaches table with teamId, userId, coachName
        KeyHolder kh = new GeneratedKeyHolder();
        int rowsAffected = template.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO TeamCoaches (teamId, userId, coachName, coachRoleId) VALUES (?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setInt(1, teamId);
                    ps.setInt(2, coach.getUserId());
                    ps.setString(3, coach.getCoachName());
                    ps.setInt(4, 2);
                    return ps;
                },
                kh);
    }

    public static class userMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int i) throws SQLException {

            User u = new User();

            u.setUserId(rs.getInt("userId"));
            u.setUsername(rs.getString("username"));
            u.setPrivacyId(rs.getInt("privacyId"));
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

    private static class IntMapper implements RowMapper<Integer> {

        @Override
        public Integer mapRow(ResultSet rs, int arg1) throws SQLException {
            return rs.getInt("teamId");
        }
    }

    private static class StringMapper implements RowMapper<String> {

        @Override
        public String mapRow(ResultSet rs, int arg1) throws SQLException {
            return rs.getString("username");
        }
    }

    public static class coachMapper implements RowMapper<Coach> {

        @Override
        public Coach mapRow(ResultSet rs, int arg1) throws SQLException {
            Coach co = new Coach();

            co.setUserId(rs.getInt("userId"));
            co.setUsername(rs.getString("username"));
            co.setPrivacyId(rs.getInt("privacyId"));
            co.setFirstName(rs.getString("firstName"));
            co.setLastName(rs.getString("lastName"));
            co.setEmail(rs.getString("email"));
            co.setPhoneNumber(rs.getString("phoneNumber"));
            co.setCity(rs.getString("city"));
            co.setState(rs.getString("state"));
            co.setCountry(rs.getString("country"));
            co.setCoachName(rs.getString("coachName"));
            co.setBiography(rs.getString("biography"));
            return co;
        }
    }
}

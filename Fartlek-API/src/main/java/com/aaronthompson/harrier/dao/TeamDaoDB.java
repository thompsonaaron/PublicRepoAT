/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.dao;

import com.aaronthompson.harrier.controller.HarrierController;
import com.aaronthompson.harrier.entities.Coach;
import com.aaronthompson.harrier.entities.Resource;
import com.aaronthompson.harrier.entities.Team;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author thomp
 */
@Repository
public class TeamDaoDB implements TeamDao {

    @Autowired
    JdbcTemplate template;

    @Autowired
    ListingDaoDB listingDao;

    private final static Logger logger = LogManager.getLogger(HarrierController.class);

    @Override
    public Team getTeamById(Integer orgId) {

        try {
            Team foundTeam = template.queryForObject("SELECT * FROM Teams WHERE teamId = ?", new TeamMapper(), orgId);
            Integer teamId = foundTeam.getTeamId();

            String coachesQuery = "SELECT * FROM TeamCoaches tc INNER JOIN Coaches c on c.userId = tc.userId inner join Users u on u.userId = c.userId WHERE teamId = ? AND coachRoleId = 2";
            List<Coach> teamCoaches = template.query(coachesQuery, new UserDaoDB.coachMapper(), teamId);
            foundTeam.setAssistantCoaches(teamCoaches);

            String headCoachQuery = "SELECT * FROM TeamCoaches tc INNER JOIN Coaches c on c.userId = tc.userId inner join Users u on u.userId = c.userId WHERE teamId = ? AND coachRoleId = 1";
            Coach headCoach = template.queryForObject(headCoachQuery, new UserDaoDB.coachMapper(), teamId);
            foundTeam.setHeadCoach(headCoach);

            List<Resource> teamResources = template.query("SELECT * FROM TeamResources WHERE teamId = ?", new resourceMapper(), teamId);
            foundTeam.setTeamResources(teamResources);

            return foundTeam;
        } catch (NullPointerException ex) {
            logger.warn("Null pointer encountered in getTeamById in TeamDaoDB. " + ex.getMessage());
            return null;
        }
    }

    @Override
    public void removeUserFromTeam(Integer userId, Integer teamId) {
        template.update("DELETE FROM UserTeams WHERE userId = ? AND teamID = ?;", userId, teamId);
    }

    @Override
    @Transactional
    public void updateTeamResources(Team currentTeam) {
        // delete from TeamResources table where teamId = currentTeam.getTeamId();
        // then insert into TeamResources table
        Integer teamId = currentTeam.getTeamId();
        KeyHolder key = new GeneratedKeyHolder();
        template.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "DELETE FROM TeamResources Where teamId = ?",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setInt(1, teamId);

                    return ps;
                },
                key);

        for (Resource res : currentTeam.getTeamResources()) {
            KeyHolder kh = new GeneratedKeyHolder();
            template.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                "INSERT INTO TeamResources (url, title, teamId) VALUES (?,?,?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setString(1, res.getUrl());
                        ps.setString(2, res.getTitle());
                        ps.setInt(3, teamId);

                        return ps;
                    },
                    kh);
        }
    }

    @Override
    public Resource getResourceById(Integer resourceId) {
        return template.queryForObject("SELECT * FROM TeamResources Where resourceId = ?", new resourceMapper(), resourceId);
    }

    @Override
    public void deleteResourceById(Integer resourceId) {
        KeyHolder kh = new GeneratedKeyHolder();
        template.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "DELETE FROM TeamResources Where resourceId = ?",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setInt(1, resourceId);

                    return ps;
                },
                kh);
    }

    // NOT ACCURATE
    @Override
    public List<Team> getAllTeams() {
        return template.query("SELECT * FROM Teams Where teamPrivacyId = 1 ORDER BY name", new TeamMapper());
    }

    @Override
    @Transactional
    public Team addTeam(Team team) throws DaoException {
        KeyHolder kh = new GeneratedKeyHolder();
        try {
            int rowsAffected = template.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                "INSERT INTO Teams (`name`, address, city, state, zipcode, teamPrivacyId) VALUES (?,?,?,?,?,?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setString(1, team.getName());
                        ps.setString(2, team.getAddress());
                        ps.setString(3, team.getCity());
                        ps.setString(4, team.getState());
                        ps.setString(5, team.getZipcode());
                        ps.setInt(6, team.getTeamPrivacyId());
                        return ps;
                    },
                    kh);
            int generatedId = kh.getKey().intValue();
            team.setTeamId(generatedId);
        } catch (DuplicateKeyException e) {
            throw new DaoException("Team name must be unique. Please rename your team and try again.");
        }

        try {
            Coach headCoach = team.getHeadCoach();
            KeyHolder key = new GeneratedKeyHolder();
            int rows = template.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                "INSERT INTO TeamCoaches (teamId, userId, coachRoleId) "
                                        + "VALUES (?,?,?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setInt(1, team.getTeamId());
                        ps.setInt(2, headCoach.getUserId());
                        ps.setInt(3, 1);
                        return ps;
                    },
                    key);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException(e.getMessage());
        }
        return team;
    }

    @Override
    public void removeCoachFromTeam(Integer teamId, Integer userId) {
        KeyHolder key = new GeneratedKeyHolder();
        int rows = template.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "DELETE FROM TeamCoaches Where teamId = ? AND userId = ?",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setInt(1, teamId);
                    ps.setInt(2, userId);
                    return ps;
                },
                key);
    }

    public void editTeamInfo(Team team) {
        KeyHolder key = new GeneratedKeyHolder();
        template.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "UPDATE Teams SET teamPrivacyId = ?, name = ?, address = ?, city = ?, state = ?, zipcode = ? Where teamId = ?", Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, team.getTeamPrivacyId());
                    ps.setString(2, team.getName());
                    ps.setString(3, team.getAddress());
                    ps.setString(4, team.getCity());
                    ps.setString(5, team.getState());
                    ps.setString(6, team.getZipcode());
                    ps.setInt(7, team.getTeamId());
                    return ps;
                },
                key);
    }

    public static class TeamMapper implements RowMapper<Team> {

        @Override
        public Team mapRow(ResultSet rs, int i) throws SQLException {
            Team org = new Team();

            org.setTeamId(rs.getInt("teamId"));
            org.setName(rs.getString("name"));
            org.setAddress(rs.getString("address"));
            org.setCity(rs.getString("city"));
            org.setState(rs.getString("state"));
            org.setZipcode(rs.getString("zipcode"));
            org.setTeamPrivacyId(rs.getInt("teamPrivacyId"));

            return org;
        }
    }

    private static class resourceMapper implements RowMapper<Resource> {

        @Override
        public Resource mapRow(ResultSet rs, int arg1) throws SQLException {
            Resource res = new Resource();

            res.setResourceId(rs.getInt("resourceId"));
            res.setUrl(rs.getString("url"));
            res.setTitle(rs.getString("title"));
            res.setTeamId(rs.getInt("teamId"));

            return res;
        }
    }
}

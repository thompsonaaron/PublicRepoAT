/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.fartlek.dao;

import com.aaronthompson.fartlek.dao.UserDaoDB.roleMapper;
import com.aaronthompson.fartlek.dao.UserDaoDB.userMapper;
import com.aaronthompson.fartlek.entities.Coach;
import com.aaronthompson.fartlek.entities.Role;
import com.aaronthompson.fartlek.entities.Team;
import com.aaronthompson.fartlek.entities.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thomp
 */
@Repository
public class TeamDaoDB implements TeamDao {

    @Autowired
    JdbcTemplate template;

    @Override
    public Team getTeamById(Integer orgId) {

        try {
            Team foundTeam = template.queryForObject("SELECT * FROM teams WHERE teamId = ?", new TeamMapper(), orgId);
            List<Coach> teamCoaches = template.query("SELECT * FROM teamCoaches WHERE teamId = ?", new CoachMapper(), foundTeam.getTeamId());
            foundTeam.setTeamCoaches(teamCoaches);
            List<User> teamUsers = template.query("SELECT * FROM UserTeams ut JOIN Users u on u.userId = ut.userId WHERE teamId = ?", new userMapper(), foundTeam.getTeamId());
            for (User u : teamUsers) {
                List<Role> allRoles = template.query("SELECT * FROM userRoles ur JOIN roles r on r.roleId = ur.roleId WHERE userId = ?", new roleMapper(), u.getUserId());
                u.setUserRoles(allRoles);
                
                u.setUserTeams(getAllTeamsByUserId(u.getUserId()));
            }
            foundTeam.setTeamUsers(teamUsers);
            return foundTeam;
        } catch (NullPointerException ex) {
            return null;
        }
    }

    @Override
    public List<Team> getAllTeamsByUserId(Integer userId) {

        List<Team> allTeams = template.query("SELECT t.* FROM teams t JOIN userTeams ut "
                + "on ut.teamId = t.teamId WHERE ut.userId = ?", new TeamMapper(), userId);
        return allTeams;
    }

    @Override
    public void removeUserFromTeam(Integer userId, Integer teamId) {

        template.update("DELETE FROM UserTeams WHERE userId = ? AND teamID = ?;", userId, teamId);

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

            return org;
        }
    }

    private static class CoachMapper implements RowMapper<Coach> {

        @Override
        public Coach mapRow(ResultSet rs, int arg1) throws SQLException {
            Coach co = new Coach();

            co.setUserId(rs.getInt("userId"));
            co.setTeamId(rs.getInt("teamId"));
            co.setCoachName(rs.getString("coachName"));

            return co;
        }
    }
}

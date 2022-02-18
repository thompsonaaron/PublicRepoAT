/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.dao;

import com.aaronthompson.harrier.entities.Team;
import java.util.List;
import com.aaronthompson.harrier.entities.Resource;


/**
 *
 * @author thomp
 */
public interface TeamDao {

    public Team getTeamById(Integer orgId);

//    public List<Team> getAllTeamsByUserId(Integer userId);

    public void removeUserFromTeam(Integer userId, Integer teamId);

    public void updateTeamResources(Team currentTeam);

    public Resource getResourceById(Integer resourceId);

    public void deleteResourceById(Integer resourceId);

    public List<Team> getAllTeams();

    public Team addTeam(Team team) throws DaoException;

    public void removeCoachFromTeam(Integer teamId, Integer userId);

    void editTeamInfo(Team team);
}

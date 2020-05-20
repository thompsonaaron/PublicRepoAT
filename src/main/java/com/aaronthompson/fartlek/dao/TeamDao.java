/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.fartlek.dao;

import com.aaronthompson.fartlek.entities.Team;
import java.util.List;

/**
 *
 * @author thomp
 */
public interface TeamDao {

    public Team getTeamById(Integer orgId);

    public List<Team> getAllTeamsByUserId(Integer userId);

    public void removeUserFromTeam(Integer userId, Integer teamId);
    
}

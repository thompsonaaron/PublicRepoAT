/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.dao;

import com.aaronthompson.harrier.entities.Coach;
import com.aaronthompson.harrier.entities.User;
import java.util.List;

/**
 *
 * @author thomp
 */
public interface UserDao {

//    public User createAccount(User user) throws DaoException;

    public User getUserById(Integer userId);

    public User getUserByUsername(String username);

    public List<User> getUsersByTeamId(Integer teamId);

    public void addUserToTeam(Integer teamId, Integer userId) throws DaoException;

    public List<User> getAllUsers();

    public void editAccount(User editedUser) throws DaoException;

    public User getUserByEmail(String responseEmail);

    public Coach getCoachByUserId(Integer userId);

    public void addAssistantCoachToTeam(Integer teamId, Coach coach);

    }

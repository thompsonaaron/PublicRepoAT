/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.fartlek.service;

import com.aaronthompson.fartlek.dao.DaoException;
import com.aaronthompson.fartlek.dao.ListingDao;
import com.aaronthompson.fartlek.dao.PhotoDao;
import com.aaronthompson.fartlek.dao.RoleDao;
import com.aaronthompson.fartlek.dao.SplitDao;
import com.aaronthompson.fartlek.dao.UserDao;
import com.aaronthompson.fartlek.dao.WorkoutDao;
import com.aaronthompson.fartlek.entities.Team;
import com.aaronthompson.fartlek.entities.Role;
import com.aaronthompson.fartlek.entities.User;
import com.aaronthompson.fartlek.entities.Workout;
import com.aaronthompson.fartlek.entities.WorkoutType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.aaronthompson.fartlek.dao.TeamDao;
import com.aaronthompson.fartlek.entities.Listing;

/**
 *
 * @author thomp
 */
@Service
public class ServiceImpl implements UserDetailsService {

    TeamDao teamDao;
    PhotoDao photo;
    RoleDao role;
    SplitDao split;
    UserDao userDao;
    WorkoutDao workout;
    ListingDao listingDao;

    public ServiceImpl(TeamDao teamDao, PhotoDao photo, RoleDao role, SplitDao split, UserDao userDao, WorkoutDao workout, ListingDao listingDao) {
        this.teamDao = teamDao;
        this.photo = photo;
        this.role = role;
        this.split = split;
        this.userDao = userDao;
        this.workout = workout;
        this.listingDao = listingDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User loaded = userDao.getUserByUsername(username);

        if (loaded == null) {
            throw new UsernameNotFoundException("could not find username: " + username);
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        for (Role r : loaded.getUserRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(r.getRole()));
        }
        org.springframework.security.core.userdetails.User toReturn
                = new org.springframework.security.core.userdetails.User(
                        loaded.getUsername(),
                        loaded.getPassword(),
                        grantedAuthorities);

        return toReturn;
    }

    public List<Workout> getAllWorkouts() {
        return workout.getAllWorkouts();
    }

    public User createAccount(User user) throws DaoException {
        return userDao.createAccount(user);
    }

    public User getUserById(Integer userId) {
        return userDao.getUserById(userId);
    }

    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    public List<Workout> getAllWorkoutsByUserId(Integer userId) {
        return workout.getAllWorkoutsByUserId(userId);
    }

    public WorkoutType findWorkoutTypeById(Integer workoutTypeId) {
        return workout.getWorkoutType(workoutTypeId);
    }

    public Workout addWorkout(Workout w) {
        return workout.addWorkout(w);
    }

    public Team getTeamById(Integer teamId) {
        return teamDao.getTeamById(teamId);
    }

    public List<User> getUsersByTeamId(Integer teamId) {
        return userDao.getUsersByTeamId(teamId);
    }

    public List<Listing> getAllListingsByTeamId(Integer teamId) {
        return listingDao.getAllListingsByTeamId(teamId);
    }

    public Listing addListing(Listing li) throws DaoException {
        return listingDao.addListing(li);
    }

    public List<Team> getAllTeamsByUserId(Integer userId) {
        return teamDao.getAllTeamsByUserId(userId);
    }

    public void removeUserFromTeam(Integer userId, Integer teamId) {
        teamDao.removeUserFromTeam(userId, teamId);
    }

    public User addUserToTeam(Integer teamId, Integer userId) throws DaoException {
        userDao.addUserToTeam(teamId, userId);
        return userDao.getUserById(userId);
    }

    public Listing getListingById(Integer listingId) {
        return listingDao.getListingByID(listingId);
    }

    public void editListing(Listing listing) throws DaoException {
        listingDao.editListing(listing);        
    }
    
    public List<User> getAllUsers(){
        return userDao.getAllUsers();
    }

}

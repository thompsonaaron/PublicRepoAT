/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.service;

import com.aaronthompson.harrier.dao.DaoException;
import com.aaronthompson.harrier.dao.ListingDao;
import com.aaronthompson.harrier.dao.RoleDao;
import com.aaronthompson.harrier.dao.UserDao;
import com.aaronthompson.harrier.dao.WorkoutDao;

import java.util.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.aaronthompson.harrier.dao.TeamDao;
import com.aaronthompson.harrier.entities.Listing;
import com.aaronthompson.harrier.entities.ListingFile;
import com.aaronthompson.harrier.entities.Resource;
import com.aaronthompson.harrier.entities.Role;
import com.aaronthompson.harrier.entities.Coach;
import com.aaronthompson.harrier.entities.Team;
import com.aaronthompson.harrier.entities.User;
import com.aaronthompson.harrier.entities.Workout;
import com.aaronthompson.harrier.entities.WorkoutType;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author thomp
 */
@Service
public class ServiceImpl {

    TeamDao teamDao;
    RoleDao role;
    UserDao userDao;
    WorkoutDao workout;
    ListingDao listingDao;

    Logger logger = LoggerFactory.getLogger("ServiceImpl");

    public ServiceImpl(TeamDao teamDao, RoleDao role, UserDao userDao, WorkoutDao workout, ListingDao listingDao) {
        this.teamDao = teamDao;
        this.role = role;
        this.userDao = userDao;
        this.workout = workout;
        this.listingDao = listingDao;
    }

    public User verifyToken(HttpServletRequest req) {
//https://aws.amazon.com/blogs/mobile/use-amazon-cognito-in-your-website-for-simple-aws-authentication/
        logger.debug("verifying token");
        String token = req.getHeader("Authorization");
        token = token.substring(7);
        String username = JwtParser(token);
        if (username != null) {
            return getUserByUsername(username);
        }
        return null;
    }

    public static String JwtParser(String token) {
//        String secret = "EaC9Bc2TVsoKn3vsFRi9yZwRg1J3IqgN3sjWdR1v";
        String publicKey = "pxNSMqGEkIfXpeXS3FKRCCJBScSKL2O+CFQppfPFR94=";
        DecodedJWT decodedToken = JWT.decode(token);
        Date expirationDate = decodedToken.getExpiresAt();
        Claim claimName = decodedToken.getClaim("cognito:username");
//        decodedToken.getAudience();
        String username = claimName.asString();
        String issuer = decodedToken.getIssuer();
        Date currentDate = new Date();

        if (decodedToken.getKeyId().compareTo(publicKey) == 0
                && issuer.compareTo("https://cognito-idp.us-east-2.amazonaws.com/us-east-2_0ypGIeR9H") == 0
                && expirationDate.compareTo(currentDate) >= 0) {
            return username;
        }
        return null;
    }

    public List<Workout> getAllWorkouts() {
        return workout.getAllWorkouts();
    }

    public List<Workout> getAllWorkoutsByWeek(Integer userId, Integer pageNum) {
        return workout.getAllWorkoutsByWeek(userId, pageNum);
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

    public Workout getWorkoutById(Integer workoutId) throws DaoException {
        return workout.getWorkoutById(workoutId);
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

    public List<Listing> getPaginatedListingsByTeamId(Integer teamId, Integer lastListingId) {
        if (lastListingId.intValue() == 0){
            List<Integer> output = getMinAndMaxListingId(teamId);
            int min = output.get(0);
            lastListingId = output.get(1);
        }
        return listingDao.getPaginatedListingsByTeamId(teamId, lastListingId);
    }

    public Listing addListing(Listing li) throws DaoException {
        try {
            if (li.getListingId() != null) {
                return listingDao.editListing(li);
            }
        } catch (NullPointerException ex) {
            throw new DaoException("Unable to add/edit a listing that has a null value.");
        }
        return listingDao.addListing(li);
    }

    public void removeUserFromTeam(Integer userId, Integer teamId) {
        teamDao.removeUserFromTeam(userId, teamId);
    }

    public void addUserToTeam(Integer teamId, Integer userId) throws DaoException {
        userDao.addUserToTeam(teamId, userId);
    }

    public Listing getListingById(Integer listingId) throws DaoException {
        return listingDao.getListingByID(listingId);
    }

    public void editListing(Listing listing) throws DaoException {
        listingDao.editListing(listing);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public void editAccount(User editedUser) throws DaoException {
        userDao.editAccount(editedUser);
    }

    public User getUserByEmail(String responseEmail) {
        return userDao.getUserByEmail(responseEmail);
    }

    public void updateTeamResources(Team currentTeam) {
        teamDao.updateTeamResources(currentTeam);
    }

    public void editWorkout(Workout w) {
        workout.editWorkout(w);
    }

    public void deleteWorkoutById(Integer workoutId) throws DaoException {
        workout.deleteWorkoutById(workoutId);
    }

    public Resource getResourceById(Integer resourceId) {
        return teamDao.getResourceById(resourceId);
    }

    public void deleteResourceById(Integer resourceId) {
        teamDao.deleteResourceById(resourceId);
    }

    public void deleteListingById(Integer listingId) {
        listingDao.deleteListingById(listingId);
    }

    public ListingFile getListingFileById(Integer listingFileId) {
        return listingDao.getListingFileById(listingFileId);
    }

    public void deleteListingFileById(Integer listingFileId) {
        listingDao.deleteListingFileById(listingFileId);
    }

    public List<Team> getAllTeams() {
        return teamDao.getAllTeams();
    }

    public List<Workout> getAllWorkoutsByDateRange(Integer userId, LocalDate startingLd, LocalDate endingLd) {
        return workout.getAllWorkoutsByDateRange(userId, startingLd, endingLd);
    }

    public Team addTeam(Team team) throws DaoException {
        return teamDao.addTeam(team);
    }

    public void addCoachRoleToUser(User currentUser) throws DaoException {
        List<Role> userRoles = currentUser.getUserRoles();
        List<Integer> userRoleIds = new ArrayList<>();
        for (int i = 0; i < userRoles.size(); i++) {
            userRoleIds.add(userRoles.get(i).getRoleId());
        }

        // only update account if user isn't already labeled as a coach in the DB
        if (!userRoleIds.contains(2)) {
            Role user = new Role();
            user.setRoleId(2);
            user.setRole("ROLE_COACH");
            userRoles.add(user);
            currentUser.setUserRoles(userRoles);
            userDao.editAccount(currentUser);
        }
    }

    public Coach getCoachByUserId(Integer userId) {
        return userDao.getCoachByUserId(userId);
    }

    public void addAsstCoachRoleToUser(User userToMakeCoach) throws DaoException {
        List<Role> userRoles = userToMakeCoach.getUserRoles();
        List<Integer> userRoleIds = new ArrayList<>();
        for (int i = 0; i < userRoles.size(); i++) {
            userRoleIds.add(userRoles.get(i).getRoleId());
        }

        // only update account if user isn't already labeled as an assistant coach in the DB
        if (!userRoleIds.contains(4)) {
            Role user = new Role();
            user.setRoleId(4);
            user.setRole("ROLE_ASSISTANTCOACH");
            userRoles.add(user);
            userToMakeCoach.setUserRoles(userRoles);
            userDao.editAccount(userToMakeCoach);
        }
    }

    public void addAssistantCoachToTeam(Integer teamId, Coach coach) {
        userDao.addAssistantCoachToTeam(teamId, coach);
    }

    public void removeCoachFromTeam(Integer teamId, Integer userId) {
        teamDao.removeCoachFromTeam(teamId, userId);
    }

    public List<Integer> getMinAndMaxListingId(Integer teamId) {
        return listingDao.getMinAndMaxListingId(teamId);
    }

    public List<Map<String, Object>> getRankedOrderOfMileageByTeammate(List<User> teammates, Integer selectedWeek) {
        Map<User, Double> map = workout.getRankedOrderOfMileageByTeammate(teammates, selectedWeek);

        ObjectMapper obj = new ObjectMapper();
        Map<User, Double> newMap = map.entrySet()
                .stream()
//                .sorted(new SortByName())
                .sorted(keyComparator)
                .sorted(valueComparator)
                //.sorted(Comparator.comparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap((Map.Entry::getKey), Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

//        LinkedHashMap<String, List<Map<String, Object>>> finalMap = new LinkedHashMap<>();
        List<Map<String, Object>> finalList = new ArrayList<>();

//        try {
        //List<Map<String, Object>> userList = new ArrayList<>();
            for (Map.Entry<User, Double> entry : newMap.entrySet()) {
                // parses user as JSON string for use on the front-end (not just hash value)
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("user", entry.getKey());
                userMap.put("mileage", entry.getValue());
                finalList.add(userMap);
            }
//            finalList.add(userList);
//            finalMap.put("Users", userList); this should be a list, not a map

        return finalList;
    }

    public void addUserAsCoach(Coach coach) {
        workout.addUserAsCoach(coach);
    }


    public void editTeamInfo(Team team) {
        teamDao.editTeamInfo(team);
    }

    final Comparator<Map.Entry<User, Double>> valueComparator = Map.Entry.comparingByValue(Comparator.reverseOrder());

    final Comparator<Map.Entry<User, Double>> keyComparator = Map.Entry.comparingByKey();

    final Comparator<Map.Entry<User, Double>> nameComparator = (m1, m2) -> m1.getKey().getFirstName().compareTo(m2.getKey().getFirstName());

}

// this works to sort by first and last name, ascending
//class SortByName implements Comparator<Map.Entry<User, Double>> {
//
//    @Override
//    public int compare(Map.Entry<User, Double> o1, Map.Entry<User, Double> o2) {
//        String o1Entry = o1.getKey().getFirstName() + o1.getKey().getLastName();
//        String o2Entry = o2.getKey().getFirstName() + o2.getKey().getLastName();
//        return o1Entry.compareTo(o2Entry);
//    }
//}

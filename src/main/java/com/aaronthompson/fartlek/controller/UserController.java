/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.fartlek.controller;

import com.aaronthompson.fartlek.dao.DaoException;
import com.aaronthompson.fartlek.entities.Listing;
import com.aaronthompson.fartlek.entities.Team;
import com.aaronthompson.fartlek.entities.Split;
import com.aaronthompson.fartlek.entities.User;
import com.aaronthompson.fartlek.entities.Workout;
import com.aaronthompson.fartlek.entities.WorkoutType;
import com.aaronthompson.fartlek.service.ServiceImpl;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author thomp
 */
@Controller
public class UserController {

    @Autowired
    ServiceImpl service;

    @Autowired
    PasswordEncoder encoder;

    Set<ConstraintViolation> violations = new HashSet<>();

    @GetMapping({"/", "home"})
    private String displayHomeScreen(Model model) {

//        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
//                .getContext().getAuthentication().getPrincipal();
//        int userId = service.getUserByUsername(user.getUsername()).getUserId();
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        User foundUser = service.getUserByUsername(username);

        List<Team> allTeams = new ArrayList<>();

        if (foundUser != null) {
            allTeams = service.getAllTeamsByUserId(foundUser.getUserId());
            model.addAttribute("user", foundUser);

        } else {
            model.addAttribute("userId", new User());
        }

        model.addAttribute("allTeams", allTeams);
        model.addAttribute("errors", violations);
        return "home";
    }

    @GetMapping("login")
    private String showLoginForm() {
        return "login";
    }

    @GetMapping("allworkouts")
    private String getAllWorkouts(Model model) {
        model.addAttribute("allWorkouts", service.getAllWorkouts());
        return "allWorkouts";
    }

    @GetMapping("/createAccount")
    private String createAccount(Model model) {

        List<Team> allTeams = new ArrayList<>();
        User blankUser = new User();
        model.addAttribute("allTeams", allTeams);
        model.addAttribute("user", blankUser);
        model.addAttribute("errors", model);

        return "createAccount";
    }

    @PostMapping("createAccount")
    private String createAccount(@Valid User toAdd, BindingResult result, HttpServletRequest request, Model model) {

        toAdd.setPassword(encoder.encode(toAdd.getPassword()));

        // check to see if username already exists
        if (service.getUserByUsername(toAdd.getUsername()) != null) {
            FieldError error = new FieldError("user", "username", "Username already exists. Please choose a unique username.");
            result.addError(error);
        }

        try {
            User addedUser = service.createAccount(toAdd);
        } catch (DaoException ex) {
            FieldError newError = new FieldError("User", "user", "Failed to add new user. If the problem persists, please contact us for assistance.");
            result.addError(newError);
        }
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            model.addAttribute("user", toAdd);
            return "createAccount";
        }
        model.addAttribute("errors", violations);
        return "home";
    }

    @GetMapping("trainingLog")
    private String viewTrainingLog(HttpServletRequest request, Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        User authUser = service.getUserByUsername(username);

        Integer userId = Integer.parseInt(request.getParameter("userId"));
        User destinationUser = service.getUserById(userId);
//        List<Team> allTeams = service.getAllTeamsByUserId(authUser.getUserId());

        // check if userId from http request = userID from authentication     
        if (authUser.getUserId().equals(destinationUser.getUserId())) {
            List<Workout> allWorkouts = service.getAllWorkoutsByUserId(userId);
            model.addAttribute("allTeams", destinationUser.getUserTeams());
            model.addAttribute("allWorkouts", allWorkouts);
            model.addAttribute("user", authUser);
            return "trainingLog";
            // else check if desired user is on the same team as the authenticated user trying to view their training log
        } else {
            List<Integer> authTeamIds = authUser.getUserTeams().stream()
                    .map(team -> team.getTeamId()).collect(Collectors.toList());
            List<Integer> destinationUserTeamIds = destinationUser.getUserTeams().stream()
                    .map(team -> team.getTeamId()).collect(Collectors.toList());

            for (Integer teamId : authTeamIds) {
                if (destinationUserTeamIds.contains(teamId)) {
                    List<Workout> allWorkouts = service.getAllWorkoutsByUserId(userId);
                    model.addAttribute("allTeams", destinationUser.getUserTeams());
                    model.addAttribute("allWorkouts", allWorkouts);
                    model.addAttribute("user", authUser);
                    return "trainingLog";
                }
            }
        }
        // if neither validations pass, then throw error and return to homepage
        FieldError error = new FieldError("User", "team", "You do not have access to this user's training log. Access denied.");
        Set<FieldError> errors = new HashSet<>();
        errors.add(error);
        model.addAttribute("allTeams", destinationUser.getUserTeams());
        model.addAttribute("errors", errors);
        model.addAttribute("user", authUser);
        return "home";
    }

    @GetMapping("addWorkout")
    private String addWorkoutGet(HttpServletRequest request, Model model) {
        // need to validate that userId from http = userID from authentication
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        User authUser = service.getUserByUsername(username);

        Integer userId = Integer.parseInt(request.getParameter("userId"));
        User destinationUser = service.getUserById(userId);

        List<Team> allTeams = service.getAllTeamsByUserId(authUser.getUserId());

        if (authUser.getUserId() != destinationUser.getUserId()) {
            FieldError error = new FieldError("User", "userId", "You do not have access to add workouts to this user's training log");
            Set<FieldError> errors = new HashSet<>();
            errors.add(error);
            model.addAttribute("allTeams", allTeams);
            model.addAttribute("errors", errors);
            model.addAttribute("user", destinationUser);
            return "home";
        }

        model.addAttribute("allTeams", allTeams);
        model.addAttribute("user", authUser);
        return "addWorkout";
    }

    // Need to add validation
    @PostMapping("addWorkout")
    private String addWorkoutPost(Workout workout, HttpServletRequest request, Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        User foundUser = service.getUserByUsername(username);

        String workoutDate = request.getParameter("workoutDate");
        LocalDateTime workoutDateTime = LocalDateTime.parse(workoutDate);
        workout.setWorkoutDateTime(workoutDateTime);

        // set duration
        String hours = request.getParameter("h");
        String minutes = request.getParameter("m");
        String seconds = request.getParameter("s");
        String duration = "PT" + hours + "H" + minutes + "M" + seconds + "S";
        Duration d = Duration.parse(duration);
        workout.setDuration(d.toString());

        // splits. Once done with duration, then decide on this. 
//        String[] hoursArray = request.getParameterValues("hours");
        String[] minutesArray = request.getParameterValues("minutes");
        String[] secondsArray = request.getParameterValues("seconds");

        Integer workoutId = Integer.parseInt(request.getParameter("exercise"));
        WorkoutType workoutType = service.findWorkoutTypeById(workoutId);
        workout.setWorkoutType(workoutType);

        // calculate avg pace if both distance and duration are known
        if (workout.getDuration() != null
                && workout.getDistance() != 0) {
            String wduration = workout.getDuration(); // PT2H5M29S
            Long dSeconds = convertDurationToSeconds(wduration);

            // find avg seconds per mile
            Double avgSeconds = Math.ceil(dSeconds / workout.getDistance());
            Integer avgSec = avgSeconds.intValue();

            // create string of minutes and seconds (mm:ss)
            Integer paceMinutes = avgSec / 60;
            avgSec -= (paceMinutes * 60);
            String avgPace = paceMinutes + ":" + avgSec;
            workout.setAveragePace(avgPace);
        }

        // calculate splits from the two string arrays of minuts and seconds
        List<Split> allSplits = new ArrayList<>();
        if (minutesArray != null) {
            for (int i = 0; i < minutesArray.length; i++) {
                Split oneSplit = new Split();

                String min = minutesArray[i];
                if (min.isEmpty()) {
                    min = "00";
                }
                String sec = secondsArray[i];
                if (sec.isEmpty()) {
                    sec = "00";
                }

                oneSplit.setSplitTime(min + ":" + sec);
                allSplits.add(oneSplit);
            }
        }
        workout.setSplits(allSplits);

        service.addWorkout(workout);

        List<Workout> allWorkouts = service.getAllWorkoutsByUserId(foundUser.getUserId());

        model.addAttribute("allWorkouts", allWorkouts);
        return "redirect:/trainingLog?userId=" + foundUser.getUserId();
    }

    private Long convertDurationToSeconds(String duration) {

        Duration d = Duration.parse(duration);
        return d.toSeconds();
    }

    @GetMapping("account")
    private String getAccountInformation(HttpServletRequest request, Model model) {

        // need to validate that userId from http = userID from authentication
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        User authUser = service.getUserByUsername(username);

        Integer userId = Integer.parseInt(request.getParameter("userId"));
        User accountUser = service.getUserById(userId);

        List<Team> allTeams = service.getAllTeamsByUserId(authUser.getUserId());

        model.addAttribute("allTeams", allTeams);
        model.addAttribute("user", accountUser);
        return "myAccount";
    }

    @GetMapping("teamHome")
    private String viewTeamPage(HttpServletRequest request, Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        User authUser = service.getUserByUsername(username);

        Integer teamId = Integer.parseInt(request.getParameter("teamId"));
        Team selectedTeam = service.getTeamById(teamId);

        List<Listing> teamListings = service.getAllListingsByTeamId(teamId);

        List<Team> allTeams = service.getAllTeamsByUserId(authUser.getUserId());
        List<Integer> coachIds = selectedTeam.getTeamCoaches().stream()
                .map(coach -> coach.getUserId())
                .collect(Collectors.toList());

        model.addAttribute("errors", violations);
        model.addAttribute("coachIds", coachIds);
        model.addAttribute("allTeams", allTeams);
        model.addAttribute("teamListings", teamListings);
        model.addAttribute("allUsers", selectedTeam.getTeamUsers());
        model.addAttribute("team", selectedTeam);
        model.addAttribute("user", authUser);
        return "teamHome";
    }

    @GetMapping("addListing")
    private String addListingGet(HttpServletRequest request, Model model) {
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        User authUser = service.getUserByUsername(username);

        Integer teamId = Integer.parseInt(request.getParameter("teamId"));
        Team team = service.getTeamById(teamId);

        // make sure that authUser is a coach associated with the team
        List<Integer> coachIds = team.getTeamCoaches().stream()
                .map(coach -> coach.getUserId())
                .collect(Collectors.toList());

        model.addAttribute("coachIds", coachIds);
        model.addAttribute("allTeams", authUser.getUserTeams());
        model.addAttribute("user", authUser);

        if (!coachIds.contains(authUser.getUserId())) {
            FieldError error = new FieldError("Team", "user", "You do not have access to add posts to this team's page");
            Set<FieldError> errors = new HashSet<>();
            errors.add(error);
            model.addAttribute("errors", errors);

            return "home";
        }
        model.addAttribute("errors", violations);
        model.addAttribute("team", team);
        return "teamAddListing";
    }

    // take care of DaoException later
    @PostMapping("addListing")
    private String addListingGet(Listing li, HttpServletRequest request, Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        User authUser = service.getUserByUsername(username);

        String listDate = request.getParameter("listDate");
        LocalDate localListDate = LocalDate.parse(listDate);
        li.setDate(localListDate);

        Integer teamId = li.getTeamId();
        Team selectedTeam = service.getTeamById(teamId);

        List<Listing> teamListings = service.getAllListingsByTeamId(teamId);

        List<Integer> coachIds = selectedTeam.getTeamCoaches().stream()
                .map(coach -> coach.getUserId())
                .collect(Collectors.toList());

        model.addAttribute("coachIds", coachIds);
        model.addAttribute("teamListings", teamListings);
        model.addAttribute("allUsers", selectedTeam.getTeamUsers());
        model.addAttribute("team", selectedTeam);
        model.addAttribute("user", authUser);

        try {
            Listing addedListing = service.addListing(li);
        } catch (DaoException ex) {
            Set<FieldError> errors = new HashSet<>();
            FieldError error = new FieldError("user", "username", "Unable to add post. Please try again and contact us if the problem persists.");
            errors.add(error);
            model.addAttribute("error", errors);
            return ("teamAddListing(teamId=" + teamId);
        }
        model.addAttribute("errors", violations);
        return ("redirect:/teamHome?teamId=" + selectedTeam.getTeamId());
    }

    @GetMapping("editListing")
    private String editListingGet(HttpServletRequest request, Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        User authUser = service.getUserByUsername(username);

        Integer previousListingId = Integer.parseInt(request.getParameter("listingId"));
        Listing previousListing = service.getListingById(previousListingId);
        Team selectedTeam = service.getTeamById(previousListing.getTeamId());

        List<Team> allTeams = service.getAllTeamsByUserId(authUser.getUserId());

        model.addAttribute("team", selectedTeam);
        model.addAttribute("allTeams", allTeams);
        model.addAttribute("user", authUser);
        model.addAttribute("listing", previousListing);
        return "teamEditListing";
    }

    @PostMapping("editListing")
    private String editListingPost(Listing listing, HttpServletRequest request, Model model) throws DaoException {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        User authUser = service.getUserByUsername(username);

        String listDate = request.getParameter("listDate");
        LocalDate localListDate = LocalDate.parse(listDate);
        listing.setDate(localListDate);

        List<Team> allTeams = service.getAllTeamsByUserId(authUser.getUserId());
        List<Listing> teamListings = service.getAllListingsByTeamId(listing.getTeamId());
        Team selectedTeam = service.getTeamById(listing.getTeamId());

        List<Integer> coachIds = selectedTeam.getTeamCoaches().stream()
                .map(coach -> coach.getUserId())
                .collect(Collectors.toList());

        model.addAttribute("coachIds", coachIds);
        model.addAttribute("teamListings", teamListings);
        model.addAttribute("allUsers", selectedTeam.getTeamUsers());
        model.addAttribute("team", selectedTeam);
        model.addAttribute("user", authUser);

        try {
            service.editListing(listing);
        } catch (DaoException ex) {
            Set<FieldError> errors = new HashSet<>();
            FieldError error = new FieldError("user", "username", "Unable to edit post. Please try again and contact us if the problem persists.");
            errors.add(error);
            model.addAttribute("error", errors);
            return ("teamEditListing?listingId=" + listing.getListingId());
        }

        model.addAttribute("errors", violations);
        return "teamHome";
    }

    @GetMapping("editTeam")
    private String editTeamGet(HttpServletRequest request, Model model) {

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        User authUser = service.getUserByUsername(username);

        Integer teamId = Integer.parseInt(request.getParameter("teamId"));
        Team selectedTeam = service.getTeamById(teamId);

        List<Team> allTeams = service.getAllTeamsByUserId(authUser.getUserId());
        List<Integer> coachIds = selectedTeam.getTeamCoaches().stream()
                .map(coach -> coach.getUserId())
                .collect(Collectors.toList());

        Set<FieldError> errors = new HashSet<>();
        model.addAttribute("allTeams", allTeams);
        model.addAttribute("coachIds", coachIds);
        model.addAttribute("allUsers", selectedTeam.getTeamUsers());
        model.addAttribute("team", selectedTeam);
        model.addAttribute("user", authUser);

        List<Integer> teamUserIds = selectedTeam.getTeamUsers().stream()
                .map(user -> user.getUserId())
                .collect(Collectors.toList());

        if (!teamUserIds.contains(authUser.getUserId())) {
            FieldError error = new FieldError("Team", "user", "You do not have access to edit this team");
            errors.add(error);
            model.addAttribute("allTeams", allTeams);
            model.addAttribute("errors", errors);
            model.addAttribute("user", authUser);
            return "home";
        }
        model.addAttribute("errors", errors);
        return "editTeam";
    }

    @PostMapping("removeTeammate")
    private String removeTeammate(HttpServletRequest request, Model model) {

        Integer userId = Integer.parseInt(request.getParameter("userId"));
        Integer teamId = Integer.parseInt(request.getParameter("teamId"));

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        User authUser = service.getUserByUsername(username);

        Team selectedTeam = service.getTeamById(teamId);
        List<Team> allTeams = service.getAllTeamsByUserId(authUser.getUserId());

        List<Integer> coachIds = selectedTeam.getTeamCoaches().stream()
                .map(coach -> coach.getUserId())
                .collect(Collectors.toList());

        Set<FieldError> errors = new HashSet<>();

        model.addAttribute("errors", errors);
        model.addAttribute("allTeams", allTeams);
        model.addAttribute("coachIds", coachIds);
        model.addAttribute("allUsers", selectedTeam.getTeamUsers());
        model.addAttribute("team", selectedTeam);
        model.addAttribute("user", authUser);

        if (!coachIds.contains(authUser.getUserId())) {
            FieldError error = new FieldError("Team", "user", "You are not authorized to remove users from this team");
            errors.add(error);
            return "editTeam";
        } else {
            service.removeUserFromTeam(userId, teamId);
            return ("redirect:/editTeam?teamId=" + selectedTeam.getTeamId());
        }
    }

    @PostMapping("addTeammate")
    private String addTeammate(HttpServletRequest request, Model model) {

        // hidden team id, username sent over
        String usernameToAdd = request.getParameter("username");
        User userToAdd = service.getUserByUsername(usernameToAdd);

        Integer teamId = Integer.parseInt(request.getParameter("teamId"));

        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        User authUser = service.getUserByUsername(username);

        Team selectedTeam = service.getTeamById(teamId);
        List<Team> allTeams = service.getAllTeamsByUserId(authUser.getUserId());

        List<Integer> coachIds = selectedTeam.getTeamCoaches().stream()
                .map(coach -> coach.getUserId())
                .collect(Collectors.toList());

        Set<FieldError> errors = new HashSet<>();
        model.addAttribute("errors", errors);
        model.addAttribute("allTeams", allTeams);
        model.addAttribute("coachIds", coachIds);
        model.addAttribute("allUsers", selectedTeam.getTeamUsers());
        model.addAttribute("team", selectedTeam);
        model.addAttribute("user", authUser);

        if (userToAdd == null) {
            FieldError error = new FieldError("Team", "user", "This user does not exist. Please enter a valid username");
            errors.add(error);
            return "editTeam";
        }

        try {
            User addedUser = service.addUserToTeam(teamId, userToAdd.getUserId());
        } catch (DaoException ex) {
            FieldError error = new FieldError("Team", "user", "Unable to add desired user to the team");
            errors.add(error);
            return "editTeam";
        }

        return ("redirect:/editTeam?teamId=" + selectedTeam.getTeamId());
    }
}

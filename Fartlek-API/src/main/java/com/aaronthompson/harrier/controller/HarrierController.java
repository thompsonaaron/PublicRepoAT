/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.controller;

import com.aaronthompson.harrier.common.ForbiddenException;
import com.aaronthompson.harrier.dao.DaoException;
import com.aaronthompson.harrier.entities.*;
import com.aaronthompson.harrier.service.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

//import com.twilio.rest.api.v2010.account.Message;

/**
 * @author thomp
 */
@RestController
@RequestMapping("/")
@CrossOrigin
@EnableWebMvc
public class HarrierController {

    @Autowired
    ServiceImpl service;

    private final static Logger logger = LogManager.getLogger(HarrierController.class);
    private static final String SUCCESS = "Success";
    private static final String ERROR = "Error";
    private static final ResponseEntity<Object> SUCCESS_RESPONSE = new ResponseEntity<>(SUCCESS, HttpStatus.OK);
    private static final ResponseEntity<Object> NULL_RESPONSE = new ResponseEntity<>("Error. Failed to verify user token.", HttpStatus.FORBIDDEN);

    private static final ResponseStatusException FORBIDDEN_RESPONSE = new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to verify user token");

    private ResponseEntity<Object> returnError (String errMsg) {
        return new ResponseEntity<>(errMsg, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping({"/", "/home"})
    private User verifyToken(HttpServletRequest req) {
//https://aws.amazon.com/blogs/mobile/use-amazon-cognito-in-your-website-for-simple-aws-authentication/
        logger.debug("verifying token");
        User user = service.verifyToken(req);

        if (user == null){
            throw FORBIDDEN_RESPONSE;
        }
        return user;
    }

    @GetMapping("/getUserByUsername")
    private User getUserByUsername(HttpServletRequest req, @RequestParam String username) {
//https://aws.amazon.com/blogs/mobile/use-amazon-cognito-in-your-website-for-simple-aws-authentication/
        User currentUser = service.verifyToken(req);
        return service.getUserByUsername(username);
    }

    @GetMapping("/getUser")
    private User loadCreateAccountDetails(HttpServletRequest request) {
        return verifyToken(request);
    }

    @PostMapping("/createAccount")
    private ResponseEntity<Object> createAccount(HttpServletRequest request, @RequestBody @Valid User toAdd, BindingResult result) {
        String errMsg = "";
        if (result.hasErrors()) {
            errMsg = result.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(". "));
            return returnError(errMsg);
        }

        User newUser = service.verifyToken(request);
        toAdd.setUserId(newUser.getUserId());
        // assign role of "user" by default if no pre-existing roles
        if (newUser.getUserRoles() == null) {
            List<Role> userRoles = new ArrayList<>();
            Role basicUserRole = new Role();
            basicUserRole.setRoleId(1);
            basicUserRole.setRole("ROLE_USER");
            toAdd.setUserRoles(userRoles);
        } else {
            toAdd.setUserRoles(newUser.getUserRoles());
        }
        toAdd.setUserTeams(newUser.getUserTeams() == null ? new ArrayList<>() : newUser.getUserTeams());


        if (!result.hasErrors()) {
            try {
                service.editAccount(toAdd);
                return SUCCESS_RESPONSE;
            } catch (DaoException ex) {
                errMsg = "Failed to create account.";
                logger.error(errMsg);
                return returnError(errMsg);
            }
        } else {
            errMsg = result.getAllErrors().toString();
        }
        return returnError(errMsg);
    }

    @GetMapping("/trainingLog")
    private Map<String, Object> getTrainingLog(HttpServletRequest request) throws ForbiddenException {
        User currentUser = service.verifyToken(request);
        Map<String, Object> toReturn = new HashMap<>();

        String destinationUsername = request.getParameter("username");
        User destinationUser = service.getUserByUsername(destinationUsername);

        String wk = request.getParameter("week");
        Integer week = Integer.parseInt(wk);

        // check if current user wants to view their own training log (currentUser == userId)
        if (currentUser.getUserId().equals(destinationUser.getUserId())) {
            List<Workout> allWorkouts = service.getAllWorkoutsByWeek(currentUser.getUserId(), week);
            toReturn.put("allWorkouts", allWorkouts);
            toReturn.put("relationship", "user");
            return toReturn;
        } // else check if current user is on at least one same team as the authenticated user trying to view their training log
        else {
            List<Integer> currentUserTeamIds = currentUser.getUserTeams().stream()
                    .map(Team::getTeamId).collect(Collectors.toList());
            List<Integer> destinationUserTeamIds = destinationUser.getUserTeams().stream()
                    .map(Team::getTeamId).collect(Collectors.toList());

            boolean isTeammate = false;
            for (Integer teamId : currentUserTeamIds) {
                if (destinationUserTeamIds.contains(teamId)) {
                    isTeammate = true;
                    break;
                }
            }
            if (isTeammate) {
                List<Workout> allWorkouts = service.getAllWorkoutsByWeek(destinationUser.getUserId(), week);
                toReturn.put("allWorkouts", allWorkouts);
                toReturn.put("relationship", "teammate");

                // now check if the current user is a coach
                List<Team> userTeams = destinationUser.getUserTeams();
                int numTeams = userTeams.size();
                for (int i = 0; i < numTeams; i++) {
                    List<Integer> coachIds = findCoachIds(userTeams.get(i));
                    if (coachIds.contains(currentUser.getUserId())) {
                        toReturn.put("relationship", "coach");
                    }
                }
            }
            return toReturn;
        }
    }

    @GetMapping("/trainingLogByDateRange")
    private List getTrainingLogByDateRange(HttpServletRequest request) throws ForbiddenException {
        User currentUser = service.verifyToken(request);
        List<Object> toReturn = new ArrayList<>();

        String destinationUsername = request.getParameter("username");
        User destinationUser = service.getUserByUsername(destinationUsername);

        String startingDate = request.getParameter("starting");
        LocalDate startingLd = LocalDate.parse(startingDate);

        String endingDate = request.getParameter("ending");
        LocalDate endingLd = LocalDate.parse(endingDate);

        // check if current user wants to view their own training log (currentUser == userId)
        if (currentUser.getUserId().equals(destinationUser.getUserId())) {
//                List<Workout> allWorkouts = service.getAllWorkoutsByUserId(currentUser.getUserId());
            List<Workout> allWorkouts = service.getAllWorkoutsByDateRange(currentUser.getUserId(), startingLd, endingLd);
            toReturn.add(allWorkouts);
            toReturn.add("user");
            return toReturn;
        } // else check if current user is on at least one same team as the authenticated user trying to view their training log
        else {
            List<Integer> currentUserTeamIds = currentUser.getUserTeams().stream()
                    .map(Team::getTeamId).collect(Collectors.toList());
            List<Integer> destinationUserTeamIds = destinationUser.getUserTeams().stream()
                    .map(Team::getTeamId).collect(Collectors.toList());

            for (Integer teamId : currentUserTeamIds) {
                if (destinationUserTeamIds.contains(teamId)) {
                    List<Workout> allWorkouts = service.getAllWorkoutsByDateRange(destinationUser.getUserId(), startingLd, endingLd);
                    toReturn.add(allWorkouts);
                    toReturn.add("teammate");
                    // now check if the current user is a coach, if so return true
                    List<Team> userTeams = destinationUser.getUserTeams();
                    int numTeams = userTeams.size();
                    for (int i = 0; i < numTeams; i++) {
                        List<Integer> coachIds = findCoachIds(userTeams.get(i));
                        if (coachIds.contains(currentUser.getUserId())) {
                            toReturn.remove(1); // removes false from the list
                            toReturn.add("coach");
                            return toReturn;
                        }
                    }
                } else {
                    FieldError error = new FieldError("User", "team", "Access denied. You do not have access to this user's training log.");
                    List<FieldError> errors = new ArrayList<>();
                    errors.add(error);
                    return errors;
                }
            }
        }
        return toReturn;
    }

    @PostMapping("/addWorkout")
    private ResponseEntity<Object> addWorkoutPost(HttpServletRequest request, @RequestParam Integer workoutTypeId,
                                          @RequestParam Integer workoutTimeId, @RequestBody Workout workout) throws IOException {
        return getWorkoutToAddDetails(request, "add", workout, workoutTypeId, workoutTimeId);
    }

    @GetMapping("/teamHome")
    private List<Object> getTeamPage(HttpServletRequest request) {
        List<Object> returnedList = new ArrayList<>();
        User currentUser = service.verifyToken(request);

        if (currentUser != null) {
            Integer teamId = Integer.parseInt(request.getParameter("teamId"));
            //
            Integer lastListingId = Integer.parseInt(request.getParameter("lastListingId"));
            Team selectedTeam = service.getTeamById(teamId);
            List<Listing> teamListings = service.getPaginatedListingsByTeamId(teamId, lastListingId);
            List<Integer> coachIds = findCoachIds(selectedTeam);
            List<User> teamMembers = service.getUsersByTeamId(teamId);

            returnedList.add(selectedTeam);
            returnedList.add(teamListings);
            returnedList.add(teamMembers);
            returnedList.add(coachIds.contains(currentUser.getUserId()));
            returnedList.add(verifyIsHeadCoach(currentUser.getUserId(), selectedTeam));
        } else {
            ValidationResponse res = new ValidationResponse();
            res.setStatus("ERROR");
            FieldError error = new FieldError("View team homepage", "Token Error", "Access denied. Please sign in again and retry.");
            List<FieldError> errors = new ArrayList<>();
            errors.add(error);
            res.setErrorMessageList(errors);
            returnedList.add(res);
        }
        return returnedList;
    }

    @GetMapping("/getTeamListings")
    private List<Listing> getTeamListings(HttpServletRequest request, @RequestParam Integer teamId,
                                          @RequestParam Integer lastListingId){
        User currentUser = service.verifyToken(request);

        if (currentUser != null) {
            return service.getPaginatedListingsByTeamId(teamId, lastListingId);
        }
        return null;
    }

//    @GetMapping("/getLastListingId")
//    private List<Integer> getMinAndMaxListingId(HttpServletRequest request, @RequestParam Integer teamId) {
//        User currentUser = service.verifyToken(request);
//        if (currentUser != null) {
//            return service.getMinAndMaxListingId(teamId);
//        } else {
//            return null;
//        }
//    }

    // uses for adding and editing a listing (switches in service)
    @PostMapping("/addListing")
    private ResponseEntity<Object> addNewListing(HttpServletRequest request, @RequestParam("file") Optional<List<MultipartFile>> files, @RequestParam String title,
                                             @RequestParam Integer teamId, @RequestParam String content, @RequestParam String date, @RequestParam String listingId) throws IOException {
        User currentUser = service.verifyToken(request);
        if (currentUser != null) {
            Listing li = new Listing();
            li.setTitle(title);
            li.setTeamId(teamId);
            li.setContent(content);

            //Wed Jul 01 2020 14:48:09 GMT-0500
            date = date.substring(0, 24);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss");
            LocalDateTime ldt = LocalDateTime.parse(date, formatter); // will parse it into UTC
//            ZonedDateTime zdt = ZonedDateTime.parse(date, formatter);
//            LocalDateTime ldt = zdt.toLocalDateTime(); // aws server operates on UTC and this will convert it to the current local time (UTC)
            li.setDateTime(ldt);
            if (listingId != null && !listingId.isEmpty()) {
                li.setListingId(Integer.parseInt(listingId));
            }

            Team selectedTeam = service.getTeamById(li.getTeamId());
            List<Integer> coachIds = findCoachIds(selectedTeam);

            if (coachIds.contains(currentUser.getUserId())) {
                // user is a coach for this team and has access to add listings
                try {
                    List<ListingFile> listingFiles = new ArrayList<>();

                    // convert the multipart file to a file
                    if (files.isPresent()) {
                        // not functioning as posted on public github (credentials hidden)
//                        BasicAWSCredentials awsCreds = new BasicAWSCredentials(Credentials.access_key_id, Credentials.secret_access_key);
//                        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
//                                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
//                                .withRegion(Regions.US_EAST_2)
//                                .build();
                        for (MultipartFile mpf : files.get()) {
                            File convFile = new File(mpf.getOriginalFilename());
                            try (FileOutputStream fos = new FileOutputStream(convFile)) {
                                fos.write(mpf.getBytes());

                                // limit file size
                                double fileSize = convFile.length() / 1024.0 / 1024.0;
                                if (fileSize > 2.0) {
                                    String errMsg = "User with id [" + currentUser.getUserId() + "] tried to upload file size > 2 MB.";
                                    logger.error(errMsg);
                                    return returnError(errMsg);
                                }

                                // save new file to S3
                                String filePath = "listingFiles/" + convFile.getName() + UUID.randomUUID().toString();
                                ListingFile lf = new ListingFile();
//                                lf.setFilePath("https://harrier-non-static-pages.s3.us-east-2.amazonaws.com/" + filePath);
                                lf.setFilePath("https://fartlek.us/" + filePath);
                                lf.setTitle(mpf.getOriginalFilename());
                                lf.setListingId(li.getListingId());
                                listingFiles.add(lf);

//                                disabled due to credentials:
//                                s3.putObject("harrier-non-static-pages", filePath, convFile);
                            }
                        }
                    }
                    li.setListingFiles(listingFiles);
                    Integer id = li.getListingId();
                    service.addListing(li);
                    // if this was a new listing, then send email notification
                    if (id == null) {
                        sendEmailUpdate(selectedTeam, li, currentUser);
                    }
                    return SUCCESS_RESPONSE;
//                    sendSMSupdate(selectedTeam, addedListing.getTitle());
                } catch (DaoException | MessagingException ex) {
                    logger.error(ex.getMessage());
                    return returnError(ex.getMessage());
                }
            }
        } else {
            return NULL_RESPONSE;
        }
        return SUCCESS_RESPONSE;
    }

    @GetMapping("/getEditListing")
    private Listing editListingGet(HttpServletRequest request) throws DaoException {
//        User currentUser = verifyToken(request); not doing anything with this, so won't bother to verify here
        Integer listingId = Integer.parseInt(request.getParameter("listingId"));
        return service.getListingById(listingId);
    }

    @PostMapping("/removeTeammate")
    private ResponseEntity<Object> removeTeammate(HttpServletRequest request, @RequestParam Integer teamId,
                                                  @RequestParam Integer userId) {

        User currentUser = service.verifyToken(request);
        String errMsg = "";
        if (currentUser != null) {
//            Integer teamId = map.get("teamId");
//            Integer userId = map.get("userId");
            Team selectedTeam = service.getTeamById(teamId);

            List<Integer> coachIds = findCoachIds(selectedTeam);

            // if team is public, then user can remove themselves
            if (selectedTeam.getTeamPrivacyId().equals(1) && currentUser.getUserId().equals(userId)) {
                service.removeUserFromTeam(userId, teamId);
                return SUCCESS_RESPONSE;
            }

            // otherwise team is private and only coaches can remove teammates
            if (currentUser.getUserId().intValue() == userId) {
                errMsg = "User with user id [" + userId + "] tried to remove themselves from team Id [" + teamId + "]";
                logger.warn(errMsg);
                return returnError("You cannot remove yourself from this team. ");
            }

            // is the user a coach on this team? If not, throw an error. 
            if (!coachIds.contains(currentUser.getUserId())) {
                errMsg = "User with user id [" + userId + "] is not a coach for teamId [" + teamId + "]";
                logger.warn(errMsg);
                return new ResponseEntity<>("User is not a coach for the selected team, request denied", HttpStatus.FORBIDDEN);
            }

            if (coachIds.contains(userId)) {
                errMsg = "You cannot remove another coach from this team. If you are the head coach, you must remove this user as a coach before you can remove them as a teammate.";
                logger.warn(errMsg);
                return new ResponseEntity<>(errMsg, HttpStatus.FORBIDDEN);
            } else {
                service.removeUserFromTeam(userId, teamId);
                return SUCCESS_RESPONSE;
            }
        } else {
            return NULL_RESPONSE;
        }
    }

    @PostMapping("/addTeammate")
    private ResponseEntity<Object> addTeammate(HttpServletRequest request, @RequestParam String username,
                                               @RequestParam Integer teamId) {
        // check if team privacy is public or private; if private, then check is token userId is a coach for this team
        // 1) check that token userId is a coach for this team
        // 2) check that the user and the team exist
        // 3) check that user doesn't already belong to this team
        User currentUser = service.verifyToken(request);
        String errMsg = "";
        if (currentUser != null) {
            User userToAdd = service.getUserByUsername(username);
            if (userToAdd == null) {
                errMsg = "This user does not exist. Please enter a valid username";
                logger.warn(errMsg);
                return returnError(errMsg);
            }

            Team selectedTeam = service.getTeamById(teamId);
            if (selectedTeam == null) {
                errMsg = "Cannot add user to the selected team. This team does not exist.";
                logger.warn(errMsg);
                return returnError(errMsg);
            }

            List<Integer> coachIds = findCoachIds(selectedTeam);

            if (selectedTeam.getTeamPrivacyId() == 2) {
                if (!coachIds.contains(currentUser.getUserId())) {
                    errMsg = "You are not a registered coach for this team. The selected user was not added to this team.";
                    logger.warn(errMsg);
                    return returnError(errMsg);
                }
            }

            try {
                service.addUserToTeam(teamId, userToAdd.getUserId());
                return SUCCESS_RESPONSE;
            } catch (DaoException ex) {
                logger.error(ex.getMessage());
                return returnError(ex.getMessage());
            }
        } else {
            return NULL_RESPONSE;
        }
    }

    @PostMapping("/uploadFile")
    private ResponseEntity<Object> uploadFile(HttpServletRequest req, @RequestParam Integer teamId, @RequestParam("file") List<MultipartFile> files) throws IOException {
//could consider allowing multiple files in the future using multipartFile[] and a for loop
        User currentUser = service.verifyToken(req);

        if (currentUser != null) {
            Team currentTeam = service.getTeamById(teamId);
            List<Integer> coachIds = findCoachIds(currentTeam);

            if (coachIds.contains(currentUser.getUserId())) {
//                BasicAWSCredentials awsCreds = new BasicAWSCredentials(Credentials.access_key_id, Credentials.secret_access_key);
//                final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
//                        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
//                        .withRegion(Regions.US_EAST_2)
//                        .build();

                // convert the multipartfile to a file
                for (int i = 0; i < files.size(); i++) {
                    File convFile = new File(files.get(i).getOriginalFilename() != null ? files.get(i).getOriginalFilename() : "blank");
                    try (FileOutputStream fos = new FileOutputStream(convFile)) {
                        fos.write(files.get(i).getBytes());
                    }

                    String filePath = "files/" + convFile.getName() + UUID.randomUUID().toString();

                    List<Resource> teamResources = currentTeam.getTeamResources();
                    Resource newRes = new Resource();
                    newRes.setUrl("https://harrier-non-static-pages.s3.us-east-2.amazonaws.com/" + filePath);
                    newRes.setTitle(files.get(i).getOriginalFilename());
                    newRes.setTeamId(teamId);
                    teamResources.add(newRes);
                    currentTeam.setTeamResources(teamResources);
                    service.updateTeamResources(currentTeam);

//                    disabled due to credentials
//                    s3.putObject("harrier-non-static-pages", filePath, convFile);
                }
                return SUCCESS_RESPONSE;
            } else {
                // need to account for this in JS
                String errMsg = "You are not registered as a coach for this team. File uploads require coaching credentials.";
                logger.warn(errMsg);
                return new ResponseEntity<>(errMsg, HttpStatus.FORBIDDEN);
            }
        } else {
            return NULL_RESPONSE;
        }
    }

    // not currently used
    private String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("name")) {
                return content.substring(content.indexOf("=") + 2, content.length() - 1);
            }
        }
        return "default";
    }

    @GetMapping("/getWorkout")
    private Workout getWorkoutById(Integer workoutId, HttpServletRequest request) {
        User currentUser = service.verifyToken(request);
        if (currentUser != null) {
            try {
                return service.getWorkoutById(workoutId);
            } catch (DaoException ex) {
                return null;
            }
        }
        return null;
    }

    @PostMapping("/editWorkout")
    private ResponseEntity<Object> editWorkoutById(HttpServletRequest request, @RequestParam Integer workoutTypeId,
                                                   @RequestParam Integer workoutTimeId, @RequestBody Workout workout) {
        return getWorkoutToAddDetails(request, "edit", workout, workoutTypeId, workoutTimeId);
    }

    @PostMapping("/deleteWorkout")
    private ResponseEntity<Object> deleteWorkoutById(HttpServletRequest request) throws IOException {
        // obtain workout and verify that user from token is associated with it
        // then delete
        User currentUser = service.verifyToken(request);
        Integer workoutId;
        if (currentUser != null) {
            String body = "";
            if ("POST".equalsIgnoreCase(request.getMethod())) {
                body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            }
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Integer> map = mapper.readValue(body, Map.class);
            try {
                workoutId = map.get("workoutId");
            } catch (NumberFormatException ex) {
                logger.error(ex.getMessage());
                return returnError(ex.getMessage());
            }
            try {
                Workout workoutToDelete = service.getWorkoutById(workoutId);
                if (workoutToDelete.getUserId().compareTo(currentUser.getUserId()) == 0) {
                    service.deleteWorkoutById(workoutId);
                    return SUCCESS_RESPONSE;
                }
            } catch (DaoException ex) {
                logger.error(ex.getMessage());
                return returnError(ex.getMessage());
            }
            String errMsg = "Invalid credentials. Cannot delete another user's workouts.";
            logger.warn(errMsg);
            return returnError(errMsg);
        } else {
            return NULL_RESPONSE;
        }
    }

    // need to finish this
    private ResponseEntity<Object>  getWorkoutToAddDetails(HttpServletRequest request, String methodName, Workout workout,
                                                          Integer workoutTypeId, Integer workoutTimeId)  {
        User currentUser = service.verifyToken(request);

        if (currentUser != null) {
            workout.setUserId(currentUser.getUserId());
            workout.setWorkoutType(service.findWorkoutTypeById(workoutTypeId));
            WorkoutTime workoutTime = new WorkoutTime();
            workoutTime.setTimeOfDayId(workoutTimeId);
            workout.setWorkoutTime(workoutTime);

            if (workout.getDuration().getSeconds() != 0 && workout.getDistance() != 0) {
                long dSeconds = workout.getDuration().toSeconds();
                Integer avgSec = ((Double) Math.floor(dSeconds / workout.getDistance())).intValue();
                //Integer avgSec = avgSeconds.intValue();

                // (mm:ss)
                String avgPace = convertSecondsToMinSecString(avgSec);
                workout.setAveragePace(avgPace);
            } else {
                workout.setAveragePace("");
            }

            Integer workoutId = workout.getWorkoutId();
            if (workoutId == null || workoutId == 0) {
                workout.setWorkoutId(null);
            }

            if (methodName.equals("add")) {
                service.addWorkout(workout);
            } // verifies that the person editing this is the one who did the workout
            else if (workout.getUserId().intValue() == currentUser.getUserId().intValue()) {
                service.editWorkout(workout);
            }
            return SUCCESS_RESPONSE;
        } else {
            return NULL_RESPONSE;
        }
    }

    private String convertSecondsToMinSecString(Integer avgSec) {
        int paceMinutes = avgSec / 60;
        avgSec -= (paceMinutes * 60);
        String minAvg = paceMinutes + ":";
        String secAvg = avgSec.toString();

        if (paceMinutes < 10) {
            minAvg = "0" + minAvg;
        }
        if (avgSec < 10) {
            secAvg = "0" + avgSec;
        }
        return minAvg + secAvg;
    }

    @PostMapping("/deleteResource")
    private ResponseEntity<Object> deleteResourceById(HttpServletRequest request, Integer resourceId) {
        // check to see that the token user has access (i.e. is a coach on this team)
        // obtain resource, then obtain teamId, then check if user is a coach for this team
        User currentUser = service.verifyToken(request);

        if (currentUser != null) {
            Resource foundResource = service.getResourceById(resourceId);
            Team resourceTeam = service.getTeamById(foundResource.getTeamId());

            List<Integer> coachIds = findCoachIds(resourceTeam);

            if (coachIds.contains(currentUser.getUserId())) {
                // current user has privilage to remove resources
                service.deleteResourceById(resourceId);
            }
            return SUCCESS_RESPONSE;
        } else {
            return NULL_RESPONSE;
        }
    }

    // I don't think this throws a DAO exception anymore, but w/e...
    @PostMapping("/deleteListing")
    private ResponseEntity<Object> deleteListingById(HttpServletRequest request, Integer listingId) throws DaoException {
        User currentUser = service.verifyToken(request);

        if (currentUser != null) {
            // find listing, then listing.getTeamId() --> then get team and check that currentUser is a coach on the team
            Listing foundListing = service.getListingById(listingId);
            Team foundTeam = service.getTeamById(foundListing.getTeamId());
            List<Integer> coachIds = findCoachIds(foundTeam);
            if (coachIds.contains(currentUser.getUserId())) {
                service.deleteListingById(listingId);
            }
            return SUCCESS_RESPONSE;
        } else {
            return NULL_RESPONSE;
        }
    }

    @PostMapping("/deleteListingFile")
    private ResponseEntity<Object> deleteListingFileById(HttpServletRequest request, Integer listingFileId) throws DaoException {
        User currentUser = service.verifyToken(request);

        if (currentUser != null) {
            //verify that currentUser is a coach for the team on which this listing is associated
            ListingFile lf = service.getListingFileById(listingFileId);
            Listing foundListing = service.getListingById(lf.getListingId());
            Team foundTeam = service.getTeamById(foundListing.getTeamId());
            List<Integer> coachIds = findCoachIds(foundTeam);
            if (coachIds.contains(currentUser.getUserId())) {
                service.deleteListingFileById(listingFileId);
            }
            return SUCCESS_RESPONSE;
        } else {
            return NULL_RESPONSE;
        }
    }

    @PostMapping("/addCoachingComments")
    private ResponseEntity<Object> addCoachingComments(HttpServletRequest request, @RequestParam Integer workoutId,
                                               @RequestBody Workout origWorkout) {
        User currentUser = service.verifyToken(request);

        // first, check that hasRole = coach
        // should verify that the token user is a coach for the user that submitted this workout
        if (currentUser != null) {
            try {
                Workout foundWorkout = service.getWorkoutById(workoutId);
                foundWorkout.setCcUserId(origWorkout.getCcUserId());
                Coach coach = service.getCoachByUserId(origWorkout.getCcUserId());
                foundWorkout.setCcCoachName(coach.getCoachName());
                foundWorkout.setCoachingComments(origWorkout.getCoachingComments());
                foundWorkout.setCcTimestamp(new Timestamp(System.currentTimeMillis()));

                service.editWorkout(foundWorkout);
                return SUCCESS_RESPONSE;
            } catch (DaoException ex) {
                String errMsg = "Workout not found.";
                logger.error(errMsg);
                return returnError(errMsg);
            }
        } else {
            return NULL_RESPONSE;
        }
    }

    @PostMapping("/registerCoach")
    private ResponseEntity<Object> registerCoach(HttpServletRequest request, @RequestBody Coach newCoach){
        User currentUser = verifyToken(request);

        if (currentUser == null){
            return NULL_RESPONSE;
        }

        User user = service.getUserById(newCoach.getUserId());
        try {
            if (!user.getUserRoles().stream().map(Role::getRoleId).collect(Collectors.toList()).contains(2)){
                service.addCoachRoleToUser(user);
                service.addUserAsCoach(newCoach);
            }
        } catch (DaoException e) {
            logger.error(e.getMessage());
            return returnError(e.getMessage());
        }
        return SUCCESS_RESPONSE;
    }

    @PostMapping("/addCoachToNewTeam")
    private ResponseEntity<Object> addCoachToNewTeam (HttpServletRequest request, @RequestBody Team team){
        User currentUser = service.verifyToken(request);
        Coach currentCoach = service.getCoachByUserId(currentUser.getUserId());
        team.setHeadCoach(currentCoach);
        try {
            // modify this method to set the head coach (not teamCoaches)
            Team addedTeam = service.addTeam(team);
            service.addUserToTeam(addedTeam.getTeamId(), currentUser.getUserId());
        } catch (DaoException e) {
            logger.error(e.getMessage());
            return returnError(e.getMessage());
        }
        return SUCCESS_RESPONSE;
    }

    // is this still used?
//    @PostMapping("/addCoachAndTeam")
//    private ResponseEntity addCoachAndTeam(HttpServletRequest request, @RequestBody ObjectNode objectNode) throws IOException {
//        User currentUser = service.verifyToken(request);
//        ObjectMapper objMap = new ObjectMapper();
//
//        if (currentUser != null) {
//            Team team = objMap.readValue(objectNode.get("team").toString(), Team.class);
//            Coach coach = objMap.readValue(objectNode.get("coach").toString(), Coach.class);
//            coach.setUserId(currentUser.getUserId());
////            coach.setCoachRoleId(1);
////            List<Coach> teamCoaches = new ArrayList<>();
////            teamCoaches.add(coach);
////            team.setTeamCoaches(teamCoaches);
//
//            try {
//                Team addedTeam = service.addTeam(team);
//                service.addCoachRoleToUser(currentUser);
//                service.addUserToTeam(addedTeam.getTeamId(), currentUser.getUserId());
//                return SUCCESS_RESPONSE;
//            } catch (DaoException ex) {
//                logger.error(ex.getMessage());
//                return returnError(ex.getMessage());
//            }
//        } else {
//            return NULL_RESPONSE;
//        }
//    }

    @PostMapping("/addTeam")
    private Object addTeam(HttpServletRequest request, @RequestBody Team team) {
        User currentUser = service.verifyToken(request);
        if (currentUser != null) {
            Coach coach = service.getCoachByUserId(currentUser.getUserId());
//            Coach coach = new Coach();
//            coach.setCoachRoleId(1); // head coach
//            coach.setUserId(currentUser.getUserId());
//            coach.setCoachName(coaches.get(0).getCoachName());
//            coach.setBiography(coaches.get(0).getBiography());
//            List<Coach> teamCoaches = new ArrayList<>();
//            teamCoaches.add(coach);
//            team.setTeamCoaches(teamCoaches);
            team.setHeadCoach(coach);
            try {
                Team addedTeam = service.addTeam(team);
                service.addUserToTeam(addedTeam.getTeamId(), currentUser.getUserId());
            } catch (DaoException ex) {
                logger.error(ex.getMessage());
                return returnError(ex.getMessage());
            }
        } else {
            return NULL_RESPONSE;
        }

        return SUCCESS_RESPONSE;
    }

    @PostMapping("/editTeam")
    private ResponseEntity<Object> editTeamInformation(HttpServletRequest request, @RequestBody Team team) {
        User currentUser = service.verifyToken(request);

        if (currentUser != null) {
            service.editTeamInfo(team);
        } else {
            return NULL_RESPONSE;
        }

        return SUCCESS_RESPONSE;
    }

    // TODO convert to requestParams: @RequestParam teamId, @RequestParam userId, @RequestParam coachName
    @PostMapping("/addAssistantCoach")
    private ResponseEntity<Object> addAssistantCoach(HttpServletRequest request, @RequestBody Map<String, String> map) {
        // 1) check that user is currently a user on the team
        // 2) getUserByUsername
        // 3) addAsstCoachRoleToUser(user) --> will verify that they are an assistant coach now, if not previously
        // 3) send user through service.addAssistantCoachToTeam(teamId, user) method
        // 4) insert into teamCoaches table with teamId, userId, coachName
        User currentUser = service.verifyToken(request);
        String username = map.get("username");
        String preferredName = map.get("preferredName");
        int teamId = Integer.parseInt(map.get("teamId"));

        //check that user isn't already a teamCoach / assistant
        if (currentUser != null) {
            Team currentTeam = service.getTeamById(teamId);
            List<Integer> coachIds = findCoachIds(currentTeam);
            User userToMakeCoach = service.getUserByUsername(username);
            if (coachIds.contains(userToMakeCoach.getUserId())) {
                String errMsg = "This user is already listed as a coach of this team.";
                return returnError(errMsg);
            }
            List<Team> userTeams = userToMakeCoach.getUserTeams();
            boolean isATeammate = false;
            int size = userTeams.size();
            for (int i = 0; i < size; i++) {
                if (userTeams.get(i).getTeamId() == teamId) {
                    isATeammate = true;
                    break;
                }
            }
            if (isATeammate) {
                if (preferredName.isBlank()) {
                    preferredName = userToMakeCoach.getFirstName() + " " + userToMakeCoach.getLastName();
                }
                Coach coach = new Coach();
                coach.setCoachName(preferredName);
                coach.setUserId(userToMakeCoach.getUserId());
                try {
                    service.addAsstCoachRoleToUser(userToMakeCoach);
                    service.addAssistantCoachToTeam(teamId, coach);
                    return SUCCESS_RESPONSE;
                } catch (DaoException ex) {
                    logger.error(ex.getMessage());
                    return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                String errMsg = "This user is not a member of this team. Users must belong to the team before they can be assigned as a coach.";
                return returnError(errMsg);
            }
        } else {
            String errMsg = "Failed to authenticate user. Request failed. Please sign in again and retry.";
            logger.warn(errMsg);
            return returnError(errMsg);
        }
    }

    @PostMapping("/removeCoach")
//    private ResponseEntity<Object> removeCoach(HttpServletRequest request, @RequestBody Map<String, Integer> map) {
    private ResponseEntity<Object> removeCoach(HttpServletRequest request, @RequestParam Integer teamId,
                                               @RequestParam Integer userId) {
        User currentUser = service.verifyToken(request);
//        Integer teamId = map.get("teamId");
//        Integer userId = map.get("userId");
        Team team = service.getTeamById(teamId);
        if (currentUser != null) {
            boolean isHeadCoach = verifyIsHeadCoach(currentUser.getUserId(), team);
            if (isHeadCoach) {
                if (userId.equals(currentUser.getUserId())) {
                    String errMsg = "Unable to remove yourself as a coach. The head coach cannot be removed from the team.";
                    return new ResponseEntity<>(errMsg, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                List<Integer> teamCoachIds = findCoachIds(team);
                if (!teamCoachIds.contains(userId)) {
                    String errMsg = "This user is not listed as a coach for this team.";
                    return returnError(errMsg);
                }
                service.removeCoachFromTeam(teamId, userId);
            }
            return SUCCESS_RESPONSE;
        }
        return NULL_RESPONSE;
    }

    private List<Integer> findCoachIds(Team team) {
        List<Coach> teamCoaches = team.getAssistantCoaches();
        teamCoaches.add(team.getHeadCoach());

        return teamCoaches.stream()
                .map(Coach::getUserId)
                .collect(Collectors.toList());
    }

    @GetMapping("/scoreboard")
    private @ResponseBody
    List<Map<String, Object>> getScoreboard(HttpServletRequest request, @RequestParam Integer week) {
        User currentUser = service.verifyToken(request);
        List<Map<String, Object>> finalResults = new ArrayList<>();
        if (currentUser != null) {
            List<Team> userTeams = currentUser.getUserTeams();
            // faster than the for loop (11.8 sec vs 9.5 sec) on localhost
            List<List<Map<String, Object>>> results = userTeams.parallelStream()
                    .map(team -> service.getUsersByTeamId(team.getTeamId()))
                    .map(j -> service.getRankedOrderOfMileageByTeammate(j, week))
                    .collect(Collectors.toList());
            for (int i = 0; i < results.size(); i++){
                Map<String, Object> team = new LinkedHashMap<>();
                team.put("teamName", userTeams.get(i).getName());
                team.put("users", results.get(i));
                finalResults.add(team);
            }
        }
        // should return [{MoundsView: [{User: mileage}, {User2: mileage},...]}, {WhiteBear: [{User: mileage}...]}]
        return finalResults;
    }

    @GetMapping("/getTeams")
    private List<Team> getAllTeams(HttpServletRequest request) {
        return service.getAllTeams();
    }

    private boolean verifyIsHeadCoach(Integer userId, Team selectedTeam) {
        return selectedTeam.getHeadCoach().getUserId().equals(userId);
    }

    @GetMapping("/getTeam")
    private Team getTeamById(@RequestParam Integer teamId) {
        return service.getTeamById(teamId);
    }

//    private void sendSMSupdate(Team selectedTeam, String title) {
//        List<User> allUsers = service.getUsersByTeamId(selectedTeam.getTeamId());
//
//        Twilio.init("AC809249a02601f42e1089d3ee0c14bf08", "14095311bed8dac54fd6c58b9fedb7d8");
//        for (int i = 0; i < allUsers.size(); i++) {
//            if (!allUsers.get(i).getPhoneNumber().isBlank()) {
//                Message message = Message.creator(
//                        new com.twilio.type.PhoneNumber("+1" + allUsers.get(i).getPhoneNumber()), // to
//                        new com.twilio.type.PhoneNumber("+14144228859"), // from
//                        "Your coach posted something new for " + selectedTeam.getName() + " on Fartlek.us! " + title + "...") // content
//                        .create();
//            }
//        }
//    }

    private void sendEmailUpdate(Team selectedTeam, Listing addedListing, User user) throws
            UnsupportedEncodingException, MessagingException {

        String FROM_NAME = "";
        List<Coach> teamCoaches = selectedTeam.getAssistantCoaches();
        teamCoaches.add(selectedTeam.getHeadCoach());

        List<User> allUsers = service.getUsersByTeamId(selectedTeam.getTeamId());
        List<Integer> userIds = allUsers.stream().map(User::getUserId).collect(Collectors.toList());

        for (Coach c : teamCoaches) {
            if (c.getUserId().equals(user.getUserId())) {
                FROM_NAME = c.getCoachName();
            }
            if (!userIds.contains(c.getUserId())){
                allUsers.add(c);
            }
        }

        final String FROM = "fartlek.updates@gmail.com";

        Address[] addresses = new Address[allUsers.size()];
        for (int i = 0; i < addresses.length; i++) {
            addresses[i] = new InternetAddress(allUsers.get(i).getEmail());
        }

        final String SMTP_USERNAME = "AKIAXY2G5OOW4L7I2WZU";
        final String SMTP_PASSWORD = "BHDjbAD+2lsQvmiF4ogvxAB1SUAZVhdY0rvnjMjg9YqQ";
        final String HOST = "email-smtp.us-east-2.amazonaws.com";
        final int PORT = 587;
        final String subject = addedListing.getTitle();
        final String htmlBody = addedListing.getContent();

        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM, FROM_NAME));
        msg.setRecipients(javax.mail.Message.RecipientType.TO, addresses);
        msg.setSubject(subject);

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(htmlBody, "text/html");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        for (ListingFile f : addedListing.getListingFiles()) {
            messageBodyPart = new MimeBodyPart();
            String filename = f.getTitle();
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);
        }
        msg.setContent(multipart);

        try (Transport transport = session.getTransport()) {
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
            transport.sendMessage(msg, msg.getAllRecipients());
            logger.info("Email sent!");
        } catch (Exception ex) {
            logger.error("Email failed to send. " + ex.getMessage());
        }
    }
}

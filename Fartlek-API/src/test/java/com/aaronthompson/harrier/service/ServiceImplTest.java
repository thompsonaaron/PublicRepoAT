/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.service;

import com.aaronthompson.harrier.entities.*;
import com.aaronthompson.harrier.dao.DaoException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 *
 * @author thomp
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ServiceImplTest {

    @Autowired
    JdbcTemplate template;

    @Autowired
    ServiceImpl instance;

    @BeforeEach
    public void setUp() {
        template.update("USE fartlekDBtest");
        template.update("DELETE FROM TeamResources");
        template.update("DELETE FROM ListingFiles");
        template.update("DELETE FROM Listings");
        template.update("DELETE FROM WorkoutPhotos");
        template.update("DELETE FROM Splits");
        template.update("DELETE FROM Workouts");
        template.update("DELETE FROM WorkoutTypes");
        template.update("DELETE FROM UserTeams");
        template.update("DELETE FROM TeamCoaches");
        template.update("DELETE FROM Teams");
        template.update("DELETE FROM UserRoles");
        template.update("DELETE FROM Users");

        template.update("INSERT INTO Users (userId, username, firstName, lastName, email, phoneNumber, city, state, country) VALUES\n"
                + "(1, 'user', 'Aaron', 'Thompson', 'test@gmail.com', '651-260-5171', 'Shoreview', 'MN', 'United States'),\n"
                + "(2, 'testUser', 'Michael', 'Scott', 'mscott@dundermifflin.com', '1-800-PAPERME', 'Scranton', 'NJ', 'United States')");

        template.update("INSERT INTO UserRoles (UserId, RoleId) VALUES \n"
                + "(1, 1),\n"
                + "(1, 2), \n"
                + "(1, 3)");

        template.update("INSERT INTO Teams (teamId, teamPrivacyId, `name`, address, city, state, zipcode) VALUES\n"
                + "(1, 1, 'Mounds View CC', '1900 Lake Valentine Rd.', 'Arden Hills', 'MN', '55112'),\n"
                + "(2, 2, 'Tinman Elite', null, 'Boulder', 'CO', null)");

        template.update("INSERT INTO TeamCoaches (teamId, userId, coachName, coachRoleId) VALUES\n"
                + "(2, 1, 'Tinman', 1)");

        template.update("INSERT INTO UserTeams (teamId, userId) VALUES\n"
                + "(1, 1),\n"
                + "(1, 2),\n"
                + "(2, 1);");

        template.update("INSERT INTO WorkoutTypes (typeId, `name`) VALUES \n"
                + "(1, 'Running'),\n"
                + "(2, 'Cycling'),\n"
                + "(3, 'Swimming'),\n"
                + "(4, 'Aqua Jogging'),\n"
                + "(5, 'Nordic Skiing')");

        template.update("INSERT INTO Workouts (workoutId, userId, typeId, `date`, timeOfDayId, location, distance, duration, averagePace, comments, coachingComments, cadence, averageHR) VALUES \n"
                + "(1, 1, 1, '2020-07-15', 1, 'Snail Lake Park', 5.63, 2110, '00:06:15', 'Felt good, nice and easy', null, null, 158),\n"
                + "(2, 1, 1, '2020-07-16', 2, 'Turtle Lake', 3.21, 1240, '00:06:26', 'Felt good, nice and easy', 'great job! solid run', 177, 155),\n"
                + "(3, 2, 1, '2020-07-17', 3, 'Scranton', 5.21, 2340, '00:07:30', 'Felt good, nice carboload beforehand', '', 169, 199)");

        template.update("INSERT INTO Splits (workoutId, `time`) VALUES \n"
                + "(1, '00:06:35'),\n"
                + "(1, '00:06:20'),\n"
                + "(1, '00:06:12'),\n"
                + "(1, '00:06:05'),\n"
                + "(1, '00:06:14')");

        template.update("INSERT INTO WorkoutPhotos (workoutId, `photo`) VALUES \n"
                + "(1, null)");

        template.update("INSERT INTO Listings (listingId, title, teamId, content, `dateTime`) VALUES \n"
                + "(1, 'Test Listing', 1, '<p> This is a test of the content area</p>', '2020-04-22 18:10:37'),\n"
                + "(2, 'Real Listing', 1, '<p> This would be the body of a real listing, I guess, although there isnt really a header</p>', '2020-04-22 10:30:15'),\n"
                + "(3, 'Fake Listing', 1, '<p> Dope! I''m gonna get you!</p>', '2020-04-22 18:10:37'),\n"
                + "(4, 'Mock Listing', 1, '<p> I like big butts and I cannot lie</p>', '2020-04-23 10:30:15'),\n"
                + "(5, 'Mocked Listing', 1, '<p> Hello world</p>', '2020-07-23 12:36:15'),\n"
                + "(6, 'Unreal Listing', 1, '<p> Meep Meep</p>', '2020-07-22 18:30:48')");

        template.update("INSERT INTO ListingFiles (listingFileId, listingId, filePath, title) VALUES\n"
                + "(1, 1, 'images/pot.jpg', 'An Image')");

        template.update("INSERT INTO TeamResources (url, title, teamId) VALUES \n"
                + "('https://TeamResourcesharrier-non-static-pages.s3.us-east-2.amazonaws.com/files/test-file-2.txt', 'test-file-2', '1')");
    }

    @Test
    public void testGetAllWorkoutsByWeek() {
        String ld = LocalDate.now().toString();

        template.update("INSERT INTO Workouts (workoutId, userId, typeId, timeOfDayId, `date`, location, distance, duration, averagePace, comments, coachingComments) VALUES \n"
                + "(4, 1, 1, 1," + "'" + ld + "'" + ", 'Snail Lake Park', 5.63, 2110, '00:06:15', 'Felt good, nice and easy', null),\n"
                + "(5, 1, 1, 2," + "'" + ld + "'" + ", 'Turtle Lake', 3.21, 1240, '00:06:26', 'Felt good, nice and easy', 'great job! solid run')");

        Integer userId = 1;
        Integer week = 0;
        List<Workout> allWorkoutsLastWeek = instance.getAllWorkoutsByWeek(userId, week);
        assertEquals(2, allWorkoutsLastWeek.size());
    }

    @Test
    public void testGetAllWorkoutsGP() {

        List<Workout> result = instance.getAllWorkouts();
        assertEquals(3, result.size());
        assertEquals(1, result.get(0).getUserId());
        assertEquals("Running", result.get(0).getWorkoutType().getWorkoutName());
        assertEquals("00:06:15", result.get(0).getAveragePace());
        assertEquals("Felt good, nice and easy", result.get(0).getComments());

        String woDate = "2020-07-15";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate workoutDate = LocalDate.parse(woDate, formatter);
        assertEquals(workoutDate, result.get(0).getWorkoutDate());

        assertEquals("Snail Lake Park", result.get(0).getLocation());
        assertEquals(5.63, result.get(0).getDistance());
        assertEquals(Duration.parse("PT35M10S"), result.get(0).getDuration());
        assertNull(result.get(0).getCoachingComments());
    }

    @Test
    public void testGetUserByIdGP() {

        Integer userId = 1;
        User result = instance.getUserById(userId);
        assertEquals(1, result.getUserId());
        assertEquals("user", result.getUsername());
        assertEquals("Aaron", result.getFirstName());
        assertEquals("Thompson", result.getLastName());
        assertEquals("test@gmail.com", result.getEmail());
        assertEquals("651-260-5171", result.getPhoneNumber());
        assertEquals("Shoreview", result.getCity());
        assertEquals("MN", result.getState());
        assertEquals("United States", result.getCountry());
    }

    @Test
    public void testGetUserByUsernameGP() {
        String username = "user";
        User result = instance.getUserByUsername(username);
        assertEquals(1, result.getUserId());
        assertEquals("user", result.getUsername());
        assertEquals("Aaron", result.getFirstName());
        assertEquals("Thompson", result.getLastName());
        assertEquals("test@gmail.com", result.getEmail());
        assertEquals("651-260-5171", result.getPhoneNumber());
        assertEquals("Shoreview", result.getCity());
        assertEquals("MN", result.getState());
        assertEquals("United States", result.getCountry());
    }

    @Test
    public void testGetAllWorkoutsByUserIdGP() {
        // currently this is the same as getAllWorkouts b/c user #2 doesn't have any workouts
        // note this sorts by date
        Integer userId = 1;
        List<Workout> result = instance.getAllWorkoutsByUserId(userId);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getUserId());
        assertEquals("Running", result.get(1).getWorkoutType().getWorkoutName());
        assertEquals("00:06:15", result.get(1).getAveragePace());
        assertEquals("Felt good, nice and easy", result.get(1).getComments());

        String woDate = "2020-07-15";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate workoutDate = LocalDate.parse(woDate, formatter);
        assertEquals(workoutDate, result.get(1).getWorkoutDate());

        assertEquals("Snail Lake Park", result.get(1).getLocation());
        assertEquals(5.63, result.get(1).getDistance());
        assertEquals(Duration.parse("PT35M10S"), result.get(1).getDuration());
        assertNull(result.get(1).getCoachingComments());

    }

    @Test
    public void testFindWorkoutTypeByIdGP() {
        Integer workoutTypeId = 1;
        WorkoutType result = instance.findWorkoutTypeById(workoutTypeId);
        assertEquals(1, result.getWorkoutId());
        assertEquals("Running", result.getWorkoutName());
    }
//TRY THIS AGAIN WHEN i CAN SET THE WORKOUT TIME, OTHERWISE IT WORKS
//    @Test
//    public void testAddWorkoutGP() {
//
//        Workout w = new Workout();
//        w.setUserId(1);
//        w.setWorkoutId(3);
//
//        WorkoutType wt = instance.findWorkoutTypeById(1);
//        w.setWorkoutType(wt);
//
//        String woDate = "2020-07-15";
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate workoutDate = LocalDate.parse(woDate, formatter);
//        w.setWorkoutDate(workoutDate);
//        
//        WorkoutTime wtime = new WorkoutTime();
//        wtime.setTimeOfDayId(1);
//        wtime.setName("Morning");
//        w.setWorkoutTime(wt);
//
//        w.setLocation("Home");
//        w.setDistance(8.64);
////        PT2H5M29S
//        w.setDuration(Duration.parse("PT1H2M30S"));
//        w.setAveragePace("7:14");
//        w.setComments("Cruised!!! Felt awesome");
//        w.setCoachingComments("Nice steady pace. Keep up the good work!");
//
//        List<Split> wSplits = new ArrayList<>();
//        Split oneMile = new Split();
//        oneMile.setSplitId(1);
//        oneMile.setWorkoutId(3);
//        oneMile.setSplitTime("00:07:35");
//
//        Split twoMile = new Split();
//        twoMile.setSplitId(2);
//        twoMile.setWorkoutId(3);
//        twoMile.setSplitTime("00:07:20");
//
//        Split threeMile = new Split();
//        threeMile.setSplitId(3);
//        threeMile.setWorkoutId(3);
//        threeMile.setSplitTime("00:07:11");
//
//        Split fourMile = new Split();
//        fourMile.setSplitId(4);
//        fourMile.setWorkoutId(3);
//        fourMile.setSplitTime("00:07:02");
//        wSplits.add(oneMile);
//        wSplits.add(twoMile);
//        wSplits.add(threeMile);
//        wSplits.add(fourMile);
//        w.setSplits(wSplits);
//
//        w.setAverageHR(0);
//        w.setCadence(180);
//
//        Workout result = instance.addWorkout(w);
//        assertEquals(w, result);
//    }

    @Test
    public void testGetTeamByIdGP() {
        Integer teamId = 1;
        Team result = instance.getTeamById(teamId);
        assertEquals("Mounds View CC", result.getName());
        assertEquals("1900 Lake Valentine Rd.", result.getAddress());
        assertEquals("Arden Hills", result.getCity());
        assertEquals("MN", result.getState());
        assertEquals("55112", result.getZipcode());
        assertEquals(1, result.getTeamPrivacyId());
    }

    @Test
    public void testGetUsersByTeamIdGP() {
        Integer teamId = 1;
        List<User> result = instance.getUsersByTeamId(teamId);
        assertEquals(2, result.size());
        assertEquals("user", result.get(0).getUsername());
        assertEquals("testUser", result.get(1).getUsername());
    }

    @Test
    public void testGetUsersByTeamIdNullUserOnTeam() throws DaoException {
        Integer teamId = 2;
        template.update("INSERT INTO Users (userId, username, email, phoneNumber, city, state, country) VALUES\n"
                + "(100, 'testMe', 'test@gmail.com', '651-260-5171', 'Shoreview', 'MN', 'United States')");

        instance.addUserToTeam(teamId, 100);
        List<User> result = instance.getUsersByTeamId(teamId); // should not include the unsortable user just added w/o first name
        assertEquals(1, result.size());
        assertEquals("user", result.get(0).getUsername());
    }

    @Test
    public void testRemoveUserFromTeamGP() {
        Integer userId = 2;
        Integer teamId = 1;
        instance.removeUserFromTeam(userId, teamId);
        List<User> teamUsers = instance.getUsersByTeamId(teamId);
        assertEquals(1, teamUsers.size());
        assertNotEquals("Michael", teamUsers.get(0).getFirstName());
    }

    @Test
    public void testAddUserToTeamGP() throws Exception {
        Integer teamId = 2;
        Integer userId = 2;
        instance.addUserToTeam(teamId, userId);
        List<User> teamUsers = instance.getUsersByTeamId(teamId);
        assertEquals(2, teamUsers.size());
        assertEquals("Aaron", teamUsers.get(0).getFirstName());
        assertEquals("Michael", teamUsers.get(1).getFirstName());
    }
//
//    @Test
//    public void testGetAllUsers() {
//        System.out.println("getAllUsers");
//        List<User> expResult = null;
//        List<User> result = instance.getAllUsers();
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testEditAccount() throws Exception {
//        System.out.println("editAccount");
//        User editedUser = null;
//        instance.editAccount(editedUser);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetUserByEmail() {
//        System.out.println("getUserByEmail");
//        String responseEmail = "";
//        User expResult = null;
//        User result = instance.getUserByEmail(responseEmail);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }

    @Test
    public void testGetRankedOrderOfMileageByTeammate() throws DaoException, JsonProcessingException {
        List<User> teammates = instance.getUsersByTeamId(1);
        List<Map<String, Object>> returnedList = instance.getRankedOrderOfMileageByTeammate(teammates, 1);
        assertEquals(2, teammates.size());
        assertEquals(2, returnedList.size());
        assertEquals(2, returnedList.get(1).size());
        User aa = instance.getUserById(1);
        User ms = instance.getUserById(2);

//        assertTrue(returnedMap.get(aa) >= returnedMap.get(ms));
    }

    @Test
    public void testGetRankedOrderOfMileageByTeammateNull() {
        List<User> teammates = instance.getUsersByTeamId(1);
        List<Map<String, Object>> returnedMap = instance.getRankedOrderOfMileageByTeammate(null, 1);
        assertEquals(0, returnedMap.size());
    }

    @Test
    public void getMinAndMaxListingId() {
        Integer teamId = 1;
        List<Integer> minAndMax = instance.getMinAndMaxListingId(teamId);
        assertEquals(1, minAndMax.get(0));
        assertEquals(6, minAndMax.get(1));
    }

    // listingDao listingDao listingDao listingDao listingDao listingDao listingDao 
    @Test
    public void testGetListingByIdGP() throws DaoException {
        Integer listingId = 1;
        Listing result = instance.getListingById(listingId);
        assertEquals("Test Listing", result.getTitle());
        assertEquals(1, result.getTeamId());
        assertEquals("<p> This is a test of the content area</p>", result.getContent());

        String listDate = "2020-04-22 18:10:37";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime listingDate = LocalDateTime.parse(listDate, formatter);
        assertEquals(listingDate, result.getDateTime());

        //"(2, 'Real Listing', 1, '<p> This would be the body of a real listing, I guess, although there isnt really a header</p>', '2020-04-22 10:30:15')");
        Integer listingId2 = 2;
        Listing result2 = instance.getListingById(listingId2);
        assertEquals("Real Listing", result2.getTitle());
        String content = "<p> This would be the body of a real listing, I guess, although there isnt really a header</p>";
        assertEquals(content, result2.getContent());
        assertEquals(1, result2.getTeamId());
        String listDate2 = "2020-04-22 10:30:15";
        LocalDateTime listingDate2 = LocalDateTime.parse(listDate2, formatter);
//        assertEquals(listingDate2, result.getDateTime());
// Should I configure this to not just convert on the front end?

    }

    @Test
    public void testGetListingByIdNull() {
        Integer listingId = 0;

        try {
            Listing result = instance.getListingById(listingId);
            fail("should have thrown DaoException in testGetAllListingsByTeamIdNull");
        } catch (DaoException ex) {
            // success
        }
    }

    @Test
    public void testEditListingGP() throws Exception {
        Listing listing = instance.getListingById(1);
        listing.setTitle("This title changed!");
        listing.setContent("<p>Hopefully this changed too!</p>");

        String listDate = "2020-06-15 12:02:24";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime listingDate = LocalDateTime.parse(listDate, formatter);
        listing.setDateTime(listingDate);

        ListingFile lp = new ListingFile();
        lp.setFilePath("C://somewhere");
        lp.setTitle("The title of the file");
        lp.setListingFileId(5);
        lp.setListingId(1);
        List<ListingFile> listingFiles = new ArrayList<>();
        listingFiles.add(lp);
        listing.setListingFiles(listingFiles);
        instance.editListing(listing);

        Listing editedListing = instance.getListingById(1);
        assertEquals("This title changed!", editedListing.getTitle());
        assertEquals("<p>Hopefully this changed too!</p>", editedListing.getContent());
//        assertEquals(listingDate, editedListing.getDateTime()); listingDates are messed up because the server operates on a different timezone than the local test database
        assertFalse(editedListing.getIsDeleted());
    }

    @Test
    public void testEditListingNull() throws Exception {
        Listing listing = null;
        try {
            instance.editListing(listing);
            fail("should have thrown DaoException in testEditListingNull");
        } catch (DaoException ex) {
            //success
        }
    }

    @Test
    public void testEditListingIncompleteListing() throws Exception {
        Listing listing = instance.getListingById(1);
        listing.setTitle(null); // no title
        listing.setContent("<p>Hopefully this changed too!</p>");

        String listDate = "2020-06-15 12:02:24";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime listingDate = LocalDateTime.parse(listDate, formatter);
        listing.setDateTime(listingDate);

        ListingFile lp = new ListingFile();
        lp.setFilePath("C://somewhere");
        lp.setTitle("The title of the file");
        lp.setListingFileId(5);
        lp.setListingId(1);
        List<ListingFile> listingFiles = new ArrayList<>();
        listingFiles.add(lp);
        listing.setListingFiles(listingFiles);
        try {
            instance.editListing(listing);
            fail("should have thrown DaoException in testEditListingIncompleteListing");
        } catch (DaoException ex) {

        }
    }

    @Test
    public void testEditListingEmptyListingFiles() throws Exception {
        Listing listing = instance.getListingById(1);
        listing.setContent("<p>Hopefully this changed too!</p>");

        String listDate = "2020-06-15 12:02:24";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime listingDate = LocalDateTime.parse(listDate, formatter);
        listing.setDateTime(listingDate);

        ListingFile lp = new ListingFile();
        List<ListingFile> listingFiles = new ArrayList<>();
        listingFiles.add(lp);
        listing.setListingFiles(listingFiles);
        try {
            instance.editListing(listing);
            fail("should have thrown DaoException in testEditListingIncompleteListing");
        } catch (DaoException ex) {

        }
    }

    @Test
    public void testEditListingNullListingFiles() throws Exception {
        Listing listing = instance.getListingById(1);
        listing.setContent("<p>Hopefully this changed too!</p>");

        String listDate = "2020-06-15 12:02:24";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime listingDate = LocalDateTime.parse(listDate, formatter);
        listing.setDateTime(listingDate);

        ListingFile lp = null;
        List<ListingFile> listingFiles = new ArrayList<>();
        listingFiles.add(lp);
        listing.setListingFiles(listingFiles);
        try {
            instance.editListing(listing);
            fail("should have thrown DaoException in testEditListingIncompleteListing");
        } catch (DaoException ex) {

        }
    }

    @Test
    public void testAddListingGP() throws Exception {
        Listing li = new Listing();
        li.setTeamId(1);
        li.setTitle("This is how we do!");

        String listDate = "2020-06-15 14:55:37";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime listingDate = LocalDateTime.parse(listDate, formatter);
        li.setDateTime(listingDate);

        li.setContent("<p> This is how we do barefoot running... <strong>right!</strong></p>");
        li.setListingFiles(new ArrayList<>());

        String filePath = "listingFiles/hey this is a file" + UUID.randomUUID().toString();
        ListingFile lf = new ListingFile();
        lf.setFilePath("https://harrier-non-static-pages.s3.us-east-2.amazonaws.com/" + filePath);
        lf.setTitle("The title of the file");
        lf.setListingId(li.getListingId());
        List<ListingFile> listingFiles = new ArrayList<>();
        listingFiles.add(lf);
        li.setListingFiles(listingFiles);
        Listing result = instance.addListing(li);

        // verify what came out is what went ins
        assertEquals(1, result.getTeamId());
        assertEquals("This is how we do!", result.getTitle());
        assertEquals(listingDate, result.getDateTime());
        assertEquals("<p> This is how we do barefoot running... <strong>right!</strong></p>", result.getContent());

        List<Listing> allTeamListings = instance.getAllListingsByTeamId(1);
        assertEquals(result.getListingId(), allTeamListings.get(0).getListingId());
        assertEquals(1, result.getListingFiles().size());
        assertEquals("The title of the file", result.getListingFiles().get(0).getTitle());
        assertTrue(result.getListingFiles().get(0).getFilePath().startsWith("https://harrier-non-static-pages.s3.us-east-2.amazonaws.com/listingFiles/hey this is a file"));
    }

    @Test
    public void testAddListingIncompleteListing() throws Exception {
        Listing li = new Listing();
        li.setTeamId(1);
        li.setTitle("This is how we do!");
        //no date

        li.setContent("<p> This is how we do barefoot running... <strong>right!</strong></p>");
        li.setListingFiles(new ArrayList<>());
        try {
            Listing result = instance.addListing(li);
            fail("should have thrown Dao Exception in testAddListingIncompleteListing");
        } catch (DaoException ex) {
            // success
        }
    }

    @Test
    public void testAddListingNullListing() throws Exception {
        Listing li = null;

        try {
            Listing result = instance.addListing(li);
            fail("should have thrown Dao Exception in testAddListingNullListing");
        } catch (DaoException ex) {
            // success
        }
    }

    @Test
    public void getPaginatedListingsByTeamId() {
        Integer teamId = 1;
        Integer lastListingId = 7;

        List<Listing> paginatedListings = instance.getPaginatedListingsByTeamId(teamId, lastListingId);
        assertEquals(5, paginatedListings.size());

        Listing result = paginatedListings.get(0);
        assertEquals("Unreal Listing", result.getTitle());
        assertEquals(1, result.getTeamId());
        assertEquals("<p> Meep Meep</p>", result.getContent());

        String listDate = "2020-07-22 18:30:48";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime listingDate = LocalDateTime.parse(listDate, formatter);
        assertEquals(listingDate, result.getDateTime());

        //"(2, 'Real Listing', 1, '<p> This would be the body of a real listing, I guess, although there isnt really a header</p>', '2020-04-22 10:30:15')");
        Listing result2 = paginatedListings.get(4);
        assertEquals(2, paginatedListings.get(4).getListingId()); // last listing should be id#2
        assertEquals("Real Listing", result2.getTitle());
        String content = "<p> This would be the body of a real listing, I guess, although there isnt really a header</p>";
        assertEquals(content, result2.getContent());
        assertEquals(1, result2.getTeamId());
        String listDate2 = "2020-04-22 10:30:15";
        LocalDateTime listingDate2 = LocalDateTime.parse(listDate2, formatter);
    }

    @Test
    public void deleteListingByIdGP() throws Exception {
        Integer listingId = 1;
        instance.deleteListingById(listingId);
        Listing deletedListing = instance.getListingById(listingId);
        assertTrue(deletedListing.getIsDeleted());
    }

    @Test
    public void deleteListingByIdZeroValue() throws Exception {
        Integer listingId = 0;
        instance.deleteListingById(listingId); // as long as it doesn't throw an error, this is a success
    }

    @Test
    public void getListingFileById() {
        Integer listingFileId = 1;
        ListingFile lf = instance.getListingFileById(listingFileId);
        assertEquals("An Image", lf.getTitle());
        assertEquals("images/pot.jpg", lf.getFilePath());
        assertEquals(1, lf.getListingFileId());
        assertEquals(1, lf.getListingId());
    }

    @Test
    public void deleteListingFileByIdGP() throws DaoException {
        Integer listingFileId = 1;
        instance.deleteListingFileById(listingFileId);
        assertEquals(0, instance.getListingById(1).getListingFiles().size());
    }

    @Test
    public void deleteListingFileByIdZero() throws DaoException {
        Integer listingFileId = 0;
        instance.deleteListingFileById(listingFileId);
    }
}

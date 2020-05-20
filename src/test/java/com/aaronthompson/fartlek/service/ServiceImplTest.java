/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.fartlek.service;

import com.aaronthompson.fartlek.FartlekApplicationTests;
import com.aaronthompson.fartlek.dao.ListingDao;
import com.aaronthompson.fartlek.dao.PhotoDao;
import com.aaronthompson.fartlek.dao.RoleDao;
import com.aaronthompson.fartlek.dao.RoleDaoDB.RoleMapper;
import com.aaronthompson.fartlek.dao.SplitDao;
import com.aaronthompson.fartlek.dao.TeamDao;
import com.aaronthompson.fartlek.dao.TeamDaoDB.TeamMapper;
import com.aaronthompson.fartlek.dao.UserDao;
import com.aaronthompson.fartlek.dao.UserDaoDB.PrivacyMapper;
import com.aaronthompson.fartlek.dao.WorkoutDao;
import com.aaronthompson.fartlek.entities.Listing;
import com.aaronthompson.fartlek.entities.Role;
import com.aaronthompson.fartlek.entities.Team;
import com.aaronthompson.fartlek.entities.User;
import com.aaronthompson.fartlek.entities.Workout;
import com.aaronthompson.fartlek.entities.WorkoutType;
import com.aaronthompson.fartlek.entities.Privacy;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 *
 * @author thomp
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FartlekApplicationTests.class)
public class ServiceImplTest {

    TeamDao teamDao;
    PhotoDao photo;
    RoleDao role;
    SplitDao split;
    UserDao userDao;
    WorkoutDao workout;
    ListingDao listingDao;

    @Autowired
    JdbcTemplate template;

    @Autowired
    ServiceImpl service = new ServiceImpl(teamDao, photo, role, split, userDao, workout, listingDao);

//    @BeforeAll
//    public static void setUpClass() {
//    }
//    @AfterAll
//    public static void tearDownClass() {
//    }
    @BeforeEach
    public void setUp() {
        template.update("DELETE FROM listingPhotos");
        template.update("DELETE FROM listings");
        template.update("DELETE FROM workoutPhotos");
        template.update("DELETE FROM splits");
        template.update("DELETE FROM Workouts");
        template.update("DELETE FROM WorkoutTypes");
        template.update("DELETE FROM UserTeams");
        template.update("DELETE FROM teamCoaches");
        template.update("DELETE FROM Teams");
        template.update("DELETE FROM UserRoles");
        template.update("DELETE FROM Users");
        template.update("DELETE FROM privacy");
        template.update("DELETE FROM Roles");

        template.update("INSERT INTO Roles (roleId, name) VALUES (1, 'ROLE_USER'),(2, 'ROLE_COACH'),(3, 'ROLE_ADMIN')");
        List<Role> allRoles = template.query("SELECT * FROM Roles", new RoleMapper());

        template.update("INSERT INTO privacy (privacyId, `name`) VALUES (1, 'public'),(2, 'private')");
        List<Privacy> allPrivacies = template.query("SELECT * FROM Privacy", new PrivacyMapper());

        template.update("INSERT INTO USERS (userId, privacyId, username, `password`, firstName, lastName, email, phoneNumber, city, state, country) VALUES\n"
                + "(1, 1, 'athompson', '$2a$10$6/krDDN8P/i/CANT5Kn2pO6FSkczr7QbNpaaz/wnMYUpFoy/.vy9C', 'Aaron', 'Thompson', 'test@gmail.com', '651-260-5171', 'Shoreview', 'Minnesota', 'United States')");
        // this won't include user Teams and user Roles yet (data not inserted until below)
        List<User> allUsers = service.getAllUsers();

        template.update("INSERT INTO UserRoles (UserId, RoleId) VALUES "
                + "(1, 1),"
                + "(1, 2),"
                + "(1, 3)");

        template.update("INSERT INTO Teams (teamId, `name`, address, city, state, zipcode) VALUES\n"
                + "(1, 'Mounds View CC', '1900 Lake Valentine Rd.', 'Arden Hills', 'Minnesota', '55112'),\n"
                + "(2, 'Tinman Elite', null, 'Boulder', 'Colorado', null)");
        List<Team> allTeams = template.query("SELECT * FROM teams", new TeamMapper());

        template.update("INSERT INTO teamCoaches (teamId, userId, coachName) VALUES (1, 1, 'Tinman')");

        template.update("INSERT INTO UserTeams (teamId, userId) VALUES "
                + "(1, 1),"
                + "(2, 1)");

        template.update("INSERT INTO WorkoutTypes (typeId, `name`) VALUES \n"
                + "(1, 'Running'),\n"
                + "(2, 'Cycling'),\n"
                + "(3, 'Swimming'),\n"
                + "(4, 'Aqua Jogging'),\n"
                + "(5, 'Nordic Skiing');");

        template.update("INSERT INTO Workouts (workoutId, userId, typeId, `datetime`, location, distance, duration, averagePace, comments, coachingComments) VALUES \n"
                + "(1, 1, 1, '2020-04-01 10:30:15', 'Snail Lake Park', 5.63, 2110, '00:06:15', 'Felt good, nice and easy', null),\n"
                + "(2, 1, 1, '2020-04-02 16:01:57', 'Turtle Lake', 3.21, 1240, '00:06:26', 'Felt good, nice and easy', 'great job! solid run');");
        template.update("INSERT INTO splits (workoutId, `time`) VALUES \n"
                + "(1, '00:06:35'),\n"
                + "(1, '00:06:20'),\n"
                + "(1, '00:06:12'),\n"
                + "(1, '00:06:05'),\n"
                + "(1, '00:06:14');");
        template.update("INSERT INTO workoutPhotos (workoutId, `photo`) VALUES (1, null);");
        template.update("INSERT INTO listings (listingId, title, teamId, content, `date`, userId) VALUES \n"
                + "(1, 'Test Listing', 1, '<p> This is a test of the content area</p>', '2020-04-22', 1),\n"
                + "(2, 'Real Listing', 1, '<p> This would be the body of a real listing, I guess, although there isnt really a header</p>', '2020-04-19', 1);");
        template.update("INSERT INTO listingPhotos (listingPhotoId, listingId, filePath) VALUES (1, 1, 'images/pot.jpg');");
    }

//    @AfterEach
//    public void tearDown() {
//    }
    @Test
    public void testLoadUserByUsernameGP() {

        User loadedUser = service.getUserByUsername("athompson");

        assertEquals(1, loadedUser.getUserId());
        assertEquals(1, loadedUser.getPrivacyId());
        assertEquals("Aaron", loadedUser.getFirstName());
        assertEquals("Thompson", loadedUser.getLastName());
        assertEquals("test@gmail.com", loadedUser.getEmail());
        assertEquals("651-260-5171", loadedUser.getPhoneNumber());
        assertEquals("Shoreview", loadedUser.getCity());
        assertEquals("Minnesota", loadedUser.getState());
        assertEquals("United States", loadedUser.getCountry());
        assertEquals(2, loadedUser.getUserTeams().size());
        assertEquals(3, loadedUser.getUserRoles().size());
    }

    @Test
    public void testGetUserByIdGP() {
        User loadedUser = service.getUserById(1);

        assertEquals(1, loadedUser.getUserId());
        assertEquals(1, loadedUser.getPrivacyId());
        assertEquals("Aaron", loadedUser.getFirstName());
        assertEquals("Thompson", loadedUser.getLastName());
        assertEquals("test@gmail.com", loadedUser.getEmail());
        assertEquals("651-260-5171", loadedUser.getPhoneNumber());
        assertEquals("Shoreview", loadedUser.getCity());
        assertEquals("Minnesota", loadedUser.getState());
        assertEquals("United States", loadedUser.getCountry());
        assertEquals(2, loadedUser.getUserTeams().size());
        assertEquals(3, loadedUser.getUserRoles().size());
    }

    @Test
    public void testGetUserByUsernameGP() {
        User loadedUser = service.getUserByUsername("athompson");

        assertEquals(1, loadedUser.getUserId());
        assertEquals(1, loadedUser.getPrivacyId());
        assertEquals("Aaron", loadedUser.getFirstName());
        assertEquals("Thompson", loadedUser.getLastName());
        assertEquals("test@gmail.com", loadedUser.getEmail());
        assertEquals("651-260-5171", loadedUser.getPhoneNumber());
        assertEquals("Shoreview", loadedUser.getCity());
        assertEquals("Minnesota", loadedUser.getState());
        assertEquals("United States", loadedUser.getCountry());
        assertEquals(2, loadedUser.getUserTeams().size());
        assertEquals(3, loadedUser.getUserRoles().size());
    }

    @Test
    public void testGetAllWorkouts() {
        List<Workout> allWorkouts = service.getAllWorkouts();
        
        assertEquals(2, allWorkouts.size());
    }

    @Test
    public void testCreateAccount() throws Exception {
        
        User toAdd = new User();
        toAdd.setUsername("mscott");
        toAdd.setFirstName("Michael");
        toAdd.setLastName("Scott");
        toAdd.setEmail("mscott@gmail.com");
        toAdd.setPhoneNumber("123-456-7899");
        toAdd.setPrivacyId(1);
        toAdd.setCity("Scranton");
        toAdd.setState("New Jersey");
        toAdd.setCountry("United States");
        toAdd.setPassword("password");
        
        User addedUser = service.createAccount(toAdd);
        
//        Need to set these too:
//        addedUser.setUserTeams(userTeams);
//        addedUser.setUserRoles(userRoles);

        assertEquals("mscott", addedUser.getUsername());
        assertEquals("Michael", addedUser.getFirstName());
        assertEquals("Scott", addedUser.getLastName());
        assertEquals("mscott@gmail.com", addedUser.getEmail());
        assertEquals("123-456-7899", addedUser.getPhoneNumber());
        assertEquals("Scranton", addedUser.getCity());
        assertEquals("New Jersey", addedUser.getState());
        assertEquals("United States", addedUser.getCountry());
    }
//
//    @Test
//    public void testGetAllWorkoutsByUserId() {
//        System.out.println("getAllWorkoutsByUserId");
//        Integer userId = null;
//        ServiceImpl instance = null;
//        List<Workout> expResult = null;
//        List<Workout> result = instance.getAllWorkoutsByUserId(userId);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testFindWorkoutTypeById() {
//        System.out.println("findWorkoutTypeById");
//        Integer workoutTypeId = null;
//        ServiceImpl instance = null;
//        WorkoutType expResult = null;
//        WorkoutType result = instance.findWorkoutTypeById(workoutTypeId);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testAddWorkout() {
//        System.out.println("addWorkout");
//        Workout w = null;
//        ServiceImpl instance = null;
//        Workout expResult = null;
//        Workout result = instance.addWorkout(w);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetTeamById() {
//        System.out.println("getTeamById");
//        Integer teamId = null;
//        ServiceImpl instance = null;
//        Team expResult = null;
//        Team result = instance.getTeamById(teamId);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetUsersByTeamId() {
//        System.out.println("getUsersByTeamId");
//        Integer teamId = null;
//        ServiceImpl instance = null;
//        List<User> expResult = null;
//        List<User> result = instance.getUsersByTeamId(teamId);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetAllListingsByTeamId() {
//        System.out.println("getAllListingsByTeamId");
//        Integer teamId = null;
//        ServiceImpl instance = null;
//        List<Listing> expResult = null;
//        List<Listing> result = instance.getAllListingsByTeamId(teamId);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testAddListing() throws Exception {
//        System.out.println("addListing");
//        Listing li = null;
//        ServiceImpl instance = null;
//        Listing expResult = null;
//        Listing result = instance.addListing(li);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetAllTeamsByUserId() {
//        System.out.println("getAllTeamsByUserId");
//        Integer userId = null;
//        ServiceImpl instance = null;
//        List<Team> expResult = null;
//        List<Team> result = instance.getAllTeamsByUserId(userId);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testRemoveUserFromTeam() {
//        System.out.println("removeUserFromTeam");
//        Integer userId = null;
//        Integer teamId = null;
//        ServiceImpl instance = null;
//        instance.removeUserFromTeam(userId, teamId);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testAddUserToTeam() throws Exception {
//        System.out.println("addUserToTeam");
//        Integer teamId = null;
//        Integer userId = null;
//        ServiceImpl instance = null;
//        User expResult = null;
//        User result = instance.addUserToTeam(teamId, userId);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetListingById() {
//        System.out.println("getListingById");
//        Integer listingId = null;
//        ServiceImpl instance = null;
//        Listing expResult = null;
//        Listing result = instance.getListingById(listingId);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testEditListing() throws Exception {
//        System.out.println("editListing");
//        Listing listing = null;
//        ServiceImpl instance = null;
//        instance.editListing(listing);
//        fail("The test case is a prototype.");
//    }

}

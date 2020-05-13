/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.daos;

import com.sg.ChrisList.models.Role;
import com.sg.ChrisList.models.User;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.sg.ChrisList.ChrisListApplicationTests;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author thomp
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ChrisListApplicationTests.class)
public class UserDaoDBTest {

    @Autowired
    UserDao userDao = new UserDaoDB();

    @Autowired
    RoleDao role;

    @Autowired
    JdbcTemplate template;

    @BeforeEach
    public void setUp() {
        template.update("DELETE FROM user_role");
        template.update("DELETE FROM listingkeywords");
        template.update("DELETE FROM listings");
        template.update("DELETE FROM user");

        template.update("INSERT into `user` (`username`,`password`,`enabled`, firstName, lastName, email, phoneNumber) VALUES ('oldSocks24', 'password', 1, 'Phil', 'Philipson', 'phil@phil.com', '(320)234-2342')");
        template.update("INSERT into `user` (`username`,`password`,`enabled`, firstName, lastName, email, phoneNumber) VALUES ('LampLamp', 'password', 1, 'Dalton', 'Galloway', 'daltong@gmail.com', '(320)234-23412')");
        template.update("INSERT into `user` (`username`,`password`,`enabled`, firstName, lastName, email, phoneNumber) VALUES ('AnimeIsCool', 'password', 1, 'Patricia', 'Patterson', 'PattyP@yahoo.com', '(320)234-2340')");

        template.update("insert into `user_role` (`user_id`, `role_id`) VALUES "
                + "(" + userDao.getAllUsers().get(0).getId() + "," + role.getAllRoles().get(0).getId() + "),"
                + "(" + userDao.getAllUsers().get(0).getId() + "," + role.getAllRoles().get(1).getId() + "),"
                + "(" + userDao.getAllUsers().get(1).getId() + "," + role.getAllRoles().get(1).getId() + ")"
        );
    }

    @After
    public void tearDown() {
    }

    @Test
    public void createUserGoldenPathTest() throws DaoException {
//        username, password, firstName, lastName, email, phoneNumber, enabled, roles

        User toAdd = new User();

        toAdd.setUsername("BestBoss");
        toAdd.setEnabled(true);
        toAdd.setPassword("DunderMifflin");
        toAdd.setFirstName("Michael");
        toAdd.setLastName("Scott");
        toAdd.setEmail("michael@dunder.com");
        toAdd.setPhoneNumber("555-5555");

        // create two roles and add to allRoles, which will go into the toAdd user
        List<Role> allRoles = role.getAllRoles();
        Set<Role> allRoles2 = new HashSet<>(); 
        allRoles2.addAll(allRoles);
        toAdd.setRoles(allRoles2);

        // create user in database
        userDao.createUser(toAdd);

//        //retrieve recently added user for testing
//        User addedUser = userDao.getUserById(3);
        User addedUser = userDao.getUserByUsername("BestBoss");

//        assertEquals("BestBoss", addedUser.getUsername());
        assertEquals("DunderMifflin", addedUser.getPassword());
        assertEquals("Michael", addedUser.getFirstName());
        assertEquals("Scott", addedUser.getLastName());
        assertEquals("michael@dunder.com", addedUser.getEmail());
        assertEquals("555-5555", addedUser.getPhoneNumber());
        assertEquals(2, addedUser.getRoles().size());
        // should check each object in the roles set, but not sure how to get each item out of a set
    }

    @Test
    public void createUserNullUserTest() {
        User toAdd = new User();

        Set<Role> allRoles = new HashSet<>();
        toAdd.setRoles(allRoles);

        try {
            userDao.createUser(toAdd);
            fail("should have thrown DAO exception");
        } catch (DaoException ex) {
            // success
        }
    }

    @Test
    public void getAllUsersGoldenPathTest() {

        List<User> allUsers = userDao.getAllUsers();

        assertEquals(3, allUsers.size());
//        assertEquals(1, allUsers.get(0).getId());
        assertEquals("oldSocks24", allUsers.get(0).getUsername());
        assertEquals("Phil", allUsers.get(0).getFirstName());
        assertEquals("Philipson", allUsers.get(0).getLastName());
        assertEquals("phil@phil.com", allUsers.get(0).getEmail());
        assertEquals("(320)234-2342", allUsers.get(0).getPhoneNumber());

//        assertEquals(2, allUsers.get(2).getId());
        assertEquals("AnimeIsCool", allUsers.get(2).getUsername());
        assertEquals("Patricia", allUsers.get(2).getFirstName());
        assertEquals("Patterson", allUsers.get(2).getLastName());
        assertEquals("PattyP@yahoo.com", allUsers.get(2).getEmail());
        assertEquals("(320)234-2340", allUsers.get(2).getPhoneNumber());
    }

//    @Test
//    public void getUserByIdGoldenPathTest() {
//        int userId = 1;
//
//        User foundUser = userDao.getUserById(userId);
//
////        assertEquals(1, foundUser.getId());
//        assertEquals("admin", foundUser.getUsername());
//        assertEquals("Mike", foundUser.getFirstName());
//        assertEquals("Wazowski", foundUser.getLastName());
//        assertEquals("user@me.com", foundUser.getEmail());
//        assertEquals("555-5555", foundUser.getPhoneNumber());
//    }
//    @Test
//    public void getUserByIdInvalidIdTest() {
//        int userId = 0;
//        User foundUser = userDao.getUserById(userId);
//        assertNull(foundUser);
//    }
    @Test
    public void getUserByUsernameGoldenPathTest() {
        String username = "oldSocks24";

        User foundUser = userDao.getUserByUsername(username);

//        assertEquals(1, foundUser.getId());
        assertEquals("oldSocks24", foundUser.getUsername());
        assertEquals("Phil", foundUser.getFirstName());
        assertEquals("Philipson", foundUser.getLastName());
        assertEquals("phil@phil.com", foundUser.getEmail());
        assertEquals("(320)234-2342", foundUser.getPhoneNumber());
    }

    @Test
    public void getUserByUsernameInvalidUsernameTest() {
        String username = "hello";

        User foundUser = userDao.getUserByUsername(username);
        assertNull(foundUser);
    }

    @Test
    public void updateUserGoldenPathTest() throws DaoException {
        User originalUser = userDao.getUserByUsername("AnimeIsCool");

        User editedUser = new User();
        editedUser.setId(originalUser.getId());
        editedUser.setUsername(originalUser.getUsername());
        editedUser.setFirstName(originalUser.getFirstName());
        editedUser.setLastName("Test");
        editedUser.setPassword(originalUser.getPassword());
        editedUser.setEmail(originalUser.getEmail());
        editedUser.setPhoneNumber("111-1111");
        editedUser.setRoles(originalUser.getRoles());

        userDao.updateUser(editedUser);
//        ('AnimeIsCool', 'password', 1, 'Patricia', 'Patterson', 'PattyP@yahoo.com', '(320)-234-2340')"
        assertEquals("AnimeIsCool", editedUser.getUsername());
        assertEquals("Patricia", editedUser.getFirstName());
        assertEquals("Test", editedUser.getLastName());
        assertEquals("PattyP@yahoo.com", editedUser.getEmail());
        assertEquals("111-1111", editedUser.getPhoneNumber());
        assertEquals(0, editedUser.getRoles().size());
    }

    @Test
    public void updateUserNullField() {
        User originalUser = userDao.getUserByUsername("AnimeIsCool");

        originalUser.setUsername(null);
        try {
            userDao.updateUser(originalUser);
            fail("should have thrown DaoException, translated from DataIntegrityViolation");
        } catch (DaoException ex) {
            // success
        }
    }

    // this fails because of data edits in prior tests
    @Test
    void updateUserRolesChangedTest() throws DaoException {
//        User originalUser = userDao.getUserById(1);
        User originalUser = userDao.getUserByUsername("LampLamp");
        Set<Role> newRoles = originalUser.getRoles();
        newRoles.clear();

        originalUser.setRoles(newRoles);
        userDao.updateUser(originalUser);
        userDao.getUserById(originalUser.getId());

//        assertEquals(1, originalUser.getId());
        assertEquals("LampLamp", originalUser.getUsername());
        assertEquals("Dalton", originalUser.getFirstName());
        assertEquals("Galloway", originalUser.getLastName());
        assertEquals("daltong@gmail.com", originalUser.getEmail());
        assertEquals("(320)234-23412", originalUser.getPhoneNumber());
        assertEquals(0, originalUser.getRoles().size());
    }
}

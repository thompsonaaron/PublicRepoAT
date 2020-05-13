/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.services;

import com.sg.ChrisList.ChrisListApplicationTests;
import com.sg.ChrisList.daos.ConditionDao;
import com.sg.ChrisList.daos.DaoException;
import com.sg.ChrisList.daos.KeywordDao;
import com.sg.ChrisList.daos.ListingDao;
import com.sg.ChrisList.daos.RoleDao;
import com.sg.ChrisList.daos.UserDao;
import com.sg.ChrisList.models.Listing;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
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
@SpringBootTest(classes = ChrisListApplicationTests.class)
public class UserServiceTest {

    @Autowired
    KeywordDao keyword;

    @Autowired
    ListingDao listing;

    @Autowired
    RoleDao role;

    @Autowired
    UserDao user;

    @Autowired
    ConditionDao condition;

    @Autowired
    UserService service = new UserService(keyword, listing, role, user, condition);

    @Autowired
    JdbcTemplate template;

    @BeforeEach
    public void setUp() {
        template.update("DELETE FROM user_role");
        template.update("DELETE FROM `role`");
        template.update("DELETE FROM listingkeywords");
        template.update("DELETE FROM listings");

        template.update("DELETE FROM keyword");
        template.update("DELETE FROM user");
        // Do we have to delete from conditions? These will stay the same throughout
//        template.update("DELETE FROM Conditions");
        template.update("insert into Conditions (conditionType) values ('New'),('Like New'),('Used')");

        template.update("insert into `role` (`role`) VALUES ('ROLE_ADMIN'),('ROLE_USER')");

        // insert into user table, no changing ids here
        template.update("INSERT into `user` (`username`,`password`,`enabled`, firstName, lastName, email, phoneNumber) VALUES ('admin', '$2a$10$6/krDDN8P/i/CANT5Kn2pO6FSkczr7QbNpaaz/wnMYUpFoy/.vy9C', 1,'Mike', 'Wazowski', 'user@me.com', '555-5555')");
        template.update("INSERT into `user` (`username`,`password`,`enabled`, firstName, lastName, email, phoneNumber) VALUES (\"user\", \"$2a$10$6/krDDN8P/i/CANT5Kn2pO6FSkczr7QbNpaaz/wnMYUpFoy/.vy9C\", 1, 'fake', 'user', 'user@me.com', '555-5555')");
        template.update("INSERT into `user` (`username`,`password`,`enabled`, firstName, lastName, email, phoneNumber) VALUES ('oldSocks24', 'password', 1, 'Phil', 'Philipson', 'phil@phil.com', '(320)-234-2342')");
        template.update("INSERT into `user` (`username`,`password`,`enabled`, firstName, lastName, email, phoneNumber) VALUES ('LampLamp', 'password', 1, 'Dalton', 'Galloway', 'daltong@gmail.com', '(320)-234-23412')");
        template.update("INSERT into `user` (`username`,`password`,`enabled`, firstName, lastName, email, phoneNumber) VALUES ('AnimeIsCool', 'password', 1, 'Patricia', 'Patterson', 'PattyP@yahoo.com', '(320)-234-2340')");

        template.update("insert into `user_role` (`user_id`, `role_id`) VALUES "
                + "(" + user.getAllUsers().get(0).getId() + "," + role.getAllRoles().get(0).getId() + "),"
                + "(" + user.getAllUsers().get(0).getId() + "," + role.getAllRoles().get(1).getId() + "),"
                + "(" + user.getAllUsers().get(1).getId() + "," + role.getAllRoles().get(1).getId() + ")"
        );

        // no changing ids
        template.update(" insert into keyword (`name`, isDeleted) VALUES ('car', '0'),('couch','0'),('furniture', '0')");

        // insert into listings table
        template.update("insert into listings (`Title`, City, ListDate, content, isDeleted, conditionId, userId, price) VALUES "
                + "('Old Socks', 'Bloomington', '2016-04-06', 'These are my old socks. I do not want them in my house anymore.', '0', 3," + user.getAllUsers().get(2).getId() + ", '50'),"
                + "('Old Ladder', 'Richfield', '2016-04-04', 'Old ladder. It is old, but is still a ladder.', '0', 3," + user.getAllUsers().get(2).getId() + ", '100'),"
                + "('Cool Lamp', 'Minneapolis', '2016-04-06', 'This is a cool lamp. New. Is in the shape of a Cool Lamp', '0', 1," + user.getAllUsers().get(3).getId() + ", '1'),"
                + "('Anime Figure', 'Hopkins', '2016-04-05', 'This is a Dragonball Z character. Selling it becuase I don''t identify with the character anymore', '0', 2," + user.getAllUsers().get(4).getId() + ", '1000')");

        template.update("insert into ListingKeywords(ListingId, KeywordId) values "
                + "(" + listing.getAllListings().get(0).getListingId() + ", " + keyword.getAllActiveKeywords().get(0).getId() + "),"
                + "(" + listing.getAllListings().get(0).getListingId() + ", " + keyword.getAllActiveKeywords().get(1).getId() + "),"
                + "(" + listing.getAllListings().get(1).getListingId() + ", " + keyword.getAllActiveKeywords().get(1).getId() + "),"
                + "(" + listing.getAllListings().get(2).getListingId() + ", " + keyword.getAllActiveKeywords().get(2).getId() + ")");

        // should later INSERT INTO user_role table somehow so we can test a users roles better. Right now roles has a size of 0 for each user
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testLoadUserByUsername() {
//        System.out.println("loadUserByUsername");
//        String username = "";
//        UserService instance = new UserService();
//        UserDetails expResult = null;
//        UserDetails result = instance.loadUserByUsername(username);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
    }

    @Test
    public void GetListingsByKeyword() throws DaoException {

        List<Listing> ofCondition = service.getListingsByCondition("Used");
        Listing used = ofCondition.get(0);

        LocalDate toCheck = LocalDate.of(2016, Month.APRIL, 06);

        assertEquals("Old Socks", used.getTitle());
        assertEquals("Bloomington", used.getCity());
        assertEquals(toCheck, used.getDate());
        assertEquals("These are my old socks. I do not want them in my house anymore.", used.getContent());
        assertEquals(false, used.getIsDeleted());
        assertEquals(condition.getAllConditions().get(2).getConditionId(), used.getListingCondition().getConditionId());
        assertEquals(user.getAllUsers().get(2).getId(), used.getUserId());

        LocalDate otherCheck = LocalDate.of(2016, Month.APRIL, 04);

        Listing used2 = ofCondition.get(1);

        assertEquals("Old Ladder", used2.getTitle());
        assertEquals("Richfield", used2.getCity());
        assertEquals(otherCheck, used2.getDate());
        assertEquals("Old ladder. It is old, but is still a ladder.", used2.getContent());
        assertEquals(false, used2.getIsDeleted());
        assertEquals(condition.getAllConditions().get(2).getConditionId(), used2.getListingCondition().getConditionId());
        assertEquals(user.getAllUsers().get(2).getId(), used2.getUserId());
    }

    @Test
    public void testGetListingsByKeywordGoldenPath() {

//        try {
        LocalDate toCheck = LocalDate.of(2016, Month.APRIL, 06);
        List<Listing> byKeyword = service.getListingsByKeyword("furniture");

        Listing a = byKeyword.get(0);

        assertEquals("Cool Lamp", a.getTitle());
        assertEquals("Minneapolis", a.getCity());
        assertEquals(toCheck, a.getDate());
        assertEquals("This is a cool lamp. New. Is in the shape of a Cool Lamp", a.getContent());
        assertEquals(false, a.getIsDeleted());
        assertEquals(1, a.getListingCondition().getConditionId());
        assertEquals(user.getAllUsers().get(3).getId(), a.getUserId());
    }
}

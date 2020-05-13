/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.daos;

import com.sg.ChrisList.models.Condition;
import com.sg.ChrisList.models.Keyword;
import com.sg.ChrisList.models.Listing;
import com.sg.ChrisList.models.Role;
import com.sg.ChrisList.models.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 *
 * @author ursul
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ListingDaoDBTest {

    @Autowired
    ListingDao listingDao = new ListingDaoDB();

    @Autowired
    ConditionDao conditionDao = new ConditionDaoDB();

    @Autowired
    KeywordDao keywordDao = new KeywordDaoDB();

    @Autowired
    RoleDao roleDao = new RoleDaoDB();

    @Autowired
    UserDao userDao = new UserDaoDB();

    @Autowired
    JdbcTemplate template;

    @BeforeEach
    public void setUpClass() {
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
                + "(" + userDao.getAllUsers().get(0).getId() + "," + roleDao.getAllRoles().get(0).getId() + "),"
                + "(" + userDao.getAllUsers().get(0).getId() + "," + roleDao.getAllRoles().get(1).getId() + "),"
                + "(" + userDao.getAllUsers().get(1).getId() + "," + roleDao.getAllRoles().get(1).getId() + ")"
        );

        // no changing ids
        template.update(" insert into keyword (`name`, isDeleted) VALUES ('car', '0'),('couch','0'),('furniture', '0')");

        // insert into listings table
        template.update("insert into listings (`Title`, City, ListDate, content, isDeleted, conditionId, userId, price) VALUES "
                + "('Old Socks', 'Bloomington', '2016-04-06', 'These are my old socks. I do not want them in my house anymore.', '0', 3," + userDao.getAllUsers().get(2).getId() + ", '50'),"
                + "('Old Ladder', 'Richfield', '2016-04-04', 'Old ladder. It is old, but is still a ladder.', '0', 3," + userDao.getAllUsers().get(2).getId() + ", '100'),"
                + "('Cool Lamp', 'Minneapolis', '2016-04-06', 'This is a cool lamp. New. Is in the shape of a Cool Lamp', '0', 1," + userDao.getAllUsers().get(3).getId() + ", '1'),"
                + "('Anime Figure', 'Hopkins', '2016-04-05', 'This is a Dragonball Z character. Selling it becuase I don''t identify with the character anymore', '0', 2," + userDao.getAllUsers().get(4).getId() + ", '1000')");

        template.update("insert into ListingKeywords(ListingId, KeywordId) values "
                + "(" + listingDao.getAllListings().get(0).getListingId() + ", " + keywordDao.getAllActiveKeywords().get(0).getId() + "),"
                + "(" + listingDao.getAllListings().get(0).getListingId() + ", " + keywordDao.getAllActiveKeywords().get(1).getId() + "),"
                + "(" + listingDao.getAllListings().get(1).getListingId() + ", " + keywordDao.getAllActiveKeywords().get(1).getId() + "),"
                + "(" + listingDao.getAllListings().get(2).getListingId() + ", " + keywordDao.getAllActiveKeywords().get(2).getId() + ")");

    }

    @Test
    public void testGetListingsByConditionUsed() {

        List<Listing> used = listingDao.getListingsByCondition("Used");

        LocalDate toCheck = LocalDate.of(2016, Month.APRIL, 06);
        Listing indexOne = used.get(0);

        assertEquals("Old Socks", indexOne.getTitle());
        assertEquals("Bloomington", indexOne.getCity());
        assertEquals(toCheck, indexOne.getDate());
        assertEquals("These are my old socks. I do not want them in my house anymore.", indexOne.getContent());
        assertEquals(false, indexOne.getIsDeleted());
        assertEquals(3, indexOne.getListingCondition().getConditionId());
        assertEquals(userDao.getAllUsers().get(2).getId(), indexOne.getUserId());

        LocalDate otherCheck = LocalDate.of(2016, Month.APRIL, 04);

        Listing indexTwo = used.get(1);

        assertEquals("Old Ladder", indexTwo.getTitle());
        assertEquals("Richfield", indexTwo.getCity());
        assertEquals(otherCheck, indexTwo.getDate());
        assertEquals("Old ladder. It is old, but is still a ladder.", indexTwo.getContent());
        assertEquals(false, indexTwo.getIsDeleted());
        assertEquals(3, indexTwo.getListingCondition().getConditionId());
        assertEquals(userDao.getAllUsers().get(2).getId(), indexTwo.getUserId());
    }

    @Test
    public void testGetListingsByConditionLikeNew() {

        LocalDate likeNewCheck = LocalDate.of(2016, Month.APRIL, 05);
        List<Listing> likeNew = listingDao.getListingsByCondition("Like New");
        Listing ln = likeNew.get(0);

        assertEquals("Anime Figure", ln.getTitle());
        assertEquals("Hopkins", ln.getCity());
        assertEquals(likeNewCheck, ln.getDate());
        assertEquals("This is a Dragonball Z character. Selling it becuase I don't identify with the character anymore", ln.getContent());
        assertEquals(false, ln.getIsDeleted());
        assertEquals(2, ln.getListingCondition().getConditionId());
        assertEquals(userDao.getAllUsers().get(4).getId(), ln.getUserId());

    }

    @Test
    public void testGetListingsByConditionNew() {

        LocalDate newCheck = LocalDate.of(2016, Month.APRIL, 06);
        List<Listing> newCondition = listingDao.getListingsByCondition("New");
        Listing n = newCondition.get(0);

        assertEquals("Cool Lamp", n.getTitle());
        assertEquals("Minneapolis", n.getCity());
        assertEquals(newCheck, n.getDate());
        assertEquals("This is a cool lamp. New. Is in the shape of a Cool Lamp", n.getContent());
        assertEquals(false, n.getIsDeleted());
        assertEquals(1, n.getListingCondition().getConditionId());
        assertEquals(userDao.getAllUsers().get(3).getId(), n.getUserId());

//        LocalDate checked = LocalDate.of(2016, Month.MARCH, 05);
//        Listing m = newCondition.get(1);
//
//        assertEquals("Waffle Maker", m.getTitle());
//        assertEquals("Hopkins", m.getCity());
//        assertEquals(checked, m.getDate());
//        assertEquals("New, but I like Pancakes better", m.getContent());
//        assertEquals(false, m.getIsDeleted());
//        assertEquals(1, m.getListingCondition().getConditionId());
//        assertEquals(userDao.getAllUsers().get(4).getId(), m.getUserId());
    }

    @Test
    public void testEditListingGoldenPath() {

    }

    @Test
    public void testGetListingsByKeywordGoldenPath() {

        LocalDate toCheck = LocalDate.of(2016, Month.APRIL, 06);
        List<Listing> byKeyword = listingDao.getListingsByKeyword("furniture");
        Listing a = byKeyword.get(0);

        assertEquals("Cool Lamp", a.getTitle());
        assertEquals("Minneapolis", a.getCity());
        assertEquals(toCheck, a.getDate());
        assertEquals("This is a cool lamp. New. Is in the shape of a Cool Lamp", a.getContent());
        assertEquals(false, a.getIsDeleted());
        assertEquals(1, a.getListingCondition().getConditionId());
        assertEquals(userDao.getAllUsers().get(3).getId(), a.getUserId());

    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.daos;

import com.sg.ChrisList.ChrisListApplicationTests;
import com.sg.ChrisList.models.Keyword;
import java.util.List;
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
 * @author board
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ChrisListApplicationTests.class)
public class KeywordDaoDBTest {

    @Autowired
    KeywordDao keywordDao;

    @Autowired
    JdbcTemplate template;

    public KeywordDaoDBTest() {
    }

    @BeforeEach
    public void setUp() {
//        template.update("DELETE FROM keyword");    
    }

    /**
     * Test of addKeyword method, of class KeywordDaoDB.
     */
    @Test
    public void testAddKeywordGoldenPathTest() {

        Keyword toAdd = new Keyword();

        toAdd.setName("Carpet");
        toAdd.setDeleted(false);

        Keyword toAdd2 = new Keyword();

        toAdd2.setName("Car");
        toAdd2.setDeleted(false);

        keywordDao.addKeyword(toAdd);

        assertEquals("Carpet", toAdd.getName());
        assertEquals(false, toAdd.isDeleted());

        keywordDao.addKeyword(toAdd2);

        assertEquals("Car", toAdd2.getName());
        assertEquals(false, toAdd2.isDeleted());

    }

    /**
     * Test of deleteKeyword method, of class KeywordDaoDB.
     */
    @Test
    public void testDeleteKeywordGoldenPathTest() {
        
        
        
    }

    /**
     * Test of editKeyword method, of class KeywordDaoDB.
     */
    @Test
    public void testEditKeywordGoldenPathTest() {
        
        
        
    }

}

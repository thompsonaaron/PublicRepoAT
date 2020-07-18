/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.BlackJackServ.service;

import com.aaronthompson.BlackJackServ.model.Card;
import static com.aaronthompson.BlackJackServ.model.Suit.DIAMONDS;
import static com.aaronthompson.BlackJackServ.model.Suit.HEARTS;
import static com.aaronthompson.BlackJackServ.model.Suit.SPADES;
import static com.aaronthompson.BlackJackServ.model.Value.SEVEN;
import static com.aaronthompson.BlackJackServ.model.Value.TEN;
import static com.aaronthompson.BlackJackServ.model.Value.TWO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author thomp
 */
public class BlackjackServiceTest {

    BlackjackService service = new BlackjackService();

    @BeforeEach
    public void setUp() {
    }

//
    @Test
    public void testCalculateTotalGP() {
        Card firstCard = new Card(TWO, SPADES);
        Card secondCard = new Card(TEN, HEARTS);
        Card[] cardArray = {firstCard, secondCard};
        int expResult = 12;
        int result = service.calculateTotal(cardArray);
        assertEquals(expResult, result);
    }

    @Test
    public void testCalculateTotalNullInput() {
        Card[] cardArray = null;
        int expResult = 0;
        int result = service.calculateTotal(cardArray);
        assertEquals(expResult, result);
    }

    @Test
    public void testValidateBust() {
        int count = 20;
        BlackjackService service = new BlackjackService();
        boolean expResult = false;
        boolean result = service.validateBust(count);
        assertEquals(expResult, result);
    }

    @Test
    public void testValidateBust2() {
        int count = 22;
        BlackjackService service = new BlackjackService();
        boolean expResult = true;
        boolean result = service.validateBust(count);
        assertEquals(expResult, result);
    }

    @Test
    public void testDealerHitOrStay1() {
        int dealerCount = 16;
        BlackjackService service = new BlackjackService();
        String expResult = "hit";
        String result = service.dealerHitOrStay(dealerCount);
        assertEquals(expResult, result);
    }

    @Test
    public void testDealerHitOrStay2() {
        int dealerCount = 17;
        BlackjackService service = new BlackjackService();
        String expResult = "stay";
        String result = service.dealerHitOrStay(dealerCount);
        assertEquals(expResult, result);
    }

    @Test
    public void testDetermineWinner() {
        Card firstPlayerCard = new Card(SEVEN, SPADES);
        Card secondPlayerCard = new Card(TEN, DIAMONDS);

        Card firstDealerCard = new Card(TEN, SPADES);
        Card secondDealerCard = new Card(TEN, HEARTS);

        Card[] dealerCards = {firstDealerCard, secondDealerCard};
        Card[] playerCards = {firstPlayerCard, secondPlayerCard};

        Map<String, Card[]> allCards = new HashMap<>();
        allCards.put("dealer", dealerCards);
        allCards.put("player", playerCards);

        BlackjackService service = new BlackjackService();
        List<String> expResult = new ArrayList<>();
        expResult.add("dealer");
        expResult.add("You LOSE! The dealer had 20 and you had 17");
        expResult.add("-1.0");

        List<String> result = service.determineWinner(allCards);
        assertEquals(expResult, result);
    }

    @Test
    public void testDetermineWinnerNullInput() {

        Card firstDealerCard = new Card(TEN, SPADES);
        Card secondDealerCard = new Card(TEN, HEARTS);

        Card[] dealerCards = {firstDealerCard, secondDealerCard};
        Card[] playerCards = null;

        Map<String, Card[]> allCards = new HashMap<>();
        allCards.put("dealer", dealerCards);
        allCards.put("player", playerCards);

        BlackjackService service = new BlackjackService();
        List<String> expResult = new ArrayList<>();
        expResult.add("dealer");
        expResult.add("You LOSE! The dealer had 20 and you had 0");
        expResult.add("-1.0");

        List<String> result = service.determineWinner(allCards);
        assertEquals(expResult, result);
    }

}

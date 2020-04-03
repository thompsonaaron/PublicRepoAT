/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.BlackJackServ.service;

import com.aaronthompson.BlackJackServ.model.Card;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 *
 * @author thomp
 */
@Service
public class BlackjackService {

    List<Card> cards = new ArrayList<>();

    public int calculateTotal(Card[] cardArray) {

        cards.clear();

        for (int i = 0; i < cardArray.length; i++) {
            cards.add(cardArray[i]);
        }

        int totalValue = 0;
        int aces = 0;
        for (Card aCard : cards) {
            switch (aCard.getValue()) {
                case TWO:
                    totalValue += 2;
                    break;
                case THREE:
                    totalValue += 3;
                    break;
                case FOUR:
                    totalValue += 4;
                    break;
                case FIVE:
                    totalValue += 5;
                    break;
                case SIX:
                    totalValue += 6;
                    break;
                case SEVEN:
                    totalValue += 7;
                    break;
                case EIGHT:
                    totalValue += 8;
                    break;
                case NINE:
                    totalValue += 9;
                    break;
                case TEN:
                    totalValue += 10;
                    break;
                case JACK:
                    totalValue += 10;
                    break;
                case QUEEN:
                    totalValue += 10;
                    break;
                case KING:
                    totalValue += 10;
                    break;
                case ACE:
                    aces += 1;
                    break;
            }
        }
        for (int i = 0; i < aces; i++) {
            if (totalValue > 10) {
                totalValue += 1;
            } else {
                totalValue += 11;
            }
        }
        return totalValue;
    }

    // goes to userBusted value in .js file
    public boolean validateBust(int count) {
        if (count > 21) {
            return true;
        }
        return false;
    }

    public String dealerHitOrStay(int dealerCount) {
        if (dealerCount < 17) {
            return "hit";
        } else {
            return "stay";
        }
    }

    public Map<String, BigDecimal> determineWinner(Map<String, Card[]> allCards) {

        Card[] dealerCards = allCards.get("dealer");
        Card[] playerCards = allCards.get("player");

        Integer dealerSum = calculateTotal(dealerCards);
        Integer playerSum = calculateTotal(playerCards);

        Map<String, BigDecimal> returnedMap = new HashMap<>();

        if (playerSum == 21 && playerCards.length == 2) {
            returnedMap.put("BLACKJACK", new BigDecimal("1.5"));
        } else if (dealerSum > 21) {
            returnedMap.put("The dealer busted! You win!", new BigDecimal("1.0"));
        } else if (dealerSum > playerSum) {
            returnedMap.put("You LOSE", new BigDecimal("-1.0"));
        } else if (dealerSum < playerSum) {
            returnedMap.put("You WIN", new BigDecimal("1.0"));
        } else if (dealerSum == playerSum) {
            returnedMap.put("You TIED", new BigDecimal("0"));;
        }
        return returnedMap;
    }
}

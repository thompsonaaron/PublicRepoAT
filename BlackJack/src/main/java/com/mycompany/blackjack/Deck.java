/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.blackjack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author thomp
 */
public class Deck {

    List<Card> cards = new ArrayList<>();

    public void createFullDeck() {
        for (Suit cardSuit : Suit.values()) {
            for (Value cardValue : Value.values()) {
                cards.add(new Card(cardValue, cardSuit));
            }
        }
    }

    public void shuffleDeck() {
        Random rng = new Random();
        List<Card> tempDeck = new ArrayList<>();
        int randomCardIndex = 0;
        for (int i = 0; i < 52; i++) {
            randomCardIndex = rng.nextInt(cards.size());
            tempDeck.add(cards.get(randomCardIndex));
            cards.remove(randomCardIndex);
        }
        cards = tempDeck;
    }

    public String toString() {
        String cardListOutput = "";
        for (Card thisCard : cards) {
        }
        return cardListOutput;
    }

    public void removeCard(int i) {
        cards.remove(i);
    }

    public Card getCard(int i) {
        return cards.get(i);
    }

    public void addCard(Card addCard) {
        cards.add(addCard);
    }

    public void draw(Deck drawingFrom) {
        cards.add(drawingFrom.getCard(0));
        drawingFrom.removeCard(0);
    }

    public int cardsValue() {
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
            if (totalValue > 10){
                totalValue += 1;
            } else {
                totalValue += 11;
            }
        }
        return totalValue;
    }

}

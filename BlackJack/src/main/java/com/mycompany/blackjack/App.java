/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.blackjack;

import static com.mycompany.blackjack.Value.ACE;
import static com.mycompany.blackjack.Value.TEN;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author thomp
 */
public class App {

    public static void main(String[] args) {

        System.out.println("Welcome to Aaron's BlackJack Shack!");

        List<Integer> handScoresList = new ArrayList<>();
        Deck playingDeck = new Deck();
        Deck doubleDeck = new Deck();
        playingDeck.createFullDeck();
            
        playingDeck.shuffleDeck();
        doubleDeck.createFullDeck();
        doubleDeck.shuffleDeck();
        
//        Card testCard = new Card(Value.TEN, Suit.CLUBS);
//        Card testCard2 = new Card(Value.TEN, Suit.HEARTS);
//        for (int i = 0; i < 51; i++) {
//            doubleDeck.addCard(testCard);
//            doubleDeck.addCard(testCard2);
//        }
        int deckSize = playingDeck.cards.size();

        for (int j = 0; j < 6; j++) {
            playingDeck.createFullDeck();
            playingDeck.shuffleDeck();
            for (int i = 0; i < deckSize; i++) {
                doubleDeck.draw(playingDeck);
            }
        }
        Deck playerDeck = new Deck();
        Deck playerDeckTwo = new Deck();
        Deck dealerDeck = new Deck();

        System.out.println("How much money do you want to bring to the table? ");
        Scanner sc = new Scanner(System.in);
        double playerMoney = Double.parseDouble(sc.nextLine());
        int cardCounter = 0;
        // recently added
        boolean donePlaying = false;

        while (playerMoney > 0 && doubleDeck.cards.size() > 10 && !donePlaying) {
            System.out.println("");
            System.out.println("You have $" + playerMoney + ". How much would you like to bet?");
            double playerBet = 0;
            try {
                playerBet = Double.parseDouble(sc.nextLine());
            } catch (NumberFormatException ex) {
                System.out.println("That is not a valid bet amount");
            }

            boolean split = true;
            boolean betAmountOk = true;

            while (betAmountOk && split) {
                if (playerBet > playerMoney) {
                    System.out.println("You do not have that much money! \n");
                    betAmountOk = true;
                    break;
                } else if (playerBet <= 0) {
                    System.out.println("");
                    betAmountOk = true;
                    break;
                } else {
                    betAmountOk = false;

                    // deal cards, but if user already has cards from a split, don't deal new ones
                    if (playerDeck.cards.size() < 2) {
                        playerDeck.draw(doubleDeck);
                        playerDeck.draw(doubleDeck);

                    }
                    // for split scenarios
                    if (dealerDeck.cards.size() < 2) {
                        dealerDeck.draw(doubleDeck);
                        dealerDeck.draw(doubleDeck);
                    }

                    // insurance for the case where the dealer has an ACE on the upcard
                    boolean insurance = false;
                    if (dealerDeck.getCard(0).getValue().equals(ACE)) {
                        System.out.println("The dealer's top card is " + dealerDeck.getCard(0) + ". Would you like to buy insurance?");
                        String userInsuranceChoice = sc.nextLine();
                        if (userInsuranceChoice.equals("yes")) {
                            if (playerBet * 1.5 - playerBet > playerMoney - playerBet) {
                                System.out.println("You don't have enough money to buy insurance!");
                            } else {
                                playerBet *= 1.5;
                                insurance = true;
                            }
                        }
                    }

                    boolean hitOrStay = true;

                    System.out.print("Your first card is " + playerDeck.getCard(0).getValue() + " of " + playerDeck.getCard(0).getSuit() + ". ");
                    System.out.println("The dealer's first card is " + dealerDeck.cards.get(0));

                    while (split && cardCounter < 95) {

                        int hitCounter = 0;
                        int dealerHitCounter = 0;
                        int doubleDownCounter = 0;

                        while (hitOrStay) {

                            System.out.println("Your next card is " + playerDeck.getCard(1 + hitCounter + doubleDownCounter).getValue()
                                    + " of " + playerDeck.getCard(1 + hitCounter + doubleDownCounter).getSuit());

                            System.out.println("Your total is " + playerDeck.cardsValue());
                            if (playerDeck.cardsValue() > 21) {
                                hitOrStay = false;
                                break;
                            }

                            boolean notSplitYet = true;
                            // SPLIT SCENARIO
                            if (playerDeck.getCard(0).getValue() == playerDeck.getCard(1).getValue() && notSplitYet && playerMoney >= 2 * playerBet) {
                                System.out.println("Would you like to split? ");
                                String splitChoice = sc.nextLine();
                                splitChoice = splitChoice.toLowerCase();
                                if (splitChoice.equals("yes") || splitChoice.equals("split")) {
                                    playerDeckTwo.draw(playerDeck); // transfer card from deck one to deck two
                                    playerDeck.draw(doubleDeck); // deal new card for playerDeck 
                                    playerDeckTwo.draw(doubleDeck); // deal second card for deckTwo
                                    System.out.println("Let's play with your first hand. You have a " + playerDeck.getCard(0) + " and " + playerDeck.getCard(1));
                                    notSplitYet = false;
                                }
                            }

                            if (playerDeck.cardsValue() == 21) {
                                hitOrStay = false;
                            } else {

                                System.out.println("Would you like to hit, stay, or double down?");
                                String userInput = sc.nextLine();
                                userInput = userInput.toLowerCase();

                                switch (userInput) {
                                    case "hit":
                                        playerDeck.draw(doubleDeck);
                                        hitCounter++;
                                        break;
                                    case "stay":
                                        hitOrStay = false;
                                        break;
                                    case "double down":
                                        playerDeck.draw(doubleDeck);
                                        doubleDownCounter++;
                                        playerBet += playerBet;
                                        System.out.println("Your next card is " + playerDeck.getCard(1 + hitCounter + doubleDownCounter).getValue()
                                                + " of " + playerDeck.getCard(1 + hitCounter + doubleDownCounter).getSuit());
                                        hitOrStay = false;
                                        break;
                                    default:
                                        System.out.println("That is not a valid option");

                                }

                            }
                        } // end of while loop for player hit or stay option

                        // if player split, don't show second card on first time through. 
                        handScoresList.add(playerDeck.cardsValue());
                        if (playerDeckTwo.cards.isEmpty()) {
//                            for (Integer handValues : handScoresList) {
                            for (int i = 0; i < handScoresList.size(); i++) {

                                if (i == 0) {
                                    if ((handScoresList.get(i) < 22 && !(handScoresList.get(i) == 21 && hitCounter == 0 && doubleDownCounter == 0))) {
                                        System.out.println("The dealer's second card is " + dealerDeck.getCard(1));
                                    }
                                }

                                while (dealerDeck.cardsValue() < 17) {
                                    dealerDeck.draw(doubleDeck);
                                    System.out.println("The dealer was dealt: " + dealerDeck.getCard(2 + dealerHitCounter));
                                    dealerHitCounter++;
                                }
                                
                                    //scenario in which user bought insurance and dealer had blackjack
                                if (insurance && dealerDeck.cardsValue() == 21 && dealerHitCounter == 0) {
                                    System.out.println("The dealer had blackjack! Good thing you had insurance...");
                                    playerMoney = playerMoney + playerBet / 1.5;

                                } 
                                    //scenarios for user with blackjack and dealer had blackjack with insurance
                                else if (insurance && dealerDeck.cardsValue() == 21 && dealerHitCounter == 0
                                        && handScoresList.get(i) == 21 && hitCounter == 0 && doubleDownCounter == 0) {
                                    System.out.println("The dealer had a blackjack... but so did YOU!!");
                                    System.out.println("You get EVEN MONEY since you bought insurance");
                                    playerMoney = playerMoney + (playerBet / 1.5);
                                } 
                                    // dealer had blackjack but no insurance purchased
                                else if (dealerDeck.cardsValue() == 21 && dealerHitCounter == 0) {
                                    System.out.println("The dealer had a blackjack! You lose!");
                                    System.out.println("Should have bought that insurance...");
                                    playerMoney -= playerBet;
                                } 
                                  // scoring scenarios for a hand
                                else {
                                    if (handScoresList.get(i) == 21 && hitCounter == 0 && doubleDownCounter == 0) {
                                        System.out.println("For hand " + (i + 1) + ", you got a BLACKJACK!!!");
                                        playerMoney += playerBet * 1.5;
                                    } else if (handScoresList.get(i) > 21) {
                                        System.out.println("For hand " + (i + 1) + ", you BUSTED!!!");
                                        playerMoney -= playerBet;
                                    } else if (dealerDeck.cardsValue() > 21) {
                                        System.out.println("For hand " + (i + 1) + ", you WIN! You had " + handScoresList.get(i) + " and the dealer busted with " + dealerDeck.cardsValue());
                                        playerMoney += playerBet;
                                    } else if (handScoresList.get(i) == dealerDeck.cardsValue()) {
                                        System.out.println("For hand " + (i + 1) + ", you PUSHED with " + playerDeck.cardsValue());
                                    } else if (handScoresList.get(i) > dealerDeck.cardsValue()) {
                                        System.out.println("For hand " + (i + 1) + ", you WIN! You had " + handScoresList.get(i) + " and the dealer had " + dealerDeck.cardsValue());
                                        playerMoney += playerBet;
                                    } else {
                                        System.out.println("For hand " + (i + 1) + ", you LOST! You had " + handScoresList.get(i) + " and the dealer had " + dealerDeck.cardsValue());
                                        playerMoney -= playerBet;
                                    }
                                }
                            }
                        }

                        // if here means the player split at least once
                        if (!playerDeckTwo.cards.isEmpty()) {
                            for (int i = 0; i <= 1 + hitCounter + doubleDownCounter; i++) {
                                playerDeck.removeCard(0);
                            }
//                            SHOULD NOT REMOVE DEALER CARDS FOR A SPLIT SCENARIO!!!
//                            for (int i = 0; i <= 1 + dealerHitCounter; i++) {
//                                dealerDeck.removeCard(0);
//                            }
                            cardCounter += 4 + dealerHitCounter + hitCounter + doubleDownCounter;
                            hitCounter = 0;    // reset the counters so cards get placed in the correct position
                            doubleDownCounter = 0;
                            playerDeck.draw(playerDeckTwo);  //transfer first card from deckTwo
                            playerDeck.draw(playerDeckTwo); //transfer the second card from deckTwo
//                            playerDeckTwo.removeCard(0); //
//                            playerDeckTwo.removeCard(0); // 
                            split = true;
                            hitOrStay = true;
                            betAmountOk = true;
                            // else means that the patient did not split their hand
                        } else {
                            for (int i = 0; i <= 1 + hitCounter + doubleDownCounter; i++) {
                                playerDeck.removeCard(0);
                            }
                            for (int i = 0; i <= 1 + dealerHitCounter; i++) {
                                dealerDeck.removeCard(0);
                            }
                            handScoresList.clear();
                            cardCounter += 4 + dealerHitCounter + hitCounter + doubleDownCounter;
                            split = false; // hitOrStay is also false;

                            System.out.println("Would you like to cash out?");
                            String userDonePlaying = sc.nextLine();
                            if (userDonePlaying.equals("yes")) {
                                donePlaying = true;
                            }

                        }
                    }// end split while loop here

                } // this ends the else from the betAmountOk while loop
            } // this ends the betAmountOk while loop
        }
        System.out.println("GAME OVER!! You have " + playerMoney);
        System.out.println("There were a total of " + cardCounter + " cards used.");
    }    
}

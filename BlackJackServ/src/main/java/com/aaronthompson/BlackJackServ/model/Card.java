/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.BlackJackServ.model;

/**
 *
 * @author thomp
 */
public class Card {
    
    private Suit suit;
    private Value value;
    
    public Card (Value value, Suit suit){
        this.suit = suit;
        this.value = value;
    }
    

//    public String toString (){
//        return this.value.toString() + " " + this.suit.toString();
//    }
    
    public Value getValue (){
        return this.value;
    }
    
    public Suit getSuit(){
        return this.suit;
    }
    
    public void setValue(Value value){
        this.value = value;
    }
    
    public void setSuit(Suit suit){
        this.suit = suit;
    }
}

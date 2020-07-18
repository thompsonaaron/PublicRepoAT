/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.BlackJackServ.controller;

import com.aaronthompson.BlackJackServ.model.Card;
import com.aaronthompson.BlackJackServ.service.BlackjackService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thomp
 */
@RestController
@RequestMapping("/blackjack/api")
@CrossOrigin
public class BlackjackController {
    
    private final BlackjackService service;
    
    @Autowired
    public BlackjackController(BlackjackService service){
        this.service = service;
    }
    
    @RequestMapping("/calc")
    private Integer calculateTotal(@RequestBody Card[] cards){
        return service.calculateTotal(cards);
    }
    
    @GetMapping("/validateBust/{count}")
    private boolean validateBust(@PathVariable int count){
        return service.validateBust(count);
    }
    
    @PostMapping("/dealerHitOrStay/")
    private ResponseEntity dealerHitOrStay(@RequestBody Card[] dealerCards){
        int dealerTotal = service.calculateTotal(dealerCards);
        String hitOrStay = service.dealerHitOrStay(dealerTotal);
        return new ResponseEntity<String>(hitOrStay, HttpStatus.OK); 
    }
    
    @PostMapping("/determineWinner")
    private ResponseEntity determineWinner (@RequestBody Map<String, Card[]> allCards){
       List<String> returnedList = new ArrayList<>();
       returnedList = service.determineWinner(allCards);    
       return new ResponseEntity<List>(returnedList, HttpStatus.OK);
    } 
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.BlackJackServ.controller;

import com.aaronthompson.BlackJackServ.model.Card;
import com.aaronthompson.BlackJackServ.service.BlackjackService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
    
    @GetMapping("/dealerVal/{dealerTotal}")
    private ResponseEntity dealerHitOrStay(@PathVariable int dealerTotal){
        String hitOrStay = service.dealerHitOrStay(dealerTotal);
        return new ResponseEntity<>(hitOrStay, HttpStatus.OK); 
    }
    
    @PostMapping("/determineWinner")
    private ResponseEntity determineWinner (@RequestBody Map<String, Card[]> allCards){
       Map<String, BigDecimal> returnedMap = new HashMap<>();
        
       returnedMap = service.determineWinner(allCards);
//       Set messageSet = returnedMap.keySet();
      
//       Set<String> set = new HashSet<>();
//set.add("123");
//set.add("456");
//
//List<String> list = new ArrayList<>();
//list.addAll(set);
       
       return new ResponseEntity<Map>(returnedMap, HttpStatus.OK);
    }
    
//        @PostMapping("/determineWinner")
//    private ResponseEntity determineWinner (@RequestBody Card[] allCards){
//       
//       String message = service.determineWinner(allCards);
//        
//       return new ResponseEntity<String>(message, HttpStatus.OK);
//    }
    
}

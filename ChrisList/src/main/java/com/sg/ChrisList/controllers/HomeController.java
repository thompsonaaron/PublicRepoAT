/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.controllers;

import com.sg.ChrisList.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author board
 */
@Controller
public class HomeController {

    @Autowired
    UserService service;

    @GetMapping({"/", "/home"})
    public String displayHomePage(Model m) {
        m.addAttribute("keywords", service.getAllActiveKeywords());
        return "home";
    }

    @GetMapping("founders")
    public String meetFounders() {
        return "founders";

    }

}

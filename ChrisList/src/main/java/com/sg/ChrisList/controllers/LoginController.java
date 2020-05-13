/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.controllers;

import com.sg.ChrisList.daos.DaoException;
import com.sg.ChrisList.models.Role;
import com.sg.ChrisList.models.User;
import com.sg.ChrisList.services.UserService;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author board
 */
@Controller
public class LoginController {

    @Autowired
    UserService service;

    @Autowired
    PasswordEncoder encoder;

    Set<ConstraintViolation<User>> violations = new HashSet<>();

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // this is not active, was meant to allow users to see their account info
    @GetMapping("/account")
    public String displayContentPage(HttpServletRequest request, Model model) {
        // need to grab the user id from the parameter
        int userId = Integer.parseInt(request.getParameter("id"));
        User currentUser = service.getUserById(userId);

        model.addAttribute("user", currentUser);

        return "account";
    }

    @GetMapping("/createAccount")
    public String createAccount(Model model) {
        User blankUser = new User();
        blankUser.setId(0);
        blankUser.setUsername("");
        blankUser.setFirstName("");
        blankUser.setLastName("");
        blankUser.setPassword("");
        blankUser.setPhoneNumber("");
        Set<Role> roles = new HashSet<>();
        Role blankRole = new Role();
        roles.add(blankRole);
        blankUser.setRoles(roles);

        model.addAttribute("user", blankUser);
        model.addAttribute("errors", violations);
        return "createAccount";
    }

    @PostMapping("/createAccount")
    public String createAccount(@Valid User addedUser, BindingResult result, Model model) {

        if (result.hasErrors()) {

            model.addAttribute("user", addedUser);
            model.addAttribute("errors", result.getAllErrors());
            return "createAccount";
        } else {

            try {
                Set<Role> userRoles = new HashSet<>();
                userRoles.add(service.getRoleByRole("ROLE_USER"));
                addedUser.setRoles(userRoles);

                addedUser.setPassword(encoder.encode(addedUser.getPassword()));
                service.createUser(addedUser);

                return "redirect:/home";
            } catch (DaoException ex) {
                FieldError error = new FieldError("user", "username", "Username already exists. Please choose a unique username.");
                result.addError(error);
                model.addAttribute("errors", result.getAllErrors());
                return "createAccount";
            }
        }
    }
}

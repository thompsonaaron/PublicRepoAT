/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.controllers;

import com.sg.ChrisList.daos.DaoException;
import com.sg.ChrisList.daos.RoleDao;
import com.sg.ChrisList.daos.UserDao;
import com.sg.ChrisList.models.Keyword;
import com.sg.ChrisList.models.Role;
import com.sg.ChrisList.models.User;
import com.sg.ChrisList.services.UserService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author board
 */
@Controller
public class AdminController {

    @Autowired
    UserService service;

    @Autowired
    PasswordEncoder encoder;

    @GetMapping("/admin")
    public String displayAdminPage(Model m) {
        m.addAttribute("users", service.getAllUsers());
        m.addAttribute("keywords", service.getAllKeywords());
        return "admin";
    }

    @PostMapping("/addUser")
    public String addUser(String username, String password) throws DaoException {
        User toAdd = new User();
        toAdd.setUsername(username);
        toAdd.setPassword(password);
        toAdd.setEnabled(true);

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(service.getRoleByRole("ROLE_USER"));
        toAdd.setRoles(userRoles);

        service.createUser(toAdd);

        return "redirect:/admin";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(Integer id) {

        service.deleteUser(id);

        return "redirect:/admin";

    }

    @GetMapping("/editUser")
    public String editUserDisplay(Model model, Integer id, Integer error) {
        User user = service.getUserById(id);
        List roleList = service.getAllRoles();

        model.addAttribute("user", user);
        model.addAttribute("roles", roleList);

        if (error != null) {
            if (error == 1) {
                model.addAttribute("error", "Passwords did not match, password was not updated.");
            }
        }

        return "editUser";
    }

    @PostMapping(value = "/editUser")
    public String editUserAction(String[] roleIdList, Boolean enabled, Integer id) throws DaoException {
        User user = service.getUserById(id);
        if (enabled != null) {
            user.setEnabled(enabled);
        } else {
            user.setEnabled(false);
        }

        Set<Role> roleList = new HashSet<>();
        for (String roleId : roleIdList) {
            Role role = service.getRoleById(Integer.parseInt(roleId));
            roleList.add(role);
        }
        user.setRoles(roleList);
        service.updateUser(user);

        return "redirect:/admin";
    }

    @PostMapping("editPassword")
    public String editPassword(Integer id, String password, String confirmPassword) throws DaoException {
        User user = service.getUserById(id);

        if (password.equals(confirmPassword)) {
            user.setPassword(encoder.encode(password));
            service.updateUser(user);
            return "redirect:/admin";
        } else {
            return "redirect:/editUser?id=" + id + "&error=1";
        }
    }

    @PostMapping("/addKeyword")
    public String addKeyword(String name) {
        Keyword toAdd = new Keyword();
        toAdd.setName(name);
        toAdd.setDeleted(false);

        service.addKeyword(toAdd);

        return "redirect:/admin";
    }

    @PostMapping("/deleteKeyword")
    public String deleteKeyword(Integer id) {

        service.deleteKeyword(id);

        return "redirect:/admin";

    }
    
    @GetMapping("/editKeyword")
    public String editKeywordDisplay(Model m, Integer id) {
        Keyword keyword = service.getKeywordById(id);
        
        m.addAttribute("keyword", keyword);
        
        return "editKeyword";
    }
    
    @PostMapping("/editKeyword")
    public String editKeywordAction(Keyword toEdit) {
        
        service.editKeyword(toEdit);
        
        return "redirect:/admin";
    }
    
    

}

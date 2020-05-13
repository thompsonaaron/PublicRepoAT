/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.controllers;

import com.sg.ChrisList.daos.DaoException;
import com.sg.ChrisList.models.Condition;
import com.sg.ChrisList.models.Keyword;
import com.sg.ChrisList.models.Listing;
import com.sg.ChrisList.models.User;
import com.sg.ChrisList.services.UserService;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ursul
 */
@Controller
public class ListingController {

    @Autowired
    UserService service;

    @Autowired
    ServletContext context;

    Set<ConstraintViolation<Listing>> violations = new HashSet<>();

    @GetMapping("/createlisting")
    public String displayListingPage(Model m) {
        m.addAttribute("errors", violations);
        m.addAttribute("keywords", service.getAllActiveKeywords());

        return "createlisting";
    }

    @GetMapping("/editlisting")
    public String displayEditListingPage(HttpServletRequest request, Model m) {

        int listingId = Integer.parseInt(request.getParameter("id"));

        Listing listing = service.getListingById(listingId);

        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        int userId = service.getUserByUsername(user.getUsername()).getId();

        // check to see if the user that is signed in matches the userId of the listing
        if (userId != listing.getUserId()) {
            FieldError error = new FieldError("Listing", "editListing", "You do not have authorization to edit this listing");
            Set<FieldError> errors = new HashSet<>();
            errors.add(error);

            String username = service.getUserById(listing.getUserId()).getUsername();

            m.addAttribute("errors", errors);
            m.addAttribute("listing", listing);
            m.addAttribute("username", username);
            return "viewListing";
        }
        // else continue on to edit listing
        m.addAttribute("listing", listing);
        m.addAttribute("errors", violations);
        m.addAttribute("keywords", service.getAllActiveKeywords());

        return "editlisting";
    }

    @PostMapping("/editlisting")
    public String editListing(Listing toEdit, HttpServletRequest request, Model m, @RequestParam("files") MultipartFile[] files) {

        try {
            org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            int userId = service.getUserByUsername(user.getUsername()).getId();

            String[] keywordIds = request.getParameterValues("keywordIds");

            List<Keyword> listingKeywords = new ArrayList<>();

            for (String k : keywordIds) {
                Keyword key = service.getKeywordById(Integer.parseInt(k));
                listingKeywords.add(key);
            }

            int listingIds = Integer.parseInt(request.getParameter("id"));
            toEdit.setListingId(listingIds);
            toEdit.setKeywords(listingKeywords);

            toEdit.setUserId(userId);
            toEdit.setDate(LocalDate.now());
            toEdit.setIsDeleted(false);

            Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
            violations = validate.validate(toEdit);

            // get listing id from toEdit, then use to find previous imagePaths, then set those to toEdit
            toEdit.setImagePaths(service.getListingById(toEdit.getListingId()).getImagePaths());

            // if there were no photos, then set imagePaths from null to empty list
            if (toEdit.getImagePaths() == null) {
                List<String> emptyList = new ArrayList<>();
                toEdit.setImagePaths(emptyList);
            }

            // if no constraint violations
            if (violations.isEmpty()) {
                saveUploadedFiles(Arrays.asList(files), toEdit);
                service.editListing(toEdit);

                String username = service.getUserById(toEdit.getUserId()).getUsername();
                m.addAttribute("username", username);

                return ("redirect:/viewListing?id=" + toEdit.getListingId());
            }

            // if there are constraint violations
            m.addAttribute("keywords", service.getAllActiveKeywords());
            m.addAttribute("errors", violations);
            m.addAttribute("listing", toEdit);
            return "editlisting";

        } catch (DaoException ex) {
            return null;
        }

    }

    // we have duplicate viewListing mappings, one in listing, one in search
    @GetMapping("/viewListing")
    public String viewListing(HttpServletRequest request, Model m) {

        int listingId = Integer.parseInt(request.getParameter("id"));
        Listing toGet = service.getListingById(listingId);

        int listingUserId = toGet.getUserId();
        User listingUser = service.getUserById(listingUserId);
        String username = listingUser.getUsername();

        Set<FieldError> errors = new HashSet<>();

        m.addAttribute("errors", errors);
        m.addAttribute("listing", toGet);
        m.addAttribute("username", username);

        return "viewListing";
    }

    @PostMapping("addlisting")
    public String addListing(Listing toAdd, HttpServletRequest request, @RequestParam("files") MultipartFile[] files, Model m) {

        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        int userId = service.getUserByUsername(user.getUsername()).getId();

        // find all keywordIds sent from html page, then get keyword objects corresponding
        // and add to toAdd listing object
        String[] keywordIds = request.getParameterValues("keywordIds");
        List<Keyword> listingKeywords = new ArrayList<>();

        if (keywordIds != null) {

            for (String k : keywordIds) {
                Keyword key = service.getKeywordById(Integer.parseInt(k));
                listingKeywords.add(key);
            }
        }

        toAdd.setKeywords(listingKeywords);

        Condition listingCondition = new Condition();
        int conditionId = Integer.parseInt(request.getParameter("conditionId"));

        listingCondition.setConditionId(conditionId);

        toAdd.setUserId(userId);
        toAdd.setListingCondition(listingCondition);
        toAdd.setDate(LocalDate.now());
        toAdd.setIsDeleted(false);
        List<String> emptyList = new ArrayList<>();
        toAdd.setImagePaths(emptyList);

        // for uploading files:
        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(toAdd);

        if (violations.isEmpty()) {

            saveUploadedFiles(Arrays.asList(files), toAdd);
            Listing addedListing = service.addListing(toAdd, keywordIds);

            return ("redirect:/viewListing?id=" + addedListing.getListingId());

        }

        m.addAttribute("keywords", service.getAllActiveKeywords());
        m.addAttribute("errors", violations);
        return ("createListing");

    }

    @GetMapping("deleteListing")
    public String deleteListing(HttpServletRequest request) {
        int deleteId = Integer.parseInt(request.getParameter("id"));
        service.deleteListing(deleteId);

        return "redirect:/search";
    }

    private String getPath() throws UnsupportedEncodingException {
        String path = this.getClass().getClassLoader().getResource("").getPath();
        String fullPath = URLDecoder.decode(path, "UTF-8");
        String pathArr[] = fullPath.split("/WEB-INF/classes/");
//        System.out.println(fullPath);
//        System.out.println(pathArr[0]);
        fullPath = pathArr[0];

        String responsePath = "";
// to read a file from webcontent
        responsePath = new File(fullPath).getPath() + File.separatorChar;
        return responsePath;
    }

    private void saveUploadedFiles(List<MultipartFile> asList, Listing toAdd) {

        for (MultipartFile file : asList) {

            if (file.isEmpty()) {
                continue; //next pls
            }

            try {

                String root = getPath();
                Path currentPath = Paths.get(root, "static", "images", file.getOriginalFilename());
                Path nextPath = Paths.get(".", "src", "main", "resources", "static", "images", file.getOriginalFilename());

                File currentFile = currentPath.toFile();
                File nextFile = nextPath.toFile();

                // check if the two files exist, if so delete
                if (currentFile.exists()) {
                    currentFile.delete();
                }
                if (nextFile.exists()) {
                    nextFile.delete();
                }

                Files.write(currentPath, file.getBytes());
                Files.write(nextPath, file.getBytes());

                String source = "images/" + file.getOriginalFilename();
                List<String> ofSources = toAdd.getImagePaths();
                ofSources.add(source);

                toAdd.setImagePaths(ofSources);

            } catch (IOException ex) {
                Logger.getLogger(ListingController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
}

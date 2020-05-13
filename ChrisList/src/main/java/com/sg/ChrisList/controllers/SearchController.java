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
import com.sg.ChrisList.services.UserService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author board
 */
@Controller
public class SearchController {

    @Autowired
    UserService service;

    @GetMapping("search")
    public String displaySearchPage(String keyword, Model model) {
        // need to send keyword and userId to this page (so user can access their account with userId)

        List<Listing> listings = service.getAllListingsByMostRecent();
        Keyword blankKeyword = new Keyword();

        model.addAttribute("listings", listings);
        model.addAttribute("keyword", blankKeyword);

        return "search";
    }

//  replaced by getting by keyword and condition    
//    @GetMapping("condition")
//    public String displayListingsByCondition(Model m, String conditionType) throws DaoException {
//        m.addAttribute("allListing", service.getListingsByCondition(conditionType));
//        return "condition";
//    }
    @GetMapping("keyword")
    public String displayListingsByKeyword(Model m, String keyword) throws DaoException {
        m.addAttribute("allListing", service.getListingsByKeyword(keyword));
        return "keyword";
    }

    @GetMapping("listings")
    public String displayListingsByKeywordId(HttpServletRequest request, Model m) {
        int keywordId = Integer.parseInt(request.getParameter("id"));

        Keyword foundWord = service.getKeywordById(keywordId);

        List<Listing> listings = service.getListingsByKeywordId(keywordId);

        m.addAttribute("listings", listings);
        m.addAttribute("keyword", foundWord);

        return "search";
    }

    @GetMapping("filterByCondition")
    public String displayListingsByKeywordAndCondition(HttpServletRequest request, Model m) {
        try {
            // if user clicks button without selecting a condition, this will throw numberformatexception
            String conditionId = request.getParameter("conditionId");

            String keywordId = request.getParameter("keywordId");
            Keyword foundWord = service.getKeywordById(Integer.parseInt(keywordId));

            String sortOrder = request.getParameter("sortOrder");

            List<Listing> listings = service.getListingsByKeywordAndCondition(keywordId, conditionId, sortOrder);

            Condition condition = new Condition();

            if (conditionId != null) {
                condition = service.getConditionById(Integer.parseInt(conditionId));
                m.addAttribute("condition", condition);
            }

            Keyword blankKeyword = new Keyword();

            if (foundWord == null) {
                m.addAttribute("keyword", blankKeyword);
            } else {
                m.addAttribute("keyword", foundWord);
            }

            m.addAttribute("listings", listings);
            m.addAttribute("sortOrder", sortOrder);

            return "search";

        } catch (DaoException ex) {
            int keywordId = Integer.parseInt(request.getParameter("keywordId"));
            Keyword foundWord = service.getKeywordById(keywordId);

            // will not filter by condition as is done in the try statement
            List<Listing> listings = service.getListingsByKeywordId(keywordId);

            m.addAttribute("listings", listings);
//            m.addAttribute("keyword", foundWord);

            return "search";
        }
    }

    @GetMapping("viewall")
    public String displayAllListings(Model m) throws DaoException {
        List<Listing> allListings = service.returnAllListings();
        m.addAttribute("allListings", service);
        return "viewall";
    }

    // duplicated and working version is in listing controller
//    @GetMapping("/viewlisting")
//    public String viewListingDetails(HttpServletRequest request, Model model);


}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.services;

import com.sg.ChrisList.daos.ConditionDao;
import com.sg.ChrisList.daos.DaoException;
import com.sg.ChrisList.daos.KeywordDao;
import com.sg.ChrisList.daos.ListingDao;
import com.sg.ChrisList.daos.RoleDao;
import com.sg.ChrisList.daos.UserDao;
import com.sg.ChrisList.models.Condition;
import com.sg.ChrisList.models.Keyword;
import com.sg.ChrisList.models.Listing;
import com.sg.ChrisList.models.Role;
import com.sg.ChrisList.models.User;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author board
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    KeywordDao keyword;

    @Autowired
    ListingDao listing;

    @Autowired
    RoleDao role;

    @Autowired
    UserDao user;
    
    @Autowired
    ConditionDao condition;

    public UserService(KeywordDao keyword, ListingDao listing, RoleDao role, UserDao user, ConditionDao condition) {
        this.keyword = keyword;
        this.listing = listing;
        this.role = role;
        this.user = user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User loaded = user.getUserByUsername(username);

        if (loaded == null) {
            throw new UsernameNotFoundException("could not find username: " + username);
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        for (Role role : loaded.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getRole()));
        }
        org.springframework.security.core.userdetails.User toReturn
                = new org.springframework.security.core.userdetails.User(
                        loaded.getUsername(),
                        loaded.getPassword(),
                        grantedAuthorities);

        return toReturn;
    }

    public User createUser(User toAdd) throws DaoException {
        User foundUser = user.getUserByUsername(toAdd.getUsername());
        if (foundUser == null) {
            return user.createUser(toAdd);
        } else {
            throw new DaoException("Username already exists!");
        }
    }

    public List<User> getAllUsers() {
        return user.getAllUsers();
    }

    public User getUserById(int id) {
        return user.getUserById(id);
    }

    public User getUserByUsername(String username) {
        return user.getUserByUsername(username);
    }

    public void updateUser(User newData) throws DaoException {
        user.updateUser(newData);
    }

    public void deleteUser(int id) {
        user.deleteUser(id);
    }

    public Role getRoleById(int id) {
        return role.getRoleById(id);
    }

    public Role getRoleByRole(String r) {
        return role.getRoleByRole(r);
    }

    public List<Role> getAllRoles() {
        return role.getAllRoles();
    }
    
    public Listing getListingById(int listingId){
        return listing.getListingById(listingId);
    }

    
    // this is not used
    public List<Listing> getListingsByCondition(String conditionType) throws DaoException {
        return listing.getListingsByCondition(conditionType);
    }

    public List<Listing> getListingsByKeywordId(int keywordId){
        return listing.getListingsByKeywordId(keywordId);
    }
    
    public List<Listing> getListingsByKeyword(String keyword) {
        return listing.getListingsByKeyword(keyword);
    }

    public List<Listing> getAllListingsByMostRecent() {
        
        return listing.getListingsByMostRecent();
    }

    public Keyword addKeyword(Keyword toAdd) {
        return keyword.addKeyword(toAdd);
    }

    public void editKeyword(Keyword toEdit) {
        keyword.editKeyword(toEdit);
    }

    public void deleteKeyword(int id) {
        keyword.deleteKeyword(id);
    }

    public Keyword getKeywordById(int id) {
        return keyword.getKeywordById(id);
    }

    public List<Keyword> getAllKeywords() {
        return keyword.getAllKeywords();
    }

    public List<Keyword> getAllActiveKeywords() {
        return keyword.getAllActiveKeywords();
    }

    public List<Listing> returnAllListings() throws DaoException {
        return listing.getAllListings();
    }

    public Listing addListing(Listing toAdd, String[] keywordIds) {
        return listing.addListing(toAdd, keywordIds);
    }

    public void deleteListing(int toDelete) {
        listing.deleteListing(toDelete);
    }

    public List<Listing> getListingsByKeywordAndCondition(String keywordId, String conditionId, String sortOrder) throws DaoException {
        return listing.getListingsByKeywordAndCondition(keywordId, conditionId, sortOrder);
    }
    
//    public List<Listing> getListingsByKeywordSortedByPrice(int keywordId, String order){
//        return listing.getListingsByKeywordSortedByPrice(keywordId, order);
//    }
    
    public void editListing(Listing toEdit) throws DaoException {
        listing.editListing(toEdit);
    }
    
    public Condition getConditionById (Integer id){
        return condition.getConditionById(id);
    }

}

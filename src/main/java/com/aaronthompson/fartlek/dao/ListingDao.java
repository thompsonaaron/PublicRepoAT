/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.fartlek.dao;

import com.aaronthompson.fartlek.entities.Listing;
import java.util.List;

/**
 *
 * @author thomp
 */
public interface ListingDao {

    public List<Listing> getAllListingsByTeamId(Integer teamId);

    public Listing addListing(Listing li) throws DaoException;

    public Listing getListingByID(Integer listingId);

    public void editListing(Listing listing) throws DaoException;
    
}

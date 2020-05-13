/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.daos;

import com.sg.ChrisList.models.Listing;
import java.util.List;

/**
 *
 * @author ursul
 */
public interface ListingDao {

    public Listing getListingById(int listingId);

    public List<Listing> getListingsByKeywordAndCondition(String keywordId, String conditionId, String sortOrder) throws DaoException;

    public List<Listing> getListingsByKeywordId(int keywordId);

    public List<Listing> getListingsByCondition(String conditionType);
    
    public Listing addListing (Listing toAdd, String[] keywords);
    
    public List<Listing> getListingsByKeyword(String keywordName);

    public void editListing(Listing toEdit) throws DaoException;

    public List<Listing> getListingsByMostRecent();

    public List<Listing> getAllListings();

    public void deleteListing(int toDelete);

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.dao;

import com.aaronthompson.harrier.entities.Listing;
import com.aaronthompson.harrier.entities.ListingFile;
import java.util.List;

/**
 *
 * @author thomp
 */
public interface ListingDao {

    public List<Listing> getAllListingsByTeamId(Integer teamId);
    
    public List<Listing> getPaginatedListingsByTeamId(Integer teamId, Integer lastListingId);

    public Listing addListing(Listing li) throws DaoException;

    public Listing getListingByID(Integer listingId) throws DaoException;

    public Listing editListing(Listing listing) throws DaoException;
    
    public void deleteListingById(Integer listingId);

    public ListingFile getListingFileById(Integer listingFileId);

    public void deleteListingFileById(Integer listingFileId);

    public List<Integer> getMinAndMaxListingId(Integer teamId);
    
}

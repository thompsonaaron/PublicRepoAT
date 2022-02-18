/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.dao;

import com.aaronthompson.harrier.entities.Listing;
import com.aaronthompson.harrier.entities.ListingFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thomp
 */
@Repository
public class ListingDaoDB implements ListingDao {

    @Autowired
    JdbcTemplate template;

    @Override
    public List<Listing> getAllListingsByTeamId(Integer teamId) {

        List<Listing> allListings = template.query("SELECT * FROM Listings li WHERE isDeleted = 0 AND teamId = ? "
                + "ORDER BY listingId DESC", new listingMapper(), teamId);

        for (Listing li : allListings) {
            List<ListingFile> allFiles = template.query("SELECT * FROM ListingFiles "
                    + "WHERE listingId = ?", new listingFileMapper(), li.getListingId());
            li.setListingFiles(allFiles);
        }
        return allListings;
    }

    @Override
    public List<Listing> getPaginatedListingsByTeamId(Integer teamId, Integer lastListingId) {

        List<Listing> allListings = template.query("SELECT * FROM Listings li WHERE isDeleted = 0 AND teamId = ? AND listingId <= ? "
                + "ORDER BY listingId DESC LIMIT 5", new listingMapper(), teamId, lastListingId);

        for (Listing li : allListings) {
            List<ListingFile> allFiles = template.query("SELECT * FROM ListingFiles "
                    + "WHERE listingId = ?", new listingFileMapper(), li.getListingId());
            li.setListingFiles(allFiles);
        }
        return allListings;
    }

    @Override
    @Transactional
    public Listing addListing(Listing li) throws DaoException {

        KeyHolder kh = new GeneratedKeyHolder();

        try {
            int rowsAffected = template.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                "INSERT INTO Listings (title, teamId, content, `dateTime`) VALUES (?,?,?,?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setString(1, li.getTitle());
                        ps.setInt(2, li.getTeamId());
                        ps.setString(3, li.getContent());
                        ps.setTimestamp(4, Timestamp.valueOf(li.getDateTime()));

                        return ps;
                    },
                    kh);
        } catch (DataIntegrityViolationException | NullPointerException ex) {
            throw new DaoException("Cannot add an incomplete listing. All required fields must have a non-null value.");
        }

        int generatedId = kh.getKey().intValue();

        li.setListingId(generatedId);
        li.setIsDeleted(false);

        // need to add into the listingFiles bridge table here
        List<ListingFile> listingFiles = li.getListingFiles();
        for (ListingFile lf : listingFiles) {
            int rows = template.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                "INSERT INTO ListingFiles (listingId, filePath, title) VALUES (?,?,?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setInt(1, generatedId);
                        ps.setString(2, lf.getFilePath());
                        ps.setString(3, lf.getTitle());

                        return ps;
                    },
                    kh);
        }
        return li;
    }

    @Override
    public Listing getListingByID(Integer listingId) throws DaoException {
        try {
            Listing foundListing = template.queryForObject("SELECT * From Listings WHERE "
                    + "listingId = ?", new listingMapper(), listingId);

            List<ListingFile> allFiles = template.query("SELECT * FROM ListingFiles "
                    + "WHERE listingId = ?", new listingFileMapper(), listingId);
            foundListing.setListingFiles(allFiles);
            return foundListing;
        } catch (NullPointerException | EmptyResultDataAccessException ex) {
            throw new DaoException("Unable to find listing with ID of zero.");
        }
    }

    @Override
    public Listing editListing(Listing li) throws DaoException {
        //don't think this is required because now it's handled in a separate method to delete files
//        template.update("DELETE FROM ListingFiles WHERE listingId = ?", li.getListingId());

        KeyHolder kh = new GeneratedKeyHolder();

        try {
            int rowsAffected = template.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                "Update Listings set title = ?, teamId = ?, content = ? Where listingId = ?",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setString(1, li.getTitle());
                        ps.setInt(2, li.getTeamId());
                        ps.setString(3, li.getContent());
//                        ps.setTimestamp(4, Timestamp.valueOf(li.getDateTime())); don't want to update the date, that way index listingId corresponds to datetime
                        ps.setInt(4, li.getListingId());

                        return ps;
                    },
                    kh);
        } catch (DataIntegrityViolationException | NullPointerException ex) {
            throw new DaoException("Cannot add an incomplete user. All required fields must have a non-null value.");
        }

        try {
            for (ListingFile lf : li.getListingFiles()) {
                KeyHolder key = new GeneratedKeyHolder();
                String filePath = lf.getFilePath();
                try {
                    int rowsAffected = template.update(
                            connection -> {
                                PreparedStatement ps = connection.prepareStatement(
                                        "INSERT INTO ListingFiles (listingId, filePath, title) VALUES (?,?,?)",
                                        Statement.RETURN_GENERATED_KEYS
                                );
                                ps.setInt(1, li.getListingId());
                                ps.setString(2, filePath);
                                ps.setString(3, lf.getTitle());

                                return ps;
                            },
                            key);
                } catch (DataIntegrityViolationException ex) {
                    throw new DaoException("Cannot add to ListingFiles table." + ex.getMessage());
                }
            }
        } catch (NullPointerException ex) {
            throw new DaoException("Unable to add files along with the listing. No data located for the specified file.");
        }
        return li;
    }

    @Override
    public void deleteListingById(Integer listingId) {
        KeyHolder kh = new GeneratedKeyHolder();
        int rowsAffected = template.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "UPDATE Listings set isDeleted = 1 Where listingId = ?",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setInt(1, listingId);
                    return ps;
                },
                kh);
    }

    @Override
    public ListingFile getListingFileById(Integer listingFileId) {
        return template.queryForObject("SELECT * From ListingFiles Where listingFileId = ?", new listingFileMapper(), listingFileId);
    }

    @Override
    public void deleteListingFileById(Integer listingFileId) {
        KeyHolder kh = new GeneratedKeyHolder();
        int rowsAffected = template.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "DELETE FROM ListingFiles WHERE listingFileId = ?",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setInt(1, listingFileId);
                    return ps;
                },
                kh);
    }

    @Override
    public List<Integer> getMinAndMaxListingId(Integer teamId) {
        return template.queryForObject("SELECT MIN(listingId), MAX(listingId) FROM Listings li WHERE isDeleted = 0 and teamId = ?", new lastListingMapper(), teamId);
    }

    private static class listingMapper implements RowMapper<Listing> {

        @Override
        public Listing mapRow(ResultSet rs, int arg1) throws SQLException {
            Listing li = new Listing();

            li.setListingId(rs.getInt("listingId"));
            li.setTeamId(rs.getInt("teamId"));
            li.setTitle(rs.getString("title"));
            li.setContent(rs.getString("content"));

            LocalDateTime dateTime = rs.getTimestamp("dateTime").toLocalDateTime();
            li.setDateTime(dateTime);

            li.setIsDeleted(rs.getBoolean("isDeleted"));

            // need to align images with a listing
//            li.setImagePaths(rs.getString(arg0));
            return li;
        }
    }

    private static class listingFileMapper implements RowMapper<ListingFile> {

        @Override
        public ListingFile mapRow(ResultSet rs, int arg1) throws SQLException {
            ListingFile lf = new ListingFile();

            lf.setListingId(rs.getInt("listingId"));
            lf.setListingFileId(rs.getInt("listingFileId"));
            lf.setFilePath(rs.getString(("filePath")));
            lf.setTitle(rs.getString("title"));

            return lf;
        }
    }

    private static class lastListingMapper implements RowMapper<List<Integer>> {

        @Override
        public List<Integer> mapRow(ResultSet rs, int arg1) throws SQLException {
            Integer min = rs.getInt("MIN(listingId)");
            Integer max = rs.getInt("MAX(listingId)");
            List<Integer> minAndMax = new ArrayList<>();
            minAndMax.add(min);
            minAndMax.add(max);
            return minAndMax;
        }
    }

}

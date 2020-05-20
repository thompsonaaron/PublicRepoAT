/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.fartlek.dao;

import com.aaronthompson.fartlek.entities.Listing;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

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
        return template.query("SELECT * FROM listings li WHERE teamId = ? ORDER BY date DESC", new listingMapper(), teamId);
    }

    @Override
    public Listing addListing(Listing li) throws DaoException {

        KeyHolder kh = new GeneratedKeyHolder();

        try {
            int rowsAffected = template.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                "INSERT INTO listings (title, teamId, content, `date`, userId) VALUES (?,?,?,?,?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setString(1, li.getTitle());
                        ps.setInt(2, li.getTeamId());
                        ps.setString(3, li.getContent());
                        ps.setDate(4, java.sql.Date.valueOf(li.getDate()));
                        ps.setInt(5, li.getUserId());

                        return ps;
                    },
                    kh);
        } catch (DataIntegrityViolationException ex) {
            throw new DaoException("Cannot add an incomplete user. All requires fields must have a non-null value.");
        }

        int generatedId = kh.getKey().intValue();

        li.setListingId(generatedId);
        li.setIsDeleted(false);

        return null;
    }

    @Override
    public Listing getListingByID(Integer listingId) {
        return template.queryForObject("SELECT * From Listings WHERE listingId = ?", new listingMapper(), listingId);
    }

    @Override
    public void editListing(Listing li) throws DaoException {
        template.update("DELETE FROM Listings WHERE listingId = ?", li.getListingId());

        KeyHolder kh = new GeneratedKeyHolder();

        try {
            int rowsAffected = template.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                "INSERT INTO listings (title, teamId, content, `date`, userId, listingId) VALUES (?,?,?,?,?,?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setString(1, li.getTitle());
                        ps.setInt(2, li.getTeamId());
                        ps.setString(3, li.getContent());
                        ps.setDate(4, java.sql.Date.valueOf(li.getDate()));
                        ps.setInt(5, li.getUserId());
                        ps.setInt(6, li.getListingId());

                        return ps;
                    },
                    kh);
        } catch (DataIntegrityViolationException ex) {
            throw new DaoException("Cannot add an incomplete user. All requires fields must have a non-null value.");
        }
    }

    private static class listingMapper implements RowMapper<Listing> {

        @Override
        public Listing mapRow(ResultSet rs, int arg1) throws SQLException {
            Listing li = new Listing();

            li.setListingId(rs.getInt("listingId"));
            li.setTeamId(rs.getInt("teamId"));
            li.setTitle(rs.getString("title"));
            li.setContent(rs.getString("content"));
            li.setDate(rs.getDate("date").toLocalDate());
            li.setUserId(rs.getInt("userId"));
            li.setIsDeleted(rs.getBoolean("isDeleted"));
            // need to align images with a listing
//            li.setImagePaths(rs.getString(arg0));

            return li;
        }
    }

}

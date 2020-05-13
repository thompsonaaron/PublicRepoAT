/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.daos;

import com.sg.ChrisList.daos.KeywordDaoDB.KeywordMapper;
import com.sg.ChrisList.models.Condition;
import com.sg.ChrisList.models.Image;
import com.sg.ChrisList.models.Keyword;
import com.sg.ChrisList.models.Listing;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ursul
 */
@Repository
public class ListingDaoDB implements ListingDao {

    @Autowired
    JdbcTemplate template;

    @Autowired
    ConditionDao condition;

    @Override
    public Listing getListingById(int listingId) {

        try {
            Listing foundListing = template.queryForObject("SELECT * FROM CurrentListings cl INNER JOIN Conditions cond ON cl.ConditionId = cond.ConditionId WHERE cl.ListingId = ?", new ListingMapper(), listingId);

            List<Keyword> newKeywords = template.query("SELECT k.* FROM ListingKeywords lk JOIN keyword k ON lk.KeywordId = k.KeywordId WHERE lk.listingID = ?", new KeywordMapper(), listingId);

            foundListing.setKeywords(newKeywords);

            List<Image> toAdd = template.query("SELECT * FROM images WHERE ListingId = ?;", new ImageMapper(), foundListing.getListingId());

            List<String> images = new ArrayList<>();
            for (Image i : toAdd) {
                images.add(i.getPathString());
            }
            foundListing.setImagePaths(images);

            return foundListing;

        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<Listing> getListingsByKeywordId(int keywordId) {
        try {
            List<Listing> byKeyword = template.query("Select * \n"
                    + "FROM CurrentListings cl \n"
                    + "JOIN conditions c ON cl.conditionId = c.conditionId \n"
                    + "JOIN listingkeywords lk on lk.listingid = cl.listingid\n"
                    + "WHERE lk.keywordId = ?\n"
                    + "ORDER BY ListDate DESC;", new ListingMapper(), keywordId);

            for (Listing l : byKeyword) {
                List<Image> toAdd = template.query("SELECT * FROM images WHERE ListingId = ?;", new ImageMapper(), l.getListingId());

                List<String> images = new ArrayList<>();
                for (Image i : toAdd) {
                    images.add(i.getPathString());
                }
                l.setImagePaths(images);
            }

            return byKeyword;

        } catch (DataAccessException ex) {
            return null;
        }
    }

    // I don't think this works as expected
    @Override
    public List<Listing> getListingsByKeyword(String keywordName) {
        try {

            List<Listing> byKeyword = template.query("Select * FROM CurrentListings cl JOIN ListingKeywords lk ON cl.ListingId = lk.ListingId JOIN CurrentKeyword ck ON lk.KeywordId = ck.KeywordId WHERE ck.`name` = ? ORDER BY ListDate DESC", new ListingMapper(), keywordName);

            for (Listing l : byKeyword) {
                List<Image> toAdd = template.query("SELECT * FROM images WHERE ListingId = ?;", new ImageMapper(), l.getListingId());

                List<String> images = new ArrayList<>();
                for (Image i : toAdd) {
                    images.add(i.getPathString());
                }
                l.setImagePaths(images);
            }

            return byKeyword;

        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<Listing> getListingsByKeywordAndCondition(String keywordId, String conditionId, String sortOrder) throws DaoException {

        List<Object> varObjects = new ArrayList<>();
        if (!keywordId.equals("0")) {
            varObjects.add(keywordId);
        }
        if (conditionId != null) {
            varObjects.add(conditionId);
        }

        String theQuery = "Select * \n"
                + "FROM CurrentListings cl \n"
                + "JOIN conditions c ON cl.conditionId = c.conditionId \n"
                + "JOIN listingkeywords lk on lk.listingid = cl.listingid\n"
                + "WHERE 1=1 "
                + (keywordId.equals("0") ? "" : " AND lk.keywordId = ? ")
                + (conditionId == null ? "" : "AND c.conditionId = ? ");
        if (sortOrder == null) {
            theQuery += "ORDER BY ListDate DESC";
        } else if (sortOrder.equals("ASC")) {
            theQuery += "ORDER BY cl.price ASC";
        } else if (sortOrder.equals("DESC")) {
            theQuery += "ORDER BY cl.price DESC";
        } else {
            throw new DaoException("could not sort by given string");
        }

        try {
            List<Listing> allListings = template.query(theQuery,
                    new ListingMapper(), varObjects.toArray());

            for (Listing l : allListings) {
                List<Image> toAdd = template.query("SELECT * FROM images WHERE ListingId = ?;", new ImageMapper(), l.getListingId());

                List<String> images = new ArrayList<>();
                for (Image i : toAdd) {
                    images.add(i.getPathString());
                }
                l.setImagePaths(images);
            }

            return allListings;

        } catch (DataAccessException ex) {
            return null;
        }
    }

    // this will become deprecated once we get the keyword one working from the controller/html
    @Override
    public List<Listing> getListingsByMostRecent() {
        try {
            List<Listing> allListings = template.query("Select * FROM CurrentListings ORDER BY ListDate DESC",
                    new ListingMapper());

            for (Listing l : allListings) {
                List<Image> toAdd = template.query("SELECT * FROM images WHERE ListingId = ?;", new ImageMapper(), l.getListingId());

                List<String> images = new ArrayList<>();
                for (Image i : toAdd) {
                    images.add(i.getPathString());
                }
                l.setImagePaths(images);
            }

            return allListings;

        } catch (DataAccessException ex) {
            return null;
        }
    }

    // userId not included as should not be able to transfer user who owns the post
    @Override
    @Transactional
    public void editListing(Listing toEdit) throws DaoException {
        List<Keyword> keywords = toEdit.getKeywords();

        try {
            template.update("UPDATE listings SET Title = ?, City = ?, ListDate = ?, Content = ?, isDeleted = ?, ConditionId = ? WHERE ListingId = ?",
                    toEdit.getTitle(), toEdit.getCity(), toEdit.getDate(), toEdit.getContent(), toEdit.getIsDeleted(),
                    toEdit.getListingCondition().getConditionId(), toEdit.getListingId());

            template.update("DELETE FROM listingkeywords WHERE ListingId = ? ", toEdit.getListingId());

            for (Keyword keyId : keywords) {
                template.update("INSERT INTO ListingKeywords(KeywordId, ListingId) VALUES( ?, ?)", keyId.getId(), toEdit.getListingId());
            }

            template.update("DELETE FROM Images WHERE ListingId = ?", toEdit.getListingId());
            List<String> pathStrings = toEdit.getImagePaths();
            for (String s : pathStrings) {

                template.update("INSERT INTO Images (pathString, ListingId) VALUES (?,?)", s, toEdit.getListingId());

            }

        } catch (DataIntegrityViolationException ex) {
            throw new DaoException("Could not edit listing. At least one required field was empty.");
        }
    }

    @Override
    @Transactional
    public Listing addListing(Listing toAdd, String[] keywords) {

        List<Integer> keywordIds
                = keywords == null
                        ? new ArrayList<>()
                        : //Arrays.stream(keywords).mapToInt(Integer::parseInt).collect(Collectors.toList());
                        Arrays.stream(keywords).map(Integer::valueOf).collect(Collectors.toList());

        KeyHolder kh = new GeneratedKeyHolder();
        int rowsAffected = template.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO listings (conditionId, Title, City, ListDate, content, isDeleted, userId, price) VALUES (?,?,?,?,?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setInt(1, toAdd.getListingCondition().getConditionId());
                    ps.setString(2, toAdd.getTitle());
                    ps.setString(3, toAdd.getCity());
                    ps.setDate(4, java.sql.Date.valueOf(toAdd.getDate()));
                    ps.setString(5, toAdd.getContent());
                    ps.setBoolean(6, toAdd.getIsDeleted());
                    ps.setInt(7, toAdd.getUserId());
                    ps.setBigDecimal(8, toAdd.getPrice());
                    //     ps.setString(9, toAdd.getImagePath());

                    return ps;
                },
                kh);
        int generatedId = kh.getKey().intValue();

        toAdd.setListingId(generatedId);

        for (int keywordId : keywordIds) {

            KeyHolder key = new GeneratedKeyHolder();
            int bridgeRowsAffected = template.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                "INSERT INTO ListingKeywords(KeywordId, ListingId) VALUES( ?, ?);",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setInt(1, keywordId);
                        ps.setInt(2, generatedId);

                        return ps;
                    },
                    key);
        }

        List<String> pathStrings = toAdd.getImagePaths();

        for (String s : pathStrings) {

            KeyHolder thirdKey = new GeneratedKeyHolder();
            int bRowsAffected = template.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                "INSERT INTO Images(pathString, ListingId) VALUES (?, ? );",
                                Statement.RETURN_GENERATED_KEYS
                        );

                        ps.setString(1, s);
                        ps.setInt(2, toAdd.getListingId());
                        return ps;
                    },
                    thirdKey);
        }

        return toAdd;

    }

    @Override
    public List<Listing> getAllListings() {
        List<Listing> allListings = template.query("Select * FROM CurrentListings",
                new ListingMapper());

        // only the condition ID is set in the listingMapper, so must go through and make full condition objects
        for (Listing ls : allListings) {
            Condition foundCondition = condition.getConditionById(ls.getListingCondition().getConditionId());
            ls.setListingCondition(foundCondition);

            List<Image> toAdd = template.query("SELECT * FROM images WHERE ListingId = ?;", new ImageMapper(), ls.getListingId());

            List<String> images = new ArrayList<>();
            for (Image i : toAdd) {
                images.add(i.getPathString());
            }
            ls.setImagePaths(images);

        }

        return allListings;
    }

    @Override
    public void deleteListing(int toDelete) {

        template.update(
                "UPDATE CurrentListings SET isDeleted = 1 WHERE ListingId = ?", toDelete);
    }

    @Override
    public List<Listing> getListingsByCondition(String conditionType) {

        String theQuery = "Select * \n"
                + "FROM CurrentListings cl \n"
                + "JOIN conditions c ON cl.conditionId = c.conditionId \n"
                + "WHERE c.conditionType = ? ";

        try {
            List<Listing> allListings = template.query(theQuery,
                    new ListingMapper(), conditionType);

            for (Listing l : allListings) {
                List<Image> toAdd = template.query("SELECT * FROM images WHERE ListingId = ?;", new ImageMapper(), l.getListingId());

                List<String> images = new ArrayList<>();
                for (Image i : toAdd) {
                    images.add(i.getPathString());
                }
                l.setImagePaths(images);
            }

            return allListings;

        } catch (DataAccessException ex) {
            return null;
        }

    }

    private static class ListingMapper implements RowMapper<Listing> {

        @Override
        public Listing mapRow(ResultSet row, int rowNum) throws SQLException {

            Listing converted = new Listing();

            Condition listingCondition = new Condition();

            listingCondition.setConditionId(row.getInt("conditionId"));
//            listingCondition.setConditionType(row.getString("ConditionType"));

            converted.setListingId(row.getInt("ListingId"));
            converted.setListingCondition(listingCondition);
            converted.setTitle(row.getString("Title"));
            converted.setCity(row.getString("City"));
            converted.setDate(row.getDate("ListDate").toLocalDate());
            converted.setContent(row.getString("Content"));
            converted.setIsDeleted(row.getBoolean("isDeleted"));
            converted.setUserId(row.getInt("userId"));
            converted.setPrice(row.getBigDecimal("price"));
            //    converted.setImagePath(row.getString("imagePath"));

            return converted;
        }
    }

    private static class ImageMapper implements RowMapper<Image> {

        @Override
        public Image mapRow(ResultSet row, int rowNum) throws SQLException {

            Image converted = new Image();

            converted.setImageId(row.getInt("ImageId"));
            converted.setListingId(row.getInt("ListingId"));
            converted.setPathString(row.getString("pathString"));

            return converted;
        }

    }
}

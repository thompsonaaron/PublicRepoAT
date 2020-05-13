/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.daos;

import com.sg.ChrisList.models.Keyword;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 *
 * @author board
 */
@Repository
public class KeywordDaoDB implements KeywordDao {

    @Autowired
    JdbcTemplate template;

    @Override
    public Keyword addKeyword(Keyword toAdd) {

        KeyHolder kh = new GeneratedKeyHolder();

        int rowsAffected = template.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO keyword (name, isDeleted) VALUES (?,?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setString(1, toAdd.getName());
                    ps.setBoolean(2, toAdd.isDeleted());

                    return ps;
                },
                kh);
        int generatedId = kh.getKey().intValue();

        toAdd.setId(generatedId);

        return toAdd;

    }

    @Override
    public void deleteKeyword(int id) {
        template.update(
                "UPDATE CurrentKeyword SET isDeleted = 1 WHERE KeywordId = ?", id);
    }

    @Override
    public void editKeyword(Keyword toEdit) {
        template.update(
                "UPDATE keyword SET name = ?, isDeleted = ? WHERE keywordId = ?"
                , toEdit.getName(),toEdit.isDeleted(), toEdit.getId());
    }

    @Override
    public Keyword getKeywordById(int id) {
        try {
            Keyword retrieved = template.queryForObject(
                    "SELECT * FROM keyword WHERE keywordId = ?", new KeywordMapper(), id);
            
            return retrieved;
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<Keyword> getAllKeywords() {
        return template.query("SELECT * FROM keyword", new KeywordMapper());
    }

    @Override
    public List<Keyword> getAllActiveKeywords() {
        return template.query(
                "SELECT * FROM keyword WHERE isDeleted = 0 ORDER BY name asc", new KeywordMapper());
    }

    public static class KeywordMapper implements RowMapper<Keyword> {

        @Override
        public Keyword mapRow(ResultSet row, int i) throws SQLException {

            Keyword toReturn = new Keyword();

            toReturn.setId(row.getInt("keywordId"));
            toReturn.setName(row.getString("name"));
            toReturn.setDeleted(row.getBoolean("isDeleted"));

            return toReturn;
        }

    }

}

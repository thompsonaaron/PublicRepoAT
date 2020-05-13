/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.daos;

import com.sg.ChrisList.models.Condition;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thomp
 */
@Repository
public class ConditionDaoDB implements ConditionDao {
    
    @Autowired
    JdbcTemplate template; 
    
    @Override
    public List<Condition> getAllConditions(){        
        return template.query("SELECT * FROM conditions", new conditionMapper());
    }
    
    @Override
    public Condition getConditionById(Integer id){
        
        try{
            return template.queryForObject("SELECT * FROM conditions WHERE conditionId = ?", new conditionMapper(), id);
        } catch (DataAccessException ex){
            return null;
        }
    }

    private static class conditionMapper implements RowMapper<Condition> {

        @Override
        public Condition mapRow(ResultSet row, int rowNum) throws SQLException {
            Condition converted = new Condition();
            
            converted.setConditionId(row.getInt("conditionId"));
            converted.setConditionType(row.getString("conditionType"));
            
            return converted;
        } 
    }
    
}

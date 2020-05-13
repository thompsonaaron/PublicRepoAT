/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.daos;

import com.sg.ChrisList.models.Role;
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
 * @author board
 */
@Repository
public class RoleDaoDB implements RoleDao {

    @Autowired
    JdbcTemplate template;

    @Override
    public Role getRoleById(int id) {
        try {
            return template.queryForObject(
                    "SELECT * FROM `role` WHERE id = ?", new RoleMapper(), id);
        } catch (DataAccessException ex){
            return null;
        }
    }

    @Override
    public Role getRoleByRole(String role) {
        try{
            return template.queryForObject(
                    "SELECT * FROM `role` WHERE `role` = ?", new RoleMapper(), role);
        } catch (DataAccessException ex) {
            return null;
        }
        
    }

    @Override
    public List<Role> getAllRoles() {

        return template.query("SELECT * FROM `role`", new RoleMapper());
    }

    @Override
    public void deleteRole(int id) {
  template.update("UPDATE `role` SET enabled = 0 WHERE id = ?", id);
    }

    public static class RoleMapper implements RowMapper<Role> {

        @Override
        public Role mapRow(ResultSet row, int i) throws SQLException {

            Role toReturn = new Role();
            toReturn.setId(row.getInt("id"));
            toReturn.setRole((row.getString("role")));

            return toReturn;
        }
    }

}

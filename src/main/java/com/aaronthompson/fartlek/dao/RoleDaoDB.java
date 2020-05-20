/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.fartlek.dao;

import com.aaronthompson.fartlek.entities.Role;
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
public class RoleDaoDB implements RoleDao {

    @Autowired
    JdbcTemplate template;

    @Override
    public Role getRoleById(int id) {
        try {
            return template.queryForObject(
                    "SELECT * FROM roles WHERE id = ?", new RoleMapper(), id);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public Role getRoleByName(String name) {
        try {
            return template.queryForObject(
                    "SELECT * FROM roles WHERE role = ?", new RoleMapper(), name);
        } catch (DataAccessException ex) {
            return null;
        }

    }

    @Override
    public List<Role> getAllRoles() {

        return template.query("SELECT * FROM roles", new RoleMapper());
    }

    public static class RoleMapper implements RowMapper<Role> {

        @Override
        public Role mapRow(ResultSet row, int i) throws SQLException {

            Role toReturn = new Role();
            toReturn.setRoleId(row.getInt("roleId"));
            toReturn.setRole((row.getString("name")));

            return toReturn;
        }
    }

}

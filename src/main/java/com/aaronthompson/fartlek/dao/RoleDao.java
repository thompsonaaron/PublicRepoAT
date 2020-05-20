/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.fartlek.dao;

import com.aaronthompson.fartlek.entities.Role;
import java.util.List;

/**
 *
 * @author thomp
 */
public interface RoleDao {

    public Role getRoleById(int id);

    public Role getRoleByName(String name);

    public List<Role> getAllRoles();

}

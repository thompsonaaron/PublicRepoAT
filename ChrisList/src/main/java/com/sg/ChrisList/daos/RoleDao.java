/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.daos;

import com.sg.ChrisList.models.Role;
import java.util.List;

/**
 *
 * @author board
 */
public interface RoleDao {
    
    Role getRoleById(int id);
    Role getRoleByRole(String role);
    public void deleteRole(int id);
    List<Role> getAllRoles();
    
}

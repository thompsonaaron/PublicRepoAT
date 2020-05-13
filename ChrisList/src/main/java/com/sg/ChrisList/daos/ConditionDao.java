/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.daos;

import com.sg.ChrisList.models.Condition;
import java.util.List;

/**
 *
 * @author thomp
 */
public interface ConditionDao {
    
    public List<Condition> getAllConditions();
    
    public Condition getConditionById(Integer id);
    
}

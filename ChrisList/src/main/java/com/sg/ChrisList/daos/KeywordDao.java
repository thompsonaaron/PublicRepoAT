/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.daos;

import com.sg.ChrisList.models.Keyword;
import java.util.List;

/**
 *
 * @author board
 */
public interface KeywordDao {
    
    Keyword addKeyword(Keyword toAdd);
    
    void deleteKeyword(int id);
    
    void editKeyword(Keyword toEdit);
    
    List<Keyword> getAllKeywords();
    
    List<Keyword> getAllActiveKeywords();
    
    public Keyword getKeywordById(int id);
}

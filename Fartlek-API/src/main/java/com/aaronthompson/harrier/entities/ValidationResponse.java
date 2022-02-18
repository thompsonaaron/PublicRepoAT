/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.entities;

import java.util.List;

/**
 *
 * @author thomp
 */
public class ValidationResponse {
    
    private String status;
    private List errorMessageList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List getErrorMessageList() {
        return errorMessageList;
    }

    public void setErrorMessageList(List errorMessageList) {
        this.errorMessageList = errorMessageList;
    }
    
    
}

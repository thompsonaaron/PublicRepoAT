/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.models;

import java.util.Objects;

/**
 *
 * @author ursul
 */
public class Condition {
    
    private int conditionId;
    private String conditionType;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.conditionId;
        hash = 17 * hash + Objects.hashCode(this.conditionType);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Condition other = (Condition) obj;
        if (this.conditionId != other.conditionId) {
            return false;
        }
        if (!Objects.equals(this.conditionType, other.conditionType)) {
            return false;
        }
        return true;
    }

    public int getConditionId() {
        return conditionId;
    }

    public void setConditionId(int conditionId) {
        this.conditionId = conditionId;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }
    
}

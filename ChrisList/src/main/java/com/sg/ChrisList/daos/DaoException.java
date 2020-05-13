/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.daos;

/**
 *
 * @author thomp
 */
public class DaoException extends Exception {

    public DaoException(String msg) {
        super(msg);
    }

    public DaoException(String msg, Throwable inner) {
        super(msg, inner);
    }

}

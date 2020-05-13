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
public class Image {

    private int imageId;
    private int listingId;
    private String path;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.imageId;
        hash = 41 * hash + this.listingId;
        hash = 41 * hash + Objects.hashCode(this.path);
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
        final Image other = (Image) obj;
        if (this.imageId != other.imageId) {
            return false;
        }
        if (this.listingId != other.listingId) {
            return false;
        }
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        return true;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getListingId() {
        return listingId;
    }

    public void setListingId(int listingId) {
        this.listingId = listingId;
    }

    public String getPathString() {
        return path;
    }

    public void setPathString(String pathString) {
        this.path = pathString;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.entities;

/**
 *
 * @author thomp
 */
public class ListingFile {

    private Integer listingFileId;
    private Integer listingId;
    private String filePath;
    private String title;

    public Integer getListingFileId() {
        return listingFileId;
    }

    public void setListingFileId(Integer listingFileId) {
        this.listingFileId = listingFileId;
    }

    public Integer getListingId() {
        return listingId;
    }

    public void setListingId(Integer listingId) {
        this.listingId = listingId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}

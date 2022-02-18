/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.entities;

import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;

/**
 *
 * @author thomp
 */
public class Listing {

    private Integer listingId;
    private Integer teamId;
    private String title;
    private LocalDateTime dateTime;

    @NotBlank(message = "Please enter summary for your listing")
    private String content;
    private Boolean isDeleted;
    private List<ListingFile> listingFiles;
    
    public Integer getListingId() {
        return listingId;
    }

    public void setListingId(Integer listingId) {
        this.listingId = listingId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public List<ListingFile> getListingFiles() {
        return listingFiles;
    }

    public void setListingFiles(List<ListingFile> listingFiles) {
        this.listingFiles = listingFiles;
    }

}

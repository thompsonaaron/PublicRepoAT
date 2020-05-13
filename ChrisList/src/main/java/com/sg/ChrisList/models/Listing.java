/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author John S (user20)
 */
public class Listing {
    
    @NotNull(message="Please select keywords")
    private int listingId;
    
    @NotBlank(message="Please enter title")
    private String title;
    
    @NotBlank(message="Please enter city")
    private String city;
    
    @Min(0)
    @NotNull(message="Please enter price")
    @Digits(integer=10, fraction=2)
    private BigDecimal price;
    
    private LocalDate date;
    
    @NotBlank(message="Please enter summary for your listing")
    private String content;
    
    private int userId;
    
    private Boolean isDeleted;
    
    private Condition listingCondition;
    
    private List<Keyword> keywords;
    
   private List<String> imagePaths;

   
   
    public int getListingId() {
        return listingId;
    }

    public void setListingId(int listingId) {
        this.listingId = listingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Condition getListingCondition() {
        return listingCondition;
    }

    public void setListingCondition(Condition listingCondition) {
        this.listingCondition = listingCondition;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.listingId;
        hash = 73 * hash + Objects.hashCode(this.title);
        hash = 73 * hash + Objects.hashCode(this.city);
        hash = 73 * hash + Objects.hashCode(this.price);
        hash = 73 * hash + Objects.hashCode(this.date);
        hash = 73 * hash + Objects.hashCode(this.content);
        hash = 73 * hash + this.userId;
        hash = 73 * hash + Objects.hashCode(this.isDeleted);
        hash = 73 * hash + Objects.hashCode(this.listingCondition);
        hash = 73 * hash + Objects.hashCode(this.keywords);
        hash = 73 * hash + Objects.hashCode(this.imagePaths);
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
        final Listing other = (Listing) obj;
        if (this.listingId != other.listingId) {
            return false;
        }
        if (this.userId != other.userId) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.city, other.city)) {
            return false;
        }
        if (!Objects.equals(this.content, other.content)) {
            return false;
        }
        if (!Objects.equals(this.price, other.price)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.isDeleted, other.isDeleted)) {
            return false;
        }
        if (!Objects.equals(this.listingCondition, other.listingCondition)) {
            return false;
        }
        if (!Objects.equals(this.keywords, other.keywords)) {
            return false;
        }
        if (!Objects.equals(this.imagePaths, other.imagePaths)) {
            return false;
        }
        return true;
    }

    

    


    


}

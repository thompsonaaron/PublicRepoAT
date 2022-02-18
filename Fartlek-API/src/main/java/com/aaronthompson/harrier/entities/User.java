/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.harrier.entities;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author thomp
 */
public class User implements Comparable<User> {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) &&
                Objects.equals(privacyId, user.privacyId) &&
                Objects.equals(username, user.username) &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(email, user.email) &&
                Objects.equals(phoneNumber, user.phoneNumber) &&
                Objects.equals(city, user.city) &&
                Objects.equals(state, user.state) &&
                Objects.equals(country, user.country) &&
                Objects.equals(userRoles, user.userRoles) &&
                Objects.equals(userTeams, user.userTeams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, privacyId, username, firstName, lastName, email, phoneNumber, city, state, country, userRoles, userTeams);
    }

    private Integer userId;

    @NotNull
    private Integer privacyId;

    @Size(min = 4, message = "Please enter a username with at least 4 characters")
    private String username;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Size(min = 5, message = "Please enter a valid email address")
    private String email;

//    @Size(min = 7, max = 15, message = "Please enter a valid phone number")
    private String phoneNumber;

//    @NotBlank (message = "Please enter a city")
    private String city;
    private String state;
    private String country;
    private List<Role> userRoles;
    private List<Team> userTeams;
    
    // could have added List<Workout> userWorkouts here...

    public List<Team> getUserTeams() {
        return userTeams;
    }

    public void setUserTeams(List<Team> userTeams) {
        this.userTeams = userTeams;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPrivacyId() {
        return privacyId;
    }

    public void setPrivacyId(Integer privacyId) {
        this.privacyId = privacyId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Role> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<Role> userRoles) {
        this.userRoles = userRoles;
    }

    @Override
    public int compareTo(User u) {
        return this.getFirstName().compareTo(u.getFirstName());
    }
}

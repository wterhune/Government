package com.wisaterhunep.government;

import java.io.Serializable;
import java.util.Objects;

/*
This class is the data class for the GovernmentOfficial object.
It has a GovernmentOfficial constructor with all fields needed to show for the Officer Activity.
There is also getter methods, setter methods, toString method, equals method, and hashCode method here.
 */
public class GovernmentOfficial implements Serializable {
    private String role;
    private String name;
    private String politicalParty;
    private String address;
    private String phone;
    private String websiteURL;
    private String email;
    private String photo;
    private String facebook;
    private String twitter;
    private String youtube;

    //Constructor will have to have multiple String fields
    public GovernmentOfficial(String title, String name, String politicalParty, String address, String phoneNumber,
                              String websiteURL, String emailAddress, String photo, String facebook, String twitter, String youtube){
        this.name =  name;
        this.role =  title;
        this.politicalParty = politicalParty;
        this.address = address;
        this.phone = phoneNumber;
        this.websiteURL = websiteURL;
        this.email = emailAddress;
        this.photo = photo;
        this.facebook = facebook;
        this.twitter = twitter;
        this.youtube = youtube;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoliticalParty() {
        return politicalParty;
    }

    public void setPoliticalParty(String politicalParty) {
        this.politicalParty = politicalParty;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }

    public void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    @Override
    public String toString() {
        return "GovernmentOfficial{" +
                "role='" + role + '\'' +
                ", name='" + name + '\'' +
                ", politicalParty='" + politicalParty + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", websiteURL='" + websiteURL + '\'' +
                ", email='" + email + '\'' +
                ", photo='" + photo + '\'' +
                ", facebook='" + facebook + '\'' +
                ", twitter='" + twitter + '\'' +
                ", youtube='" + youtube + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GovernmentOfficial that = (GovernmentOfficial) o;
        return Objects.equals(role, that.role) &&
                Objects.equals(name, that.name) &&
                Objects.equals(politicalParty, that.politicalParty) &&
                Objects.equals(address, that.address) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(websiteURL, that.websiteURL) &&
                Objects.equals(email, that.email) &&
                Objects.equals(photo, that.photo) &&
                Objects.equals(facebook, that.facebook) &&
                Objects.equals(twitter, that.twitter) &&
                Objects.equals(youtube, that.youtube);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, name, politicalParty, address, phone, websiteURL, email, photo, facebook, twitter, youtube);
    }
}

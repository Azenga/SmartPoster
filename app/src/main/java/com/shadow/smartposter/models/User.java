package com.shadow.smartposter.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class User implements Serializable {

    @Exclude
    private String uid;

    private String name;
    private String nickName; //Optional
    private String contact;
    private String imageName; //Optional
    private String county;
    private String constituency;
    private String ward;
    private String website; //Optional
    private String group;

    public User() {
    }

    public User(String name, String contact, String county, String constituency, String ward, String group) {
        this.name = name;
        this.contact = contact;
        this.county = county;
        this.constituency = constituency;
        this.ward = ward;
        this.group = group;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getConstituency() {
        return constituency;
    }

    public void setConstituency(String constituency) {
        this.constituency = constituency;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}

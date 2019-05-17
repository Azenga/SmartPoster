package com.shadow.smartposter.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Application implements Serializable {

    @Exclude
    private String id;
    private String senderId;

    private String name;
    private String position;
    private String area;

    public Application() {
    }

    public Application(String senderId, String name, String position, String area) {
        this.senderId = senderId;
        this.name = name;
        this.position = position;
        this.area = area;
    }

    public String getId() {
        return id;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getArea() {
        return area;
    }

    public void setId(String id) {
        this.id = id;
    }
}

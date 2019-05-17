package com.shadow.smartposter.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Post implements Serializable {

    @Exclude
    private String id;

    private String ownerId;
    private String imageName;
    private String[] likes;
    private String caption;
    private Timestamp timestamp;

    public Post(){}

    public Post(String ownerId, String imageName, String[] likes, String caption, Timestamp timestamp) {
        this.ownerId = ownerId;
        this.imageName = imageName;
        this.likes = likes;
        this.caption = caption;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String[] getLikes() {
        return likes;
    }

    public void setLikes(String[] likes) {
        this.likes = likes;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}

package com.shadow.smartposter.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Post implements Serializable {

    @Exclude
    private String id;

    private String ownerId;
    private int image;
    private int likes;
    private String caption;

    public Post(String ownerId, int image, int likes, String caption) {
        this.ownerId = ownerId;
        this.image = image;
        this.likes = likes;
        this.caption = caption;
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

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}

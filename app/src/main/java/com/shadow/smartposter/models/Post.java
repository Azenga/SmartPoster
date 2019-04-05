package com.shadow.smartposter.models;

import java.io.Serializable;

public class Post implements Serializable {

    private String id;

    private String ownerAvatar;
    private String onwerUsername;
    //    private String image;
    private int image;
    private String type;
    private int likes;
    private String message;

    public Post(String ownerAvatar, String onwerUsername, int image, String type, int likes, String message) {
        this.ownerAvatar = ownerAvatar;
        this.onwerUsername = onwerUsername;
        this.image = image;
        this.type = type;
        this.likes = likes;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerAvatar() {
        return ownerAvatar;
    }

    public void setOwnerAvatar(String ownerAvatar) {
        this.ownerAvatar = ownerAvatar;
    }

    public String getOnwerUsername() {
        return onwerUsername;
    }

    public void setOnwerUsername(String onwerUsername) {
        this.onwerUsername = onwerUsername;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

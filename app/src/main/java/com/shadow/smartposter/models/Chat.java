package com.shadow.smartposter.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Chat implements Serializable {

    @Exclude
    private String uid;

    private String sender;
    private String receiver;
    private String content;

    public Chat() {
    }

    public Chat(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }
}

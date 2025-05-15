package com.example.myproject.models;

public class Message {
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private String timestamp;
    private boolean read;

    public Message(String id, String senderId, String content, String receiverId, String timestamp, boolean read) {
        this.id = id;
        this.senderId = senderId;
        this.content = content;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
        this.read = read;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

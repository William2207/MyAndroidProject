package com.example.myproject.models;

public class ChatBotMessage {
    private String content;
    private boolean isUser; // true: người dùng, false: chatbot

    public ChatBotMessage(String content, boolean isUser) {
        this.content = content;
        this.isUser = isUser;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public String getContent() {
        return content;
    }

    public boolean isUser() {
        return isUser;
    }
}

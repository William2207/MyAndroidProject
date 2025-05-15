package com.example.myproject.models;

import java.util.List;

public class ChatRequest {
    public String model;
    public List<MessageBot> messages;

    public ChatRequest(String model, List<MessageBot> messages) {
        this.model = model;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<MessageBot> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageBot> messages) {
        this.messages = messages;
    }
}

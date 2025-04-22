package com.example.myproject.models;

import com.google.gson.annotations.SerializedName;

public class User {
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @SerializedName("profileImage")
    private String image;
    @SerializedName("username")
    private String name;
    private String email;

    private String bio;

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public User(String name, String image, String email, String bio){
        this.email = email;
        this.name = name;
        this.image = image;
        this.bio = bio;
    }



    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}

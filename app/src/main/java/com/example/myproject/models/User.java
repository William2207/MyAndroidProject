package com.example.myproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {
    private int userId;
    @SerializedName("profileImage")
    private String image;
    @SerializedName("username")
    private String name;
    private String email;
    private String bio;

    private int followers;
    private int followings;

    private int posts;

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowings() {
        return followings;
    }

    public void setFollowings(int followings) {
        this.followings = followings;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }



    public User(int userId, String uname, String bio, String profileImage, String email) {
        this.userId = userId;
        this.name = uname;
        this.bio = bio;
        this.image = profileImage;
        this.email = email;
    }

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
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    protected User(Parcel in) {
        userId = in.readInt();
        image = in.readString();
        name = in.readString();
        email = in.readString();
        bio = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(bio);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


}

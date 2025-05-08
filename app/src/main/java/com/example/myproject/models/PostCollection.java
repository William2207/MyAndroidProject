package com.example.myproject.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostCollection {
    @SerializedName("id")
    private String id;

    @SerializedName("post_id")
    private int postId;

    public PostCollection(int userId, String content, List<String> mediaUrls) {
        this.userId = userId;
        this.content = content;
        this.mediaUrls = mediaUrls;
    }

    @SerializedName("user_id")
    private int userId;

    @SerializedName("content")
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @SerializedName("media_urls")
    private List<String> mediaUrls;

    @SerializedName("created_at")
    private String createdAt; // Dùng String thay LocalDateTime để dễ parse JSON
}

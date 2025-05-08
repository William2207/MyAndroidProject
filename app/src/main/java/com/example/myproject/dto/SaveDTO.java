package com.example.myproject.dto;

import com.google.gson.annotations.SerializedName;

public class SaveDTO {
    @SerializedName("userId")
    private int userId;

    @SerializedName("postId")
    private int postId;

    public SaveDTO(int userId, int postId) {
        this.userId = userId;
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}

package com.example.myproject.dto;

import com.google.gson.annotations.SerializedName;

public class LikeDTO {
    @SerializedName("userId")
    private int userId;

    @SerializedName("postId")
    private int postId;

    public int getUserId() {
        return userId;
    }

    public LikeDTO(int userId, int postId) {
        this.userId = userId;
        this.postId = postId;
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

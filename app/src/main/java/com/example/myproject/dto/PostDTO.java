package com.example.myproject.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.myproject.models.User;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO implements Parcelable {
    @SerializedName("postId")
    private int postId;

    @SerializedName("user")
    private User user;

    @SerializedName("likes_count")
    private int likesCount;

    @SerializedName("content")
    private String content;

    @SerializedName("mediaUrls")
    private List<String> mediaUrls;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("comments")
    private List<CommentDTO> comments;
    @SerializedName("liked")
    private boolean liked;
    @SerializedName("saved")
    private boolean saved;

    protected PostDTO(Parcel in) {
        postId = in.readInt();
        user = in.readParcelable(User.class.getClassLoader());
        likesCount = in.readInt();
        content = in.readString();
        mediaUrls = in.createStringArrayList();
        createdAt = in.readString();
        comments = in.createTypedArrayList(CommentDTO.CREATOR);
        liked = in.readByte() != 0;
        saved = in.readByte() != 0;
    }

    public static final Creator<PostDTO> CREATOR = new Creator<PostDTO>() {
        @Override
        public PostDTO createFromParcel(Parcel in) {
            return new PostDTO(in);
        }

        @Override
        public PostDTO[] newArray(int size) {
            return new PostDTO[size];
        }
    };

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(postId);
        parcel.writeParcelable(user, i);
        parcel.writeInt(likesCount);
        parcel.writeString(content);
        parcel.writeStringList(mediaUrls);
        parcel.writeString(createdAt);
        parcel.writeTypedList(comments);
        parcel.writeByte((byte) (liked ? 1 : 0));
        parcel.writeByte((byte) (saved ? 1 : 0));
    }
}

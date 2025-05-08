package com.example.myproject.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.myproject.models.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CommentDTO implements Parcelable {
    @SerializedName("commentId")
    private Integer commentId;

    @SerializedName("parentId")
    private Integer parentId;

    @SerializedName("user")
    private User user;

    @SerializedName("content")
    private String content;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("replies")
    private List<CommentDTO> replies;


    public CommentDTO(int commentId, User user, String content, String createdAt, List<CommentDTO> replies) {
        this.commentId = commentId;
        this.user = user;
        this.content = content;
        this.createdAt = createdAt;
        this.replies = replies;
    }



    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
    public CommentDTO() {

    }

    protected CommentDTO(Parcel in) {
        commentId = in.readByte() == 0 ? null : in.readInt();
        parentId = in.readByte() == 0 ? null : in.readInt();
        content = in.readString();
        createdAt = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        replies = new ArrayList<>();
        in.readTypedList(replies, CommentDTO.CREATOR);
    }

    public static final Creator<CommentDTO> CREATOR = new Creator<CommentDTO>() {
        @Override
        public CommentDTO createFromParcel(Parcel in) {
            return new CommentDTO(in);
        }

        @Override
        public CommentDTO[] newArray(int size) {
            return new CommentDTO[size];
        }
    };

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<CommentDTO> getReplies() {
        return replies;
    }

    public void setReplies(List<CommentDTO> replies) {
        this.replies = replies;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (commentId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(commentId);
        }
        if (parentId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(parentId);
        }
        dest.writeString(content);
        dest.writeString(createdAt);
        dest.writeParcelable(user, flags);
        dest.writeTypedList(replies);
    }
}

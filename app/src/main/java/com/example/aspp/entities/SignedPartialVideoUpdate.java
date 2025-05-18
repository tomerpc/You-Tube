package com.example.aspp.entities;

import androidx.room.Ignore;

import java.util.Arrays;
import java.util.List;

public class SignedPartialVideoUpdate {

    @Ignore
    private List<String> usersLikes;
    @Ignore private List<Comment> comments;
    private int likeCount, views;

    public SignedPartialVideoUpdate(List<String> usersLikes, List<Comment> comments, int likeCount, int views) {
        this.usersLikes = usersLikes;
        this.comments = comments;
        this.likeCount = likeCount;
        this.views = views;
    }

    public SignedPartialVideoUpdate() {
    }

    @Override
    public String toString() {
        return "SignedPartialVideoUpdate{" +
                "usersLikes=" + usersLikes +
                ", comments=" + comments +
                ", likeCount=" + likeCount +
                ", views=" + views +
                '}';
    }

    public List<String> getUsersLikes() {
        return usersLikes;
    }

    public void setUsersLikes(List<String> usersLikes) {
        this.usersLikes = usersLikes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }
}

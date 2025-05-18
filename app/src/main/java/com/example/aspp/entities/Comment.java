package com.example.aspp.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@Entity
public class Comment implements Serializable{
//    @PrimaryKey(autoGenerate = true)

    String _id;
    String user, content;
    Date date;
    List<String> usersLikes;
    List<Reply> replies;

    public Comment() {
    }

    public Comment(String _id, String user, String content, Date date, List<String> usersLikes, List<Reply> replies) {
        this._id = _id;
        this.user = user;
        this.content = content;
        this.date = date;
        this.usersLikes = usersLikes;
        this.replies = replies;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getUsersLikes() {
        return usersLikes;
    }

    public void setUsersLikes(List<String> usersLikes) {
        this.usersLikes = usersLikes;
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }
}

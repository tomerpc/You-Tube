package com.example.aspp.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.List;
@Entity
public class Reply {

    @PrimaryKey @NonNull String _id;
    String user, content;
    Date date;
    List<String> usersLikes;

    public Reply() {
    }

    public Reply(String _id, String user, String content, Date date, List<String> usersLikes) {
        this._id = _id;
        this.user = user;
        this.content = content;
        this.date = date;
        this.usersLikes = usersLikes;
    }

    public Reply(Comment comment) {
        this._id = comment._id;
        this.user = comment.getUser();
        this.content = comment.getContent();
        this.date = comment.getDate();
        this.usersLikes = comment.getUsersLikes();
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
}

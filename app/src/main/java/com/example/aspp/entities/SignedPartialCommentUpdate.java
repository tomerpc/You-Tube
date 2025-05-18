package com.example.aspp.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@Entity
public class SignedPartialCommentUpdate implements Serializable {
//    @PrimaryKey(autoGenerate = true)

    String _id;
    List<String> usersLikes;
    List<Reply> replies;

    public SignedPartialCommentUpdate() {
    }

    public SignedPartialCommentUpdate(String _id, List<String> usersLikes, List<Reply> replies) {
        this._id = _id;
        this.usersLikes = usersLikes;
        this.replies = replies;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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
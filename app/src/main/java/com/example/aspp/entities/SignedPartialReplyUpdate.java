package com.example.aspp.entities;

import java.util.List;

public class SignedPartialReplyUpdate {
    String _id;
    List<String> usersLikes;

    public SignedPartialReplyUpdate(String _id, List<String> usersLikes) {
        this._id = _id;
        this.usersLikes = usersLikes;
    }

    public SignedPartialReplyUpdate(Reply update) {
        this._id = update.get_id();
        this.usersLikes = update.getUsersLikes();
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
}

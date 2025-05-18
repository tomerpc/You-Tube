package com.example.aspp.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
@Entity
public class User implements Serializable {

    @PrimaryKey @NonNull
    String _id;
    String username, displayname, password, image;
    int __v;

    public User() {
    }

    public User(String _id, String username, String displayname, String password, String image, int __v) {
        this._id = _id;
        this.username = username;
        this.displayname = displayname;
        this.password = password;
        this.image = image;
        this.__v = __v;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    @Override
    public String toString() {
        return "User{" +
                "_id='" + _id + '\'' +
                ", username='" + username + '\'' +
                ", displayname='" + displayname + '\'' +
                ", password='" + password + '\'' +
                ", image='" + image + '\'' +
                ", __v=" + __v +
                '}';
    }
}


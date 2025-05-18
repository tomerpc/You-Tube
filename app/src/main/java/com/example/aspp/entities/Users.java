package com.example.aspp.entities;

import com.example.aspp.Helper;

public class Users {
    String _id, username, displayname, password, passwordAgain, image;

    public Users(String _id, String username, String displayname, String password, String passwordAgain, String image) {
        this.username = username;
        this.displayname = displayname;
        this.password = password;
        this.passwordAgain = passwordAgain;
        this.image = image;
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

    public String getPasswordAgain() {
        return passwordAgain;
    }

    public void setPasswordAgain(String passwordAgain) {
        this.passwordAgain = passwordAgain;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

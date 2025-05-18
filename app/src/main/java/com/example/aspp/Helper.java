package com.example.aspp;

import android.app.Application;
import android.content.Context;

import androidx.browser.trusted.Token;

import com.example.aspp.entities.User;

public class Helper extends Application {
    public static Context context;
    public static String token;
    public static User signedInUser;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static User getSignedInUser() {
        return signedInUser;
    }

    public static void setSignedInUser(User signedInUser) {
        Helper.signedInUser = signedInUser;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Helper.token = token;
//        Log.i("Token", token.serialize().toString());
    }
    public static boolean isSignedIn() {
        if (signedInUser != null) {
            return true;
        }
        return false;
    }
}

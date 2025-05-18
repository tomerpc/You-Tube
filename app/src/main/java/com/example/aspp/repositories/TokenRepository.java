package com.example.aspp.repositories;

import android.content.Context;

import androidx.browser.trusted.Token;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.aspp.api.TokenAPI;
import com.example.aspp.api.UserAPI;
import com.example.aspp.dao.UserDao;
import com.example.aspp.entities.AuthResponse;
import com.example.aspp.entities.User;

import java.util.List;

public class TokenRepository {
    private TokenRepository.TokenData TokenData;
    private UserDao dao;
    private TokenAPI api;
    private Context context;

    public TokenRepository() {
//        this.context = context;
        TokenData = new TokenRepository.TokenData();
        api= new TokenAPI();
    }

    public LiveData<AuthResponse> processLogin(User user) {
        api.processLogin(TokenData, user);
        return TokenData;
    }
    class TokenData extends MutableLiveData<AuthResponse> {
        public TokenData() {
            super();
            //load data from db
            setValue(new AuthResponse());
        }

        @Override
        protected void onActive() {
            super.onActive();

//            new Thread(() -> {
//                VideoAPI api = new VideoAPI(Helper.context);
//                api.getAllVideos(this);
//            }).start();
        }
    }
}

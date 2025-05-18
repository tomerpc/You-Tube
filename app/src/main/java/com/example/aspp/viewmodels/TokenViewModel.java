package com.example.aspp.viewmodels;

import androidx.browser.trusted.Token;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.aspp.entities.AuthResponse;
import com.example.aspp.entities.User;
import com.example.aspp.entities.Users;
import com.example.aspp.repositories.TokenRepository;
import com.example.aspp.repositories.UserRepository;

import java.util.List;

public class TokenViewModel extends ViewModel {
    private TokenRepository repository;
    private LiveData<AuthResponse> token;

    public TokenViewModel () {
        repository = new TokenRepository();
    }
    public LiveData<AuthResponse> processLogin(User user) {
        token = repository.processLogin(user);
        return token;
    }
}


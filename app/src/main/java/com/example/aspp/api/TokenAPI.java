package com.example.aspp.api;

import static androidx.camera.core.impl.utils.ContextUtil.getApplicationContext;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.browser.trusted.Token;
import androidx.lifecycle.MutableLiveData;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.aspp.Helper;
import com.example.aspp.R;
import com.example.aspp.entities.AuthResponse;
import com.example.aspp.entities.User;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenAPI {
    Retrofit retrofit;
    tokenWebServiceAPI tokenWebServiceAPI;
    public TokenAPI() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Helper.context.getResources().getString(R.string.BaseURL))
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tokenWebServiceAPI = retrofit.create(tokenWebServiceAPI.class);
    }
    public void processLogin(MutableLiveData<AuthResponse> token, User user) {
        Call<AuthResponse> call = tokenWebServiceAPI.processLogin(user);

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
//                Log.i("All Videos", response.);
                token.postValue(response.body());
//                saveToken(token);
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }

}

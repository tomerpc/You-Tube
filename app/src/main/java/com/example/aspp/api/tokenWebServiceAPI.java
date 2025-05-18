package com.example.aspp.api;

import com.example.aspp.entities.AuthResponse;
import com.example.aspp.entities.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface tokenWebServiceAPI {
    @POST("tokens")
    Call<AuthResponse> processLogin(@Body User newUser);
}

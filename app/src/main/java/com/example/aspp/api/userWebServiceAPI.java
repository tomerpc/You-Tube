package com.example.aspp.api;

import com.example.aspp.entities.User;
import com.example.aspp.entities.Users;
import com.example.aspp.entities.Video;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface userWebServiceAPI {
    @GET("users")
    Call<List<User>> getAllUsers();

    @GET("users/{id}")
    Call<User> getUser(@Path("id") String id);

    @POST("users")
    Call<User> createUser(@Body Users newUser);
    @GET("users/{id}/videos")
    Call<List<Video>> getUserVideos(@Path("id") String id);

    @GET("users/username/{username}")
    Call<User> getUserByUserName(@Path("username") String username);


    @PUT("users/{id}")
    Call<User> updateUser(@Header("authorization") String token, @Path("id") String id, @Body Users updatedUser);

    @PATCH("users/{id}")
    Call<Video> partialUpdateUser(@Path("id") String id, @Body User updatedUser);

    @DELETE("users/{id}")
    Call<Void> deleteUser(@Header("authorization") String token, @Path("id") String id);
}

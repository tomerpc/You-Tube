package com.example.aspp.api;

import androidx.browser.trusted.Token;

import com.example.aspp.Helper;
import com.example.aspp.entities.RelatedVideosHelper;
import com.example.aspp.entities.SignedPartialVideoUpdate;
import com.example.aspp.entities.UnsignedPartialVideoUpdate;
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
import retrofit2.http.Query;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;



public interface videoWebServiceAPI {
    @GET("videos/all")
    Call<List<Video>> getAllVideos();
    @GET("videos/all")
    Call<List<Video>> getAllVideos(@Query("username") String username);

    @GET("videos")
    Call<List<Video>> getVideos();

    @GET("videos/{id}/related")
    Call<RelatedVideosHelper> getRelatedVideos(@Path("id") String id);

    @GET("videos/{id}")
    Call<Video> getVideoById(@Path("id") String id);

    @POST("videos")
    Call<Video> createVideo(@Header("authorization") String token,@Body Video newVid);

    @PUT("videos/{id}")
    Call<Video> updateVideo(@Path("id") String id, @Header("authorization") String token, @Body Video updatedVid);

    @PATCH("videos/{id}")
    Call<Video> partialUpdateVideo(@Path("id") String id
            , @Header("authorization") String token, @Body SignedPartialVideoUpdate partialUpdate);

    @PATCH("videos/{id}")
    Call<Video> partialUpdateVideo(@Path("id") String id
            , @Header("authorization") String token, @Body UnsignedPartialVideoUpdate partialUpdate);

    @DELETE("videos/{id}")
    Call<Void> deleteVideo(@Path("id") String id);
}

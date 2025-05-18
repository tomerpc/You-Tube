package com.example.aspp.api;

import com.example.aspp.entities.Comment;
import com.example.aspp.entities.Comments;
import com.example.aspp.entities.SignedPartialCommentUpdate;
import com.example.aspp.entities.SignedPartialVideoUpdate;
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

public interface commentWebServiceAPI {
    @GET("videos/{videoId}/comments")
    Call<Comments> getComments(@Path("videoId") String videoId);

    @GET("videos/{videoId}/comments/{commentId}")
    Call<Comment> getComment(@Path("videoId") String videoId, @Path("commentId") String commentId);

    @POST("videos/{id}/comments")
    Call<Comment> createComment(@Path("id") String id
            , @Header("authorization") String token, @Body Comment partialUpdate);
    @PUT("videos/{videoId}/comments/{commentId}")
    Call<Comment> updateComment(@Path("videoId") String videoId, @Path("commentId") String commentId
            , @Header("authorization") String token, @Body Comment update);

    @PATCH("videos/{videoId}/comments/{commentId}")
    Call<Comment> partialUpdateComment(@Path("videoId") String videoId, @Path("commentId") String commentId
            , @Header("authorization") String token, @Body SignedPartialCommentUpdate update);

    @DELETE("videos/{videoId}/comments/{commentId}")
    Call<Void> deleteComment(@Path("videoId") String videoId, @Path("commentId") String commentId
            , @Header("authorization") String token);
}

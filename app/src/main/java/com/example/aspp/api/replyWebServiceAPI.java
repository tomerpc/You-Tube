package com.example.aspp.api;

import com.example.aspp.entities.Comment;
import com.example.aspp.entities.Reply;
import com.example.aspp.entities.SignedPartialReplyUpdate;
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

public interface replyWebServiceAPI {
    @GET("videos/{videoId}/comments/{commentId}/replies")
    Call<List<Reply>> getReplies(@Path("videoId") String videoId, @Path("commentId") String commentId);

    @GET("videos/{videoId}/comments/{commentId}/replies/{replyId}")
    Call<Reply> getReply(@Path("videoId") String videoId, @Path("commentId") String commentId
            , @Path("replyId") String replyId);

    @POST("videos/{videoId}/comments/{commentId}/replies")
    Call<Reply> createReply(@Path("videoId") String id, @Path("commentId") String cId
            , @Header("authorization") String token, @Body Reply update);

    @PATCH("videos/{videoId}/comments/{commentId}/replies/{replyId}")
    Call<Reply> partialUpdateReply(@Path("videoId") String videoId, @Path("commentId") String commentId
            , @Header("authorization") String token, @Path("replyId") String replyId
            , @Body SignedPartialReplyUpdate update);


    @PUT("videos/{videoId}/comments/{commentId}/replies/{replyId}")
    Call<Reply> updateReply(@Path("videoId") String videoId, @Path("commentId") String commentId
            , @Header("authorization") String token, @Path("replyId") String replyId
            , @Body Reply update);

    @DELETE("videos/{videoId}/comments/{commentId}/replies/{replyId}")
    Call<Reply> deleteReply(@Path("videoId") String videoId, @Path("commentId") String commentId
            , @Header("authorization") String token, @Path("replyId") String replyId);
}

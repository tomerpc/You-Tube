package com.example.aspp.api;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.aspp.AuthInterceptor;
import com.example.aspp.Helper;
import com.example.aspp.R;
import com.example.aspp.entities.Comment;
import com.example.aspp.entities.Comments;
import com.example.aspp.entities.Reply;
import com.example.aspp.entities.SignedPartialCommentUpdate;

import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentAPI {
    Retrofit retrofit;
    commentWebServiceAPI commentWebServiceAPI;
    public CommentAPI() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(Helper.context))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Helper.context.getResources().getString(R.string.BaseURL))
                .client(client)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        commentWebServiceAPI = retrofit.create(commentWebServiceAPI.class);
    }

    public void getComments(MutableLiveData<List<Comment>> comments, String videoId) {
        Call<Comments> call = commentWebServiceAPI.getComments(videoId);

        call.enqueue(new Callback<Comments>() {
            @Override
            public void onResponse(Call<Comments> call, Response<Comments> response) {
//                Log.i("All Comments", response.body().toString());
                comments.postValue(response.body().getComments());
            }

            @Override
            public void onFailure(Call<Comments> call, Throwable t) {
                Log.e("ERROR GETTING COMMENTS", t.getMessage());
            }
        });
    }
    public void getComment(MutableLiveData<Comment> comments, String videoId, String commentId) {
        Call<Comment> call = commentWebServiceAPI.getComment(videoId, commentId);

        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                Log.i("All Videos", response.raw().toString());
                comments.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }
    public void createComment(MutableLiveData<Comment> commentData, Comment comment, String videoId) {
        Call<Comment> call = commentWebServiceAPI.createComment(videoId, Helper.getToken(), comment);

        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                Log.i("Create comment", response.raw().toString());
                commentData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }
    public void updateComment(MutableLiveData<Comment> commentData,
                              String videoId, String commentId, Comment comment) {
        Call<Comment> call = commentWebServiceAPI.updateComment(videoId, commentId, Helper.getToken(),comment);

        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                Log.i("All Videos", response.raw().toString());
                commentData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }
    public void partialUpdateComment(String videoId, String commentId, SignedPartialCommentUpdate comment) {
        Call<Comment> call = commentWebServiceAPI
                .partialUpdateComment(videoId, commentId, Helper.getToken(), comment);

        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                Log.i("Partial Comment Update", response.raw().toString());
//                reply.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }
    public void deleteComment(String videoId, String commentId) {
        Call<Void> call = commentWebServiceAPI.deleteComment(videoId, commentId, Helper.getToken());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i("All Videos", response.raw().toString());
//                reply.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }
}

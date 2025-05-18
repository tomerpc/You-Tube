package com.example.aspp.api;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.aspp.AuthInterceptor;
import com.example.aspp.Helper;
import com.example.aspp.R;
import com.example.aspp.entities.Reply;
import com.example.aspp.entities.SignedPartialReplyUpdate;
import com.example.aspp.entities.Video;

import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Path;

public class ReplyAPI {
    Retrofit retrofit;
    replyWebServiceAPI replyWebServiceAPI;
    public ReplyAPI() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(Helper.context))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Helper.context.getResources().getString(R.string.BaseURL))
                .client(client)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        replyWebServiceAPI = retrofit.create(replyWebServiceAPI.class);
    }
    public void getReplies(MutableLiveData<List<Reply>> replies, String videoId, String commentId) {
        Call<List<Reply>> call = replyWebServiceAPI.getReplies(videoId, commentId);

        call.enqueue(new Callback<List<Reply>>() {
            @Override
            public void onResponse(Call<List<Reply>> call, Response<List<Reply>> response) {
                Log.i("All Videos", response.raw().toString());
                replies.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Reply>> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }
    public void getReply(MutableLiveData<Reply> reply, String videoId, String commentId, String replyId) {
        Call<Reply> call = replyWebServiceAPI.getReply(videoId, commentId, replyId);

        call.enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(Call<Reply> call, Response<Reply> response) {
                Log.i("All Videos", response.raw().toString());
                reply.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Reply> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }
    public void createReply(MutableLiveData<Reply> replyLiveData, Reply reply, String videoId, String commentId) {
        Call<Reply> call = replyWebServiceAPI.createReply(videoId, commentId, Helper.getToken(),reply);

        call.enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(Call<Reply> call, Response<Reply> response) {
                Log.i("Reply", response.raw().toString());
                replyLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Reply> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }
    public void partialUpdateReply(String videoId, String commentId, SignedPartialReplyUpdate update) {
        Call<Reply> call = replyWebServiceAPI.partialUpdateReply(videoId, commentId, Helper.getToken(), update.get_id(), update);

        call.enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(Call<Reply> call, Response<Reply> response) {
                Log.i("Partial update", response.raw().toString());
//                reply.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Reply> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }
    public void updateReply(String videoId, String commentId, Reply update) {
        Call<Reply> call = replyWebServiceAPI.updateReply(videoId, commentId, Helper.getToken(), update.get_id(), update);

        call.enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(Call<Reply> call, Response<Reply> response) {
                Log.i("Update", response.raw().toString());
//                reply.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Reply> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }
    public void deleteReply(String videoId, String commentId, String replyId) {
        Call<Reply> call = replyWebServiceAPI.deleteReply(videoId, commentId, Helper.getToken(), replyId);

        call.enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(Call<Reply> call, Response<Reply> response) {
                Log.i("All Videos", response.raw().toString());
//                reply.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Reply> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }

}

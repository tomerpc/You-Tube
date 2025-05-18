package com.example.aspp.api;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.aspp.AuthInterceptor;
import com.example.aspp.Helper;
import com.example.aspp.R;
import com.example.aspp.entities.RelatedVideosHelper;
import com.example.aspp.entities.SignedPartialVideoUpdate;
import com.example.aspp.entities.UnsignedPartialVideoUpdate;
import com.example.aspp.entities.Video;

import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoAPI {
    private static final String TAG = "VideoAPI";
    private final Retrofit retrofit;
    private final videoWebServiceAPI videoWebServiceAPI;

    public VideoAPI(Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(context))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Helper.context.getResources().getString(R.string.BaseURL))
                .client(client)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        videoWebServiceAPI = retrofit.create(videoWebServiceAPI.class);
    }

    public void getVideos(MutableLiveData<List<Video>> videos) {
        Call<List<Video>> call = videoWebServiceAPI.getVideos();

        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                Log.i("Videos", response.raw().toString());
                videos.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    public void getAllVideos(MutableLiveData<List<Video>> videos) {
        Call<List<Video>> call = videoWebServiceAPI.getAllVideos();

        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                Log.i("All Videos", response.raw().toString());
                videos.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                Log.e(TAG, "Error fetching all videos: " + t.getMessage());
            }
        });
    }

    public void getAllVideos(MutableLiveData<List<Video>> videos, String username) {
        Call<List<Video>> call = videoWebServiceAPI.getAllVideos(username);
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                Log.i("All Videos of " + username, response.raw().toString());
                videos.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }
    public void getRelatedVideos(MutableLiveData<List<Video>> videos, String id) {
        Call<RelatedVideosHelper> call = videoWebServiceAPI.getRelatedVideos(id);
        call.enqueue(new Callback<RelatedVideosHelper>() {
            @Override
            public void onResponse(Call<RelatedVideosHelper> call, Response<RelatedVideosHelper> response) {
//                Log.i("RESPONSE", response.body().toString());
                videos.postValue(response.body().getRelatedVideos());
            }

            @Override
            public void onFailure(Call<RelatedVideosHelper> call, Throwable t) {
                Log.e(TAG, "Error fetching related videos: " + t.getMessage());
            }
        });
    }

    public void getVideoById(MutableLiveData<Video> video, String id) {
        Call<Video> call = videoWebServiceAPI.getVideoById(id);
        call.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
//                Log.i("RESPONSE", response.body().toString());
                video.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    public void createVideo(MutableLiveData<Video> videoLive, Video video) {
        Call<Video> call = videoWebServiceAPI.createVideo(Helper.getToken(), video);

        call.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                Log.i("CREATE RESPONSE", String.valueOf(response.isSuccessful()));
                Log.i("CREATE RESPONSE", response.toString());
                videoLive.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                Log.e("VideoAPI!!!!!", "Error creating video: " + t.getMessage());
            }
        });
    }

    public void updateVideo(MutableLiveData<Video> videoLive,Video video) {
        Call<Video> call = videoWebServiceAPI.updateVideo(video.get_id(), Helper.getToken(), video);

        call.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                Log.i("RESPONSE", response.body().toString());
                videoLive.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    public void partialUpdateVideo(MutableLiveData<Video> videoMutableLiveData
            , SignedPartialVideoUpdate video, String id) {
        Call<Video> call = videoWebServiceAPI.partialUpdateVideo(id, Helper.getToken(),video);

        call.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                Log.i("RESPONSE", String.valueOf(response.code()));
                videoMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }
    public void partialUpdateVideo(MutableLiveData<Video> videoMutableLiveData
            ,UnsignedPartialVideoUpdate video, String id) {
        Call<Video> call = videoWebServiceAPI.partialUpdateVideo(id, Helper.getToken(),video);

        call.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                Log.i("RESPONSE", String.valueOf(response.code()));
                videoMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    public void deleteVideo(String id) {
        Call<Void> call = videoWebServiceAPI.deleteVideo(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }
}

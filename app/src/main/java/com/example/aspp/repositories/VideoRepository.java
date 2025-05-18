package com.example.aspp.repositories;

import static com.example.aspp.Helper.context;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;

import com.example.aspp.AppDB;
import com.example.aspp.DownloadVideoHelper;
import com.example.aspp.Helper;
import com.example.aspp.api.VideoAPI;
import com.example.aspp.dao.VideoDao;
import com.example.aspp.entities.Reply;
import com.example.aspp.entities.SignedPartialVideoUpdate;
import com.example.aspp.entities.UnsignedPartialVideoUpdate;
import com.example.aspp.entities.Video;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoRepository {
    private VideoListData videoListData;
    private VideoData videoData;
    private VideoDao dao;
    private VideoAPI api;
    private static final String TAG = "VideoRepository";
    private DownloadVideoHelper downloadHelper;


    public VideoRepository() {
        // Initialize LiveData containers
        videoListData = new VideoListData();
        videoData = new VideoData();

        // Set up Room database without allowing main thread queries
        AppDB db = Room.databaseBuilder(context, AppDB.class, "Videos").allowMainThreadQueries()
                .build();  // Removed .allowMainThreadQueries()
        dao = db.videoDao();
        api = new VideoAPI(context);
        downloadHelper = new DownloadVideoHelper(context);

        // Make API call to get all videos asynchronously
        api.getAllVideos(videoListData);
    }

    public LiveData<List<Video>> getAll() {
        api.getAllVideos(videoListData);
        List<Video> videosFromDb = dao.index();
        videoListData.postValue(videosFromDb);
        return videoListData;
    }
    public LiveData<List<Video>> getAll(String username) {
        api.getAllVideos(videoListData, username);
        List<Video> videoFromDb = dao.getByUsername(username);
        videoListData.postValue(videoFromDb);
        return videoListData;
    }
    public LiveData<Video> getVideoById(String id) {
        api.getVideoById(videoData, id);
        Video video = dao.get(id);
        videoData.postValue(video);
        return videoData;
    }

    public LiveData<List<Video>> getRelatedVideos(String id) {
        if (isNetworkAvailable()) {
            // Fetch related videos from the network
            api.getRelatedVideos(videoListData, id);
            return videoListData;
        }
        List<Video> videoList = dao.getAllExcept(id);
        videoListData.postValue(videoList);
        return videoListData;
    }

    public LiveData<Video> partialUpdateVideo(SignedPartialVideoUpdate video, String id) {
        api.partialUpdateVideo(videoData, video, id);
        dao.update(videoData.getValue());
        return videoData;
    }

    public LiveData<Video> partialUpdateVideo(UnsignedPartialVideoUpdate video, String id) {
        api.partialUpdateVideo(videoData, video, id);
        dao.update(videoData.getValue());
        return videoData;
    }

    public LiveData<Video> createVideo(Video vid) {
        api.createVideo(videoData, vid);
        dao.insert(videoData.getValue());
        return videoData;
    }


    public LiveData<Video> updateVideo(Video vid) {
        api.updateVideo(videoData, vid);
        dao.insert(videoData.getValue());
        return videoData;
    }

    public void delete(Video vid) {
        api.deleteVideo(vid.get_id());
        dao.delete(vid);
    }

    public void reload(Video vid) {
    }

    class VideoListData extends MutableLiveData<List<Video>> {
        public VideoListData() {
            super();
            //load data from db
            setValue(new LinkedList<>());
        }

        @Override
        protected void onActive() {
            super.onActive();

            new Thread(() -> {
                VideoAPI api = new VideoAPI(context);
//                api.getAllVideos(this);
            }).start();
        }
    }

    class VideoData extends MutableLiveData<Video> {
        public VideoData() {
            super();
            //load data from db
            setValue(new Video(new String[]{"", ""}
                    ,new LinkedList<>(),"","","","",  "", "", "", 20, 30
                    ,  20, new ArrayList<>()));
        }

        @Override
        protected void onActive() {
            super.onActive();

//            new Thread(() -> {
//                VideoAPI api = new VideoAPI(Helper.context);
//                api.getAllVideos(this);
//            }).start();
        }
    }



    public LiveData<List<Video>> get() {
        Log.d(TAG, "get() method called");

        MutableLiveData<List<Video>> videoListData = new MutableLiveData<>();

        if (!isNetworkAvailable()) {
            Log.d(TAG, "No network available. Fetching videos from local database...");

            // Fetch videos from the local Room database
            new Thread(() -> {
                synchronized (VideoRepository.class) {
                    List<Video> videosFromDb = dao.index();
                    videoListData.postValue(videosFromDb);
                    Log.d(TAG, "Offline Mode: LiveData updated with videos from DB, total count: " + (videosFromDb != null ? videosFromDb.size() : 0));
                }
            }).start();
        } else {
            api.getVideos(new MutableLiveData<List<Video>>() {
                @Override
                public void postValue(List<Video> videos) {
                    super.postValue(videos);

                    new Thread(() -> {
                        synchronized (VideoRepository.class) {
                            if (videos != null && !videos.isEmpty()) {
                                Log.d(TAG, "Videos fetched from API: " + videos.size());
                                if(videos.size() < dao.getVideoCount()){dao.deleteAll();}
                                for (Video video : videos) {
                                    Video dbVideo = dao.get(video.get_id());
                                    if (dbVideo == null) {
                                        try {
                                            dao.insert(video);
                                            Log.d(TAG, "Inserted new video into local Room database: " + video.get_id());
                                            // Start download for the new video
                                        } catch (Exception e) {
                                            Log.e(TAG, "Error inserting video into DB: " + e.getMessage());
                                        }
                                    } else {
                                        Log.d(TAG, "Video already exists in DB with ID: " + video.get_id());
                                    }
                                }
                            } else {
                                Log.d(TAG, "No videos fetched from API or API returned empty list.");
                            }

                            List<Video> videosFromDb = dao.index();
                            videoListData.postValue(videosFromDb);
                            Log.d(TAG, "LiveData updated with videos from DB, total count: " + videosFromDb.size());
                        }
                    }).start();
                }

                public void onFailure(Call<List<Video>> call, Throwable t) {
                    Log.e(TAG, "Failed to fetch videos from API: " + t.getMessage());
                    new Thread(() -> {
                        synchronized (VideoRepository.class) {
                            List<Video> videosFromDb = dao.index();
                            videoListData.postValue(videosFromDb);
                            Log.d(TAG, "Offline Mode (API failure): LiveData updated with videos from DB, total count: " + (videosFromDb != null ? videosFromDb.size() : 0));
                        }
                    }).start();
                }
            });
        }
        List<Video> localVideos = dao.index();
        for (Video video : localVideos) {
            downloadVideoFromServer(video);
            System.out.println("Video Title: " + video.getTitle());
            System.out.println("Video URL: " + video.getSource());
        }
        return getAll();
    }

    public void downloadVideoFromServer(Video video) {
        if (video == null || video.getSource() == null) {
            Log.e(TAG, "Video or source is null. Cannot download.");
            return;
        }
        // Start the download using DownloadVideoHelper
        long downloadId = downloadHelper.downloadVideo(video);

        if (downloadId != -1) {
            Log.d(TAG, "Download started for video: " + video.getTitle() + " with download ID: " + downloadId);
        } else {
            Log.e(TAG, "Failed to start download for video: " + video.getTitle());
        }
    }

    // Utility method to check for network availability
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

package com.example.aspp.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.aspp.entities.SignedPartialVideoUpdate;
import com.example.aspp.entities.UnsignedPartialVideoUpdate;
import com.example.aspp.entities.Video;
import com.example.aspp.repositories.VideoRepository;

import java.util.List;

public class VideosViewModel extends ViewModel {
    private static final String TAG = "VideoModel";
    private final VideoRepository repository;
    private LiveData<List<Video>> videos;
    private LiveData<Video> video;

    // LiveData to manage upload status
    private final MutableLiveData<Boolean> isUploading = new MutableLiveData<>();
    private final MutableLiveData<String> uploadStatusMessage = new MutableLiveData<>();

    public VideosViewModel() {
        repository = new VideoRepository();
    }

    public LiveData<List<Video>> getTopVideos() {
        videos = repository.get();
        return videos;
    }

    public LiveData<List<Video>> get() {
        videos = repository.getAll();
        return videos;
    }

    public LiveData<List<Video>> get(String username) {
        videos = repository.getAll(username);
        return videos;
    }

    public LiveData<Video> getVideoById(String id) {
        video = repository.getVideoById(id);
        return video;
    }

    public LiveData<List<Video>> getRelatedVideos(String id) {
        videos = repository.getRelatedVideos(id);
        return videos;
    }

    public LiveData<Video> partialUpdateVideo(SignedPartialVideoUpdate videoUpdate, String id) {
        video = repository.partialUpdateVideo(videoUpdate, id);
        return video;
    }

    public LiveData<Video> partialUpdateVideo(UnsignedPartialVideoUpdate videoUpdate, String id) {
        video = repository.partialUpdateVideo(videoUpdate, id);
        return video;
    }

    public LiveData<Video> updateVideo(Video update) {
        video = repository.updateVideo(update);
        return video;
    }

    public LiveData<Boolean> getIsUploading() {
        return isUploading;
    }

    public LiveData<String> getUploadStatusMessage() {
        return uploadStatusMessage;
    }

    public void addVideo(Video newVideo) {
        Log.d(TAG, "Preparing to upload video2: " + newVideo.toString());
        isUploading.setValue(true);  // Set uploading state to true

        LiveData<Video> uploadResult = repository.createVideo(newVideo);

        uploadResult.observeForever(video -> {
            isUploading.setValue(false);  // Reset uploading state
            if (video != null) {
                Log.d(TAG, "Video uploaded successfully3: " + video.toString());
                uploadStatusMessage.setValue("Video uploaded successfully!");
            } else {
                Log.e(TAG, "Video upload failed.");
                uploadStatusMessage.setValue("Video upload failed. Please try again.");
            }
        });
    }

    public void delete(Video vid) {
        repository.delete(vid);
    }

    public void reload(Video vid) {
        repository.reload(vid);
    }
}

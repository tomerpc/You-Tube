package com.example.aspp.entities;

import java.util.List;

public class RelatedVideosHelper {
    List<Video> videos;

    public RelatedVideosHelper(List<Video> videos) {
        this.videos = videos;
    }

    public List<Video> getRelatedVideos() {
        return videos;
    }

    public void setRelatedVideos(List<Video> videos) {
        this.videos = videos;
    }
}

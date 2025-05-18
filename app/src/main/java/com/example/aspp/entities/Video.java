package com.example.aspp.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
public class Video {
      private String[] tags;
@PrimaryKey @NonNull String _id;
    private String title, description, source, video, duration, thumbnail, upload_date, username;
    private int likeCount, __v, views;
    private List<Comment> comments;
    private List<String> usersLikes;
    @Ignore private List<Video> relatedVideos;

    @Override
    public String toString() {
        return "Video{" +
                "tags=" + Arrays.toString(tags) +
                ", usersLikes=" + usersLikes +
                ", _id='" + _id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", source='" +  '\'' +
                ", thumbnail='" + "thumbnail" + '\'' +
                ", upload_date='" + upload_date + '\'' +
                ", username='" + username + '\'' +
                ", likeCount=" + likeCount +
                "duration"  +
                ", __v=" + __v +
                ", views=" + views +
                ", comments=" + comments +
                '}';
    }

    public Video() {
    }

    public Video(String[] tags, List<String> usersLikes, @NonNull String _id, String title, String description, String source, String thumbnail, String upload_date, String username, int likeCount, int __v, int views, List<Comment> comments) {
        this.tags = tags;
        this.usersLikes = usersLikes;
        this._id = _id;
        this.title = title;
        this.description = description;
        this.source = source;
        this.thumbnail = thumbnail;
        this.upload_date = upload_date;
        this.username = username;
        this.likeCount = likeCount;
        this.__v = __v;
        this.views = views;
        this.comments = comments;
        this.duration = "00:05";
        this.video = this.source;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public List<String> getUsersLikes() {
        return usersLikes;
    }

    public void setUsersLikes(List<String> usersLikes) {
        this.usersLikes = usersLikes;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUpload_date() {
        try {
            // Check if upload_date is null or empty
            if (upload_date == null || upload_date.isEmpty()) {
                return "Date not available";
            }

            // Check if upload_date contains a 'T' (indicating ISO 8601 format)
            if (upload_date.contains("T")) {
                // Extract date part before 'T'
                int tIndex = upload_date.indexOf("T");
                String datePart = upload_date.substring(0, tIndex);

                // Split the date into year, month, and day
                String[] dateParts = datePart.split("-");
                if (dateParts.length == 3) {
                    String day = dateParts[2];
                    String month = dateParts[1];
                    String year = dateParts[0];

                    // Format the date as DD.MM.YYYY
                    String formattedDate = day + "." + month + "." + year;
                    return formattedDate;
                } else {
                    return "Invalid date format";
                }
            } else {
                // Assume the date is already in "DD.MM.YYYY" format
                return upload_date;
            }
        } catch (Exception e) {
            return "Cannot retrieve date right now";
        }
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getDuration() {
        return duration;
    }

    public List<Video> getRelatedVideos() {
        return relatedVideos;
    }

    public void setRelatedVideos(List<Video> relatedVideos) {
        this.relatedVideos = relatedVideos;
    }
}

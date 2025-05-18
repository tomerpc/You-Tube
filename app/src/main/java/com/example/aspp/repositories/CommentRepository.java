package com.example.aspp.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.aspp.api.CommentAPI;
import com.example.aspp.api.UserAPI;
import com.example.aspp.dao.UserDao;
import com.example.aspp.entities.Comment;
import com.example.aspp.entities.SignedPartialCommentUpdate;
import com.example.aspp.entities.User;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CommentRepository {
    private CommentRepository.CommentListData commentsListData;
    private CommentRepository.CommentData commentData;
//    private UserDao dao;
    private CommentAPI api;
//    private Context context;
    private String videoId;

    public CommentRepository(String videoId) {
//        this.context = context;
        this.videoId = videoId;
        commentsListData = new CommentRepository.CommentListData();
        commentData = new CommentRepository.CommentData();
        api= new CommentAPI();
        api.getComments(commentsListData, videoId);
    }

    public LiveData<List<Comment>> getComments() {
        return commentsListData;
    }
    public LiveData<Comment> createComment(Comment newComment) {
        api.createComment(commentData, newComment, videoId);
        return commentData;
    }
    public LiveData<Comment> updateComment(Comment newComment) {
        api.updateComment(commentData, videoId, newComment.get_id(), newComment);
        return commentData;
    }
    public void deleteComment(String commentId) {
        api.deleteComment(videoId, commentId);
    }
    public void partialUpdateComment(SignedPartialCommentUpdate newComment) {
        api.partialUpdateComment(videoId, newComment.get_id(), newComment);
    }
    class CommentListData extends MutableLiveData<List<Comment>> {
        public CommentListData() {
            super();
            //load data from db
            setValue(new LinkedList<>());
        }

        @Override
        protected void onActive() {
            super.onActive();

            new Thread(() -> {
                CommentAPI api = new CommentAPI();
                api.getComments(this, videoId);
            }).start();
        }
    }

    class CommentData extends MutableLiveData<Comment> {
        public CommentData() {
            super();
            //load data from db
            setValue(new Comment("","","",new Date(),new LinkedList<>(),new LinkedList<>()));
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
}

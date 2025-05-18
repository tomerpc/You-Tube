package com.example.aspp.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.aspp.AppDB;
import com.example.aspp.Converters;
import com.example.aspp.Helper;
import com.example.aspp.api.ReplyAPI;
import com.example.aspp.api.UserAPI;
import com.example.aspp.dao.ReplyDao;
import com.example.aspp.dao.UserDao;
import com.example.aspp.entities.Comment;
import com.example.aspp.entities.Reply;
import com.example.aspp.entities.SignedPartialReplyUpdate;
import com.example.aspp.entities.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ReplyRepository {
    private ReplyRepository.ReplyListData repliesListData;
    private ReplyRepository.ReplyData replyData;
    private ReplyDao dao;
    private ReplyAPI api;
    private String videoId, commentId;

    public ReplyRepository(String videoId, String commentId) {

        this.commentId = commentId;
        this.videoId = videoId;
        repliesListData = new ReplyRepository.ReplyListData();
        replyData = new ReplyRepository.ReplyData();
//        Converters c = new Converters();
        AppDB db = Room.databaseBuilder(Helper.context, AppDB.class, "Replies").allowMainThreadQueries()
                .build();
        dao = db.replyDao();
        api = new ReplyAPI();
        api.getReplies(repliesListData, videoId, commentId);
    }

    public LiveData<List<Reply>> getReplies(Comment parent) {
        api.getReplies(repliesListData, videoId, commentId);
        dao.insert(repliesListData.getValue().toArray(new Reply[0]));
        ArrayList<Reply> replies = new ArrayList<>();
        for (Reply reply:parent.getReplies()) {
            replies.add(dao.get(reply.get_id()));
        }
        repliesListData.postValue(replies);
        return repliesListData;
    }

    public LiveData<Reply> createReply(Reply newReply) {
        dao.insert(newReply);
        api.createReply(replyData, newReply,videoId,commentId);
        return replyData;
    }
    public void deleteReply(Reply r) {
        dao.delete(r);
        api.deleteReply(videoId,commentId,r.get_id());
    }
    public void partialUpdateReply(Reply update) {
        dao.update(update);
        SignedPartialReplyUpdate partialUpdate = new SignedPartialReplyUpdate(update);
        api.partialUpdateReply(videoId,commentId,partialUpdate);
    }
    public void updateReply(Reply update) {
        dao.update(update);
        api.updateReply(videoId,commentId,update);
    }

    class ReplyListData extends MutableLiveData<List<Reply>> {
        public ReplyListData() {
            super();
            //load data from db
            setValue(new LinkedList<>());
        }

        @Override
        protected void onActive() {
            super.onActive();

//            new Thread(() -> {
//                ReplyAPI api = new ReplyAPI();
//                api.getReplies(this, videoId, commentId);
//            }).start();
        }
    }

    class ReplyData extends MutableLiveData<Reply> {
        public ReplyData() {
            super();
            //load data from db
            setValue(new Reply("","","",new Date(),new LinkedList<>()));
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

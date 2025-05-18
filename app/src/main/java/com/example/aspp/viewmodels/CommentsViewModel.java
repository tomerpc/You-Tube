package com.example.aspp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.aspp.entities.Comment;
import com.example.aspp.entities.SignedPartialCommentUpdate;
import com.example.aspp.entities.User;
import com.example.aspp.entities.Video;
import com.example.aspp.repositories.CommentRepository;
import com.example.aspp.repositories.UserRepository;

import java.util.List;

public class CommentsViewModel extends ViewModel {
    private CommentRepository repository;
    private LiveData<List<Comment>> comments;
    private LiveData<Comment> comment;

    public CommentsViewModel (String videoId) {
        repository = new CommentRepository(videoId);
        comments = repository.getComments();
    }
    public LiveData<List<Comment>> get() {
        return comments;
    }
    public LiveData<Comment> createComment(Comment newComment) {
        comment = repository.createComment(newComment);
        return comment;
    }
    public LiveData<Comment> updateComment(Comment newComment) {
        comment = repository.updateComment(newComment);
        return comment;
    }
    public void deleteComment(String commentId) {
        repository.deleteComment(commentId);
    }
    public void partialUpdateComment(SignedPartialCommentUpdate newComment) {
        repository.partialUpdateComment(newComment);
    }
}

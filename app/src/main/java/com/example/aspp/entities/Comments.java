package com.example.aspp.entities;

import java.util.List;

public class Comments {
    List<Comment> comments;
    int totalComments, currentPage, totalPages;

    public Comments() {
    }

    public Comments(List<Comment> comments, int totalComments, int currentPage, int totalPages) {
        this.comments = comments;
        this.totalComments = totalComments;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}

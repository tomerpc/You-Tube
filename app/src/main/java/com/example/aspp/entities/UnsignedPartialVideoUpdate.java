package com.example.aspp.entities;

public class UnsignedPartialVideoUpdate {
    private int views;

    public UnsignedPartialVideoUpdate(int views) {
        this.views = views;
    }

    public UnsignedPartialVideoUpdate() {
    }

    @Override
    public String toString() {
        return "UnsignedPartialVideoUpdate{" +
                "views=" + views +
                '}';
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }
}

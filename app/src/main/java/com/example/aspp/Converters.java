package com.example.aspp;

import androidx.room.ProvidedTypeConverter;
import androidx.room.TypeConverter;

import com.example.aspp.entities.Comment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ProvidedTypeConverter
public class Converters {

    @TypeConverter
    public static Date toDate(Long dateLong) {
        return dateLong == null ? null : new Date(dateLong);
    }

    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String fromList(List<String> likes) {
        return new Gson().toJson(likes);
    }

    @TypeConverter
    public static List<String> toList(String likes) {
        return new Gson().fromJson(likes, new TypeToken<List<String>>() {}.getType());
    }

    @TypeConverter
    public static String fromStringArray(String[] tags) {
        return tags == null ? null : new Gson().toJson(tags);
    }

    @TypeConverter
    public static String[] toStringArray(String tags) {
        return tags == null ? null : new Gson().fromJson(tags, String[].class);
    }

    @TypeConverter
    public static String fromCommentList(List<Comment> comments) {
        return new Gson().toJson(comments);
    }

    @TypeConverter
    public static List<Comment> toCommentList(String comments) {
        Type listType = new TypeToken<List<Comment>>() {}.getType();
        return new Gson().fromJson(comments, listType);
    }
}

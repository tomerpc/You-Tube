package com.example.aspp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.aspp.entities.Reply;
import com.example.aspp.entities.Video;

import java.util.List;

@Dao
public interface ReplyDao {
    @Query("SELECT * FROM Reply")
    List<Reply> index();

    @Query("SELECT * FROM Reply WHERE _id = :id")
    Reply get(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Reply... replies);

    @Update
    void update(Reply... replies);

    @Delete
    void delete(Reply... replies);
}

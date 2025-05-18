package com.example.aspp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.aspp.entities.Video;

import java.util.List;
@Dao
public interface VideoDao {
    @Query("SELECT * FROM Video")
    List<Video> index();

    @Query("SELECT * FROM Video WHERE _id = :id")
    Video get(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Video... videos);

    @Update
    void update(Video... videos);

    @Delete
    void delete(Video... videos);

    @Query("SELECT * FROM Video WHERE username = :username")
    List<Video> getByUsername(String username);

    @Query("DELETE FROM Video")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM Video")
    int getVideoCount();

    @Query("SELECT * FROM Video WHERE _id != :videoId")
    List<Video> getAllExcept(String videoId);
}

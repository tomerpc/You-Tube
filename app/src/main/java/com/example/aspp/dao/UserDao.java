package com.example.aspp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.aspp.entities.User;
import com.example.aspp.entities.Video;

import java.util.List;
@Dao
public interface UserDao {
    @Query("SELECT * FROM User")
    List<User> index();

    @Query("SELECT * FROM User WHERE _id = :id")
    User get(int id);

    @Query("SELECT * FROM User WHERE username = :username")
    User get(String username);

    @Query("SELECT * FROM User WHERE username = :username")
    LiveData<User> get1(String username);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User... users);

    @Update
    void update(User... users);

    @Delete
    void delete(User... users);
}

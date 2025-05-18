package com.example.aspp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.aspp.entities.User;
import com.example.aspp.entities.Users;
import com.example.aspp.repositories.UserRepository;

import java.util.List;

public class UsersViewModel extends ViewModel {
    private UserRepository repository;
    private LiveData<List<User>> users;
    private LiveData<User> user;

    public UsersViewModel () {
        repository = new UserRepository();
        users = repository.getAll();
    }
    public LiveData<User> getUserByUsername(String username) {
        user = repository.getUserByUsername(username);
        return user;
    }
    public LiveData<User> createUser(Users newUser) {
        user = repository.createUser(newUser);
        return user;
    }
    public LiveData<User> updateUser(Users newUser, String id) {
        user = repository.updateUser(newUser, id);
        return user;
    }
    public void deleteUser(String usernameID) {
        repository.deleteUser(usernameID);
    }
}

package com.example.aspp.repositories;

import static com.example.aspp.Helper.context;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.aspp.AppDB;
import com.example.aspp.Helper;
import com.example.aspp.api.UserAPI;
import com.example.aspp.dao.UserDao;
import com.example.aspp.entities.User;
import com.example.aspp.entities.Users;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;

public class UserRepository {
    private final UserListData usersListData;
    private final UserData userData;
    private final UserDao dao;
    private final UserAPI api;
    private final ExecutorService executorService;
    private boolean isUsersLoaded = false;

    public UserRepository() {
        usersListData = new UserListData();
        userData = new UserData();

        // Initialize the database and DAO without needing context directly
        AppDB db = Room.databaseBuilder(Helper.context, AppDB.class, "Users")
                .allowMainThreadQueries() // Consider using ExecutorService for background queries
                .build();
        dao = db.userDao();

        // Initialize the API
        api = new UserAPI();

        // Initialize ExecutorService for background tasks
        executorService = Executors.newSingleThreadExecutor();
        Log.d("UserRepository", "Im On !!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        usersListData.postValue(this.getAll().getValue());

    }

    // Method to fetch users from API and sync with local DB
    public LiveData<List<User>> getAll() {
        Log.d("UserRepository", "getAllUsers() method called");

        MutableLiveData<List<User>> userListData = new MutableLiveData<>();

        // Check for network connectivity before making API call
        if (!isNetworkAvailable()) {
            Log.d("UserRepository", "No network available. Fetching users from local database...");

            // Fetch users from the local Room database
            new Thread(() -> {
                synchronized (UserRepository.class) {
                    List<User> usersFromDb = dao.index();  // Assuming `dao.getAll()` fetches all users from the local DB
                    if (usersFromDb != null && !usersFromDb.isEmpty()) {
                        Log.d("UserRepository", "Offline Mode: Users fetched from DB: " + usersFromDb.size());
                    } else {
                        Log.d("UserRepository", "Offline Mode: No users found in local DB.");
                    }

                    userListData.postValue(usersFromDb);
                    Log.d("UserRepository", "Offline Mode: LiveData updated with users from DB, total count: " + (usersFromDb != null ? usersFromDb.size() : 0));
                }
            }).start();
        } else {
            // Make API call to fetch users
            api.getAllUsers(new MutableLiveData<List<User>>() {
                @Override
                public void postValue(List<User> users) {
                    super.postValue(users);

                    new Thread(() -> {
                        synchronized (UserRepository.class) {
                            if (users != null && !users.isEmpty()) {
                                Log.d("UserRepository", "Users fetched from API: " + users.size());

                                for (User user : users) {
                                    User dbUser = dao.get(user.get_id());  // Assuming `getUserById` checks if the user already exists in the DB
                                    Log.d("UserRepository", "Checking if user exists in DB for ID: " + user.get_id());

                                    if (dbUser == null) {
                                        try {
                                            dao.insert(user);
                                            Log.d("UserRepository", "Inserted new user into local Room database: " + user.get_id());
                                        } catch (Exception e) {
                                            Log.e("UserRepository", "Error inserting user into DB: " + e.getMessage());
                                        }
                                    } else {
                                        Log.d("UserRepository", "User already exists in DB with ID: " + user.get_id());
                                    }
                                }
                            } else {
                                Log.d("UserRepository", "No users fetched from API or API returned empty list.");
                            }

                            List<User> usersFromDb = dao.index();
                            Log.d("UserRepository", "Users fetched from DB: " + usersFromDb.size());
                            userListData.postValue(usersFromDb);
                            Log.d("UserRepository", "LiveData updated with users from DB, total count: " + usersFromDb.size());
                        }
                    }).start();
                }

                public void onFailure(Call<List<User>> call, Throwable t) {
                    Log.e("UserRepository", "Failed to fetch users from API: " + t.getMessage());
                    Log.d("UserRepository", "Fetching users from local database due to API failure...");

                    new Thread(() -> {
                        synchronized (UserRepository.class) {
                            List<User> usersFromDb = dao.index();
                            if (usersFromDb != null && !usersFromDb.isEmpty()) {
                                Log.d("UserRepository", "Offline Mode (API failure): Users fetched from DB: " + usersFromDb.size());
                            } else {
                                Log.d("UserRepository", "Offline Mode (API failure): No users found in local DB.");
                            }

                            userListData.postValue(usersFromDb);
                            Log.d("UserRepository", "Offline Mode (API failure): LiveData updated with users from DB, total count: " + (usersFromDb != null ? usersFromDb.size() : 0));
                        }
                    }).start();
                }
            });
        }

        return userListData;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public LiveData<User> getUserByUsername(String username) {
        api.getUserByUsername(userData, username);
        User user = dao.get(username);
        userData.postValue(user);
        return userData;
    }


    public LiveData<User> createUser(Users user) {
        api.createUser(userData, user);
        return userData;
    }

    public void deleteUser(String usernameID) {
        executorService.execute(() -> {
            api.deleteUser(usernameID);
            User user = dao.get(usernameID);
            if (user != null) {
                dao.delete(user);
                Log.d("UserRepository", "User deleted from local database: " + usernameID);
            }
        });
    }

    public LiveData<User> updateUser(Users user, String id) {
        api.updateUser(userData, user, id);
        executorService.execute(() -> {
            User updatedUser = userData.getValue();
            if (updatedUser != null) {
                dao.update(updatedUser);
                Log.d("UserRepository", "User updated in local database: " + updatedUser.get_id());
            }
        });
        return userData;
    }

    class UserListData extends MutableLiveData<List<User>> {
        public UserListData() {
            super();
            // Initialize with empty list to avoid null pointer exceptions
            setValue(new LinkedList<>());
        }

        @Override
        protected void onActive() {
            super.onActive();
            executorService.execute(() -> api.getAllUsers(this));
        }
    }

    class UserData extends MutableLiveData<User> {
        public UserData() {
            super();
            // Initialize with default User object to avoid null pointer exceptions
            setValue(new User("", "", "", "", "", 0));
        }

        @Override
        protected void onActive() {
            super.onActive();
            // Optional: Fetch or refresh data when LiveData becomes active
        }
    }
}

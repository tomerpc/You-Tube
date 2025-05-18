package com.example.aspp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aspp.adapters.HomeRVAdapter;
import com.example.aspp.viewmodels.UsersViewModel;
import com.example.aspp.viewmodels.VideosViewModel;

import java.util.LinkedList;

public class CreatorActivity extends AppCompatActivity {
    RecyclerView videos;
    TextView username, numOfVideos;
    ImageView profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_creator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        videos = findViewById(R.id.videos);
        username = findViewById(R.id.username);
        numOfVideos = findViewById(R.id.num_of_videos);
        profile = findViewById(R.id.profilePic);
        HomeRVAdapter related = new HomeRVAdapter(this, new LinkedList<>());
        username.setText(getIntent().getStringExtra("username"));
        UsersViewModel vm = new ViewModelProvider(this).get(UsersViewModel.class);
        vm.getUserByUsername(getIntent().getStringExtra("username")).observe(this,
                user -> {
                    String profile_url_str = getResources().getString(R.string.Base_Url)
                            + user.getImage();
                    Glide.with(this)
                            .load(profile_url_str)
                            .into(profile);
                    VideosViewModel vvm = new ViewModelProvider(this).get(VideosViewModel.class);
                    vvm.get(getIntent().getStringExtra("username")).observe(this, videos1 ->
                    {
                        related.setVideos(videos1);
                        numOfVideos.setText(videos1.size() + " Videos");
                        Log.i("Related Videos", videos1.toString());
                        related.notifyDataSetChanged();
                    });
                });
        videos.setAdapter(related);
        videos.setLayoutManager(new LinearLayoutManager(this));

    }
}
package com.example.aspp.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aspp.R;
import com.example.aspp.VideoPlayerActivity;
import com.example.aspp.entities.UnsignedPartialVideoUpdate;
import com.example.aspp.fragments.HomeFragment;
import com.example.aspp.entities.Video;
import com.example.aspp.viewmodels.UsersViewModel;
import com.example.aspp.viewmodels.VideosViewModel;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeRVAdapter extends RecyclerView.Adapter<HomeRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<Video> videos;
    int pos;
    public HomeRVAdapter(Context context, List<Video> videos) {
        this.context = context;
        this.videos = new ArrayList<>(videos);
    }

    public void setVideos(List<Video> videos) {
        this.videos = new ArrayList<>(videos);
    }

    @NonNull
    @Override
    public HomeRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.home_rv_template, parent, false);

        return new HomeRVAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRVAdapter.MyViewHolder holder, int position) {
        holder.videoName.setText(videos.get(position).getTitle());
        holder.publisher.setText(videos.get(position).getUsername());
        holder.views.setText(videos.get(position).getViews() + " Views");
        holder.time.setText(videos.get(position).getUpload_date());

        String photo_url_str = context.getResources().getString(R.string.Base_Url)
                + videos.get(position).getThumbnail();
        Glide.with(context)
                .load(photo_url_str)
                .into(holder.thumbnail);



        UsersViewModel vm = new UsersViewModel();
        vm.getUserByUsername(videos.get(position).getUsername()).observe((LifecycleOwner) context, user ->
        {
            if(user != null) {
                String profile_url_str = context.getResources().getString(R.string.Base_Url)
                        + user.getImage();
                Glide.with(context)
                        .load(profile_url_str)
                        .into(holder.profilePic);
            }
            else {
                String profile_url_str = context.getResources().getString(R.string.Base_Url);
                Glide.with(context)
                    .load(profile_url_str)
                    .placeholder(R.drawable.outline_face_retouching_natural_24) // Show a placeholder while loading
                    .error(R.drawable.outline_face_retouching_natural_24) // Show a default image if loading fails or URL is null
                    .into(holder.profilePic);}
        });

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Video selectedVideo = videos.get(position);
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("id", selectedVideo.get_id());
                UnsignedPartialVideoUpdate update = new UnsignedPartialVideoUpdate(
                        selectedVideo.getViews() + 1
                );
                VideosViewModel viewModel = new VideosViewModel();
                viewModel.partialUpdateVideo(update, selectedVideo.get_id()).observe((LifecycleOwner) context, update1 ->
                {
                });
                context.startActivity(intent);
            }
        });
        holder.v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showBottomDialog(context, videos.get(position));
                return true;
            }
        });

    }
    public void showBottomDialog(Context context, Video choosenVideo) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.home_bottom_sheet_layout);

        LinearLayout layout_not_interested = dialog.findViewById(R.id.layout_not_interested);
        layout_not_interested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                videos.remove(choosenVideo);
                HomeFragment.adp.notifyDataSetChanged();
                Toast.makeText(context, choosenVideo.getTitle()+" was removed", Toast.LENGTH_SHORT).show();

            }
        });

        LinearLayout layout_playlist = dialog.findViewById(R.id.layout_playlist);
        layout_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        LinearLayout layout_queue = dialog.findViewById(R.id.layout_queue);
        layout_queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        LinearLayout layout_watch_later = dialog.findViewById(R.id.layout_watch_later);
        layout_watch_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        LinearLayout layout_share = dialog.findViewById(R.id.layout_share);
        layout_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String body = "Check out this video!";
                String sub = "Check out this video!\n" + choosenVideo.getTitle();
                shareIntent.putExtra(Intent.EXTRA_TEXT, body);
                shareIntent.putExtra(Intent.EXTRA_TEXT, sub);
                context.startActivity(Intent.createChooser(shareIntent, "Share using"));

            }
        });

        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView videoName, publisher, views, time;
        ImageView thumbnail, profilePic;
        View v;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            videoName = itemView.findViewById(R.id.videoName);
            publisher = itemView.findViewById(R.id.publisher);
            views = itemView.findViewById(R.id.views);
            time = itemView.findViewById(R.id.time);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            profilePic = itemView.findViewById(R.id.profilePic);
            v = itemView;
        }
    }
}

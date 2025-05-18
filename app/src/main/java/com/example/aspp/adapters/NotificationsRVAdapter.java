package com.example.aspp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aspp.R;
import com.example.aspp.VideoPlayerActivity;
import com.example.aspp.entities.UnsignedPartialVideoUpdate;
import com.example.aspp.entities.Video;
import com.example.aspp.viewmodels.UsersViewModel;
import com.example.aspp.viewmodels.VideosViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NotificationsRVAdapter extends RecyclerView.Adapter<NotificationsRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<Video> notifications;
    int pos;
    public NotificationsRVAdapter(Context context, ArrayList<Video> notifications) {
        this.context = context;
        this.notifications = notifications;
    }
    public void setVideos(List<Video> notifications) {
        this.notifications = new ArrayList<>(notifications);
    }

    @NonNull
    @Override
    public NotificationsRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.notifications_rv_template, parent, false);

        return new NotificationsRVAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsRVAdapter.MyViewHolder holder, int position) {
        holder.title.setText(notifications.get(position).getTitle());
        holder.publisher.setText(notifications.get(position).getUsername());
        holder.time.setText(notifications.get(position).getUpload_date());

        String photo_url_str = context.getResources().getString(R.string.Base_Url)
                + notifications.get(position).getThumbnail();
        Glide.with(context)
                .load(photo_url_str)
                .into(holder.thumbnail);

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Video selectedVideo = notifications.get(position);
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

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, publisher, time;
        ImageView thumbnail, profilePic;
        View v;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            publisher = itemView.findViewById(R.id.publisher);
            time = itemView.findViewById(R.id.time);
            thumbnail = itemView.findViewById(R.id.thumbnail);
//            profilePic = itemView.findViewById(R.id.profilePic);
            v = itemView;
        }
    }
}

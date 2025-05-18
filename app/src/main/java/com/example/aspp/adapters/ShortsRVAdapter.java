package com.example.aspp.adapters;

//import static com.example.aspp.Utils.loadComments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aspp.Helper;
import com.example.aspp.R;
import com.example.aspp.VideoPlayerActivity;
import com.example.aspp.entities.SignedPartialVideoUpdate;
import com.example.aspp.entities.Video;
import com.example.aspp.viewmodels.UsersViewModel;
import com.example.aspp.viewmodels.VideosViewModel;

import java.io.File;
import java.util.ArrayList;

public class ShortsRVAdapter extends RecyclerView.Adapter<ShortsRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<Video> shorts;
    int pos;
    public ShortsRVAdapter(Context context, ArrayList<Video> videos) {
        this.context = context;
        this.shorts = videos;
    }

    @NonNull
    @Override
    public ShortsRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.shorts_rv_template, parent, false);

        return new ShortsRVAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ShortsRVAdapter.MyViewHolder holder, int position) {
//        if (shorts.get(position).getComments().isEmpty())
//            loadComments(shorts.get(position).getId());
        holder.videoName.setText(shorts.get(position).getTitle());
        holder.publisher.setText(shorts.get(position).getUsername());
        final VideoView vid = holder.videoView;
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                float videoRatio = mediaPlayer.getVideoWidth() / (float) mediaPlayer.getVideoHeight();
                float screenRatio = vid.getWidth() / (float) vid.getHeight();

                float scale = videoRatio / screenRatio;
                if (scale >= 1f)
                    vid.setScaleX(scale);
                else
                    vid.setScaleY(1f/scale);
            }
        });
        holder.videoView.setScaleX(vid.getScaleX());
        holder.videoView.setScaleY(vid.getScaleY());
//        shorts.get(position).addView();
        VideosViewModel viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(VideosViewModel.class);
        final boolean[] alreadyLiked = {false};
        viewModel.getVideoById(shorts.get(position).get_id())
                .observe((LifecycleOwner) context, video -> {
                    if (video != null) {
                        loadVideo(holder, shorts.get(position));
                        if (Helper.isSignedIn() &&
                                shorts.get(position).getUsersLikes().contains(Helper.getSignedInUser().get_id())) {
                            alreadyLiked[0] = true;
                            holder.like.setBackgroundTintList(context.getColorStateList(R.color.dark_blue));
                        }
                    }
                });
        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
//        String vid_path = shorts.get(position).getVideoPath();
//        if (shorts.get(position).getThumbnailDrawableId() != 0)
//            holder.videoView.setVideoURI(Uri.parse("android.resource://com.example.aspp/"+context.getResources().getIdentifier(vid_path,"raw",context.getPackageName())));
//        else
//            holder.videoView.setVideoURI(Uri.parse(vid_path));
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Helper.isSignedIn()) {
                    Toast.makeText(context,
                            "In order to like a video you first need to sign in", Toast.LENGTH_LONG).show();
                    return;
                }
                if (viewModel.getVideoById(shorts.get(position).get_id()).hasActiveObservers()) {
                    viewModel.getVideoById(shorts.get(position).get_id())
                            .removeObservers((LifecycleOwner) context);
                }
                if (alreadyLiked[0]) {
                    holder.like.setBackgroundTintList(context.getColorStateList(R.color.colorSecondaryContainer_day));
                    shorts.get(position).setLikeCount(shorts.get(position).getLikeCount() - 1);
                    shorts.get(position).getUsersLikes().add(Helper.getSignedInUser().get_id());
                    SignedPartialVideoUpdate update = new SignedPartialVideoUpdate(
                            shorts.get(position).getUsersLikes(),
                            shorts.get(position).getComments(),
                            shorts.get(position).getLikeCount(),
                            shorts.get(position).getViews()
                    );
                    viewModel.partialUpdateVideo(update, shorts.get(position).get_id())
                            .observe((LifecycleOwner) context, video -> {

                            });
                    alreadyLiked[0] = false;
                } else {
                    holder.like.setBackgroundTintList(context.getColorStateList(R.color.dark_blue));
                    shorts.get(position).setLikeCount(shorts.get(position).getLikeCount() + 1);
                    shorts.get(position).getUsersLikes().add(Helper.getSignedInUser().get_id());
                    SignedPartialVideoUpdate update = new SignedPartialVideoUpdate(
                            shorts.get(position).getUsersLikes(),
                            shorts.get(position).getComments(),
                            shorts.get(position).getLikeCount(),
                            shorts.get(position).getViews()
                    );
                    viewModel.partialUpdateVideo(update, shorts.get(position).get_id())
                            .observe((LifecycleOwner) context, video -> {

                            });
                    alreadyLiked[0] = true;
                }
            }
        });
//        holder.dislike.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                shorts.get(position).subLike();
//            }
//        });
//        holder.comment_section.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                VideoPlayerActivity.showCommentsDialog(context,
//                        shorts.get(position).getComments(), shorts.get(position));
//            }
//        });
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String body = "Check out this video!";
                String sub = "Check out this video!\n" + shorts.get(position).getTitle();
                shareIntent.putExtra(Intent.EXTRA_TEXT, body);
                shareIntent.putExtra(Intent.EXTRA_TEXT, sub);
                context.startActivity(Intent.createChooser(shareIntent, "Share using"));
            }
        });
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog(context, shorts.get(position));
            }
        });
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.videoView.isPlaying())
                    holder.videoView.pause();
                else
                    holder.videoView.resume();
            }
        });
    }
    private void loadVideo(MyViewHolder holder, Video currentVideo) {
        String vid = context.getResources().getString(R.string.Base_Url);
        vid += currentVideo.getSource();
        String videoFileName = currentVideo.getSource();

        File file = new File(videoFileName);
        String fileName = file.getName();
        if (currentVideo.getSource().equals(""))
            return;
        File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        if (destinationFile.exists()) {
            holder.videoView.setVideoURI(Uri.parse(String.valueOf(destinationFile)));
        } else {
            holder.videoView.setVideoURI(Uri.parse(vid));
        }
        Log.i("Current Vid", currentVideo.toString());
        Log.i("PATH", vid);

        holder.videoName.setText(currentVideo.getTitle());
        holder.publisher.setText(currentVideo.getUsername());
        UsersViewModel vm = new UsersViewModel();
        vm.getUserByUsername(currentVideo.getUsername()).observe((LifecycleOwner) context, user ->
        {
            String profile_url_str = context.getResources().getString(R.string.Base_Url)
                    + user.getImage();
            Glide.with(context)
                    .load(profile_url_str)
                    .into(holder.profilePic);
        });
    }
    public void showBottomDialog(Context context, Video currentVideo) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.description_bottom_sheet_layout);

        TextView dtitle = dialog.findViewById(R.id.title);
        dtitle.setText(currentVideo.getTitle());
        TextView dviews = dialog.findViewById(R.id.views);
        dviews.setText(currentVideo.getViews() + "\nViews");
        TextView dlikes = dialog.findViewById(R.id.likes);
        dlikes.setText(currentVideo.getLikeCount() + "\nLikes");
        TextView ddescription = dialog.findViewById(R.id.description);
        ddescription.setText(currentVideo.getDescription());
        ImageView layout_cancel = dialog.findViewById(R.id.cancelButton);
        layout_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        return shorts.size();
    }

    public void setVideos(ArrayList<Video> shortsArrayList) {
        this.shorts = new ArrayList<>(shortsArrayList);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView videoName, publisher;
        ImageView profilePic, like, dislike, comment_section, share, more;
        View v;
        VideoView videoView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            videoName = itemView.findViewById(R.id.title);
            publisher = itemView.findViewById(R.id.username);
            profilePic = itemView.findViewById(R.id.profilePic);
            like = itemView.findViewById(R.id.like);
            comment_section = itemView.findViewById(R.id.comment_section);
            share = itemView.findViewById(R.id.share);
            more = itemView.findViewById(R.id.more);
            videoView = itemView.findViewById(R.id.videoView2);
            v = itemView;
        }
    }
}


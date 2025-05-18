package com.example.aspp;

import static com.example.aspp.Utils.generateId;
//import static com.example.aspp.Utils.loadComments;
import static com.example.aspp.fragments.HomeFragment.adp;
//import static com.example.aspp.fragments.HomeFragment.videoArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aspp.adapters.CommentsRVAdapter;
import com.example.aspp.adapters.HomeRVAdapter;
import com.example.aspp.api.VideoAPI;
import com.example.aspp.entities.Reply;
import com.example.aspp.entities.SignedPartialVideoUpdate;
import com.example.aspp.entities.UnsignedPartialVideoUpdate;
import com.example.aspp.fragments.HomeFragment;
import com.example.aspp.entities.Comment;
import com.example.aspp.entities.Video;
import com.example.aspp.repositories.VideoRepository;
import com.example.aspp.viewmodels.CommentsViewModel;
import com.example.aspp.viewmodels.UsersViewModel;
import com.example.aspp.viewmodels.VideosViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class VideoPlayerActivity extends AppCompatActivity {
    private int EDIT_MODE = 0;
    EditText editTitle;
    TextView title, views, time, more, publisher, subscribers, comments, comment;
    RecyclerView related_videos;
    ImageView c_profile, profilePic, edit, delete;
    Button subscribe, like, share, watch_later, playlist;
    MediaController mediaController;
    VideoView videoView;
    ProgressBar progressBar;
    HomeRVAdapter related;
    ArrayList<Video> relatedVideoArrayList;
    Video currentVideo;
    //    private boolean commentsAvailable = false;
    private List<Comment> commentSection;
    private boolean alreadyLiked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
//        currentVideo = HomeFragment.videoArrayList.get(intent.getIntExtra("pos",0));
        progressBar = findViewById(R.id.progressBar);
        progressBar.setActivated(true);
        commentSection = new ArrayList<>();
        like = findViewById(R.id.like);
        edit = findViewById(R.id.edit);
        delete = findViewById(R.id.delete);
        VideosViewModel viewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        Log.i("VideoPlayerActivity", "hereeeeeeeeeeeeeee");

        viewModel.getVideoById(intent.getStringExtra("id"))
                .observe(this, video -> {
                    currentVideo = video;
                    if (currentVideo != null) {
                        Log.i("VideoPlayerActivity", "hereeeeeeeeeeeeeee1");
                        loadVideo();
                        commentSection = video.getComments();
                        loadComments();
                        Log.i("VideoPlayerActivity", "hereeeeeeeeeeeeeee2");
                        if (Helper.isSignedIn() &&
                                currentVideo.getUsersLikes().contains(Helper.getSignedInUser().get_id())) {
                            alreadyLiked = true;
                            like.setBackgroundTintList(VideoPlayerActivity.this.getColorStateList(R.color.dark_blue));
                        }
                        Log.i("VideoPlayerActivity", "hereeeeeeeeeeeeeee3");
                        if (Helper.isSignedIn() && currentVideo.getUsername().equals(Helper.getSignedInUser().getUsername())) {
                            edit.setVisibility(View.VISIBLE);
                            delete.setVisibility(View.VISIBLE);
                        }
                    }
                });

        delete.setOnClickListener(v -> {
            AlertDialog a = new android.app.AlertDialog.Builder(this)
                    .setTitle("Delete Video")
                    .setMessage("Are you sure you want to delete this video?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        viewModel.delete(currentVideo);
                        Toast.makeText(VideoPlayerActivity.this,
                                "Video Deleted successfully", Toast.LENGTH_LONG).show();

                        Intent intent1 = new Intent(this, MainActivity.class);
                        intent1.putExtra("loggedInUser", Helper.getSignedInUser());
                        startActivity(intent1);
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            Button positive = a.getButton(DialogInterface.BUTTON_POSITIVE);
            positive.setTextColor(this.getColor(R.color.black));

            Button negative = a.getButton(DialogInterface.BUTTON_NEGATIVE);
            negative.setTextColor(this.getColor(R.color.black));

        });
//        if (currentVideo.getComments().isEmpty())
//            loadComments(currentVideo.getId());
//        currentVideo = HomeFragment.videoArrayList.get(intent.getIntExtra("pos",0));

        new Thread(() -> {
            while (currentVideo == null) {
                Log.i("Something", "Something");
            }

        }).start();
        editTitle = findViewById(R.id.editTitle);
        edit.setOnClickListener(v -> {
            if (EDIT_MODE == 0) {
                editTitle.setVisibility(View.VISIBLE);
                editTitle.setText(title.getText());
                title.setVisibility(View.GONE);
                edit.setImageResource(R.drawable.outline_file_download_24);
                EDIT_MODE = 1;
            } else {
                currentVideo.setTitle(editTitle.getText().toString().trim());
                viewModel.updateVideo(currentVideo).observe(this, video -> {
                    title.setVisibility(View.VISIBLE);
                    title.setText(editTitle.getText());
                    editTitle.setVisibility(View.GONE);
                    edit.setImageResource(R.drawable.outline_draw_24);
                    EDIT_MODE = 0;
                });
            }


        });




        videoView = findViewById(R.id.videoView);
        mediaController = new MediaController(this);
//        if (intent.getIntExtra("video_thumbnail",0) != 0)
//            videoView.setVideoURI(Uri.parse("android.resource://com.example.aspp/"+getResources().getIdentifier(vid,"raw",getPackageName())));
//        else
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.setMediaController(mediaController);
                progressBar.setVisibility(View.GONE);
                mediaPlayer.start();
            }
        });
        title = findViewById(R.id.title);

        views = findViewById(R.id.views);
        time = findViewById(R.id.time);

        more = findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog(VideoPlayerActivity.this);
            }
        });
        publisher = findViewById(R.id.publisher);

        subscribers = findViewById(R.id.subscribers);
        comments = findViewById(R.id.comments);
        comment = findViewById(R.id.comment);

        LinearLayout profileLayout = findViewById(R.id.profileLayout);
        profileLayout.setOnClickListener(v ->
        {
            viewModel.get().removeObserver(videoList -> {});
            Intent i = new Intent(VideoPlayerActivity.this, CreatorActivity.class);
            i.putExtra("username", currentVideo.getUsername());
            startActivity(i);
        });
        LinearLayout layout_commentSection = findViewById(R.id.comment_section);
        layout_commentSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentsDialog(VideoPlayerActivity.this, commentSection, viewModel);
            }
        });
        c_profile = findViewById(R.id.c_profile);
        profilePic = findViewById(R.id.profilePic);
        subscribe = findViewById(R.id.subscribe);

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("VideoPlayerActivity", "hereeeeeeeeeeeeeee4");
                if (!Helper.isSignedIn()) {
                    Toast.makeText(VideoPlayerActivity.this,
                            "In order to like a video you first need to sign in", Toast.LENGTH_LONG).show();
                    return;
                }
                if (viewModel.getVideoById(intent.getStringExtra("id")).hasActiveObservers()) {
                    viewModel.getVideoById(intent.getStringExtra("id"))
                            .removeObservers(VideoPlayerActivity.this);
                }
                if (alreadyLiked) {
                    like.setBackgroundTintList(VideoPlayerActivity.this.getColorStateList(R.color.colorSecondaryContainer_day));
                    currentVideo.setLikeCount(currentVideo.getLikeCount() - 1);
                    currentVideo.getUsersLikes().add(Helper.getSignedInUser().get_id());
                    SignedPartialVideoUpdate update = new SignedPartialVideoUpdate(
                            currentVideo.getUsersLikes(),
                            currentVideo.getComments(),
                            currentVideo.getLikeCount(),
                            currentVideo.getViews()
                    );
                    viewModel.partialUpdateVideo(update, currentVideo.get_id())
                            .observe(VideoPlayerActivity.this, video -> {

                            });
                    alreadyLiked = false;
                } else {
                    like.setBackgroundTintList(VideoPlayerActivity.this.getColorStateList(R.color.dark_blue));
                    currentVideo.setLikeCount(currentVideo.getLikeCount() + 1);
                    currentVideo.getUsersLikes().add(Helper.getSignedInUser().get_id());
                    SignedPartialVideoUpdate update = new SignedPartialVideoUpdate(
                            currentVideo.getUsersLikes(),
                            currentVideo.getComments(),
                            currentVideo.getLikeCount(),
                            currentVideo.getViews()
                    );
                    viewModel.partialUpdateVideo(update, currentVideo.get_id())
                            .observe(VideoPlayerActivity.this, video -> {

                            });
                    alreadyLiked = true;
                }
            }
        });
        share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String body = "Check out this video!";
                String sub = "Check out this video!\n" + currentVideo.getTitle();
                shareIntent.putExtra(Intent.EXTRA_TEXT, body);
                shareIntent.putExtra(Intent.EXTRA_TEXT, sub);
                startActivity(Intent.createChooser(shareIntent, "Share using"));
            }
        });
        watch_later = findViewById(R.id.watch_later);
        watch_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        playlist = findViewById(R.id.playlist);
        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        related_videos = findViewById(R.id.related_videos);
        related = new HomeRVAdapter(this, new LinkedList<>());
        viewModel.getRelatedVideos(intent.getStringExtra("id")).observe(this, videos ->
        {
            related.setVideos(videos);
            Log.i("Related Videos", videos.toString());
            related.notifyDataSetChanged();
        });
        related_videos.setAdapter(related);
        related_videos.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadComments() {
        try {
            comments.setText(commentSection.size() + " ");
            comment.setText(commentSection.get(0).getContent());
            UsersViewModel vm = new UsersViewModel();
            vm.getUserByUsername(commentSection.get(0).getUser()).observe(this, user ->
            {
                String profile_url_str = getResources().getString(R.string.Base_Url)
                        + user.getImage();
                Glide.with(this)
                        .load(profile_url_str)
                        .into(c_profile);
            });
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
            comment.setText("No comments are available right now");
        }
    }

    private void loadVideo() {
        String baseUrl = getResources().getString(R.string.Base_Url);
        String videoFileName = currentVideo.getSource();
        File file = new File(videoFileName);
        String fileName = file.getName();
        // Define the base URL and the local path
        if (videoFileName == null || videoFileName.isEmpty()) {
            Log.e("VideoPlayer", "No video source provided.");
            return; // Exit if no source is available
        }
        File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);


        //File localVideoFile = new File(destinationFile);
        Log.i("VideoPlayer", "Local video path: " + destinationFile);

        // Check if the video exists locally
        if (destinationFile.exists()) {
            // Play the video from local storage
            videoView.setVideoURI(Uri.parse(String.valueOf(destinationFile)));
            Log.i("VideoPlayer", "Playing video from local storage: " + destinationFile);
        } else {
            // Play the video from the online source
            String onlineVideoUrl = baseUrl + currentVideo.getSource();
            videoView.setVideoURI(Uri.parse(onlineVideoUrl));
            Log.i("VideoPlayer", "Playing video from online source: " + onlineVideoUrl);
        }

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Set video information
        time.setText(currentVideo.getUpload_date());
        title.setText(currentVideo.getTitle());
        views.setText(currentVideo.getViews() + " Views");
        publisher.setText(currentVideo.getUsername());

        // Load the publisher's profile picture using Glide
        UsersViewModel vm = new UsersViewModel();
        vm.getUserByUsername(currentVideo.getUsername()).observe(this, user -> {
            String profileUrl = baseUrl + user.getImage();
            Glide.with(this)
                    .load(profileUrl)
                    .into(profilePic);
        });
    }

    public void showCommentsDialog(Context context, List<Comment> c, VideosViewModel viewModel) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.comment_section_bottom_sheet_layout);

        EditText comment = dialog.findViewById(R.id.comment);
        Button send = dialog.findViewById(R.id.send);
        RecyclerView comments = dialog.findViewById(R.id.comment_section);
        if (commentSection == null) {
            Toast.makeText(VideoPlayerActivity.this, "Comments are loading", Toast.LENGTH_LONG).show();
            return;
        }
        CommentsRVAdapter Cadp = new CommentsRVAdapter(context, commentSection);
        Cadp.setVideoIdParent(currentVideo.get_id());
        Cadp.setEditTextAndBtn(send, comment);
        comments.setAdapter(Cadp);
        comments.setLayoutManager(new LinearLayoutManager(dialog.getContext()));
        ImageView profilePic = dialog.findViewById(R.id.profilePic);
        if (viewModel.getVideoById(currentVideo.get_id()).hasActiveObservers()) {
            viewModel.getVideoById(currentVideo.get_id())
                    .removeObservers(VideoPlayerActivity.this);
        }
        Log.i("VideoPlayerActivity", "hereeeeeeeeeeeeeee5");
        if (Helper.isSignedIn()) {
            Log.i("VideoPlayerActivity", "hereeeeeeeeeeeeeee6");
            String profile_url_str = getResources().getString(R.string.Base_Url)
                    + Helper.getSignedInUser().getImage();
            Glide.with(this)
                    .load(profile_url_str)
                    .into(profilePic);
        } else {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo);
            profilePic.setImageBitmap(bm);
        }
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("VideoPlayerActivity", "hereeeeeeeeeeeeeee7");
                if (!Helper.isSignedIn()) {
                    Log.i("VideoPlayerActivity", "hereeeeeeeeeeeeeee8");
                    Toast.makeText(VideoPlayerActivity.this,
                            "In order to comment on a video you first need to sign in",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (comment.getText().toString().isEmpty()) {
                    comment.setError("Please enter a comment");
                } else {
                    Comment newComment = new Comment(Helper.getSignedInUser().get_id(),
                            Helper.getSignedInUser().getUsername(), comment.getText().toString().trim(),
                            Calendar.getInstance().getTime(), new LinkedList<>(), new LinkedList<>());
                    c.add(newComment);
                    CommentsViewModel vm = new CommentsViewModel(currentVideo.get_id());
                    vm.createComment(newComment).observe(VideoPlayerActivity.this,
                            comment1 -> {
                                comment.setText("");
                                Cadp.setComments(c);
                                Cadp.notifyDataSetChanged();
                            });
                }
            }
        });
        ImageView layout_cancel = dialog.findViewById(R.id.cancelButton);
        layout_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void showBottomDialog(Context context) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.description_bottom_sheet_layout);

        TextView dtitle = dialog.findViewById(R.id.title);
        dtitle.setText(currentVideo.getTitle());
        TextView dviews = dialog.findViewById(R.id.views);
        dviews.setText(currentVideo.getViews() + "\nViews");
        TextView dtime = dialog.findViewById(R.id.time);
        dtime.setText(currentVideo.getUpload_date());
        TextView dlikes = dialog.findViewById(R.id.likes);
        dlikes.setText(currentVideo.getLikeCount() + "\nLikes");
        TextView ddescription = dialog.findViewById(R.id.description);
        ddescription.setText(currentVideo.getDescription());
        ImageView layout_cancel = dialog.findViewById(R.id.cancelButton);
        ImageView dedit = dialog.findViewById(R.id.edit);
        if ((Helper.getSignedInUser() != null) && (currentVideo.getUsername().equals(Helper.getSignedInUser().getUsername()))) {
            dedit.setVisibility(View.VISIBLE);
            Log.i("Dialog", "Same user");
        }
        VideosViewModel viewModel = new ViewModelProvider(this).get(VideosViewModel.class);

        EditText deditTitle = dialog.findViewById(R.id.editTitle);
        EditText deditDes = dialog.findViewById(R.id.editDes);
        final int[] dEDIT_MODE = {0};
        dedit.setOnClickListener(v -> {
            if (dEDIT_MODE[0] == 0) {
                Log.i("Dialog", "Edit");
                deditTitle.setVisibility(View.VISIBLE);
                deditTitle.setText(dtitle.getText());
                dtitle.setVisibility(View.GONE);
                ddescription.setVisibility(View.GONE);
                deditDes.setVisibility(View.VISIBLE);
                deditDes.setText(ddescription.getText());
                dedit.setImageResource(R.drawable.outline_file_download_24);
                dEDIT_MODE[0] = 1;
            } else {
                currentVideo.setTitle(deditTitle.getText().toString().trim());
                currentVideo.setDescription(deditDes.getText().toString().trim());
                viewModel.updateVideo(currentVideo).observe(this, video -> {
                    dtitle.setVisibility(View.VISIBLE);
                    dtitle.setText(deditTitle.getText());
                    deditTitle.setVisibility(View.GONE);
                    ddescription.setVisibility(View.VISIBLE);
                    ddescription.setText(deditDes.getText());
                    deditDes.setVisibility(View.GONE);
                    dedit.setImageResource(R.drawable.outline_draw_24);
                    dEDIT_MODE[0] = 0;
                });
            }

        });
        layout_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.updateVideo(currentVideo).removeObserver(video -> {});
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
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }
}

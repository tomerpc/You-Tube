package com.example.aspp.adapters;

//import static com.example.aspp.Utils.getVideoById;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aspp.Helper;
import com.example.aspp.R;
import com.example.aspp.VideoPlayerActivity;
import com.example.aspp.entities.Reply;
import com.example.aspp.entities.SignedPartialCommentUpdate;
import com.example.aspp.entities.SignedPartialVideoUpdate;
import com.example.aspp.entities.Video;
import com.example.aspp.fragments.HomeFragment;
import com.example.aspp.entities.Comment;
import com.example.aspp.viewmodels.CommentsViewModel;
import com.example.aspp.viewmodels.RepliesViewModel;
import com.example.aspp.viewmodels.UsersViewModel;
import com.example.aspp.viewmodels.VideosViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class CommentsRVAdapter extends RecyclerView.Adapter<CommentsRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<Comment> comments;
    Button update;
    EditText text;
    int posToReply;
    String id;
    public CommentsRVAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = new ArrayList<>(comments);
    }
    public void setComments(List<Comment> comments) {
        this.comments = new ArrayList<>(comments);
    }
    public void setEditTextAndBtn(Button btn, EditText et) {
        text = et;
        update = btn;
    }
    public void setVideoIdParent(String id) {
        this.id = id;
    }
    @NonNull
    @Override
    public CommentsRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.comment_rv_template, parent, false);

        return new CommentsRVAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsRVAdapter.MyViewHolder holder, int position) {
        UsersViewModel vm = new UsersViewModel();
        vm.getUserByUsername(comments.get(position).getUser()).observe((LifecycleOwner) context, user ->
        {
            holder.username.setText(user.getUsername());
            String profile_url_str = context.getResources().getString(R.string.Base_Url)
                    + user.getImage();
            Glide.with(context)
                    .load(profile_url_str)
                    .into(holder.c_profile);
            if (!Helper.isSignedIn()) {
                return;
            }
            if (comments.get(position).getUsersLikes().contains(Helper.getSignedInUser().get_id())) {
                holder.like.setImageResource(R.drawable.baseline_thumb_up_24);
            }
            if (user.get_id().equals(Helper.getSignedInUser().get_id())) {
                holder.edit.setVisibility(View.VISIBLE);
                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        text.setText(comments.get(position).getContent());

                        update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!text.getText().toString().trim().
                                        equals(comments.get(position).getContent().trim())) {
                                    if (text.getText().toString().trim().equals("")) {
                                        CommentsViewModel vm = new CommentsViewModel(id);
                                        // update comment in the server
                                        vm.deleteComment(comments.get(position).get_id());
                                        comments.remove(position);
                                        notifyDataSetChanged();
                                        return;
                                    }
                                    comments.get(position).setContent(text.getText().toString().trim());
                                    comments.get(position).setUsersLikes(new ArrayList<>());
                                    comments.get(position).setReplies(new ArrayList<>());
                                    CommentsViewModel vm = new CommentsViewModel(id);
                                    // update comment in the server
                                    vm.updateComment(comments.get(position)).observe(((LifecycleOwner) context), comment ->
                                    {
                                        notifyDataSetChanged();
                                    });
                                }
                                text.setText("");
                            }
                        });
                    }
                });
            }
        });
        final boolean[] alreadyLiked = {false};
        holder.like.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!Helper.isSignedIn()) {
                    Toast.makeText(context,
                            "In order to like a comment you first need to sign in", Toast.LENGTH_LONG).show();
                    return;
                }
                if (comments.get(position).getUsersLikes().contains(Helper.getSignedInUser().get_id())) {
                    alreadyLiked[0] = true;
                }
//                if (viewModel.getVideoById(intent.getStringExtra("id")).hasActiveObservers()) {
//                    viewModel.getVideoById(intent.getStringExtra("id"))
//                            .removeObservers(VideoPlayerActivity.this);
//                }
                if (alreadyLiked[0]) {
                    ((ImageView)view).setImageResource(R.drawable.sharp_thumb_up_24);
                    comments.get(position).getUsersLikes().remove(Helper.getSignedInUser().get_id());
                    CommentsViewModel vm = new CommentsViewModel(id);
                    SignedPartialCommentUpdate updates = new SignedPartialCommentUpdate(
                            comments.get(position).get_id(),
                            comments.get(position).getUsersLikes(),
                            comments.get(position).getReplies()
                    );
                    vm.partialUpdateComment(updates);
                    alreadyLiked[0] = false;
                } else {
                    ((ImageView)view).setImageResource(R.drawable.baseline_thumb_up_24);
                    comments.get(position).getUsersLikes().add(Helper.getSignedInUser().get_id());
                    CommentsViewModel vm = new CommentsViewModel(id);
                    SignedPartialCommentUpdate updates = new SignedPartialCommentUpdate(
                            comments.get(position).get_id(),
                            comments.get(position).getUsersLikes(),
                            comments.get(position).getReplies()
                    );
                    vm.partialUpdateComment(updates);
                    alreadyLiked[0] = true;
                }
                notifyDataSetChanged();
            }
        });
        holder.numOfReplays.setText(comments.get(position).getReplies().size() + " Replays");
        holder.comment.setText(comments.get(position).getContent());
//        holder.numOfDislikes.setText(comments.get(position).getDislikes() + " Dislikes");
        holder.numOfLikes.setText(comments.get(position).getUsersLikes().size() + " Likes");

//        holder.c_profile.setImageBitmap();
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posToReply = position;
                showCommentsDialog(context, comments.get(position).getReplies());
            }
        });

    }
    public void showCommentsDialog(Context context, List<Reply> c) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.comment_section_bottom_sheet_layout);

        EditText comment = dialog.findViewById(R.id.comment);
        Button send = dialog.findViewById(R.id.send);
        RecyclerView commentsRV = dialog.findViewById(R.id.comment_section);
        if (c == null) {
            Toast.makeText(context, "Replies are loading", Toast.LENGTH_LONG).show();
            return;
        }
        Reply parent = new Reply(comments.get(posToReply));
        RepliesRVAdapter Cadp = new RepliesRVAdapter(context, c, parent);
        Cadp.setVideoAndCommentIdParent(id, comments.get(posToReply));
        Cadp.setEditTextAndBtn(send, comment);
        commentsRV.setAdapter(Cadp);
        commentsRV.setLayoutManager(new LinearLayoutManager(dialog.getContext()));
        ImageView profilePic = dialog.findViewById(R.id.profilePic);
//        if (viewModel.getVideoById(currentVideo.get_id()).hasActiveObservers()) {
//            viewModel.getVideoById(currentVideo.get_id())
//                    .removeObservers(VideoPlayerActivity.this);
//        }
        if (Helper.isSignedIn()) {
            String profile_url_str = context.getResources().getString(R.string.Base_Url)
                    + Helper.getSignedInUser().getImage();
            Glide.with(context)
                    .load(profile_url_str)
                    .into(profilePic);
        } else {
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_foreground);
            profilePic.setImageBitmap(bm);
        }
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Helper.isSignedIn()) {
                    Toast.makeText(context,
                            "In order to reply on a comment you first need to sign in",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (comment.getText().toString().isEmpty()) {
                    comment.setError("Please enter a reply");
                } else {
                    Reply newReply = new Reply("",Helper.getSignedInUser().getUsername(),
                            "@"+parent.getUser()+ " " + comment.getText().toString().trim(),
                            Calendar.getInstance().getTime(), new LinkedList<>());
                    c.add(newReply);
                    RepliesViewModel vm = new RepliesViewModel(id, comments.get(posToReply));
                    vm.createReply(newReply).observe((LifecycleOwner) context,
                            comment1 -> {
                                comment.setText("");
                                Cadp.setReplies(c);
                                Cadp.notifyDataSetChanged();
                                notifyItemChanged(posToReply);
                            });
//                    comment.setText("");
//                    Cadp.setReplies(c);
//                    Cadp.notifyItemInserted(c.size());
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

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView comment, numOfLikes, username, numOfReplays;
        ImageView c_profile, edit;
        ImageButton like;
        View v;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            comment = itemView.findViewById(R.id.comment);
            username = itemView.findViewById(R.id.username);
            numOfLikes = itemView.findViewById(R.id.num_of_likes);
            numOfReplays = itemView.findViewById(R.id.num_of_replays);
            c_profile = itemView.findViewById(R.id.c_profile);
            edit = itemView.findViewById(R.id.edit);
            like = itemView.findViewById(R.id.like);
            v = itemView;
        }
    }
}

package com.example.aspp.adapters;

//import static com.example.aspp.Utils.getVideoById;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aspp.Helper;
import com.example.aspp.R;
import com.example.aspp.entities.Comment;
import com.example.aspp.entities.Reply;
import com.example.aspp.entities.SignedPartialCommentUpdate;
import com.example.aspp.entities.SignedPartialReplyUpdate;
import com.example.aspp.viewmodels.CommentsViewModel;
import com.example.aspp.viewmodels.RepliesViewModel;
import com.example.aspp.viewmodels.UsersViewModel;

import java.util.ArrayList;
import java.util.List;

public class RepliesRVAdapter extends RecyclerView.Adapter<RepliesRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<Reply> replies;
    Button update;
    EditText text;
    int posToEdit;
    String id;
    Comment cId;
    public RepliesRVAdapter(Context context, List<Reply> replies, Reply parent) {
        this.context = context;
        this.replies = new ArrayList<>();
        this.replies.add(parent);
        this.replies.addAll(replies);
    }
    public void setReplies(List<Reply> replies) {
        ArrayList<Reply> temp = new ArrayList<>();
        temp.add(this.replies.get(0));
        temp.addAll(replies);
        this.replies = new ArrayList<>(temp);
    }
    public void setEditTextAndBtn(Button btn, EditText et) {
        text = et;
        update = btn;
    }
    public void setVideoAndCommentIdParent(String id, Comment cId) {
        this.id = id;
        this.cId = cId;
    }
    @NonNull
    @Override
    public RepliesRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.comment_rv_template, parent, false);

        return new RepliesRVAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (position == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                holder.v.setBackground(context.getDrawable(R.drawable.top_profile_back));
            }
        } else {
            holder.v.setPaddingRelative(40,0,0,0);
        }
        UsersViewModel vm = new UsersViewModel();
        vm.getUserByUsername(replies.get(position).getUser()).observe((LifecycleOwner) context, user ->
        {
            if (user == null)
                return;
            holder.username.setText(user.getUsername());
            String profile_url_str = context.getResources().getString(R.string.Base_Url)
                    + user.getImage();
            Glide.with(context)
                    .load(profile_url_str)
                    .into(holder.c_profile);
            if (!Helper.isSignedIn()) {
                return;
            }
            if (replies.get(position).getUsersLikes().contains(Helper.getSignedInUser().get_id())) {
                holder.like.setImageResource(R.drawable.baseline_thumb_up_24);
            }
            if ((Helper.getSignedInUser() != null) && (user.get_id().equals(Helper.getSignedInUser().get_id()))) {
                holder.edit.setVisibility(View.VISIBLE);
                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        text.setText(replies.get(position).getContent());

                        update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!text.getText().toString().trim().
                                        equals(replies.get(position).getContent().trim())) {
                                    if (text.getText().toString().trim().equals("")) {
                                        RepliesViewModel vm = new RepliesViewModel(id, cId);
                                        // update comment in the server
                                        vm.deleteReply(replies.get(position));
                                        replies.remove(position);
                                        notifyDataSetChanged();
                                        return;
                                    }
                                    replies.get(position).setContent(text.getText().toString().trim());
                                    replies.get(position).setUsersLikes(new ArrayList<>());
                                    RepliesViewModel vm = new RepliesViewModel(id, cId);
                                    // update comment in the server
                                    vm.updateReply(replies.get(position));
                                    notifyDataSetChanged();
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
                if (replies.get(position).getUsersLikes().contains(Helper.getSignedInUser().get_id())) {
                    alreadyLiked[0] = true;
                }
//                if (viewModel.getVideoById(intent.getStringExtra("id")).hasActiveObservers()) {
//                    viewModel.getVideoById(intent.getStringExtra("id"))
//                            .removeObservers(VideoPlayerActivity.this);
//                }
                if (alreadyLiked[0]) {
                    ((ImageView)view).setImageResource(R.drawable.sharp_thumb_up_24);
                    replies.get(position).getUsersLikes().remove(Helper.getSignedInUser().get_id());
                    RepliesViewModel vm = new RepliesViewModel(id, cId);
                    vm.partialUpdateReply(replies.get(position));
                    alreadyLiked[0] = false;
                } else {
                    ((ImageView)view).setImageResource(R.drawable.baseline_thumb_up_24);
                    replies.get(position).getUsersLikes().add(Helper.getSignedInUser().get_id());
                    RepliesViewModel vm = new RepliesViewModel(id, cId);
                    vm.partialUpdateReply(replies.get(position));
                    alreadyLiked[0] = true;
                }
                notifyDataSetChanged();
            }
        });
        holder.numOfReplays.setVisibility(View.GONE);
        holder.comment.setText(replies.get(position).getContent());
//        holder.numOfDislikes.setText(comments.get(position).getDislikes() + " Dislikes");
        holder.numOfLikes.setText(replies.get(position).getUsersLikes().size() + " Likes");

//        holder.c_profile.setImageBitmap();

    }
    @Override
    public int getItemCount() {
        return replies.size();
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


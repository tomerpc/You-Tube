package com.example.aspp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aspp.Helper;
import com.example.aspp.MainActivity;
import com.example.aspp.R;
import com.example.aspp.SignInActivity;
import com.example.aspp.SignUpActivity;
import com.example.aspp.adapters.HomeRVAdapter;
import com.example.aspp.entities.User;
import com.example.aspp.entities.Video;
import com.example.aspp.viewmodels.UsersViewModel;
import com.example.aspp.viewmodels.VideosViewModel;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    RecyclerView videos;
    TextView username, numOfVideos;
    ImageView profile, options;
    Spinner spinner;
    String[] listOf = {"My videos", "Liked Videos"};

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private HomeRVAdapter related;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        videos = v.findViewById(R.id.videos);
        username = v.findViewById(R.id.username);
        numOfVideos = v.findViewById(R.id.num_of_videos);
        profile = v.findViewById(R.id.profilePic);
        options = v.findViewById(R.id.options);
        options.setOnClickListener(view -> {
            showBottomDialog(getContext());
        });
        related = new HomeRVAdapter(getContext(), new LinkedList<>());
        username.setText(Helper.getSignedInUser().getUsername());
        String profile_url_str = getResources().getString(R.string.Base_Url)
                + Helper.getSignedInUser().getImage();
        Glide.with(this)
                .load(profile_url_str)
                .into(profile);
        VideosViewModel vvm = new ViewModelProvider(this).get(VideosViewModel.class);
        vvm.get(Helper.getSignedInUser().getUsername()).observe((LifecycleOwner) getContext(), videos1 ->
        {
            related.setVideos(videos1);
            numOfVideos.setText(videos1.size() + " Videos");
            Log.i("Related Videos", videos1.toString());
            related.notifyDataSetChanged();
        });
        videos.setAdapter(related);
        videos.setLayoutManager(new LinearLayoutManager(getContext()));
        spinner = v.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        // Create the instance of ArrayAdapter
        // having the list of courses
        ArrayAdapter ad
                = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, listOf);

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        spinner.setAdapter(ad);

        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // make toastof name of course
        // which is selected in spinner
        VideosViewModel vvm = new ViewModelProvider(this).get(VideosViewModel.class);
        if (i == 0) {
            vvm.get(Helper.getSignedInUser().getUsername()).observe((LifecycleOwner) getContext(), videos1 ->
            {
                related.setVideos(videos1);
                numOfVideos.setText(videos1.size() + " Videos");
                Log.i("Related Videos", videos1.toString());
                related.notifyDataSetChanged();
            });
        } else {
            vvm.get().observe((LifecycleOwner) getContext(), videos1 ->
            {
                ArrayList<Video> liked = new ArrayList<>();
                if (videos1 == null || videos1.isEmpty()){
                    numOfVideos.setText("0 Videos");
                    return;
                }

                for (int j = 0; j < videos1.size(); j++) {
                    if (videos1.get(j).getUsersLikes().contains(Helper.getSignedInUser().get_id()))
                        liked.add(videos1.get(j));
                }
                related.setVideos(liked);
                numOfVideos.setText(liked.size() + " Videos");
                Log.i("Related Videos", liked.toString());
                Log.i("Related Videos id", Helper.getSignedInUser().get_id());
                related.notifyDataSetChanged();
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void showBottomDialog(Context context) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_bottom_sheet_layout);
        UsersViewModel vm = new ViewModelProvider(this).get(UsersViewModel.class);

        LinearLayout layout_Update = dialog.findViewById(R.id.layout_Update);
        layout_Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SignUpActivity.class);
                i.putExtra("update", true);
                startActivity(i);
            }
        });


        LinearLayout layout_delete = dialog.findViewById(R.id.layout_delete);
        layout_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vm.deleteUser(Helper.getSignedInUser().get_id());
                startActivity(new Intent(getContext(), SignInActivity.class));
            }
        });

        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

}
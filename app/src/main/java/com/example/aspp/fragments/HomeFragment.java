package com.example.aspp.fragments;

import static com.example.aspp.Utils.readCommentsList;
import static com.example.aspp.Utils.readVideosList;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.aspp.R;
import com.example.aspp.adapters.HomeRVAdapter;
import com.example.aspp.api.VideoAPI;
import com.example.aspp.entities.Comment;
import com.example.aspp.entities.User;
import com.example.aspp.entities.Video;
import com.example.aspp.repositories.VideoRepository;
import com.example.aspp.viewmodels.VideosViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private User myUser;
    RecyclerView surveyListContainer;
    SwipeRefreshLayout swipeRefreshLayout;
    public static HomeRVAdapter adp;
    public ArrayList<Video> videoArrayList, filterdVideos;
    public static ArrayList<Comment> commentArrayList;
    Toolbar toolbar;
    MenuItem searchMenuItem, notifMenuItem;
    SearchView searchView;
    DrawerLayout drawerLayout;
    ImageButton openSideNav;
    Button all_btn, gaming_btn, music_btn, tutorials_btn;
    VideosViewModel viewModel;
    boolean nightMode;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public HomeFragment(boolean mode, User user) {
        nightMode = mode;
        myUser = user;
    }

    public HomeFragment(User user) {
        myUser = user;
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AvailableSurveysFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_bar, menu);
        notifMenuItem = menu.findItem(R.id.notifications);
        notifMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragmentContainer, new NotificationFragment())
                        .commit();
                return false;
            }
        });
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setIconified(true);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mySearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mySearch(query);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void mySearch(String query) {
        if (query.equals("")) {
            adp.setVideos(videoArrayList);
            return;
        }

        Set<Video> updatedSet = new HashSet<>();

        for (Video v:filterdVideos) {
            if (v.getTitle().startsWith(query.trim()))
                updatedSet.add(v);
            else if (v.getUsername().startsWith(query.trim()))
                updatedSet.add(v);
//            else if (v.getDescription().startsWith(query.trim()))
//                updatedSet.add(v);
        }

        if (updatedSet.isEmpty()) {
            for (Video v:filterdVideos) {
                if (v.getTitle().contains(query.trim()))
                    updatedSet.add(v);
                else if (v.getUsername().contains(query.trim()))
                    updatedSet.add(v);
//                else if (v.getDescription().contains(query.trim()))
//                    updatedSet.add(v);
//                else if (v.getTags().contains(query.trim()))
//                    updatedSet.add(v);
            }
        }

        if (updatedSet.isEmpty()) {
            Toast.makeText(getContext(), "We didn't find anything like that! are you sure it even exists?", Toast.LENGTH_LONG).show();
            return;
        }

        filterdVideos = new ArrayList<>(updatedSet);
//        filterdVideos.addAll(updatedSet);
        adp.setVideos(filterdVideos);
//        viewModel.get().observe(getViewLifecycleOwner(), videos -> {
//            filterdVideos = new ArrayList<>(videos);
//        });
        adp.notifyDataSetChanged();

        viewModel.getTopVideos().removeObservers(getViewLifecycleOwner());
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sp = getContext().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sp.getBoolean("night", false);

        commentArrayList = readCommentsList(getContext());
        drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        Toolbar activtytoolbar = getActivity().findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, activtytoolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        drawerLayout.closeDrawer(GravityCompat.START);

        openSideNav = v.findViewById(R.id.open_side_nav);
        openSideNav.setOnClickListener(item -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        all_btn = v.findViewById(R.id.all);
        if (nightMode)
            all_btn.setBackgroundTintList(getContext().getColorStateList(R.color.colorOnSurface_night));
        else
            all_btn.setBackgroundTintList(getContext().getColorStateList(R.color.colorOnSurface_day));
        all_btn.setOnClickListener(this::onFilterList);
        gaming_btn = v.findViewById(R.id.gaming);
        gaming_btn.setOnClickListener(this::onFilterList);
        music_btn = v.findViewById(R.id.music);
        music_btn.setOnClickListener(this::onFilterList);
        tutorials_btn = v.findViewById(R.id.tutorials);
        tutorials_btn.setOnClickListener(this::onFilterList);

        toolbar = v.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("");
        surveyListContainer = v.findViewById(R.id.ActiveSurveys);
        adp = new HomeRVAdapter(getContext(), new ArrayList<>());
        viewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        viewModel.getTopVideos().observe(getViewLifecycleOwner(), videos -> {
            videoArrayList = new ArrayList<>(videos);
            adp.setVideos(videos);
            adp.notifyDataSetChanged();

        });
        viewModel.get().observe(getViewLifecycleOwner(), videos -> {
            filterdVideos = new ArrayList<>(videos);
        });
        surveyListContainer.setAdapter(adp);
        surveyListContainer.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::onRefreshList);
        //TODO when clicking an item a dialog will pop up with details
//        CustomSurveyDialog dialog = new CustomSurveyDialog(getActivity());
//        dialog.show();
        return v;
    }

    private void onRefreshList() {
        viewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        viewModel.get().observe(getViewLifecycleOwner(), videos -> {
            videoArrayList = new ArrayList<>(videos);
            adp.setVideos(videos);
            adp.notifyDataSetChanged();
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    private void onFilterList(View view) {
        if (view.getId() == R.id.all) {
//            videoArrayList = readVideosList(getContext());
//            adp = new HomeRVAdapter(getContext(), videoArrayList);
            surveyListContainer.setAdapter(adp);
        } else {
            mySearch(((Button)view).getText().toString().trim());
        }
        if (nightMode) {
            all_btn.setBackgroundTintList(getContext().getColorStateList(R.color.colorPrimary_night));
            all_btn.setTextColor(getContext().getColorStateList(R.color.white));
            gaming_btn.setBackgroundTintList(getContext().getColorStateList(R.color.colorPrimary_night));
            gaming_btn.setTextColor(getContext().getColorStateList(R.color.white));
            music_btn.setBackgroundTintList(getContext().getColorStateList(R.color.colorPrimary_night));
            music_btn.setTextColor(getContext().getColorStateList(R.color.white));
            tutorials_btn.setBackgroundTintList(getContext().getColorStateList(R.color.colorPrimary_night));
            tutorials_btn.setTextColor(getContext().getColorStateList(R.color.white));
            view.setBackgroundTintList(getContext().getColorStateList(R.color.colorOnSurface_night));
            ((Button)view).setTextColor(getContext().getColorStateList(R.color.white));
        } else {
            all_btn.setBackgroundTintList(getContext().getColorStateList(R.color.colorPrimary_day));
            all_btn.setTextColor(getContext().getColorStateList(R.color.black));
            gaming_btn.setBackgroundTintList(getContext().getColorStateList(R.color.colorPrimary_day));
            gaming_btn.setTextColor(getContext().getColorStateList(R.color.black));
            music_btn.setBackgroundTintList(getContext().getColorStateList(R.color.colorPrimary_day));
            music_btn.setTextColor(getContext().getColorStateList(R.color.black));
            tutorials_btn.setBackgroundTintList(getContext().getColorStateList(R.color.colorPrimary_day));
            tutorials_btn.setTextColor(getContext().getColorStateList(R.color.black));
            view.setBackgroundTintList(getContext().getColorStateList(R.color.colorOnSurface_day));
        }
    }
}
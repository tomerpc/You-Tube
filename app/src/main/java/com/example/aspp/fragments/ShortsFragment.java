package com.example.aspp.fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.aspp.R;
import com.example.aspp.adapters.ShortsRVAdapter;
import com.example.aspp.entities.Video;
import com.example.aspp.viewmodels.VideosViewModel;

import java.util.ArrayList;

/**
 * A simple {@link androidx.fragment.app.Fragment} subclass.
 * Use the {@link ShortsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShortsFragment extends androidx.fragment.app.Fragment {

    ViewPager2 viewPager2;
    ShortsRVAdapter adp;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Video> shortsArrayList;

    public ShortsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActiveSurveysFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShortsFragment newInstance(String param1, String param2) {
        ShortsFragment fragment = new ShortsFragment();
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
        View v = inflater.inflate(R.layout.fragment_shorts, container, false);
        viewPager2 = v.findViewById(R.id.viewPager2);
        VideosViewModel viewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        shortsArrayList = new ArrayList<>();
        viewModel.get().observe(getViewLifecycleOwner(), videos -> {
            shortsArrayList = new ArrayList<>(videos);
            adp.setVideos(shortsArrayList);
            adp.notifyDataSetChanged();
        });
        adp = new ShortsRVAdapter(getContext(), shortsArrayList);
        viewPager2.setAdapter(adp);
        return v;
    }
}
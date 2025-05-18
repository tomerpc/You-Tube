package com.example.aspp.fragments;

import static com.example.aspp.Utils.readVideosList;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aspp.R;
import com.example.aspp.adapters.NotificationsRVAdapter;
import com.example.aspp.entities.Video;
import com.example.aspp.viewmodels.VideosViewModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {

    Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView notificationListContainer;
    static ArrayList<Video> notificationArrayList;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private NotificationsRVAdapter adp;
    private VideosViewModel viewModel;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
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
        View v = inflater.inflate(R.layout.fragment_notification, container, false);
        toolbar = v.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragmentContainer, new HomeFragment())
                        .commit();
            }
        });

        notificationListContainer = v.findViewById(R.id.recent_notifications);
        viewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        notificationArrayList = new ArrayList<>();
        viewModel.get().observe(getViewLifecycleOwner(), videos -> {
            notificationArrayList = new ArrayList<>(videos);
            adp.setVideos(notificationArrayList);
            adp.notifyDataSetChanged();
        });
        Log.i("DATA", notificationArrayList.toString());
        adp = new NotificationsRVAdapter(getContext(), notificationArrayList);
        notificationListContainer.setAdapter(adp);
        notificationListContainer.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::onRefreshList);

        return v;
    }

    private void onRefreshList() {
        notificationArrayList = readVideosList(getContext());
        adp = new NotificationsRVAdapter(getContext(), notificationArrayList);
        notificationListContainer.setAdapter(adp);
        swipeRefreshLayout.setRefreshing(false);
    }
}
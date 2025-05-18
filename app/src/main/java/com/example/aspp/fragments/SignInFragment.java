package com.example.aspp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.aspp.Helper;
import com.example.aspp.MainActivity;
import com.example.aspp.R;
import com.example.aspp.entities.User;
import com.example.aspp.viewmodels.TokenViewModel;
import com.example.aspp.viewmodels.UsersViewModel;
import com.example.aspp.viewmodels.VideosViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class SignInFragment extends Fragment {

    private static final int REQUEST_PERMISSION_CODE = 123;
    private static final int REQUEST_CAMERA_PERMISSION_CODE = 456;
    private static final String TAG = "SignInFragment";

    private EditText usernameEditText;
    private EditText passwordEditText;

    private User myUser;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        usernameEditText = view.findViewById(R.id.usernameText);
        passwordEditText = view.findViewById(R.id.passwordText);

        Button loginBtn = view.findViewById(R.id.login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                isValidCredentials(username, password);
            }
        });

        TextView signUp = view.findViewById(R.id.signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUp();
            }
        });

        // Request external storage and camera permission if not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_CODE);
            }
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION_CODE);
            }
        }
        return view;
    }

    private void isValidCredentials(String username, String password) {
        TokenViewModel viewModel = new ViewModelProvider(this).get(TokenViewModel.class);
        myUser = new User("", username, "", password, "", 0);
        viewModel.processLogin(myUser).observe(getViewLifecycleOwner(), token -> {
            if (token != null) {
//                Helper.setToken(token);
                Log.i("Token", Objects.requireNonNull(token).toString());
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.putExtra("loggedInUser", myUser);
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToSignUp() {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.switchFragment(new SignUpFragment());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "External storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "External storage permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Camera permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
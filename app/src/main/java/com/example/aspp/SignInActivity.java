package com.example.aspp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.aspp.entities.User;
import com.example.aspp.fragments.SignUpFragment;
import com.example.aspp.viewmodels.TokenViewModel;
import com.example.aspp.viewmodels.UsersViewModel;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 123;
    private static final int REQUEST_CAMERA_PERMISSION_CODE = 456;
    private static final String TAG = "SignInFragment";

    private EditText usernameEditText;
    private EditText passwordEditText;

    private User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        usernameEditText = findViewById(R.id.usernameText);
        passwordEditText = findViewById(R.id.passwordText);

        Button loginBtn = findViewById(R.id.login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                boolean fillAll = true;
                if (username.equals("")) {
                    usernameEditText.setError("You most fill this field");
                    fillAll = false;
                }
                if (password.equals("")) {
                    passwordEditText.setError("You most fill this field");
                    fillAll = false;
                }
                if (!fillAll)
                    return;
                isValidCredentials(username, password);
            }
        });

        TextView signUp = findViewById(R.id.signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUp();
            }
        });

        // Request external storage and camera permission if not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_CODE);
            }
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION_CODE);
            }
        }
//        return view;
    }

    private void isValidCredentials(String username, String password) {
        UsersViewModel vm = new ViewModelProvider(this).get(UsersViewModel.class);
        vm.getUserByUsername(username).observe(this, user -> {
            if (user == null) {
                return;
            }
            myUser = user;
            myUser.setPassword(password);
            TokenViewModel viewModel = new ViewModelProvider(this).get(TokenViewModel.class);
            viewModel.processLogin(myUser).observe(this, token -> {
                if (token != null && token.getToken() != null) {
                    Log.i("SignIn",token.getToken());
                    saveToken(token.getToken());
//                    Helper.setToken(token);
                    Helper.setSignedInUser(myUser);
//                    Log.i("Token", token.getToken());
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("loggedInUser", myUser);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Invalid username or password, " +
                            "Please ensure your connection", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private void saveToken(String token) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    "MyAppPrefs",
                    masterKeyAlias,
                    Helper.context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("auth_token", token);
            editor.apply();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            // Handle the exception
        }
    }

    private void goToSignUp() {
        Intent i = new Intent(this, SignUpActivity.class);
        i.putExtra("update", false);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "External storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "External storage permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
package com.example.aspp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Base64;


import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.aspp.entities.Users;
import com.example.aspp.fragments.SignInFragment;
import com.example.aspp.viewmodels.UsersViewModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private ImageView profile;
    private EditText name;
    private EditText username;
    private EditText password;
    private EditText repeatPassword;
    private Button signUp;

    private Users newUser;

    private boolean hasProfilePicture = false, exists;
    private String profilePictureUrl;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        profile = findViewById(R.id.profilepicture);
        name = findViewById(R.id.firstandlastname);
        username = findViewById(R.id.usernameText);
        password = findViewById(R.id.passwordText);
        repeatPassword = findViewById(R.id.repeatPassword);
        signUp = findViewById(R.id.signup);

        Intent i = getIntent();
        signUp.setOnClickListener(v -> {
            if (i.getExtras().getBoolean("update")) {
                updateUser();
            } else {
                isSuccessSignUp();
            }
        });

        profile.setOnClickListener(v -> showImageOptions());

//        return view;
    }

    private void updateUser() {
        if (attemptSignUp(true)) {
            String user = username.getText().toString().trim();
            String password1 = password.getText().toString().trim();
            String password2 = repeatPassword.getText().toString().trim();
            String fullName = name.getText().toString().trim();
            BitmapDrawable drawable = (BitmapDrawable) profile.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
            byte[] bb = bos.toByteArray();
            String image = Base64.encodeToString(bb, Base64.DEFAULT);

            newUser = new Users(Helper.getSignedInUser().get_id(), user, fullName, password1, password2, image);
            try {
                UsersViewModel vm = new ViewModelProvider(this).get(UsersViewModel.class);
                vm.updateUser(newUser, Helper.getSignedInUser().get_id()).observe(this,
                        user1 -> {
                    Helper.setSignedInUser(user1);
                    navigateToSignInScreen();
                        });
            } catch (Exception e) {

            }
        }
    }

    private void showImageOptions() {
        String[] options = {"Choose from Gallery", "Take a Photo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Profile Picture");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                chooseFromGallery();
            } else if (which == 1) {
                dispatchTakePictureIntent();
            }
        });
        builder.show();
    }

    private void chooseFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
        //=====================================================
//        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
        //-------------------------------------------------------------
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                Toast.makeText(this, "Error occurred while creating the image file", Toast.LENGTH_SHORT).show();
//            }
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.aspp.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
//        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(this.getFilesDir(), "images");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == 1) {
//                videoPath = data.toURI().split(";")[0];
//                Log.i("DATA", data.toURI().split(";")[0] + "  ");
                Bitmap b = (Bitmap) data.getExtras().get("data");
                Bitmap circularBitmap = getCircularBitmap(b);
                profile.setImageBitmap(circularBitmap);
                hasProfilePicture = true;
            }
            if (requestCode == 100) {
                Uri uri = data.getData();
                Bitmap bitmap = null;
                Bitmap decoded = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 10, out);
                    decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Bitmap circularBitmap = getCircularBitmap(decoded);
                profile.setImageBitmap(circularBitmap);
                hasProfilePicture = true;
            }
        }
    }

    private Bitmap getCircularBitmap(Bitmap squareBitmap) {
        int width = squareBitmap.getWidth();
        int height = squareBitmap.getHeight();

        int diameter = Math.min(width, height);
        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, diameter, diameter);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(squareBitmap, rect, rect, paint);

        return output;
    }


    private void isSuccessSignUp() {
        if (attemptSignUp(false)) {
            String user = username.getText().toString().trim();
            String password1 = password.getText().toString().trim();
            String password2 = repeatPassword.getText().toString().trim();
            String fullName = name.getText().toString().trim();
            BitmapDrawable drawable = (BitmapDrawable) profile.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
            byte[] bb = bos.toByteArray();
            String image = Base64.encodeToString(bb, Base64.DEFAULT);

            newUser = new Users("", user, fullName, password1, password2, image);
            try {
                UsersViewModel vm = new ViewModelProvider(this).get(UsersViewModel.class);
                vm.createUser(newUser).observe(this, user1 -> {
                    if (user1 != null) {
                        navigateToSignInScreen();
                        Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {

            }
        }
    }

    private boolean attemptSignUp(boolean update) {
        String user = username.getText().toString().trim();
        String password1 = password.getText().toString().trim();
        String password2 = repeatPassword.getText().toString().trim();
        String fullName = name.getText().toString().trim();
        final boolean[] valid = {true};
        if (fullName.isEmpty() || user.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!update) {
            UsersViewModel vm = new UsersViewModel();
            vm.getUserByUsername(user).observe(this, user1 -> {
                if (user1 != null && user1.getUsername().equals("")) {

                }
                else if (user1 != null && !user1.getUsername().equals("")) {
                    username.setError("Username already exists, try another one");
                    valid[0] = false;
                }
            });
        }
        if (!checkPasswordLength(password1)) {
            password.setError("Password should contain at least 8 characters");
            valid[0] = false;
        }

        if (!checkPasswordMatch(password1, password2)) {
            repeatPassword.setError("Passwords don't match");
            valid[0] = false;
        }

        if (!isPasswordValid(password1)) {
            password.setError("Password must contain both lower and upper case characters and also numbers");
            valid[0] = false;
        }

        if (!hasProfilePicture) {
            Toast.makeText(this, "Please select a profile picture", Toast.LENGTH_SHORT).show();
            valid[0] = false;
        }

        if (!isFullnameValid(fullName)) {
            name.setError("Please enter private and family name");
            valid[0] = false;
        }
        return valid[0];
    }

    private boolean isFullnameValid(String fullName) {
        String regex = "([A-Za-z])+ ([A-Za-z])+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fullName);
        return matcher.matches();
    }

    private boolean checkPasswordLength(String password1) {
        if (password1.length() < 8) {
//            Toast.makeText(this, "Your password must contain at least 8 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkPasswordMatch(String password, String repeatPassword) {
        if (!password.equals(repeatPassword)) {
//            Toast.makeText(this, "Your passwords don't match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        String regex = "^.*(?=.{8,})(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
//        Toast.makeText(this, "Your password must contain characters and numbers", Toast.LENGTH_SHORT).show();
        return matcher.matches();
    }

    private void navigateToSignInScreen() {
        startActivity(new Intent(this, SignInActivity.class));
    }
}
package com.example.aspp.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.aspp.Helper;
import com.example.aspp.MainActivity;
import com.example.aspp.R;
import com.example.aspp.SignUpActivity;
import com.example.aspp.entities.User;
import com.example.aspp.entities.Video;
import com.example.aspp.viewmodels.VideosViewModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class AddVideoFragment extends Fragment {
    private static final String TAG = "AddVideoFragment";
    private VideosViewModel videosViewModel;
    private User myUser;
    private Button createVideo, cancel;
    private EditText videoTitle, videoDescription, videoTags;
    private ImageView videoThumbnail;
    private Uri videoUri;
    private boolean photoWasSelected = false;
    private boolean videoWasSelected = false;

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted. Showing bottom dialog.");
            showBottomDialog(getContext());
        } else {
            Log.d(TAG, "Camera permission denied.");
        }
    });
    private Uri uri;

    public AddVideoFragment() {
        // Required empty public constructor
    }

    public AddVideoFragment(User user) {
        myUser = user;
    }

    public static AddVideoFragment newInstance(String param1, String param2) {
        AddVideoFragment fragment = new AddVideoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Fragment created.");
        videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_video, container, false);

        Log.d(TAG, "Initializing views.");
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting camera permission.");
            activityResultLauncher.launch(android.Manifest.permission.CAMERA);
        } else {
            Log.d(TAG, "Camera permission already granted. Showing bottom dialog.");
            showBottomDialog(getContext());
        }

        videoThumbnail = view.findViewById(R.id.thumbnail);
        videoThumbnail.setOnClickListener(v -> {
            Log.d(TAG, "Thumbnail clicked. Opening gallery to select image.");
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });

        videoTitle = view.findViewById(R.id.title);
        videoDescription = view.findViewById(R.id.description);
        videoTags = view.findViewById(R.id.tags);
        createVideo = view.findViewById(R.id.create);

        createVideo.setOnClickListener(v -> {
            Log.d(TAG, "Create video button clicked.");
            String title = videoTitle.getText().toString();
            String description = videoDescription.getText().toString();
            String tags = videoTags.getText().toString();
            boolean isValid = true;

            if (title.isEmpty()) {
                isValid = false;
                videoTitle.setError("Title is required");
                Log.w(TAG, "Title is empty.");
            }
            if (description.isEmpty()) {
                isValid = false;
                videoDescription.setError("Description is required");
                Log.w(TAG, "Description is empty.");
            }
            if (tags.isEmpty()) {
                isValid = false;
                videoTags.setError("Tags are required");
                Log.w(TAG, "Tags are empty.");
            }

            if (!photoWasSelected) {
                isValid = false;
                Toast.makeText(getContext(), "Thumbnail image is required, click on the lens icon", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Thumbnail image not selected.");
            }

            if (!videoWasSelected) {
                isValid = false;
                Toast.makeText(getContext(), "Video file is required", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Video file not selected.");
            }

            if (!isValid) {
                Log.w(TAG, "Video data is invalid. Aborting creation.");
                return;
            }

            Log.d(TAG, "Encoding video and thumbnail for upload.");
            BitmapDrawable drawable = (BitmapDrawable) videoThumbnail.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bb = bos.toByteArray();
            String thumbnail = Base64.encodeToString(bb, Base64.DEFAULT);
            String base64Video = encodeVideoToBase64(videoUri);

            String[] tagArray = tags.split(",\\s*");

            Video newVideo = new Video(
                    tagArray,
                    new ArrayList<>(),
                    "",
                    title,
                    description,
                    "data:video/mp4;base64," + base64Video,
                    "data:image/jpeg;base64," + thumbnail,
                    Calendar.getInstance().getTime().toString(),
                    Helper.getSignedInUser().getUsername(),
                    0,
                    0,
                    0,
                    new ArrayList<>()
            );

            Log.d(TAG, "Video object created: " + newVideo.toString());
            uploadVideo(newVideo);
            startActivity(new Intent(getContext(), MainActivity.class));
        });

        cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> {
            Log.d(TAG, "Cancel button clicked. Returning to MainActivity.");
            startActivity(new Intent(getContext(), MainActivity.class));
        });

        videosViewModel.getUploadStatusMessage().observe(getViewLifecycleOwner(), statusMessage -> {
            if (statusMessage != null) {
                Log.d(TAG, "Upload status message received: " + statusMessage);
                Toast.makeText(getContext(), statusMessage, Toast.LENGTH_SHORT).show();
            }
        });

        videosViewModel.getIsUploading().observe(getViewLifecycleOwner(), isUploading -> {
            Log.d(TAG, "Is uploading: " + isUploading);
            if (isUploading) {
                // Show loading indicator
            } else {
                // Hide loading indicator
            }
        });

        SharedPreferences sp = getContext().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        boolean nightMode = sp.getBoolean("night", false);
        if (nightMode) {
            createVideo.setBackgroundTintList(getContext().getColorStateList(R.color.colorOnSurface_night));
            cancel.setBackgroundTintList(getContext().getColorStateList(R.color.colorOnSurface_night));
            Log.d(TAG, "Night mode enabled.");
        } else {
            createVideo.setBackgroundTintList(getContext().getColorStateList(R.color.colorOnSurface_day));
            cancel.setBackgroundTintList(getContext().getColorStateList(R.color.colorOnSurface_day));
            Log.d(TAG, "Day mode enabled.");
        }

        return view;
    }

    private void uploadVideo(Video newVideo) {
        Log.d(TAG, "Starting video upload.");
        videosViewModel.addVideo(newVideo);
        Log.d(TAG, "Preparing to upload video1: " + newVideo.toString());
        videosViewModel.getIsUploading().observe(getViewLifecycleOwner(), isUploading -> {
            if (!isUploading) {
                Log.d(TAG, "Video upload completed.");
                String message = videosViewModel.getUploadStatusMessage().getValue();
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showBottomDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_video_bottom_sheet_layout);

        LinearLayout layout_camera = dialog.findViewById(R.id.layout_camera);
        layout_camera.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(intent, 1);
        });

        LinearLayout layout_gallery = dialog.findViewById(R.id.layout_gallery);
        layout_gallery.setOnClickListener(v -> {
            dialog.dismiss();
            Intent videoIntent = new Intent(Intent.ACTION_PICK);
            videoIntent.setType("video/*");
            startActivityForResult(Intent.createChooser(videoIntent, "Select Video"), 2);
        });

        LinearLayout layout_return = dialog.findViewById(R.id.layout_return);
        layout_return.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(getContext(), MainActivity.class));
        });

        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2 || requestCode == 1) {
                videoUri = data.getData();
                videoWasSelected = true;
                Log.d(TAG, "Video selected: " + videoUri);
            }
            if (requestCode == 100) {
                uri = data.getData();
                Bitmap bitmap = null;
                Bitmap decoded = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 10, out);
                    decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                    Log.d(TAG, "Thumbnail image decoded successfully.");
                } catch (IOException e) {
                    Log.e(TAG, "Error decoding image: " + e.getMessage());
                    throw new RuntimeException(e);
                }
                videoThumbnail.setImageBitmap(decoded);
                photoWasSelected = true;
            }
        } else {
            Log.w(TAG, "Activity result not OK. Request code: " + requestCode + ", Result code: " + resultCode);
        }
    }

    public String encodeVideoToBase64(Uri videoUri) {
        try {
            Log.d(TAG, "Starting encoding video to Base64.");

            // Open the InputStream to read the video file
            ContentResolver contentResolver = getContext().getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(videoUri);

            if (inputStream == null) {
                Log.e(TAG, "Failed to open input stream for video URI: " + videoUri);
                return null;
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];  // Using a larger buffer size for better efficiency
            int length;
            long totalBytesRead = 0;

            // Read the input stream and write to the ByteArrayOutputStream
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
                totalBytesRead += length;
                Log.d(TAG, "Bytes read in this chunk: " + length + ", Total bytes read so far: " + totalBytesRead);
            }

            // Convert the ByteArrayOutputStream to a byte array
            byte[] videoBytes = byteArrayOutputStream.toByteArray();
            Log.d(TAG, "Total video bytes read: " + totalBytesRead);
            Log.d(TAG, "Byte array length before Base64 encoding: " + videoBytes.length);

            // Encode the byte array to Base64
            String base64Video = Base64.encodeToString(videoBytes, Base64.DEFAULT);
            Log.d(TAG, "Video encoding to Base64 completed. Encoded length: " + base64Video.length());

            // Close the streams
            inputStream.close();
            byteArrayOutputStream.close();

            return base64Video;
        } catch (Exception e) {
            Log.e(TAG, "Error encoding video to Base64: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}





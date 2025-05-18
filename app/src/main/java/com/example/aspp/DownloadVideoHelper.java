package com.example.aspp;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.aspp.entities.Video;

import java.io.File;

public class DownloadVideoHelper {

    private Context context;

    public DownloadVideoHelper(Context context) {
        this.context = context;
    }

    public long downloadVideo(Video video) {
        try {
            // Retrieve the base URL from resources
            String baseUrl = context.getResources().getString(R.string.Base_Url);

            // Get the relative path or filename from the Video object
            String relativePath = video.getSource(); // Assuming this returns something like "2.mp4"

            // Construct the full URL
            String fullUrl = baseUrl + relativePath;

            // Ensure the URL is valid for HTTP/HTTPS download
            if (!fullUrl.startsWith("http://") && !fullUrl.startsWith("https://")) {
                Log.e("DownloadVideoHelper", "Invalid URL: " + fullUrl);
                return -1; // Invalid URL, cannot proceed with download
            }

            String fileName = new File(fullUrl).getName(); // Extract the file name from the URL

            // Define the destination file path
            File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            Log.d("DownloadVideoHelper", "Videos are being saved in the folder: " + destinationFile);

            // Check if the file already exists
            if (destinationFile.exists()) {
                Log.i("DownloadVideoHelper", "File already exists: " + destinationFile.getAbsolutePath());
                return -1; // File already exists, no need to download again
            }

            // Create a DownloadManager.Request with the target video URL
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fullUrl));

            // Set the title and description for the download notification
            request.setTitle("Downloading Video: " + video.getTitle());
            request.setDescription(video.getDescription());

            // Allow the download to proceed over both Wi-Fi and mobile data
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

            // Set the destination path for the downloaded video file
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            // Get the system's DownloadManager service
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            // Enqueue the download request and get the download ID
            return downloadManager.enqueue(request);

        } catch (Exception e) {
            // Log any errors that occur during the download process
            Log.e("DownloadVideoHelper", "Error downloading video: " + e.getMessage());
            return -1;
        }
    }
}

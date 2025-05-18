package com.example.aspp;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.example.aspp.fragments.HomeFragment;
import com.example.aspp.entities.Comment;
import com.example.aspp.entities.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Utils {
    public static ArrayList<Comment> readCommentsList(Context context) {
        BufferedReader jsonReader = new BufferedReader(new InputStreamReader(
                context.getResources().openRawResource(R.raw.comment_list)));
        StringBuilder jsonBuilder = new StringBuilder();
        ArrayList<Comment> result = new ArrayList<>();
        try {
            for (String line = null; (line = jsonReader.readLine()) != null;) {
                jsonBuilder.append(line).append("\n");
            }
            JSONArray videoData = new JSONArray(jsonBuilder.substring(
                    jsonBuilder.indexOf("["),jsonBuilder.indexOf("]")+1).toString());
            for (int i = 0; i < videoData.length(); i++) {
                JSONObject json_obj = videoData.getJSONObject(i);
                int publisher_id = json_obj.getInt("publisher");
                int id = json_obj.getInt("id");
                String text = json_obj.getString("text");
                int video = json_obj.getInt("video");

//                Comment temp = new Comment(publisher_id, text, video, id);
//                result.add(temp);
            }
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

//    public static int getVideoById(int id) {
//        for (int i = 0; i < HomeFragment.videoArrayList.size(); i++) {
//            if (HomeFragment.videoArrayList.get(i).get_id().equals(String.valueOf(id))) {
//                return i;
//            }
//        }
//        return -1;
//    }
//    public static void loadComments(String id) {
//        int pos = getVideoById(Integer.parseInt(id));
//        for (int i = 0; i < HomeFragment.commentArrayList.size(); i++) {
//            if (HomeFragment.commentArrayList.get(i).getVideoId() == Integer.parseInt(id)) {
//                int finalI = i;
//                final boolean[] load = {true};
////                HomeFragment.videoArrayList.get(pos).getComments().forEach(comment -> {
////                    if (comment.getId() == HomeFragment.commentArrayList.get(finalI).getId()){
////                        load[0] = false;
////                    }
////                });
////                if (load[0])
////                    HomeFragment.videoArrayList.get(pos).getComments().add(HomeFragment.commentArrayList.get(finalI));
//            }
//        }
//    }
    public static int generateId() {
        int id = 0;
        for (int i = 0; i < 50; i++) {
            id += (int) (Math.random() * 10);
        }
        return id;
    }
    public static ArrayList<Video> readVideosList(Context context) {
        BufferedReader jsonReader = new BufferedReader(new InputStreamReader(
                context.getResources().openRawResource(R.raw.video_list)));
        StringBuilder jsonBuilder = new StringBuilder();
        ArrayList<Video> result = new ArrayList<>();
        try {
            for (String line = null; (line = jsonReader.readLine()) != null;) {
                jsonBuilder.append(line).append("\n");
            }
            JSONArray videoData = new JSONArray(jsonBuilder.substring(
                    jsonBuilder.indexOf("["),jsonBuilder.indexOf("]")+1).toString());
            for (int i = 0; i < videoData.length(); i++) {
                JSONObject json_obj = videoData.getJSONObject(i);
                String publisher_id = json_obj.getString("publisher");
                double duration = json_obj.getDouble("duration");
                String title = json_obj.getString("title");
                String description = json_obj.getString("description");
                String tags = json_obj.getString("tags");
                int thumbnailDrawableID = string2drawbleId(context, json_obj.getString("thumbnailID"));
                String id = json_obj.getString("id");
                String videoPathInRaw = json_obj.getString("videoPathInRaw");

//                Video temp = new Video(id, publisher_id, title, description, tags, thumbnailDrawableID, videoPathInRaw);
//                result.add(temp);
            }
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static int string2drawbleId(Context context, String drawableName) {
        int drawableId =
                context.getResources().getIdentifier(
                        drawableName, "drawable",
                        context.getPackageName());
        return drawableId;
    }

    public static String getPath(final Context context, final Uri uri){
        if (DocumentsContract.isDocumentUri(context, uri)){
            if (isExternalStorageDocument(uri)){
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)){
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }else if (isDownloadsDocument(uri)){
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }else if (isMediaDocument(uri)){
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)){
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }else if ("video".equals(type)){
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }else if ("audio".equals(type)){
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }else if ("content".equalsIgnoreCase(uri.getScheme())){
                return getDataColumn(context, uri, null, null);
            }else if ("file".equalsIgnoreCase(uri.getScheme())){
                return uri.getPath();
            }
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri){
        return "com.android.externalstorage.document".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri){
        return "com.android.providers.downloads.document".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri){
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs){
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try{
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()){
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }finally {
            if (cursor != null){
                cursor.close();
            }
        }
        return null;
    }
}

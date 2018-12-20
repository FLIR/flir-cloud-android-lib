package com.flir.cloud.Utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.flir.cloud.LambdaConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import okhttp3.ResponseBody;


/**
 * Created by Moti on 06-Aug-17.
 */

public class FileUtils {

    public static String getRealPath(Context context, Uri uri){
        try {
            return getFilePath(context,uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressLint("NewApi")
    private static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        if(body != null) {
            try {
                File folder = new File(Environment.getExternalStorageDirectory() + File.separator + LambdaConstant.FILE_EXPLORER_FILES_FOLDER_NAME);
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdir();
                }
                if (success) {
                    // todo change the file location/name according to your needs
                    File futureStudioIconFile = new File(Environment.getExternalStorageDirectory() + File.separator + LambdaConstant.FILE_EXPLORER_FILES_FOLDER_NAME + File.separator + fileName);

                    InputStream inputStream = null;
                    OutputStream outputStream = null;

                    try {
                        byte[] fileReader = new byte[4096];

                        long fileSize = body.contentLength();
                        long fileSizeDownloaded = 0;

                        inputStream = body.byteStream();
                        outputStream = new FileOutputStream(futureStudioIconFile);

                        while (true) {
                            int read = inputStream.read(fileReader);

                            if (read == -1) {
                                break;
                            }

                            outputStream.write(fileReader, 0, read);

                            fileSizeDownloaded += read;

                        }
                        outputStream.flush();

                        return true;
                    } catch (IOException e) {
                        return false;
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }

                        if (outputStream != null) {
                            outputStream.close();
                        }
                    }
                }
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public static String getMineTypeFromFile(File aFile) {
        String mimeType = "";
        try {
            mimeType = aFile.toURI().toURL().openConnection().getContentType();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mimeType ;
    }

    public static String getFileNameFromFilePath(String aPath) {
        String name = "";
        if (!aPath.isEmpty())
            name = aPath.split("/")[aPath.split("/").length - 1];
        return name;
    }

    public static String getFileContentType(String fileName) {
        for (int i = fileName.length() - 1; i > 0 ; i--){
            if(fileName.charAt(i) == '.'){
                return fileName.substring(i + 1 , fileName.length());
            }
        }
        return "";
    }
}
package com.campustribune.helper;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by aditi on 24/07/16.
 */
public class ImageUploader {

    private Uri uri;
    private String path;
    private Context ctx;
    private ContentResolver contentResolver;
    private AmazonS3Client amazonS3Client;
    private URL imageS3URL;
    private boolean uploadComplete;
    private PutObjectRequest por;
    private String picId;

    public ImageUploader(Uri uri, Context ctx, ContentResolver contentResolver){
        this.uri = uri;
        this.ctx = ctx;
        this.contentResolver = contentResolver;
        this.amazonS3Client = Util.getS3Client();
    }

    public String getPath() throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(this.ctx, this.uri)) {
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
                selectionArgs = new String[] {
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            System.out.println("Inside content if");
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                System.out.println("Inside content try");
                cursor = contentResolver
                        .query(uri, projection, selection, selectionArgs, null);
                System.out.println("Inside content try cursor =" + cursor);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                System.out.println("Inside content try cursor idx=" + column_index);
                if (cursor.moveToFirst()) {
                    System.out.println("Inside if" + cursor.getString(column_index));
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Inside catch");
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void beginUpload(String filePath) {
        if (filePath == null) {
            Toast.makeText(this.ctx, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();

        }
        File file = new File(filePath);
        picId= UUID.randomUUID().toString();
        por= new PutObjectRequest( Util.BUCKET,picId , new java.io.File( filePath) );
        new Thread() {
            @Override
            public void run() {
                try {
                    amazonS3Client.putObject(por);
                    ResponseHeaderOverrides override = new ResponseHeaderOverrides();
                    override.setContentType("image/jpeg");
                    GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest( Util.BUCKET, picId );
                    Calendar cal = Calendar.getInstance();
                    cal.set(2016, Calendar.DECEMBER, 15, 23, 00, 00); //Year, month, day of month, hours, minutes and seconds
                    Date date = cal.getTime();
                    urlRequest.setExpiration(date);
                    urlRequest.setResponseHeaders(override);
                    imageS3URL = amazonS3Client.generatePresignedUrl( urlRequest );
                    System.out.println("S3 URL of the uploaded image is  >>>>>>>>>>>> "+ imageS3URL);
                    uploadComplete = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public URL getImageS3URL() {
        return imageS3URL;
    }

    public boolean isUploadComplete() {
        return uploadComplete;
    }
}

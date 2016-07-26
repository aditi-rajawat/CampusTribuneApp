package com.campustribune.post.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.campustribune.R;
import com.campustribune.beans.Post;
import com.campustribune.helper.Util;
import com.campustribune.BaseActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by snshr on 7/14/2016.
 */
public class CreatePostActivity extends BaseActivity {

    ProgressDialog progressDialog;
    @Bind(R.id.alertbox)
    CheckBox alertCheckBox;
    @Bind(R.id.headline)
    EditText headlineText;
    @Bind(R.id.content)
    EditText contentText;
    @Bind(R.id.url)
    EditText urlText;
    @Bind(R.id.imageUrl)
    EditText imageUrlText;
    @Bind(R.id.category)
    Spinner categoryText;
    @Bind(R.id.create)
    Button createBtn;
    @Bind(R.id.upload)
    Button uploadBtn;

    public String BASEURL=Util.SERVER_URL;
    AmazonS3Client s3Client;
    String path;
    PutObjectRequest por;
    String picId;
    URL picurl;
    String userId;
    String token;
    boolean uploadComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        s3Client=Util.getS3Client();
        //Code to retrieve the user details stored in shared preferences
        SharedPreferences settingsout = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        token = "Token "+settingsout.getString("authToken", "");
        userId= settingsout.getString("loggedInUserId", "");
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(CreatePostActivity.this,
                R.style.AppTheme_Dark_Dialog);

        invalidateOptionsMenu();
        createBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    createPostClick();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                uploadImage();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_frontpage, menu);
        MenuItem cp = (MenuItem) menu.findItem(R.id.submenu_createpost);
        cp.setVisible(false);
        return true;
    }


    public void uploadImage(){
        Intent intent = new Intent();
        if((Build.VERSION.SDK_INT >= 23)){
            //askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,1);
            askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 1);
            //askForPermission(Manifest.permission.ACCESS_NETWORK_STATE, 2);
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        }
        else if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT <= 22) {
            System.out.println("Inside build num"+Build.VERSION.SDK_INT);
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        } else {
            System.out.println("Inside build num ekse"+Build.VERSION.SDK_INT);
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }

        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(CreatePostActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(CreatePostActivity.this, permission)) {

                ActivityCompat.requestPermissions(CreatePostActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(CreatePostActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            try {
                Uri uri = data.getData();
                try {
                    path = getPath(uri);
                    System.out.println("Path is "+path);
                } catch (URISyntaxException e) {
                    Toast.makeText(this,
                            "Unable to get the file from the given URI.  See error log for details",
                            Toast.LENGTH_LONG).show();
                }
                imageUrlText.setText(path);
                System.out.println("URI of selected image is  ---------> " + uri);
            }catch (Exception ex){
                System.out.println("I am having this issue ----->  "+ ex.getMessage());
            }
        }
    }

    /*
     * Begins to upload the file specified by the file path.
     */
    private void beginUpload(String filePath) {
        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();

        }
        File file = new File(filePath);
        picId=UUID.randomUUID().toString();
        por= new PutObjectRequest( Util.BUCKET,picId , new java.io.File( filePath) );
        new Thread() {
            @Override
            public void run() {
                try {
                    s3Client.putObject(por);
                    ResponseHeaderOverrides override = new ResponseHeaderOverrides();
                    override.setContentType("image/jpeg");
                    GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest( Util.BUCKET, picId );
                    Calendar cal = Calendar.getInstance();
                    cal.set(2016, Calendar.DECEMBER, 15, 23, 00, 00); //Year, month, day of month, hours, minutes and seconds
                    Date date = cal.getTime();
                    urlRequest.setExpiration(date);
                    urlRequest.setResponseHeaders(override);
                    picurl = s3Client.generatePresignedUrl( urlRequest );
                    System.out.println(picurl);
                    uploadComplete=true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    @SuppressLint("NewApi")
    private String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
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
                cursor = getContentResolver()
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


    public void createPostClick() throws JSONException, UnsupportedEncodingException {
        boolean isAlert=alertCheckBox.isChecked();
        String headline = headlineText.getText().toString();
        String content = contentText.getText().toString();
        String webLink = urlText.getText().toString();
        String imgURL = imageUrlText.getText().toString();
        String selectedCategory = String.valueOf(categoryText.getSelectedItem());
        System.out.println("The values are " + isAlert + " " + headline + " " + content + " " + webLink + " " + imgURL + " " + selectedCategory);
        if(headline.trim().equalsIgnoreCase("") || headline!=null && (headline.trim().length()<10||headline.trim().length()>50)){
           headlineText.setError("Post Headline is mandatory!!Allowed character length is 10-50 characters");
        }else if(content.trim().equalsIgnoreCase("") || content!=null && (content.trim().length()<10||content.trim().length()>1500)){
           contentText.setError("Post Content is mandatory!!Allowed character length is 10-1500 characters");
        }else if(!webLink.trim().equalsIgnoreCase("") && (webLink.length()>75||!Patterns.WEB_URL.matcher(webLink).matches())){
             urlText.setError("Enter a valid URL!!Allowed character length is 75");
        }else{
            createBtn.setEnabled(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Creating Post...");
            progressDialog.show();

            if(imgURL!=null&&imgURL.length()>0) {
                System.out.println("Path is " + path);
                beginUpload(path);
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (!uploadComplete);
            }

            System.out.println("URL is " + picurl);
            JSONObject params = new JSONObject();
            params.put("isAlert", isAlert);
            params.put("headline", headline);
            params.put("content", content);
            params.put("webLink", webLink);
            params.put("userId",userId);
            if(imgURL!=null&&imgURL.length()>0) {
                params.put("imgURL", picurl.toString());
            }else{
                params.put("imgURL", "");
            }
            params.put("category",selectedCategory);

            System.out.println(params.toString());
            invokeWS(params);
        }

    }


    public void invokeWS(JSONObject params) throws UnsupportedEncodingException {

        StringEntity entity = new StringEntity(params.toString());
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("authorization",token);
        client.post(this, BASEURL+"post/create", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                progressDialog.hide();
                System.out.println(statusCode);
                try {
                    if (statusCode == 201) {
                        Toast.makeText(getApplicationContext(), "Post Created Successfully!!", Toast.LENGTH_LONG).show();
                        System.out.println(responseBody.toString());
                        ObjectMapper mapper = new ObjectMapper();
                        Post post = mapper.readValue(responseBody.toString(), Post.class);
                        Intent viewPostPage = new Intent(CreatePostActivity.this, ViewPostActivity.class);
                        viewPostPage.putExtra("post_id",String.valueOf(post.getId()));
                        CreatePostActivity.this.startActivity(viewPostPage);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error on on success!" + responseBody.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (JsonParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                progressDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getBaseContext(), "Create Post failed", Toast.LENGTH_LONG).show();

                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
                createBtn.setEnabled(true);
            }


        });


    }

}
package com.campustribune.post.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.campustribune.BaseActivity;
import com.campustribune.R;
import com.campustribune.beans.Post;
import com.campustribune.helper.Util;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;


/**
 * Created by snshr on 7/14/2016.
 */
public class ViewPostActivity extends BaseActivity {

 public String post_id;
 public Post post = new Post();

    String token;

    public String BASEURL= Util.SERVER_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        post_id = bundle.getString("post_id");
        System.out.println(post_id);

        //Code to retrieve the user details stored in shared preferences
        SharedPreferences settingsout = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        token = "Token "+settingsout.getString("authToken", "");
        invokeloadPostWS(post_id);
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setPostObj(JSONObject respBody) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(respBody.toString());
            this.post = mapper.readValue(respBody.toString(), Post.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void invokeloadPostWS(String postid){
        System.out.println("inside invokeloadPostWS");
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("authorization", token);
        client.get(BASEURL+"post/get/" + postid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                try {
                    if (statusCode == 200) {
                        System.out.println(responseBody.toString());
                        ViewPostActivity.this.setPostObj(responseBody);
                        ViewPostActivity.this.setContentView((R.layout.activity_view_post));

                    } else {
                        Toast.makeText(getApplicationContext(), responseBody.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject responseBody) {
                System.out.println("inside invokeloadPostWS failure" + statusCode);
                if (statusCode == 409) {
                    Toast.makeText(getApplicationContext(), "View Post failed", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}

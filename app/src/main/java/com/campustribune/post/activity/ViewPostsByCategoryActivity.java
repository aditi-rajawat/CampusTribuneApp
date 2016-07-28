package com.campustribune.post.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.campustribune.BaseActivity;
import com.campustribune.R;
import com.campustribune.beans.Post;
import com.campustribune.helper.Util;
import com.campustribune.post.adapter.ViewPostByCategoriesAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;



public class ViewPostsByCategoryActivity extends BaseActivity implements ViewPostByCategoriesAdapter.ViewPostInterface{

    private static ArrayList<Post> listOfPosts = new ArrayList<>();
    private static ViewPostByCategoriesAdapter adapter = null;
    private ListView postsListView;
    String token = null;
    String userId;
    String university;
    String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posts_by_category);
        postsListView = (ListView) findViewById(R.id.listView_news);
        // Retrieve the user token
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.token = new String("Token " + settings.getString("authToken", "").toString());
        userId= settings.getString("loggedInUserId", "");
        university=settings.getString("loggedInUserUniversity","");
        //university="SJSU";
        Bundle bundle = getIntent().getExtras();
        category = bundle.getString("category");
        System.out.println("Category is"+category);
        invokews();
    }

    private void invokews() {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.addHeader("authorization", this.token);
        System.out.println("Invoking GET list of posts REST API...");
        String url = Util.SERVER_URL + "post/getByCategory/"+category+"/"+university;
        System.out.println("URL is"+url);
        httpClient.get(Util.SERVER_URL + "post/getByCategory/"+category+"/"+university , new AsyncHttpResponseHandler() {
            Post[] postArray={};
            private ArrayList<Post> listOfPosts = new ArrayList<>();
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    System.out.println("Received the response from Posts Get by category API!!!!!!!!!!");
                    System.out.println("Response is " + responseBody);
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        postArray = mapper.readValue(responseBody, Post[].class);
                        System.out.println("No. of posts received ============= " + postArray.length);
                        if (postArray.length > 0) {
                            for (Post each : postArray)
                                listOfPosts.add(each);

                            System.out.println("No. of posts received = " + postArray.length);
                            adapter = new ViewPostByCategoriesAdapter(ViewPostsByCategoryActivity.this.getApplicationContext(),
                                    listOfPosts, ViewPostsByCategoryActivity.this);
                            if (postsListView != null)
                                postsListView.setAdapter(adapter);
                        }
                        System.out.println("No. of posts ====== " + listOfPosts.size());
                    } catch (Exception ex) {
                        System.out.println("Exception while parsing the response array due to " + ex.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                    Toast.makeText(getApplicationContext(), "Something went wrong on the server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be " +
                            "connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void viewPostDetail(Post post) {
        Intent viewPostPage = new Intent(ViewPostsByCategoryActivity.this,ViewPostActivity.class);
        viewPostPage.putExtra("post_id", String.valueOf(post.getId()));
        ViewPostsByCategoryActivity.this.startActivity(viewPostPage);
    }
}


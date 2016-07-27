package com.campustribune.post.activity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.campustribune.BaseActivity;
import com.campustribune.R;
import com.campustribune.helper.Util;
import com.campustribune.post.adapter.ViewPostByCategoriesAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;


public class ViewPostsByCategoryActivity extends BaseActivity {

    private static final String TAG = ViewPostsByCategoryActivity.class.getSimpleName();
    private ListView mListView;
    private ProgressBar mProgressBar;
    private ViewPostByCategoriesAdapter mListAdapter;
    private HashMap<Integer,PostListItemData> mListData;
   // private String FEED_URL = "http://10.0.0.189:8080/front-page/data/";
    private String FEED_URL = Util.SERVER_URL+"front-page/data/";
    private Integer newsCount=0;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posts_by_category);

        mListView = (ListView) findViewById(R.id.listView_news);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Initialize with empty data
        mListData = new HashMap();
        // mListAdapter = new ListViewAdapter(getApplicationContext(), newsCount, mListData);

        //Start download
        new SyncHttpTask().execute(FEED_URL);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    //Downloading data asynchronously -- Need to fix this as right now its downloading synchronously
    public class SyncHttpTask extends AsyncTask<String, Void, Integer> {
        Integer result=0;
        @Override
        protected Integer doInBackground(String... params) {
            SyncHttpClient client = new SyncHttpClient();
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            String auth_token_string = sharedPreferences.getString("authToken", "");
            String userId = sharedPreferences.getString("loggedInUserId", "");
            client.addHeader("authorization","Token "+auth_token_string);
            client.get(FEED_URL+userId, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] header, JSONArray responseArray){
                    try{
                        if (statusCode == 200) {
                            parseResult(responseArray);
                            result=1;
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Succeeded!But Error!", Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                    mProgressBar.setVisibility(View.GONE);
                    if (statusCode == 409) {
                        Toast.makeText(getBaseContext(), "Unauthorized!Please login again!", Toast.LENGTH_LONG).show();
                    } else if (statusCode == 500) {
                        Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                    }

                }
            });

            return result;

        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            if(result==1){
                mListAdapter = new ViewPostByCategoriesAdapter(ViewPostsByCategoryActivity.this,newsCount,mListData);
                mListView.setAdapter(mListAdapter);
                for (int i = 0; i < newsCount; i++) {
                    if (mListData.get(i) != null) {
                        try {
                            Void aVoid = new GetBitMapTask().execute(mListData.get(i).getImage()).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        mListData.get(i).setBitMapImage(image);
                    } else {
                        Log.d("null", "null");
                    }

                }
            }
            else{
                Toast.makeText(ViewPostsByCategoryActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void parseResult(JSONArray responseArray) {

        try {
            newsCount=0;
            PostListItemData item;
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject response = responseArray.optJSONObject(i);
                String headline = response.optString("headline");
                System.out.println("HEADLINE:"+ headline);
                String content = response.optString("content");
               // SharedPreferences sharedPreferences = PreferenceManager
                    //    .getDefaultSharedPreferences(getApplicationContext());
                //String userId = sharedPreferences.getString("loggedInUserId", "");
                String userId = response.optString("userId");
                item = new PostListItemData();
                item.setHeadline(headline);
                item.setContent(content);
                item.setUserId(userId);
                String imageURL=response.optString("imgURL");
                if (imageURL != null){
                    item.setImage(imageURL);
                }
                mListData.put(i,item);

                newsCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public class GetBitMapTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... h) {
            Log.d("image", h[0]);
            image = getBitmapFromURL(h[0]);
            return null;
        }


    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            Log.d("image", src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            try {
                connection.connect();
            } catch (Exception e) {

            }
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }
}


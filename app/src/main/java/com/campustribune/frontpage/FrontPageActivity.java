package com.campustribune.frontpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.campustribune.R;
import com.campustribune.userProfile.UserProfileActivity;
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

public class FrontPageActivity extends AppCompatActivity {

    private static final String TAG = FrontPageActivity.class.getSimpleName();
    private ListView mListView;
    private ProgressBar mProgressBar;
    private ListViewAdapter mListAdapter;
    private HashMap<Integer, ListItem> mListData;
    private String FEED_URL = "http://10.0.0.227:8080/front-page/data/";
    private Integer newsCount=0;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

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
            SharedPreferences settingsout = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            String auth_token_string = settingsout.getString("authToken", "");
            String userId = settingsout.getString("loggedInUserId", "");
            client.addHeader("authorization","Token "+auth_token_string);
            client.get(FEED_URL+"userId", new JsonHttpResponseHandler(){
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
               mListAdapter = new ListViewAdapter(FrontPageActivity.this,newsCount,mListData);
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
                Toast.makeText(FrontPageActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
           }
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void parseResult(JSONArray responseArray) {

            try {
                newsCount=0;
                ListItem item;
                for (int i = 0; i < responseArray.length(); i++) {
                    JSONObject response = responseArray.optJSONObject(i);
                    String headline = response.optString("headline");
                    System.out.println("HEADLINE:"+ headline);
                    String content = response.optString("content");
                    Integer userId = response.optInt("userId");
                    item = new ListItem();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_action_frontpage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Take appropriate action for each action item click
        switch (menuItem.getItemId()) {
            case R.id.menu_action_refresh:
                Toast.makeText(this,"Refresh button was clicked",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.submenu_userprofile:
                Toast.makeText(this,"User-profile menu was clicked",Toast.LENGTH_SHORT).show();
                goToUserProfilePage();
                return true;
            case R.id.submenu_search:
                Toast.makeText(this,"Search button was clicked", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void goToUserProfilePage(){
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
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

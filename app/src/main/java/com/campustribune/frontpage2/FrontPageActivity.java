package com.campustribune.frontpage2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.campustribune.R;
import com.campustribune.beans.Post;
import com.campustribune.event.activity.CreateEventActivity;
import com.campustribune.event.activity.ViewAllEventsActivity;
import com.campustribune.helper.Util;
import com.campustribune.login.LoginActivity;
import com.campustribune.post.activity.CreatePostActivity;
import com.campustribune.post.activity.ViewPostActivity;
import com.campustribune.post.activity.ViewPostsByCategoryListActivity;
import com.campustribune.userProfile.UserProfileActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;

public class FrontPageActivity extends AppCompatActivity {

    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        List<Post> postData= new ArrayList();
        System.out.println("Started Activity for front page");

        try {
            postData = fill_with_data(LoginActivity.postList);
            System.out.println("Started loading Data");

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        Recycler_View_Adapter adapter = new Recycler_View_Adapter(postData, getApplication(),
                new Recycler_View_Adapter.OnItemClickListener(){
                    @Override public void onItemClick(Post post) {
                        Toast.makeText(getBaseContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                        navigateToViewPostActivity(post.getId());
                }

            });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

       /* RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(100);
        System.out.println("Setting up the animation!");
        recyclerView.setItemAnimator(itemAnimator);*/

        SharedPreferences settingsout = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        token = "Token "+settingsout.getString("authToken", "");
        String userId= settingsout.getString("loggedInUserId", "");

        invokeGetUserActionsWS(userId);
    }

    private void navigateToViewPostActivity(Integer postId) {
        Intent viewPostPage = new Intent(FrontPageActivity.this, ViewPostActivity.class);
        viewPostPage.putExtra("post_id", String.valueOf(postId));
        FrontPageActivity.this.startActivity(viewPostPage);

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
                Toast.makeText(this, "Refresh button was clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.submenu_userprofile:
                Toast.makeText(this,"User-profile menu was clicked",Toast.LENGTH_SHORT).show();
                goToUserProfilePage();
                return true;
            /*case R.id.submenu_search:
                Toast.makeText(this,"Search button was clicked", Toast.LENGTH_SHORT).show();
                return true;*/
            case R.id.submenu_createpost:
                Toast.makeText(this,"Search button was clicked", Toast.LENGTH_SHORT).show();
                goToCreatePostPage();
                return true;
            case R.id.submenu_createevent:      // Added by Aditi on 07/23/2016 START
                goToCreateEventPage();
                return true;
            case R.id.submenu_viewallevents:
                goToViewAllEventsPage();
                return true;                   // Added by Aditi on 07/23/2016 END
            case R.id.submenu_viewpostsbycategory:
                goToViewPostsByCategoryPage();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void goToUserProfilePage(){
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    private void goToCreatePostPage(){
        Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
        startActivity(intent);
    }

    // Added by Aditi on 07/23/2016 START
    private void goToCreateEventPage(){
        Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
        startActivity(intent);
    }

    private void goToViewAllEventsPage(){
        Intent intent = new Intent(getApplicationContext(), ViewAllEventsActivity.class);
        startActivity(intent);
    }
    // Added by Aditi on 07/23/2016 END
    private void goToViewPostsByCategoryPage(){
        Intent intent = new Intent(getApplicationContext(), ViewPostsByCategoryListActivity.class);
        startActivity(intent);
    }

    public List<Post> fill_with_data(ArrayList<Post> postList) throws ExecutionException, InterruptedException {
        System.out.println("SIZEEEEE"+postList.size());
        List<Post> data = new ArrayList<>();
        Iterator<Post> listIterator = postList.iterator();
        Post post= new Post();
        while(listIterator.hasNext()){
            System.out.println(" ADDING DATA///");
            post = listIterator.next();
            data.add(post);
        }
        return data;
    }

    public void invokeGetUserActionsWS(String userId){
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("authorization", token);
        client.get(Util.SERVER_URL+"post/getUserActions/"+userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                try {
                    if (statusCode == 200) {
                        System.out.println(responseBody.toString());
                        SharedPreferences settings = PreferenceManager
                                .getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("userPostActions", responseBody.toString());
                        editor.commit();

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
                error.printStackTrace();
                if (statusCode == 409) {
                    Toast.makeText(getApplicationContext(), "Get user actions failed", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}

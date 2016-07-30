package com.campustribune.frontpage2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.campustribune.R;
import com.campustribune.beans.Event;
import com.campustribune.event.activity.CreateEventActivity;
import com.campustribune.event.activity.ViewAllEventsActivity;
import com.campustribune.event.activity.ViewEventActivity;
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
import java.util.Collections;
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
        setUniversityLogo(getSupportActionBar());
        List<Data> frontPageData= new ArrayList();
        System.out.println("Started Activity for front page");

        try {
            frontPageData = fill_with_data(LoginActivity.frontPageList);
            System.out.println("Started loading Data");

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        Recycler_View_Adapter adapter = new Recycler_View_Adapter(frontPageData, getApplication(),
                new Recycler_View_Adapter.OnItemClickListener(){
                    @Override public void onItemClick(Data data) {
                        Toast.makeText(getBaseContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                        if(data.getItemType().equalsIgnoreCase("Post"))
                            navigateToViewPostActivity(data.getItemId());
                        else if(data.getItemType().equalsIgnoreCase("Event")){
                            Event event = retrieveEventFromList(data.getItemId());
                            navigateToViewEventActivity(event);
                        }

                }

            });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        token = "Token "+sharedPreferences.getString("authToken", "");
        String userId= sharedPreferences.getString("loggedInUserId", "");
        invokeGetUserActionsWS(userId);
    }

    private Event retrieveEventFromList(String itemId) {
        for(Event event:LoginActivity.staticEventList){
            System.out.println("CHECK here"+ itemId);
            if((event.getId().toString()).equals(itemId));
                return event;
        }
        return null;
    }

    private void navigateToViewEventActivity(Event event) {

        Intent viewEventIntent = new Intent(FrontPageActivity.this, ViewEventActivity.class);
        viewEventIntent.putExtra("new_event", event);
        viewEventIntent.putExtra("prev_activity", new String("FrontPageActivity"));
        FrontPageActivity.this.startActivity(viewEventIntent);
    }

    private void navigateToViewPostActivity(String itemId) {
        Intent viewPostPage = new Intent(FrontPageActivity.this, ViewPostActivity.class);
        viewPostPage.putExtra("post_id", itemId);
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
        switch (menuItem.getItemId()) {
            case R.id.menu_action_refresh:
                Toast.makeText(this, "Refresh button was clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.submenu_userprofile:
                goToUserProfilePage();
                return true;
            /*case R.id.submenu_search:
                Toast.makeText(this,"Search button was clicked", Toast.LENGTH_SHORT).show();
                return true;*/
            case R.id.submenu_createpost:
                Toast.makeText(this,"Create Post was clicked", Toast.LENGTH_SHORT).show();
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
            case R.id.submenu_logout:
                handleLogout();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void setUniversityLogo(ActionBar actionbar){
        SharedPreferences settingsout = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String university=settingsout.getString("loggedInUserUniversity", "");
        switch (university) {
            case "SJSU":
                actionbar.setIcon(R.drawable.sjsulogo);
                return;
            case "UNCC":
                actionbar.setIcon(R.drawable.uncclogo);
                return;
            default:

        }
    }

    private void handleLogout() {

        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
        navigateToLoginActivity();



    }

    private void navigateToLoginActivity() {

        Intent loginIntent = new Intent(this, LoginActivity.class);
        this.finish();
        startActivity(loginIntent);
    }

    private void goToUserProfilePage() {
                Intent intent = new Intent(this, UserProfileActivity.class);
                startActivity(intent);
            }

            private void goToCreatePostPage() {
                Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
                startActivity(intent);
            }

            // Added by Aditi on 07/23/2016 START
            private void goToCreateEventPage() {
                Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
                startActivity(intent);
            }

            private void goToViewAllEventsPage() {
                Intent intent = new Intent(getApplicationContext(), ViewAllEventsActivity.class);
                startActivity(intent);
            }

            // Added by Aditi on 07/23/2016 END
            private void goToViewPostsByCategoryPage() {
                Intent intent = new Intent(getApplicationContext(), ViewPostsByCategoryListActivity.class);
                startActivity(intent);
            }

            public List<Data> fill_with_data(ArrayList<Data> dataList) throws ExecutionException, InterruptedException {
                Collections.shuffle(dataList);
                List<Data> frontPageDataList = new ArrayList<>();
                Iterator<Data> listIterator = dataList.iterator();
                Data data = new Data();
                while (listIterator.hasNext()) {
                    data = listIterator.next();
                    System.out.println("LOADING ITEM TYPE: " + data.getItemType());
                    frontPageDataList.add(data);
                }
                return frontPageDataList;
            }

            public void invokeGetUserActionsWS(String userId) {
                AsyncHttpClient client = new AsyncHttpClient();
                client.addHeader("authorization", token);
                client.get(Util.SERVER_URL + "post/getUserActions/" + userId, new JsonHttpResponseHandler() {
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

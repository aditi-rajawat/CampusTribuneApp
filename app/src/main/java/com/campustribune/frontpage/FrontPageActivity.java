package com.campustribune.frontpage;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.campustribune.beans.EventUser;
import com.campustribune.beans.Post;
import com.campustribune.beans.User;
import com.campustribune.event.activity.CreateEventActivity;
import com.campustribune.event.activity.ViewAllEventsActivity;
import com.campustribune.event.activity.ViewEventActivity;
import com.campustribune.event.utility.UpdateEventUserActions;
import com.campustribune.helper.Util;
import com.campustribune.login.LoginActivity;
import com.campustribune.post.activity.CreatePostActivity;
import com.campustribune.post.activity.ViewPostActivity;
import com.campustribune.post.activity.ViewPostsByCategoryListActivity;
import com.campustribune.userProfile.UserProfileActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

public class FrontPageActivity extends AppCompatActivity {

    String token;
    String userId;
    List<Data> frontPageData= new ArrayList();
    List<Data> newFrontPageData= new ArrayList();

    RecyclerView recyclerView;
    Recycler_View_Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setUniversityLogo(getSupportActionBar());
        try {
            frontPageData = fill_with_data(LoginActivity.frontPageList);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = setAdapterWithData(frontPageData);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        token = "Token "+sharedPreferences.getString("authToken", "");
        userId= sharedPreferences.getString("loggedInUserId", "");
        invokeGetUserActionsWS(userId);
        invokeGetEventUserActionsWS(userId);
    }

    private Recycler_View_Adapter setAdapterWithData(List<Data> frontPageData) {
        Recycler_View_Adapter adapter = new Recycler_View_Adapter(frontPageData, getApplication(),
                new Recycler_View_Adapter.OnItemClickListener(){
                    @Override public void onItemClick(Data data) {
                        //Toast.makeText(getBaseContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                        if(data.getItemType().equalsIgnoreCase("Post"))
                            navigateToViewPostActivity(data.getItemId());
                        else if(data.getItemType().equalsIgnoreCase("Event")){
                            Event event = retrieveEventFromList(data.getItemId());
                            navigateToViewEventActivity(event);
                        }
                    }

                });
        return adapter;
    }

    private Event retrieveEventFromList(String itemId) {
        for(Event event:LoginActivity.staticEventList){
            if((event.getId().toString()).equals(itemId))
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
            case R.id.action_refresh:
                handleRefresh();
                return true;
            case R.id.submenu_search:
                handleSearch();
                return true;
            case R.id.submenu_userprofile:
                goToUserProfilePage();
                return true;
            case R.id.submenu_createpost:
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



    private void handleSearch() {
        final CharSequence[] searchType= {"Posts", "Events"};

        AlertDialog.Builder builder= new AlertDialog.Builder(FrontPageActivity.this, AlertDialog.THEME_HOLO_DARK)
                .setTitle("Search")
                .setSingleChoiceItems(searchType, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (searchType[which].equals("Posts")) {
                            goToViewPostsByCategoryPage();
                            dialog.dismiss();
                        } else {
                            goToViewAllEventsPage();
                            dialog.dismiss();
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }



    private void handleRefresh() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("authorization", token);
        client.get(Util.SERVER_URL + "/user/front-page/" + userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                User user = new User();
                ObjectMapper mapper = new ObjectMapper();
                try {
                    user = mapper.readValue(responseBody.toString(), User.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (statusCode == 200) {
                        newFrontPageData.clear();
                        mapPostListToFrontPagData(user.getPostList());
                        mapEventListToFrontPageData(user.getEventList());
                        adapter.swap(newFrontPageData);

                    } else {
                        Toast.makeText(getApplicationContext(), responseBody.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                if (statusCode == 404) {
                    Toast.makeText(getBaseContext(), "Data Retrieval failure", Toast.LENGTH_LONG).show();

                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }

            }

        });

    }

    private void mapEventListToFrontPageData(ArrayList<Event> eventList) {
        if(eventList!=null){
            for(Event event:eventList){
                Data data = new Data();
                data.setItemId(String.valueOf((event.getId())));
                data.setItemTitle(event.getTitle());
                data.setItemContent(event.getDescription());
                data.setItemImageURL(event.getEventImageS3URL());
                data.setItemOwnerId(event.getCreatedBy());
                data.setItemCategory(event.getCategory());
                data.setIsItemAlert(false);
                data.setItemType("Event");
                newFrontPageData.add(data);
            }
        }
    }

    private void mapPostListToFrontPagData(ArrayList<Post> postList) {
        if(postList!=null){
            for(Post post:postList){
                Data data = new Data();
                data.setItemId(String.valueOf((post.getId())));
                data.setItemTitle(post.getHeadline());
                data.setItemContent(post.getContent());
                data.setItemImageURL(post.getImgURL());
                data.setItemOwnerId(post.getUserId());
                data.setItemCategory(post.getCategory());
                data.setIsItemAlert(post.isAlert());
                data.setItemType("Post");
                newFrontPageData.add(data);
            }
        }
    }

    private void setUniversityLogo(ActionBar actionbar){
        SharedPreferences settingsout = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String university=settingsout.getString("loggedInUserUniversity", "");
        switch (university) {
            case "SJSU":
                actionbar.setIcon(R.drawable.sjsulogospace);
                return;
            case "UNCC":
                actionbar.setIcon(R.drawable.uncclogospace);
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
                        } else if (statusCode==404) {
                            System.out.println("No user Actions!!");
                        }else
                        {
                           Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }

    public void invokeGetEventUserActionsWS(String userId){
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.addHeader("authorization", FrontPageActivity.this.token);
        httpClient.get(Util.SERVER_URL+"eventusers/"+userId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if(statusCode == HttpStatus.SC_OK){
                    SharedPreferences settings = PreferenceManager
                            .getDefaultSharedPreferences(FrontPageActivity.this.getApplicationContext());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("eventUserActions", response.toString());
                    editor.commit();
                }
                else if(statusCode == HttpStatus.SC_NO_CONTENT){
                    SharedPreferences settings = PreferenceManager
                            .getDefaultSharedPreferences(FrontPageActivity.this.getApplicationContext());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.commit();
                }
                else
                    System.out.println("Could not retrive user's event actions.. please check");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                System.out.println("Could not retrive user's event actions.. please check");
            }
        });

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String eventUserActions = new String(settings.getString("eventUserActions","").toString());

        try {
            ObjectMapper mapper = new ObjectMapper();
            EventUser eventUser = null;
            if(!eventUserActions.equals(""))
                eventUser = mapper.readValue(eventUserActions, EventUser.class);
            if(LoginActivity.staticEventList!=null && LoginActivity.staticEventList.size()>0
                    && eventUser!=null){
                new UpdateEventUserActions(LoginActivity.staticEventList, eventUser).updateAll();
            }
        }catch(Exception ex){
            System.out.println("Could not parse the user's event actions..please check");
        }

    }

}

package com.campustribune.event.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.campustribune.BaseActivity;
import com.campustribune.R;
import com.campustribune.beans.Event;
import com.campustribune.beans.EventUser;
import com.campustribune.event.adapter.ViewEventAdapter;
import com.campustribune.event.utility.Constants;
import com.campustribune.event.utility.UpdateEventUserActions;
import com.campustribune.helper.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

/**
 * Created by aditi on 08/07/16.
 */
public class ViewAllEventsActivity extends BaseActivity implements ViewEventAdapter.ViewEachEventInterface, OnMapReadyCallback{

    private static ArrayList<Event> listOfEvents = new ArrayList<>();
    private static ViewEventAdapter adapter=null;
    ListView eventsListContainer=null;
    String token=null;
    String university=null;
    String eventUserActions=null;
    EventUser eventUser=null;
    String userId=null;

    public static void updateEventList(Event oldEvent, Event updatedEvent) {
        if(listOfEvents!=null && listOfEvents.size()>0){
            try {
                int index = listOfEvents.indexOf(oldEvent);
                listOfEvents.remove(index);
                listOfEvents.add(index, updatedEvent);
                adapter.notifyDataSetChanged();
            }catch (Exception ex){
                System.out.println("ERROR while updating the list of events ---> "+ ex.getMessage());
            }
        }
    }

    public static void removeFromList(Event oldEvent){
        if(listOfEvents!=null && listOfEvents.size()>0){
            listOfEvents.remove(oldEvent);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_all_events);

        // Disable view all from side menu
        invalidateOptionsMenu();

        eventsListContainer = (ListView)findViewById(R.id.view_all_events_container);

        // Retrieve the user token
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.token = new String("Token "+settings.getString("authToken", "").toString());
        this.university = new String(settings.getString("loggedInUserUniversity","").toString());
        this.userId = new String(settings.getString("loggedInUserId", "").toString());

        //Retrieve the user actions
        invokeGetEventUserActionsWS(this.userId);
        this.eventUserActions = new String(settings.getString("eventUserActions","").toString());
        try {
            ObjectMapper mapper = new ObjectMapper();
            if(!this.eventUserActions.equals("")) {
                eventUser = mapper.readValue(this.eventUserActions, EventUser.class);
                System.out.println("ADITI -------------------> " + eventUser.getUserName());
            }
        }catch (Exception ex){
            System.out.println("Could not parse user's event actions due to "+ ex.getMessage());
        }

        // Clear the static list
        this.listOfEvents.clear();
        invokews();

        TextView textView = (TextView)findViewById(R.id.view_all_events_on_map);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment map = new MapFragment();

                if(((TextView)v).getTag().toString().equals("view_map")) {
                    map.getMapAsync(ViewAllEventsActivity.this);
                    LinearLayout container = (LinearLayout) findViewById(R.id.map_container);
                    getFragmentManager().beginTransaction().add(container.getId(), map, "events_map").commit();
                    ((TextView) v).setText("View List");
                    ((TextView) v).setTag("view_list");
                }
                else if(((TextView)v).getTag().toString().equals("view_list")){
                    getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("events_map")).commit();
                    ((TextView) v).setText("View on Maps");
                    ((TextView) v).setTag("view_map");
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_frontpage, menu);
        MenuItem viewallevents = (MenuItem)menu.findItem(R.id.submenu_viewallevents);
        viewallevents.setVisible(false);
        MenuItem ref = (MenuItem) menu.findItem(R.id.action_refresh);
        ref.setVisible(false);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(listOfEvents!=null && listOfEvents.size()>0) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(listOfEvents.get(0).getLatitude(), listOfEvents.get(0).getLongitude()))
                    .zoom(8)
                    .build();
            googleMap.clear();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            for(Event each: listOfEvents){
                googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(each.getLatitude(), each.getLongitude()))
                    .title(each.getTitle()));
            }
        }
    }

    private void invokews(){
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.addHeader("authorization", this.token);
        System.out.println("Invoking GET list of events REST API...");

        if(this.university!=null) {
            httpClient.get(Constants.baseAPIForEvents + "/" + this.university, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200) {
                        System.out.println("Received the response from EVENT GET ALL LISTS API!!!!!!!!!!");
                        System.out.println("Response is " + responseBody);
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            Event[] eventArray = mapper.readValue(responseBody, Event[].class);
                            System.out.println("No. of events received ============= " + eventArray.length);
                            if (eventArray.length > 0) {
                                for (Event each : eventArray)
                                    listOfEvents.add(each);

                                System.out.println("No. of events received = " + eventArray.length);
                                // Set all user actions
                                if(eventUser!=null && listOfEvents!=null && listOfEvents.size()>0)
                                    new UpdateEventUserActions(listOfEvents, eventUser).updateAll();

                                adapter = new ViewEventAdapter(ViewAllEventsActivity.this.getApplicationContext(),
                                        listOfEvents, ViewAllEventsActivity.this);
                                if (eventsListContainer != null)
                                    eventsListContainer.setAdapter(adapter);

                            }

                            System.out.println("No. of events ====== " + listOfEvents.size());
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
        else{
            Toast.makeText(getApplicationContext(), "User's university is not set.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void viewEventDetail(Event event) {
        Intent viewEventIntent = new Intent(ViewAllEventsActivity.this, ViewEventActivity.class);
        viewEventIntent.putExtra("new_event", event);
        viewEventIntent.putExtra("prev_activity", new String("ViewAllEventsActivity"));
        ViewAllEventsActivity.this.startActivity(viewEventIntent);
    }

    public void invokeGetEventUserActionsWS(String userId){
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.addHeader("authorization", this.token);
        httpClient.get(Util.SERVER_URL + "eventusers/" + userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (statusCode == HttpStatus.SC_OK) {
                    System.out.println("I am in success!!!");
                    System.out.println("Response for user event's action =========== " + response.toString());
                    SharedPreferences settings = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("eventUserActions", response.toString());
                    editor.commit();
                    System.out.println("Event user actions saved successfully!!!");
                } else if (statusCode == HttpStatus.SC_NO_CONTENT) {
                    System.out.println("User's events actions were empty");
                    SharedPreferences settings = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("eventUserActions", new String(""));
                    editor.commit();
                    System.out.println("Event user empty actions saved successfully!!!");
                } else
                    System.out.println("Could not retrive user's event actions.. please check");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                System.out.println("I am in failure!!!");
                System.out.println("Could not retrive user's event actions.. please check");
            }
        });

    }


}

package com.campustribune.event.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.campustribune.event.utility.EventRestCallThread;
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
import com.loopj.android.http.SyncHttpClient;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

/**
 * Created by aditi on 08/07/16.
 */
public class ViewAllEventsActivity extends BaseActivity implements ViewEventAdapter.ViewEachEventInterface, OnMapReadyCallback,
        EventRestCallThread.ViewAllEventsInterface{

    private static ArrayList<Event> listOfEvents = new ArrayList<>();
    private static ViewEventAdapter adapter=null;
    ListView eventsListContainer=null;
    EditText inputSearch;
    String token=null;
    String university=null;
    String eventUserActions=null;
    EventUser eventUser=null;
    String userId=null;
    boolean restCallDone=false;

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

        inputSearch = (EditText) findViewById(R.id.inputSearch);


        // Disable view all from side menu
        invalidateOptionsMenu();

        eventsListContainer = (ListView)findViewById(R.id.view_all_events_container);

        // Retrieve the user token
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.token = new String("Token "+settings.getString("authToken", "").toString());
        this.university = new String(settings.getString("loggedInUserUniversity","").toString());
        this.userId = new String(settings.getString("loggedInUserId", "").toString());

        // Clear the static list
        this.listOfEvents.clear();

        // Invoke WS to get user actions
        EventRestCallThread myRestClient = new EventRestCallThread(ViewAllEventsActivity.this, getApplicationContext(), new String("getuseractions"),
                null, ViewAllEventsActivity.this.token, ViewAllEventsActivity.this.userId);
        myRestClient.start();

        //Invoke WS to get list of events
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

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                int textlength = cs.length();
                ArrayList<Event> tempArrayList = new ArrayList<Event>();
                for (Event event : listOfEvents) {

                    if (event.getDescription().toLowerCase().contains(cs.toString().toLowerCase())) {
                        tempArrayList.add(event);
                    }

                }
                adapter = new ViewEventAdapter(ViewAllEventsActivity.this.getApplicationContext(),
                        tempArrayList, ViewAllEventsActivity.this);
                eventsListContainer.setAdapter(adapter);

            }


            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
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

                                adapter = new ViewEventAdapter(ViewAllEventsActivity.this.getApplicationContext(),
                                        listOfEvents, ViewAllEventsActivity.this);

                                if (eventsListContainer != null)
                                    eventsListContainer.setAdapter(adapter);

                                ViewAllEventsActivity.this.updateUserActions();
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

    @Override
    public void updateUserActions() {
        //Retrieve the user actions
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ViewAllEventsActivity.this.eventUserActions = new String(settings.getString("eventUserActions","").toString());
        try {
            if(!ViewAllEventsActivity.this.eventUserActions.equals("")) {
                ObjectMapper mapper = new ObjectMapper();
                eventUser = mapper.readValue(ViewAllEventsActivity.this.eventUserActions, EventUser.class);
                System.out.println("ADITI -------------------> " + eventUser.getUserName());
            }
        }catch (Exception ex){
            System.out.println("Could not parse user's event actions due to "+ ex.getMessage());
        }

        // Set all user actions
        if(eventUser!=null && listOfEvents!=null && listOfEvents.size()>0)
            new UpdateEventUserActions(listOfEvents, eventUser).updateAll();

        adapter.notifyDataSetChanged();
    }
}

package com.campustribune.event.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.campustribune.BaseActivity;
import com.campustribune.R;
import com.campustribune.beans.Event;
import com.campustribune.beans.EventUser;
import com.campustribune.event.adapter.ViewEventAdapter;
import com.campustribune.event.utility.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

/**
 * Created by aditi on 08/07/16.
 */
public class ViewAllEventsActivity extends BaseActivity implements ViewEventAdapter.ViewEachEventInterface{

    private static ArrayList<Event> listOfEvents = new ArrayList<>();
    private static ViewEventAdapter adapter=null;
    ListView eventsListContainer=null;
    String token=null;
    String university=null;
    String eventUserActions=null;
    EventUser eventUser=null;

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

        eventsListContainer = (ListView)findViewById(R.id.view_all_events_container);

        // Retrieve the user token
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.token = new String("Token "+settings.getString("authToken", "").toString());
        this.university = new String(settings.getString("loggedInUserUniversity","").toString());

        //Retrieve the user actions
        this.eventUserActions = new String(settings.getString("eventUserActions","").toString());
        try {
            ObjectMapper mapper = new ObjectMapper();
            eventUser = mapper.readValue(this.eventUserActions, EventUser.class);
            System.out.println("ADITI -------------------> "+ eventUser.getUserName());
        }catch (Exception ex){
            System.out.println("Could not parse user's event actions due to "+ ex.getMessage());
        }

        // Clear the static list
        this.listOfEvents.clear();

        invokews();
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

                                // Set all user actions
                                setFollowingEvents();
                                setUpvotedEvents();
                                setDownvotedEvents();
                                setGoingEvents();
                                setNotGoingEvents();
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

    private void setFollowingEvents(){
        if(this.eventUser!=null){
            List<UUID> followingEvents = eventUser.getFollowingEvents();
            if(followingEvents!=null && followingEvents.size()>0){
                for(UUID id: followingEvents){
                    int index = searchEventInList(id);
                    if(index != -1){
                        Event event = listOfEvents.get(index);
                        listOfEvents.remove(index);
                        event.setFollow(true);
                        listOfEvents.add(index, event);
                    }
                }
            }
        }
    }

    private void setUpvotedEvents(){
        if(this.eventUser!=null){
            List<UUID> upvotedEvents = eventUser.getUpVotedEvents();
            if(upvotedEvents!=null && upvotedEvents.size()>0){
                for(UUID id: upvotedEvents){
                    int index = searchEventInList(id);
                    if(index != -1){
                        Event event = listOfEvents.get(index);
                        listOfEvents.remove(index);
                        event.setUpvoted(true);
                        listOfEvents.add(index, event);
                    }
                }
            }
        }
    }

    private void setDownvotedEvents(){
        if(this.eventUser!=null){
            List<UUID> downvotedEvents = eventUser.getDownVotedEvents();
            if(downvotedEvents!=null && downvotedEvents.size()>0){
                for(UUID id: downvotedEvents){
                    int index = searchEventInList(id);
                    if(index != -1){
                        Event event = listOfEvents.get(index);
                        listOfEvents.remove(index);
                        event.setDownvoted(true);
                        listOfEvents.add(index, event);
                    }
                }
            }
        }
    }

    private void setGoingEvents(){
        if(this.eventUser!=null){
            List<UUID> goingEvents = eventUser.getGoingEvents();
            if(goingEvents!=null && goingEvents.size()>0){
                for(UUID id: goingEvents){
                    int index = searchEventInList(id);
                    if(index != -1){
                        Event event = listOfEvents.get(index);
                        listOfEvents.remove(index);
                        event.setGoing(true);
                        listOfEvents.add(index, event);
                    }
                }
            }
        }
    }

    private void setNotGoingEvents(){
        if(this.eventUser!=null){
            List<UUID> notgoingEvents = eventUser.getNotgoingEvents();
            if(notgoingEvents!=null && notgoingEvents.size()>0){
                for(UUID id: notgoingEvents){
                    int index = searchEventInList(id);
                    if(index != -1){
                        Event event = listOfEvents.get(index);
                        listOfEvents.remove(index);
                        event.setNotGoing(true);
                        listOfEvents.add(index, event);
                    }
                }
            }
        }
    }

    private int searchEventInList(UUID id){
        if(listOfEvents!=null && listOfEvents.size()>0){
            for(int index=0; index< listOfEvents.size(); index++){
                Event each = listOfEvents.get(index);
                if(each.getId().equals(id)){
                    return index;
                }
            }
        }
        return -1;
    }

}

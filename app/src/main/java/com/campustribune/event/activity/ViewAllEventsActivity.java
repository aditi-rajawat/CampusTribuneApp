package com.campustribune.event.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.campustribune.R;
import com.campustribune.beans.Event;
import com.campustribune.event.adapter.ViewEventAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

/**
 * Created by aditi on 08/07/16.
 */
public class ViewAllEventsActivity extends FragmentActivity implements ViewEventAdapter.ViewEachEventInterface{

    private static ArrayList<Event> listOfEvents = new ArrayList<>();
    private static ViewEventAdapter adapter=null;
    ListView eventsListContainer=null;

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
        invokews();
    }

    private void invokews(){
        AsyncHttpClient httpClient = new AsyncHttpClient();
        System.out.println("Invoking GET list of events REST API...");

        httpClient.get("http://10.0.2.2:8080/events/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    System.out.println("Received the response from EVENT GET ALL LISTS API!!!!!!!!!!");
                    System.out.println("Response is "+ responseBody);
                    try{
                        ObjectMapper mapper = new ObjectMapper();
                        Event[] eventArray = mapper.readValue(responseBody, Event[].class);
                        System.out.println("No. of events received ============= "+ eventArray.length);
                        if(eventArray.length>0){
                            for(Event each: eventArray)
                                listOfEvents.add(each);

                            System.out.println("No. of events received = "+ eventArray.length);
                            adapter = new ViewEventAdapter(ViewAllEventsActivity.this.getApplicationContext(),
                                    listOfEvents, ViewAllEventsActivity.this);
                            if(eventsListContainer !=null)
                                eventsListContainer.setAdapter(adapter);
                        }

                        System.out.println("No. of events ====== "+ listOfEvents.size());
                    }catch (Exception ex){
                        System.out.println("Exception while parsing the response array due to "+ ex.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR){
                    Toast.makeText(getApplicationContext(), "Something went wrong on the server end", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be " +
                            "connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });

//        httpClient.get("http://10.0.2.2:8080/events/", new JsonHttpResponseHandler(){
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                if(statusCode == 200){
//                    try{
//                        ObjectMapper mapper = new ObjectMapper();
//                        Event[] eventArray = mapper.readValue(response.toString(), Event[].class);
//                        if(eventArray.length>0){
//                            for(Event each: eventArray)
//                                listOfEvents.add(each);
//
//                            System.out.println("No. of events received = "+ eventArray.length);
//                            ViewEventAdapter adapter = new ViewEventAdapter(ViewAllEventsActivity.this.getApplicationContext(),
//                                                                                        listOfEvents, ViewAllEventsActivity.this);
//                            if(eventsListContainer !=null)
//                                eventsListContainer.setAdapter(adapter);
//                        }
//                        System.out.println("No. of events ====== "+ listOfEvents.size());
//                    }catch (Exception ex){
//
//                    }
//                }
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                System.out.println("EVENT GET LIST REST API RESPONSE REVCEIVED!!!!!!!!!");
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if(statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR){
//                    Toast.makeText(getApplicationContext(), "Something went wrong on the server end", Toast.LENGTH_LONG).show();
//                }
//                else{
//                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be " +
//                            "connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
    }

    @Override
    public void viewEventDetail(Event event) {
        Intent viewEventIntent = new Intent(ViewAllEventsActivity.this, ViewEventActivity.class);
        viewEventIntent.putExtra("new_event", event);
        viewEventIntent.putExtra("prev_activity", new String("ViewAllEventsActivity"));
        ViewAllEventsActivity.this.startActivity(viewEventIntent);
    }

}

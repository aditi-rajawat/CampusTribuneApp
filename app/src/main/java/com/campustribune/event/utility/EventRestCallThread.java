package com.campustribune.event.utility;

import android.content.Context;

import com.campustribune.beans.Event;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by aditi on 17/07/16.
 */
public class EventRestCallThread extends Thread {

    private Event event;
    private Context ctx;
    private String operationFlag = new String();

    public EventRestCallThread(Context ctx, String operationFlag, Event event){
        this.ctx = ctx;
        this.operationFlag = operationFlag;
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public void run() {
        try {
            if(this.operationFlag.equals("create"))
                createEvent();
            else if(this.operationFlag.equals("update"))
                updateEvent();
            else if(this.operationFlag.equals("delete"))
                deleteEvent();
        }catch (Exception ex){
            System.out.println("Exception thrown while making the REST call due to "+ ex.getMessage());
        }
    }

    private void createEvent() throws JSONException, UnsupportedEncodingException{

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("Create Event Request ----> " + objectMapper.writeValueAsString(this.event));
            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(this.event));
            SyncHttpClient httpClient = new SyncHttpClient();

             httpClient.post(ctx, "http://10.0.2.2:8080/events/", entity, "application/json", new JsonHttpResponseHandler() {

                 @Override
                 public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                     if (statusCode == 201)
                         System.out.println("Event created successfully in the database!!!");
                 }
             });
        }catch (Exception ex){
            System.out.println("Could not process the create event request due to the error "+ ex.getMessage());
        }
    }

    private void updateEvent(){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(this.event));
            SyncHttpClient httpClient = new SyncHttpClient();
            String api = "http://10.0.2.2:8080/events/"+this.event.getId();

            httpClient.put(ctx, api, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == HttpStatus.SC_NO_CONTENT)
                        System.out.println("Event updated successfully in the database!!!");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    System.out.println("Event could not be upated in the database...check error messages on the server side");
                }
            });

        }catch(Exception ex){
            System.out.println("Could not process the update event request due to the error "+ ex.getMessage());

        }
    }

    private void deleteEvent(){
        try{
            String api = "http://10.0.2.2:8080/events/"+this.event.getId();
            SyncHttpClient httpClient = new SyncHttpClient();
            httpClient.delete(ctx, api, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if(statusCode == HttpStatus.SC_NO_CONTENT)
                        System.out.println("Event deleted successfully from the database");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    System.out.println("Event could not be deleted from the database...check error messages on the server side");
                }
            });

        }catch (Exception ex){
            System.out.println("Could not process the delete event request due to the error "+ ex.getMessage());
        }
    }
}

package com.campustribune.event.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.campustribune.beans.Event;
import com.campustribune.event.activity.ViewAllEventsActivity;
import com.campustribune.helper.Util;
import com.loopj.android.http.AsyncHttpClient;
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
    private String token=null;
    private boolean done=false;
    private String userId=null;
    private ViewAllEventsInterface viewAllEventsInterface;

    public interface ViewAllEventsInterface{
        void updateUserActions();
    }

    public EventRestCallThread(Activity activity, Context ctx, String operationFlag, Event event, String token, String userId){
        try{
            if(activity!=null)
                this.viewAllEventsInterface = (ViewAllEventsActivity)activity;
        }catch (ClassCastException ex){
            System.out.println("ViewAllEventsActivity must implement EventRestCallThread.ViewAllEventsInterface");
        }
        this.ctx = ctx;
        this.operationFlag = operationFlag;
        this.event = event;
        this.token = new String(token.toString());
        this.userId = userId;
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
            this.done = false;
            if(this.operationFlag.equals("create"))
                createEvent();
            else if(this.operationFlag.equals("update"))
                updateEvent();
            else if(this.operationFlag.equals("delete"))
                deleteEvent();
            else if(this.operationFlag.equals("getuseractions"))
                invokeGetEventUserActionsWS(this.userId);
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
            httpClient.addHeader("authorization", this.token);


             httpClient.post(ctx, Constants.baseAPIForEvents + "/", entity, "application/json", new JsonHttpResponseHandler() {

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
            httpClient.addHeader("authorization", this.token);

            String api = Constants.baseAPIForEvents+"/"+this.event.getId();

            httpClient.put(ctx, api, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == HttpStatus.SC_NO_CONTENT)
                        System.out.println("Event updated successfully in the database!!!");
                    EventRestCallThread.this.done = true;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    System.out.println("Event could not be upated in the database...check error messages on the server side");
                    EventRestCallThread.this.done = true;
                }
            });

        }catch(Exception ex){
            System.out.println("Could not process the update event request due to the error "+ ex.getMessage());

        }
    }

    private void deleteEvent(){
        try{
            String api = Constants.baseAPIForEvents+"/"+this.event.getId();
            SyncHttpClient httpClient = new SyncHttpClient();
            httpClient.addHeader("authorization", this.token);

            httpClient.delete(ctx, api, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == HttpStatus.SC_NO_CONTENT)
                        System.out.println("Event deleted successfully from the database");
                    EventRestCallThread.this.done = true;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    System.out.println("Event could not be deleted from the database...check error messages on the server side");
                    EventRestCallThread.this.done = true;
                }
            });

        }catch (Exception ex){
            System.out.println("Could not process the delete event request due to the error "+ ex.getMessage());
        }
    }

    public boolean isDone() {
        return done;
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
                            .getDefaultSharedPreferences(EventRestCallThread.this.ctx);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("eventUserActions", response.toString());
                    editor.commit();
                    System.out.println("Event user actions saved successfully!!!");
                } else if (statusCode == HttpStatus.SC_NO_CONTENT) {
                    System.out.println("User's events actions were empty");
                    SharedPreferences settings = PreferenceManager
                            .getDefaultSharedPreferences(EventRestCallThread.this.ctx);
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

package com.campustribune.userProfile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.campustribune.BaseActivity;
import com.campustribune.R;
import com.campustribune.frontpage2.FrontPageActivity;
import com.campustribune.helper.Util;
import com.campustribune.login.LoginActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by sandyarathidas on 7/14/16.
 */
public class UserProfileActivity extends BaseActivity {
    @Bind(R.id.textview_username)
    TextView usernameTextView;
    @Bind(R.id.textview_university)
    TextView universityTextView;
    @Bind(R.id.textview_email)
    TextView emailTextView;
    @Bind(R.id.textview_userid)
    TextView useridTextView;
    @Bind(R.id.switch_notifications)
    CompoundButton allowNotificationCompoundButton;
    @Bind(R.id.switch_recommendations)
    CompoundButton allowEmailCompoundButton;
    @Bind(R.id.btn_category1)
    Button _selectCategory1;
    @Bind(R.id.btn_category2)
    Button _selectCategory2;
    @Bind(R.id.btn_category3)
    Button _selectCategory3;
    @Bind(R.id.btn_category4)
    Button _selectCategory4;
    @Bind(R.id.btn_category5)
    Button _selectCategory5;
    @Bind(R.id.btn_update)
    Button _updateButton;


    boolean isNotificationFlagSet=false;
    boolean isRecommendationFlagSet=false;
    boolean isCategory1Selected=false;
    boolean isCategory2Selected=false;
    boolean isCategory3Selected=false;
    boolean isCategory4Selected=false;
    boolean isCategory5Selected=false;

    String name;
    String userid;
    String email;
    Boolean notifyFlag;
    Boolean notifyReco;
    String university;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        ImageView backgroundImageView =(ImageView)findViewById(R.id.background);
        ButterKnife.bind(this);
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        name = sharedPreferences.getString("loggedInUserName", "");
        userid = sharedPreferences.getString("loggedInUserId", "");
        email = sharedPreferences.getString("loggedInUserEmail", "");
        university = sharedPreferences.getString("loggedInUserUniversity", "");
        notifyFlag = sharedPreferences.getBoolean("loggedInUserNotifications", true);
        notifyReco = sharedPreferences.getBoolean("loggedInUserRecommendations", true);

        if(university.equalsIgnoreCase("UNCC")) {
            backgroundImageView.setBackgroundResource(R.drawable.background_uncc);
            university="University of North Carolina";
        }
        else if(university.equalsIgnoreCase("SJSU")) {
            backgroundImageView.setBackgroundResource(R.drawable.background_sjsu);
            university="San Jose State University";
        }



        _selectCategory1.setEnabled(true);
        _selectCategory2.setEnabled(true);
        _selectCategory3.setEnabled(true);
        _selectCategory4.setEnabled(true);
        _selectCategory5.setEnabled(true);




        ArrayList<String> catlist= LoginActivity.subscriptionList;

        for (String category : catlist) {
            System.out.println(category);
            if(category.equalsIgnoreCase(_selectCategory1.getResources().getString(R.string.category1))){
                markAsSelected(_selectCategory1);
                isCategory1Selected=true;
            }

            if(category.equalsIgnoreCase(_selectCategory2.getResources().getString(R.string.category2))){
                markAsSelected(_selectCategory2);
                isCategory2Selected=true;
            }
            if(category.equalsIgnoreCase(_selectCategory3.getResources().getString(R.string.category3))){
                markAsSelected(_selectCategory3);
                isCategory3Selected=true;

            }
            if(category.equalsIgnoreCase(_selectCategory4.getResources().getString(R.string.category4))){
                markAsSelected(_selectCategory4);
                isCategory4Selected=true;

            }
            if(category.equalsIgnoreCase(_selectCategory5.getResources().getString(R.string.category5))){
                markAsSelected(_selectCategory5);
                isCategory5Selected=true;

            }


        }

        _updateButton.setEnabled(false);
        usernameTextView.setText(name);
        emailTextView.setText(email);
        useridTextView.setText(userid);
        universityTextView.setText(university);
        allowNotificationCompoundButton.setChecked(notifyFlag);
        allowEmailCompoundButton.setChecked(notifyReco);
        editor = sharedPreferences.edit();
        allowNotificationCompoundButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        System.out.println("CHECK HERE SHOULD BE: "+isChecked);
                        isNotificationFlagSet = isChecked;
                        editor.putBoolean("loggedInUserNotifications", isChecked);
                        editor.commit();
                        _updateButton.setEnabled(true);

                    }
                }
        );

        allowEmailCompoundButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        isRecommendationFlagSet = isChecked;
                        editor.putBoolean("loggedInUserRecommendations", isChecked);
                        editor.commit();
                        _updateButton.setEnabled(true);
                    }
                }
        );
        _selectCategory1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isCategory1Selected){
                    markAsDeSelected(_selectCategory1);
                    isCategory1Selected=false;
                }
                else{
                    markAsSelected(_selectCategory1);
                    isCategory1Selected=true;
                }
                _updateButton.setEnabled(true);
            }
        });
        _selectCategory2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isCategory2Selected){
                    markAsDeSelected(_selectCategory2);
                    isCategory2Selected=false;
                }
                else{
                    markAsSelected(_selectCategory2);
                    isCategory2Selected=true;
                }
                _updateButton.setEnabled(true);
            }
        });
        _selectCategory3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isCategory3Selected){
                    markAsDeSelected(_selectCategory3);
                    isCategory3Selected=false;
                }
                else{
                    markAsSelected(_selectCategory3);
                    isCategory3Selected=true;
                }

                _updateButton.setEnabled(true);
            }
        });
        _selectCategory4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isCategory4Selected){
                    markAsDeSelected(_selectCategory4);
                    isCategory4Selected=false;
                }
                else{
                    markAsSelected(_selectCategory4);
                    isCategory4Selected=true;
                }

                _updateButton.setEnabled(true);
            }
        });
        _selectCategory5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isCategory5Selected){
                    markAsDeSelected(_selectCategory5);
                    isCategory5Selected=false;
                }
                else{
                    markAsSelected(_selectCategory5);
                    isCategory5Selected=true;
                }

                _updateButton.setEnabled(true);
            }
        });
        _updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    triggerUpdate(allowNotificationCompoundButton.isChecked(), allowEmailCompoundButton.isChecked(), isCategory1Selected, isCategory2Selected, isCategory3Selected, isCategory4Selected, isCategory5Selected);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void markAsDeSelected(Button _selectCategory) {

        _selectCategory.setBackgroundColor(Color.TRANSPARENT);
        _selectCategory.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_white_18dp, 0, 0, 0);
    }

    private void markAsSelected(Button _selectCategory) {
        _selectCategory.setBackgroundColor(Color.RED);
        _selectCategory.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_black_16dp_1x, 0, 0, 0);
    }


    private void goToFrontPage(){
        Intent frontPage = new Intent(UserProfileActivity.this, FrontPageActivity.class);
        frontPage.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(frontPage);
    }

    private void triggerUpdate(boolean notificationFlagSet, boolean recommendationFlagSet, boolean isCategory1Selected, boolean isCategory2Selected, boolean isCategory3Selected, boolean isCategory4Selected, boolean isCategory5Selected) throws UnsupportedEncodingException, JSONException {

        ArrayList<String> catlist = new ArrayList<String>();
        if(isCategory1Selected)
            catlist.add(getResources().getString(R.string.category1));
        if(isCategory2Selected)
            catlist.add(getResources().getString(R.string.category2));
        if(isCategory3Selected)
            catlist.add(getResources().getString(R.string.category3));
        if(isCategory4Selected)
            catlist.add(getResources().getString(R.string.category4));
        if(isCategory5Selected)
            catlist.add(getResources().getString(R.string.category5));

        LoginActivity.subscriptionList=catlist;

        JSONObject userJSON= new JSONObject();
        userJSON.put("id",userid);
        userJSON.put("sendNotifications",notificationFlagSet);
        userJSON.put("sendRecommendations", recommendationFlagSet);
        userJSON.put("subscriptionList",new JSONArray(catlist));
        StringEntity entity= new StringEntity(userJSON.toString());
        System.out.println("USER JSON :  "+userJSON.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        String auth_token_string = sharedPreferences.getString("authToken", "");
        String userId = sharedPreferences.getString("loggedInUserId", "");
        client.addHeader("authorization","Token "+auth_token_string);
        client.post(this, Util.SERVER_URL+"user/user-profile/"+userId, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                try {
                    if (statusCode == 200) {
                        Toast.makeText(getApplicationContext(), "You are successfully updated!!", Toast.LENGTH_LONG).show();
                        _updateButton.setEnabled(false);
                        goToFrontPage();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Error on on success!" + responseBody.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject responseBody) {
                if (statusCode == 404) {
                    Toast.makeText(getBaseContext(), "User update failed", Toast.LENGTH_LONG).show();

                }
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }


        });
    }


}

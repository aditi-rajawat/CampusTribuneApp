package com.campustribune.userProfile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.CompoundButton;

import com.campustribune.R;

/**
 * Created by sandyarathidas on 7/14/16.
 */
public class UserProfileActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView useridTextView;
    private CompoundButton allowNotificationCompoundButton;
    private CompoundButton allowEmailCompoundButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        SharedPreferences settingsout = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String name = settingsout.getString("loggedInUserName", "");
        String userid= settingsout.getString("loggedInUserId", "");
        String email = settingsout.getString("loggedInUserEmail","");

        usernameTextView = (TextView) findViewById(R.id.textview_username);
        emailTextView = (TextView) findViewById(R.id.textview_email);
        useridTextView=(TextView) findViewById(R.id.textview_userid);


        usernameTextView.setText(String.format(name));
        emailTextView.setText(String.format(email));
        useridTextView.setText(String.format(userid));
        allowNotificationCompoundButton = (CompoundButton) findViewById(R.id.switch_notifications);
        if(allowNotificationCompoundButton == null)
        {
            allowNotificationCompoundButton = (CompoundButton) findViewById(R.id.checkbox_notifications);
        }
        allowEmailCompoundButton = (CompoundButton) findViewById(R.id.switch_allow_recommendations);
        if(allowEmailCompoundButton == null) {
            allowEmailCompoundButton = (CompoundButton) findViewById(R.id.checkbox_allow_email);
        }

    }
}

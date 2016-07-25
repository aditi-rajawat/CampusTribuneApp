package com.campustribune.userProfile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.campustribune.BaseActivity;
import com.campustribune.R;
import com.campustribune.beans.User;
import com.campustribune.helper.Util;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

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
    @Bind(R.id.textview_email)
    TextView emailTextView;
    @Bind(R.id.textview_userid)
    TextView useridTextView;
    @Bind(R.id.switch_notifications)
    CompoundButton allowNotificationCompoundButton;
    @Bind(R.id.switch_recommendations)
    CompoundButton allowEmailCompoundButton;
    @Bind(R.id.btn_update)
    Button _updateButton;

    boolean isNotificationFlagSet=false;
    boolean isRecommendationFlagSet=false;

    String name;
    String userid;
    String email;
    Boolean notifyFlag;
    Boolean notifyReco;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        /*Intent in = getIntent();
        Uri uriData = in.getData();
        System.out.println("URI DATA" + uriData);*/

        ButterKnife.bind(this);


        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        name = sharedPreferences.getString("loggedInUserName", "");
        userid= sharedPreferences.getString("loggedInUserId", "");
        email = sharedPreferences.getString("loggedInUserEmail", "");
        notifyFlag = sharedPreferences.getBoolean("loggedInUserNotifications", true);
        notifyReco = sharedPreferences.getBoolean("loggedInUserRecommendations", true);


        _updateButton.setEnabled(false);
        usernameTextView.setText(String.format(name));
        emailTextView.setText(String.format(email));
        useridTextView.setText(String.format(userid));
        allowNotificationCompoundButton.setChecked(notifyFlag);
        allowEmailCompoundButton.setChecked(notifyReco);
        editor = sharedPreferences.edit();
        allowNotificationCompoundButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
                        isRecommendationFlagSet=isChecked;
                        editor.putBoolean("loggedInUserRecommendations",isChecked );
                        editor.commit();
                        _updateButton.setEnabled(true);
                    }
                }
        );
        _updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    triggerUpdate(isNotificationFlagSet, isRecommendationFlagSet);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void triggerUpdate(boolean isNotificationFlagSet, boolean isRecommendationFlagSet) throws UnsupportedEncodingException, JSONException {
        User user =new User();
        user.setId(userid);
        user.setIsNotifyFlag(isNotificationFlagSet);
        user.setIsRecommendFlag(isRecommendationFlagSet);

        JSONObject userJSON= new JSONObject();
        userJSON.put("id",userid);
        userJSON.put("sendNotifications",isNotificationFlagSet);
        userJSON.put("sendRecommendations", isRecommendationFlagSet);

        StringEntity entity= new StringEntity(userJSON.toString());

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
                        moveTaskToBack(true);
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

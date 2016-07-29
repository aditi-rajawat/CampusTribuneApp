package com.campustribune.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.campustribune.R;
import com.campustribune.beans.Event;
import com.campustribune.beans.Post;
import com.campustribune.beans.User;
import com.campustribune.frontpage2.Data;
import com.campustribune.frontpage2.FrontPageActivity;
import com.campustribune.helper.Util;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    ProgressDialog progressDialog;
    @Bind(R.id.input_username) EditText _usernameText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;
    public static ArrayList<Data> frontPageList = new ArrayList<Data>();
    public static ArrayList<String> subscriptionList = new ArrayList();

    public static ArrayList<Event> staticEventList= new ArrayList<Event>();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }
        _loginButton.setEnabled(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();
        invokeWS(username, password);

    }

    public void invokeWS(String username, String password){
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(username, password);
        client.get(Util.SERVER_URL + "user/login", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                User user = new User();
                ObjectMapper mapper = new ObjectMapper();
                try {
                    user = mapper.readValue(responseBody.toString(), User.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("LOGGED IN UNIVER SITY "+user.getUniversity());
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("authToken", user.getToken());
                editor.putString("loggedInUserId", user.getId());
                editor.putString("loggedInUserEmail", user.getEmail());
                editor.putString("loggedInUserUniversity", user.getUniversity());
                editor.putString("loggedInUserName", user.getFirstName() + " " + user.getLastName());
                editor.putBoolean("loggedInUserNotifications", user.getIsNotifyFlag());
                editor.putBoolean("loggedInUserRecommendations", user.getIsRecommendFlag());
                editor.commit();
                //Code to retrieve the user details stored in shared preferences
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                String auth_token_string = sharedPreferences.getString("authToken", "");
                String user_id = sharedPreferences.getString("loggedInUserId", "");

                System.out.println("Auth Token received from sp for userId:" + user_id + " : " + auth_token_string);
                progressDialog.hide();
                try {
                    if (statusCode == 200) {
                        Toast.makeText(getApplicationContext(), "You are successfully logged in!", Toast.LENGTH_LONG).show();
                        mapPostListToFrontPagData(user.getPostList());
                        staticEventList=user.getEventList();
                        mapEventListToFrontPageData(user.getEventList());
                        subscriptionList = user.getSubscriptionList();
                        navigatetoFrontpageActivity();
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
                progressDialog.hide();
                if (statusCode == 409) {
                    Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

                    Toast.makeText(getApplicationContext(), "Unauthorized", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
                _loginButton.setEnabled(true);

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
                frontPageList.add(data);
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
                frontPageList.add(data);
            }
        }




    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }


    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty() || (username.length() < 4 || username.length() > 10)) {
            _usernameText.setError("Please check email for your username!");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
    public void navigatetoFrontpageActivity(){
        Intent homeIntent = new Intent(getApplicationContext(), FrontPageActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}

package com.campustribune;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.campustribune.login.SignupActivity;
import com.campustribune.R;

public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent signUpIntent = new Intent(LaunchActivity.this, SignupActivity.class);
                LaunchActivity.this.startActivity(signUpIntent);
                LaunchActivity.this.finish();
            }
        }, 3000);
    }
}

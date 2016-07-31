package com.campustribune.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.campustribune.R;
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

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    ProgressDialog progressDialog;

    @Bind(R.id.input_fname)
    EditText _fnameText;
    @Bind(R.id.input_lname)
    EditText _lnameText;
    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.link_login)
    TextView _loginLink;
    @Bind(R.id.university_spinner)
    Spinner _universitySpinner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        String[] universities= getResources().getStringArray(R.array.university_options);
        ArrayAdapter<CharSequence> adapter= new ArrayAdapter<CharSequence>(getApplicationContext(), android.R.layout.simple_spinner_item, universities){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView)v).setTextSize(18);
                ((TextView)v).setTextColor(getResources().getColor(R.color.editTextHintColor));
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _universitySpinner.setAdapter(adapter);
        _universitySpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    signup();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
                SignupActivity.this.startActivity(loginIntent);
                SignupActivity.this.finish();
            }

        });

    }


    public void signup() throws JSONException, UnsupportedEncodingException {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String fname = _fnameText.getText().toString();
        String lname = _lnameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String universityFullName = _universitySpinner.getSelectedItem().toString();
        String university="";
        if(universityFullName.equalsIgnoreCase("San Jose State University"))
            university="SJSU";
        else if(universityFullName.equalsIgnoreCase("University of North Carolina"))
            university="UNCC";
        else if(universityFullName.equalsIgnoreCase("Santa Clara University"))
            university="SCU";
        else if(universityFullName.equalsIgnoreCase("University of Florida"))
            university="UFL";

        System.out.println("UNIVERSITY"+ university);

        JSONObject params = new JSONObject();
        params.put("firstName", fname);
        params.put("lastName", lname);
        params.put("email", email);
        params.put("password", password);
        params.put("university", university);
        invokeWS(params);

    }
    public void invokeWS(JSONObject params) throws UnsupportedEncodingException {

        StringEntity entity = new StringEntity(params.toString());
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        //Please remember to change the below url to your system ip where the backend runs
        client.post(this, Util.SERVER_URL + "user/signUp", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                progressDialog.hide();
                try {
                    if (statusCode == 201) {
                        Toast.makeText(getApplicationContext(), "You are successfully registered!!", Toast.LENGTH_LONG).show();
                        Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
                        SignupActivity.this.startActivity(loginIntent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error on on success!" + responseBody.getString("error_msg"), Toast.LENGTH_LONG).show();
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
                if (statusCode == 404) {
                    Toast.makeText(getBaseContext(), "Sign Up failed", Toast.LENGTH_LONG).show();

                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
                _signupButton.setEnabled(true);
            }


        });


    }


    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign Up failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String fname = _fnameText.getText().toString();
        String lname = _lnameText.getText().toString();
        String university = _universitySpinner.getSelectedItem().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (fname.isEmpty() || fname.length() < 3) {
            _fnameText.setError("at least 3 characters");
            valid = false;
        } else {
            _fnameText.setError(null);
        }

        if (lname.isEmpty() || lname.length() < 3) {
            _lnameText.setError("at least 3 characters");
            valid = false;
        } else {
            _lnameText.setError(null);
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        }
        else if(university.equalsIgnoreCase("San Jose State University")) {
            if (!(email.contains("*@sjsu.edu"))) {
                _emailText.setError("enter the .edu email id of your university!");
                valid = false;
            }
        }
        else if(university.equalsIgnoreCase("University of North Carolina")) {
            if(!(email.contains("*@uncc.edu"))) {
                _emailText.setError("enter the .edu email id of your university!");
                valid = false;
            }
        }
        else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }





}

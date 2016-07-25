package com.campustribune.post.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.campustribune.R;
import com.campustribune.beans.Post;
import com.campustribune.beans.User;
import com.campustribune.post.activity.ViewPostActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by snshr on 6/24/2016.
 */
public class ViewPostFragment extends Fragment {

    @Bind(R.id.createdUser)
    TextView creator;
    @Bind (R.id.AlertText)
    TextView alert;
    @Bind(R.id.PostTitle)
    TextView headline;
    @Bind(R.id.PostContent)
    TextView content;
    @Bind(R.id.PostTitleEdit)
    EditText headlineEdit;
    @Bind(R.id.PostContentEdit)
    EditText contentEdit;
    @Bind(R.id.button_save_post)
    Button saveBtn;
    @Bind(R.id.button_cancel_post)
    Button cancelBtn;
    @Bind(R.id.gotourl)
    Button url;
    @Bind(R.id.postImage)
    ImageView postImage;
    @Bind(R.id.editPostbuttonLayout)
    LinearLayout btnLayout;

    public Post post= new Post();
    String post_id;
    String userId;
    String token;

    public String BASEURL="http://192.168.0.14:8080/";


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view =  inflater.inflate(R.layout.fragment_view_post,
                container, false);
        ButterKnife.bind(this, view);
        post_id= ((ViewPostActivity)this.getActivity()).getPost_id();

        post= ((ViewPostActivity)this.getActivity()).getPost();
        System.out.println(post_id);

        //Code to retrieve the user details stored in shared preferences
        SharedPreferences settingsout = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        token = "Token "+settingsout.getString("authToken", "");
        userId= settingsout.getString("loggedInUserId", "");

        setValuesToView();
        //invokeloadPostWS(post_id);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getContext(), "Save Post Clicked", Toast.LENGTH_LONG).show();
                try {
                    String headlineVal = headlineEdit.getText().toString();String contentVal=contentEdit.getText().toString();
                    //callSaveWS(headlineEdit.getText().toString(), contentEdit.getText().toString());
                    if(headlineVal.trim().equalsIgnoreCase("") || headlineVal!=null && (headlineVal.trim().length()<10||headlineVal.trim().length()>50)){
                        headlineEdit.setError("Post Headline is mandatory!!Allowed character length is 10-50 characters");
                    }else if(contentVal.trim().equalsIgnoreCase("") || contentVal!=null && (contentVal.trim().length()<10||contentVal.trim().length()>1500)) {
                        contentEdit.setError("Post Content is mandatory!!Allowed character length is 10-1500 characters");
                    }else{
                        callSaveWS(headlineEdit.getText().toString(), contentEdit.getText().toString());
                        content.setVisibility(v.VISIBLE);
                        contentEdit.setVisibility(v.GONE);
                        headline.setVisibility(v.VISIBLE);
                        headlineEdit.setVisibility(v.GONE);
                        btnLayout.setVisibility(v.GONE);
                        Fragment frg = null;
                        frg = getFragmentManager().findFragmentById(R.id.viewPost_fragment);
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(frg);
                        ft.attach(frg);
                        ft.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                /*content.setVisibility(v.VISIBLE);
                contentEdit.setVisibility(v.GONE);
                headline.setVisibility(v.VISIBLE);
                headlineEdit.setVisibility(v.GONE);
                btnLayout.setVisibility(v.GONE);
                Fragment frg = null;
                frg = getFragmentManager().findFragmentById(R.id.viewPost_fragment);
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();*/
            }
        });
       cancelBtn.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               content.setVisibility(v.VISIBLE);
               contentEdit.setVisibility(v.GONE);
               headline.setVisibility(v.VISIBLE);
               headlineEdit.setVisibility(v.GONE);
               btnLayout.setVisibility(v.GONE);
               Fragment frg = null;
               frg = getFragmentManager().findFragmentById(R.id.viewPost_fragment);
               final FragmentTransaction ft = getFragmentManager().beginTransaction();
               ft.detach(frg);
               ft.attach(frg);
               ft.commit();
           }
       });

        url.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String pattern ="((https|http)(:\\/\\/))?";
                    URL webURL;
                    if(post.getWebLink().matches(pattern)){
                        webURL = new URL(post.getWebLink());
                    }else{
                        webURL = new URL("http://"+post.getWebLink());
                    }
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webURL.toURI().toString())));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }
        });

        return view;
    }

    private void callSaveWS(String headline,String content) throws JSONException, UnsupportedEncodingException {
        JSONObject params = new JSONObject();
        params.put("id", post_id);
        params.put("headline", headline);
        params.put("content", content);
        String URL = "update";
        System.out.println(params.toString());
        invokeSaveWS(params, URL);

    }
    public void performEditText(){
        if (btnLayout.getVisibility() == View.GONE) {
            headlineEdit.setText(headline.getText());
            contentEdit.setText(content.getText());
            content.setVisibility(View.INVISIBLE);
            contentEdit.setVisibility(View.VISIBLE);
            headline.setVisibility(View.INVISIBLE);
            headlineEdit.setVisibility(View.VISIBLE);
            btnLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setPostObj(JSONObject respBody){
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(respBody.toString());
            this.post = mapper.readValue(respBody.toString(), Post.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setValuesToView();
    }


    private void setValuesToView(){
        headline.setText(post.getHeadline());
        content.setText(post.getContent());
        creator.setText("Created By "+post.getUserId());
        System.out.println("URL is "+post.getImgURL());

        if(post.isAlert()){
            alert.setVisibility(View.VISIBLE);
        }

        if(post.getWebLink()!=null && post.getWebLink().length()>0){
            url.setText("Go to "+post.getWebLink());
        }else{
            url.setVisibility(View.GONE);
        }

        if(post.getImgURL()!=null && post.getImgURL().length()>0) {
            /*// Create an object for subclass of AsyncTask
            GetImage task = new GetImage();
            // Execute the task
            task.execute(new String[]{post.getImgURL()});*/
            Picasso.with(getContext()).load(post.getImgURL()).into(postImage);
        }else{
            postImage.setVisibility(ImageView.GONE);
        }
    }


    public void invokeSaveWS(JSONObject params,String URL) throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity(params.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("authorization",token);
        client.put(getContext(), BASEURL+"post/" + URL, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                try {
                    if (statusCode == 200) {
                        ViewPostFragment.this.setPostObj(responseBody);
                    } else {
                        Toast.makeText(getContext(), responseBody.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject responseBody) {
                if (statusCode == 409) {
                    Toast.makeText(getContext(), "View Post failed", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



}

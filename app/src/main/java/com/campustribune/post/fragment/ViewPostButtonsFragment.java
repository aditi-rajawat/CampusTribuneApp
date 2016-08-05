package com.campustribune.post.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.campustribune.R;
import com.campustribune.beans.Post;
import com.campustribune.beans.PostUser;
import com.campustribune.frontpage.FrontPageActivity;
import com.campustribune.helper.Util;
import com.campustribune.post.activity.ViewPostActivity;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by snshr on 7/5/2016.
 */
public class ViewPostButtonsFragment extends Fragment{

    @Bind(R.id.downvote)
    ImageButton downvoteBtn;
    @Bind(R.id.upvote)
    ImageButton upvoteBtn;
    @Bind(R.id.report)
    ImageButton reportBtn;
    @Bind(R.id.follow)
    Button followBtn;
    @Bind(R.id.button_edit)
    Button editBtn;
    @Bind(R.id.button_delete)
    Button deleteBtn;
    @Bind(R.id.votescore)
    TextView voteScoreText;

    public Post post = new Post();
    ViewPostFragment viewPostFragment;
    public String BASEURL= Util.SERVER_URL;
    String post_id;
    String userId;
    String token;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view =  inflater.inflate(R.layout.fragment_view_post_buttons,
                container, false);
        post_id= ((ViewPostActivity)this.getActivity()).getPost_id();
        post= ((ViewPostActivity)this.getActivity()).getPost();
        System.out.println(post_id);

        //Code to retrieve the user details stored in shared preferences
        SharedPreferences settingsout = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        token = "Token "+settingsout.getString("authToken", "");
        userId= settingsout.getString("loggedInUserId", "");


        viewPostFragment = (ViewPostFragment)getFragmentManager().findFragmentById(R.id.viewPost_fragment);

        ButterKnife.bind(this, view);

        if(!userId.equalsIgnoreCase(post.getUserId())){
            deleteBtn.setVisibility(View.INVISIBLE);
            editBtn.setVisibility(View.INVISIBLE);
        }
        updateViewWithUserPref(Integer.valueOf(post_id));
        voteScoreText.setText(String.valueOf(post.getVoteScore()));


       upvoteBtn.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               //Toast.makeText(getContext(), "Upvote Clicked", Toast.LENGTH_LONG).show();
               if (downvoteBtn.isEnabled()) {
                   try {
                       callVoteWS("1", userId);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   } catch (UnsupportedEncodingException e) {
                       e.printStackTrace();
                   }
                   upvoteBtn.setColorFilter(Color.argb(255, 0, 153, 51)); // Green Tint
                   downvoteBtn.setEnabled(false);
                   updateUserPref("upvote",Integer.valueOf(post_id));

               } else {
                   try {
                       callVoteWS("2", userId);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   } catch (UnsupportedEncodingException e) {
                       e.printStackTrace();
                   }
                   upvoteBtn.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint
                   downvoteBtn.setEnabled(true);
                   updateUserPref("upvote", Integer.valueOf(post_id));
               }
           }
       });


        downvoteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getContext(), "Downvote Clicked", Toast.LENGTH_LONG).show();
                if (upvoteBtn.isEnabled()) {
                    try {
                        callVoteWS("3",userId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    downvoteBtn.setColorFilter(Color.argb(255, 255, 0, 0)); // Red Tint
                    upvoteBtn.setEnabled(false);
                    updateUserPref("downvote", Integer.valueOf(post_id));

                } else {
                    try {
                        callVoteWS("4",userId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    downvoteBtn.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint
                    upvoteBtn.setEnabled(true);
                    updateUserPref("downvote", Integer.valueOf(post_id));
                }
            }
        });


        reportBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getContext(), "Report Clicked", Toast.LENGTH_LONG).show();

                AlertDialog alert = new AlertDialog.Builder(getContext())
                        .setTitle("Report post")
                        .setMessage("Are you sure you want to report this post?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    callReportWS(userId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                reportBtn.setColorFilter(Color.argb(255, 255, 0, 0)); // Red Tint
                                reportBtn.setEnabled(false);
                                updateUserPref("reportpost", Integer.valueOf(post_id));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

               /* try {
                    callReportWS(userId);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                reportBtn.setColorFilter(Color.argb(255, 255, 0, 0)); // Red Tint
                reportBtn.setEnabled(false);
                updateUserPref("reportpost", Integer.valueOf(post_id));*/
            }
        });

        followBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getContext(), "Follow Clicked", Toast.LENGTH_LONG).show();
                try {
                    callFollowWS(post_id,userId);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                /*followBtn.setColorFilter(Color.argb(255, 0, 102, 255)); // Blue Tint*/
                if(followBtn.getText().equals("Follow"))
                    followBtn.setText("Unfollow");
                else
                    followBtn.setText("Follow");
                updateUserPref("follow", Integer.valueOf(post_id));
            }
        });


        editBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getContext(), "Edit Clicked", Toast.LENGTH_LONG).show();
                viewPostFragment.performEditText();
            }
        });


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getContext(), "Delete Clicked", Toast.LENGTH_LONG).show();
                AlertDialog alert = new AlertDialog.Builder(getContext())
                        .setTitle("Delete Post")
                        .setMessage("Are you sure you want to delete this post?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                System.out.println("Delete clicked");
                                try {
                                    callDeleteWS(post_id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

               /* try {
                    callDeleteWS(post_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/
            }
        });

        return view;
    }

    private void callVoteWS(String voteType,String userId) throws JSONException, UnsupportedEncodingException {
        JSONObject params = new JSONObject();
        params.put("id", post_id);
        System.out.println(params.toString());
        String URL = "vote/"+voteType+"/byUser/"+userId;
        invokeReactWS(params, URL);
    }

    private void callReportWS(String userId) throws JSONException, UnsupportedEncodingException {
        JSONObject params = new JSONObject();
        params.put("id",post_id);
        System.out.println(params.toString());
        String URL = "report/byUser/"+userId;
        invokeReactWS(params, URL);
    }

    private void callFollowWS(String postId,String userId) throws JSONException, UnsupportedEncodingException {
        JSONObject params = new JSONObject();
        params.put("id", post_id);
        String URL = "follow/byUser/"+userId;
        System.out.println(URL);
        invokeFollowWS(URL, params);
    }

    private void callDeleteWS(String postId) throws JSONException, UnsupportedEncodingException {
        JSONObject params = new JSONObject();
        String URL = "remove/"+postId;
        invokeDeleteWS(URL);
    }

    public void goToFrontPage(){
        Intent frontPage = new Intent(getContext(), FrontPageActivity.class);
        frontPage.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(frontPage);
    }

    public void setPostObj(JSONObject respBody){
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(respBody.toString());
            this.post = mapper.readValue(respBody.toString(), Post.class);
            voteScoreText.setText((String.valueOf(post.getVoteScore())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void invokeReactWS(JSONObject params,String URL) throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity(params.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("authorization",token);
        client.post(getContext(),BASEURL+"post/"+URL, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                System.out.println(statusCode);

                try {

                    if (statusCode == 200) {
                        //Toast.makeText(getContext(), "Post Updated Successfully!!", Toast.LENGTH_LONG).show();
                        if(responseBody!=null)
                             ViewPostButtonsFragment.this.setPostObj(responseBody);
                        else {
                            Toast.makeText(getContext(), "Post is being removed since we received multiple reports", Toast.LENGTH_LONG).show();
                            ViewPostButtonsFragment.this.goToFrontPage();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error on on success!" + responseBody.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject responseBody) {
                System.out.println(statusCode);
                if (statusCode == 404) {
                    Toast.makeText(getContext(), "Update Post failed", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody,Throwable error) {
                System.out.println(statusCode);
                if (statusCode == 200) {
                    System.out.println("Inside if 200");
                    Toast.makeText(getContext(), "Post is being removed since we received multiple reports", Toast.LENGTH_LONG).show();
                    ViewPostButtonsFragment.this.goToFrontPage();
                }  else {
                    Toast.makeText(getContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void invokeFollowWS(String URL,JSONObject params) throws UnsupportedEncodingException {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("authorization", token);
        StringEntity entity = new StringEntity(params.toString());
        client.post(getContext(),BASEURL + "post/" + URL, entity,"application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println(statusCode);
                if (statusCode == 200) {
                    System.out.println("Post followed!!!");
                    //Toast.makeText(getContext(), "Post Followed Successfully!!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Error on on success!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Inside failure" +statusCode);
                error.printStackTrace();
                if (statusCode == 404) {
                    Toast.makeText(getContext(), "Follow  Post failed", Toast.LENGTH_LONG).show();

                } else if (statusCode == 500) {
                    Toast.makeText(getContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    public void invokeDeleteWS(String URL) throws UnsupportedEncodingException {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("authorization",token);
        client.delete(BASEURL + "post/" + URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println(statusCode);
                if (statusCode == 200) {
                    //Toast.makeText(getContext(), "Post Deleted Successfully!!", Toast.LENGTH_LONG).show();
                    ViewPostButtonsFragment.this.goToFrontPage();
                } else {
                    Toast.makeText(getContext(), "Error on on success!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Inside failure" + statusCode);
                error.printStackTrace();
                if (statusCode == 404) {
                    Toast.makeText(getContext(), "Delete  Post failed", Toast.LENGTH_LONG).show();

                } else if (statusCode == 500) {
                    Toast.makeText(getContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }


        });
    }

    private void updateUserPref(String actionType,Integer id){
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        PostUser postUser = new PostUser();
        if(settings.contains("userPostActions")){
            String postuser_json = settings.getString("userPostActions", "");
            postUser  = gson.fromJson(postuser_json, PostUser.class);
        }
        if(actionType.equalsIgnoreCase("follow")){
            if(postUser.getFollowingPosts().contains(id))
                postUser.getFollowingPosts().remove(id);
            else
                postUser.getFollowingPosts().add(id);

        }else if(actionType.equalsIgnoreCase("upvote")){
            if(postUser.getUpVotedPosts().contains(id))
                postUser.getUpVotedPosts().remove(id);
            else
                postUser.getUpVotedPosts().add(id);
        }else if(actionType.equalsIgnoreCase("downvote")){
            if(postUser.getDownVotedPosts().contains(id))
                postUser.getDownVotedPosts().remove(id);
            else
                postUser.getDownVotedPosts().add(id);
        }else if(actionType.equalsIgnoreCase("reportpost")){
            postUser.getReportedPosts().add(id);
        }
        String postuser_json = gson.toJson(postUser);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userPostActions", postuser_json);
        editor.commit();
    }

    private void updateViewWithUserPref(Integer id){
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        PostUser postUser = new PostUser();
        if(settings.contains("userPostActions")){
            String postuser_json = settings.getString("userPostActions", "");
            postUser  = gson.fromJson(postuser_json, PostUser.class);
            if(postUser.getFollowingPosts().contains(id)) {
//                followBtn.setColorFilter(Color.argb(255, 0, 102, 255)); // Blue Tint
                  followBtn.setText("Unfollow");
            }if(postUser.getUpVotedPosts().contains(id)){
                upvoteBtn.setColorFilter(Color.argb(255, 0, 153, 51)); // Green Tint
                downvoteBtn.setEnabled(false);
            }if(postUser.getDownVotedPosts().contains(id)) {
                downvoteBtn.setColorFilter(Color.argb(255, 255, 0, 0)); // Red Tint
                upvoteBtn.setEnabled(false);
            }if(postUser.getReportedPosts().contains(id)) {
                reportBtn.setColorFilter(Color.argb(255, 255, 0, 0)); // Red Tint
                reportBtn.setEnabled(false);
            }
        }

    }


}

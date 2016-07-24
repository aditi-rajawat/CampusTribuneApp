package com.campustribune.post.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.campustribune.R;
import com.campustribune.beans.Post;
import com.campustribune.beans.PostComment;
import com.campustribune.beans.PostUser;
import com.campustribune.post.activity.ViewPostActivity;
import com.campustribune.post.adapter.CommentListAdapter;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class CommentListFragment extends Fragment{

    ProgressDialog progressDialog;
    Dialog showComment;
    @Bind(R.id.comment)
    EditText commentValue;
    @Bind(R.id.button_save)
    Button saveBtn;
    @Bind(R.id.clist)
    ListView cList;

    ArrayList<PostComment> comments = new ArrayList<PostComment>();

    TextView commentText;
    EditText commentTextEdit;
    LinearLayout editCommentBtnLayout;
    Button editCommentBtn;
    Button deleteCommentBtn;
    ImageButton reportCommentBtn;
    Button saveEditCommentBtn;
    Button cancelEditCommentBtn;
    String userId;
    String token;

    private CommentListAdapter cListAdapter;

    public String BASEURL="http://192.168.0.14:8080/";
    String post_id;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override

    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_comments_list, container, false);
        ButterKnife.bind(this, view);
        progressDialog = new ProgressDialog(CommentListFragment.this.getContext(),
                R.style.AppTheme_Dark_Dialog);

        post_id= ((ViewPostActivity)this.getActivity()).getPost_id();
        System.out.println(post_id);
        //Code to retrieve the user details stored in shared preferences
        SharedPreferences settingsout = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        token = "Token "+settingsout.getString("authToken", "");
        userId= settingsout.getString("loggedInUserId", "");
        loadCommentsWS(post_id);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    createCommentClick();
                    commentValue.setText("");
                    saveBtn.setEnabled(true);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                reloadFragment();
            }
        });

        return view;

    }

    private void showComment(View view, final PostComment comment){
        showComment = new Dialog(getActivity());
        showComment.setTitle("Dialog Title");
        showComment.setContentView(R.layout.fragment_view_comment);
        commentText = (TextView)showComment.findViewById(R.id.commentContent);
        commentTextEdit= (EditText) showComment.findViewById(R.id.commentContentEdit);
        editCommentBtnLayout=(LinearLayout)showComment.findViewById(R.id.editCommentbuttonLayout);
        editCommentBtn = (Button) showComment.findViewById(R.id.comment_button_edit);
        deleteCommentBtn = (Button) showComment.findViewById(R.id.comment_button_delete);
        reportCommentBtn =(ImageButton)showComment.findViewById(R.id.reportComment);
        saveEditCommentBtn = (Button) showComment.findViewById(R.id.button_save_comment);
        cancelEditCommentBtn = (Button) showComment.findViewById(R.id.button_cancel_comment);
        commentText.setText(comment.getCommentContent());
        updateViewWithUserPref(Integer.valueOf(comment.getId()));
        editCommentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Edit clicked");
                if (editCommentBtnLayout.getVisibility() == View.GONE) {
                    commentTextEdit.setText(commentText.getText());
                    commentText.setVisibility(View.INVISIBLE);
                    commentTextEdit.setVisibility(View.VISIBLE);
                    editCommentBtnLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        deleteCommentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Delete clicked");
                try {
                    callDeleteCommentWS(comment.getId(), comment.getPostId());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                showComment.hide();
                commentText.setVisibility(View.VISIBLE);
                commentTextEdit.setVisibility(View.GONE);
                editCommentBtnLayout.setVisibility(View.GONE);
                reloadFragment();
            }
        });

        reportCommentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Delete clicked");
                try {
                    callReportCommentWS(comment);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                reportCommentBtn.setColorFilter(Color.argb(255, 255, 0, 0)); // Red Tint
                reportCommentBtn.setEnabled(false);
                commentText.setVisibility(v.VISIBLE);
                commentTextEdit.setVisibility(v.GONE);
                editCommentBtnLayout.setVisibility(v.GONE);
                reloadFragment();
                updateUserPref("reportcomment",Integer.valueOf(comment.getId()));
            }
        });

        saveEditCommentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Save clicked");
                try {
                    //callSaveCommentWS(commentTextEdit.getText().toString(), comment);
                    String commentStr=commentTextEdit.getText().toString();
                    if(commentStr.trim().equalsIgnoreCase("") || commentStr!=null && (commentStr.trim().length()<10||commentStr.trim().length()>50)){
                        commentTextEdit.setError("Please enter a comment to save!!Allowed character length is 10-500 characters");
                    }else{
                        callSaveCommentWS(commentTextEdit.getText().toString(), comment);
                        commentText.setVisibility(View.VISIBLE);
                        commentTextEdit.setVisibility(View.GONE);
                        editCommentBtnLayout.setVisibility(View.GONE);
                        showComment.hide();
                        reloadFragment();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                /*commentText.setVisibility(View.VISIBLE);
                commentTextEdit.setVisibility(View.GONE);
                editCommentBtnLayout.setVisibility(View.GONE);
                reloadFragment();*/
            }
        });

        cancelEditCommentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Cancel clicked");
                commentText.setVisibility(v.VISIBLE);
                commentTextEdit.setVisibility(v.GONE);
                editCommentBtnLayout.setVisibility(v.GONE);
                reloadFragment();
            }
        });
        showComment.show();
    }

    public void reloadFragment(){
        Fragment frg = null;
        frg = getFragmentManager().findFragmentById(R.id.comments_fragment);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    public void createCommentClick() throws JSONException, UnsupportedEncodingException {
        String comment = commentValue.getText().toString();
        if(comment.trim().equalsIgnoreCase("") || comment!=null && (comment.trim().length()<10||comment.trim().length()>50)){
            commentValue.setError("Please enter a comment to save!!Allowed character length is 10-500 characters");
        }else{
            saveBtn.setEnabled(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Creating Comment...");
            progressDialog.show();

            JSONObject params = new JSONObject();
            params.put("postId", post_id);
            params.put("commentContent", comment);
            params.put("userId", userId);
            invokeCreateCommentWS(params);
        }
    }

    private void callSaveCommentWS(String commentTextVal,PostComment com) throws JSONException, UnsupportedEncodingException {
        JSONObject params = new JSONObject();
        params.put("id", com.getId());
        params.put("commentContent", commentTextVal);
        params.put("postId", com.getPostId());
        String URL = "update";
        invokeSaveCommentWS(params, URL);

    }

    private void callReportCommentWS(PostComment com) throws JSONException, UnsupportedEncodingException {
        JSONObject params = new JSONObject();
        params.put("id", com.getId());
        params.put("postId", com.getPostId());
        String URL = "report/byUser/"+userId;
        invokeReportCommentWS(params, URL);

    }

    private void callDeleteCommentWS(int commentId, int postId) throws JSONException, UnsupportedEncodingException {
        JSONObject params = new JSONObject();
        String URL = "delete/"+commentId+"/forPost/"+postId;
        invokeDeleteCommentWS(URL);
    }

    public void setCommentListsObj(JSONArray respBody,String rqsType){
        ObjectMapper mapper = new ObjectMapper();
        try {
            if(respBody!=null){
                PostComment[] commentList = mapper.readValue(respBody.toString(), PostComment[].class);
                if(rqsType.equalsIgnoreCase("firstload")){
                    for(PostComment comment:commentList){
                        comments.add(comment);
                    }
                    cListAdapter=new CommentListAdapter(this.getContext(), comments);
                    cList.setAdapter(cListAdapter);
                    cList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                            cList.setSelector(android.R.color.darker_gray);
                            Object o = cList.getItemAtPosition(position);
                            PostComment coms = (PostComment) o;
                            showComment(v, coms);
                        }
                    });
                }else{
                    comments.clear();
                    for(PostComment comment:commentList){
                        comments.add(comment);
                    }
                    cListAdapter.notifyDataSetChanged();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCommentsWS(String postid){
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("authorization", token);
        client.get(BASEURL + "/comment/getAll/forPost/" + postid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseBody) {
                System.out.println("Inside onSuccess" + statusCode);
                //try {
                if (statusCode == 200) {
                    CommentListFragment.this.setCommentListsObj(responseBody, "firstload");
                } else {
                    System.out.println("Inside onSuccess else");
                    Toast.makeText(getContext(), "ERROR", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject responseBody) {
                System.out.println("Inside onFailue" + statusCode);
                if (statusCode == 409) {
                    System.out.println("Inside onFailue if");
                    Toast.makeText(getContext(), "View Post failed", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    System.out.println("Inside onFailue if");
                    Toast.makeText(getContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    System.out.println("Inside onFailue++ if");
                    Toast.makeText(getContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void invokeCreateCommentWS(JSONObject params) throws UnsupportedEncodingException {

        StringEntity entity = new StringEntity(params.toString());
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("authorization", token);
        client.post(getContext(), BASEURL + "comment/create", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                progressDialog.hide();
                System.out.println(statusCode);
                try {
                    if (statusCode == 201) {
                        Toast.makeText(getContext(), "Comment Created Successfully!!", Toast.LENGTH_LONG).show();
                        CommentListFragment.this.reloadCommentsWS(post_id);

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
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                progressDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getContext(), "Create Post failed", Toast.LENGTH_LONG).show();

                } else if (statusCode == 500) {
                    Toast.makeText(getContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
                saveBtn.setEnabled(true);
            }


        });


    }

    public void reloadCommentsWS(String postid){
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("authorization", token);
        client.get(BASEURL + "comment/getAll/forPost/" + postid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseBody) {
                System.out.println("Inside onSuccess" + statusCode);
                //try {
                if (statusCode == 200) {
                    CommentListFragment.this.setCommentListsObj(responseBody, "reload");
                } else {
                    System.out.println("Inside onSuccess else");
                    Toast.makeText(getContext(), "ERROR", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject responseBody) {
                System.out.println("Inside onFailue" + statusCode);
                if (statusCode == 409) {
                    System.out.println("Inside onFailue if");
                    Toast.makeText(getContext(), "View Post failed", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    System.out.println("Inside onFailue if");
                    Toast.makeText(getContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    System.out.println("Inside onFailue++ if");
                    Toast.makeText(getContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void invokeSaveCommentWS(JSONObject params,String URL) throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity(params.toString());
        System.out.println(entity);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("authorization",token);
        client.put(getContext(), BASEURL + "comment/" + URL, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                try {
                    if (statusCode == 200) {
                        Toast.makeText(getContext(), "Comment Edited Successfully", Toast.LENGTH_LONG).show();
                        CommentListFragment.this.reloadCommentsWS(post_id);
                    } else {
                        Toast.makeText(getContext(), responseBody.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
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

    public void invokeReportCommentWS(JSONObject params,String URL) throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity(params.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("authorization",token);
        client.post(getContext(), BASEURL + "comment/" + URL, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                System.out.println(statusCode);

                try {

                    if (statusCode == 200) {
                        Toast.makeText(getContext(), "Post Updated Successfully!!", Toast.LENGTH_LONG).show();
                        CommentListFragment.this.reloadCommentsWS(post_id);
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
                    Toast.makeText(getContext(), "Create Post failed", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }

            }


        });
    }


    public void invokeDeleteCommentWS(String URL) throws UnsupportedEncodingException {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("authorization",token);
        client.delete(BASEURL + "comment/" + URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println(statusCode);
                if (statusCode == 200) {
                    Toast.makeText(getContext(), "Comment Deleted Successfully!!", Toast.LENGTH_LONG).show();
                    CommentListFragment.this.reloadCommentsWS(post_id);
                } else {
                    Toast.makeText(getContext(), "Error on on success!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Inside failure" + statusCode);
                error.printStackTrace();
                if (statusCode == 404) {
                    Toast.makeText(getContext(), "Delete  Comment failed", Toast.LENGTH_LONG).show();

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
        if(actionType.equalsIgnoreCase("reportcomment")){
            postUser.getReportedComments().add(id);
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
            if(postUser.getReportedComments().contains(id)) {
                reportCommentBtn.setColorFilter(Color.argb(255, 255, 0, 0)); // Red Tint
                reportCommentBtn.setEnabled(false);
            }
        }

    }
}
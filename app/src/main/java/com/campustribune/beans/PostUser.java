package com.campustribune.beans;

import java.util.ArrayList;

/**
 * Created by snshr on 7/22/2016.
 */
public class PostUser {

    private String user;
    private ArrayList<Integer> followingPosts = new ArrayList<Integer>();
    private ArrayList<Integer> upVotedPosts = new ArrayList<Integer>();
    private ArrayList<Integer> downVotedPosts = new ArrayList<Integer>();
    private ArrayList<Integer> reportedPosts = new ArrayList<Integer>();
    private ArrayList<Integer> reportedComments = new ArrayList<Integer>();

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ArrayList<Integer> getFollowingPosts() {
        return followingPosts;
    }

    public void setFollowingPosts(ArrayList<Integer> followingPosts) {
        this.followingPosts = followingPosts;
    }

    public ArrayList<Integer> getUpVotedPosts() {
        return upVotedPosts;
    }

    public void setUpVotedPosts(ArrayList<Integer> upVotedPosts) {
        this.upVotedPosts = upVotedPosts;
    }

    public ArrayList<Integer> getDownVotedPosts() {
        return downVotedPosts;
    }

    public void setDownVotedPosts(ArrayList<Integer> downVotedPosts) {
        this.downVotedPosts = downVotedPosts;
    }

    public ArrayList<Integer> getReportedPosts() {
        return reportedPosts;
    }

    public void setReportedPosts(ArrayList<Integer> reportedPosts) {
        this.reportedPosts = reportedPosts;
    }

    public ArrayList<Integer> getReportedComments() {
        return reportedComments;
    }

    public void setReportedComments(ArrayList<Integer> reportedComments) {
        this.reportedComments = reportedComments;
    }
}

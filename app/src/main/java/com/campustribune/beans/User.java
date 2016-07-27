package com.campustribune.beans;

import java.util.ArrayList;

/**
 * Created by sandyarathidas on 7/13/16.
 */
public class User {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String token;
    private String university;

    private Boolean isNotifyFlag;
    private Boolean isRecommendFlag;

    private ArrayList<Post> postList= new ArrayList();
    private ArrayList<String> subscriptionList = new ArrayList<>();
    //private ArrayList<Event> eventList= new ArrayList();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public Boolean getIsNotifyFlag() {
        return isNotifyFlag;
    }

    public void setIsNotifyFlag(Boolean isNotifyFlag) {
        this.isNotifyFlag = isNotifyFlag;
    }

    public Boolean getIsRecommendFlag() {
        return isRecommendFlag;
    }

    public void setIsRecommendFlag(Boolean isRecommendFlag) {
        this.isRecommendFlag = isRecommendFlag;
    }

    public ArrayList<Post> getPostList() {
        return postList;
    }

    public void setPostList(ArrayList<Post> postList) {
        this.postList = postList;
    }

    public ArrayList<String> getSubscriptionList() {
        return subscriptionList;
    }

    public void setSubscriptionList(ArrayList<String> subscriptionList) {
        this.subscriptionList = subscriptionList;
    }

    /*public ArrayList<Event> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<Event> eventList) {
        this.eventList = eventList;
    }*/



}

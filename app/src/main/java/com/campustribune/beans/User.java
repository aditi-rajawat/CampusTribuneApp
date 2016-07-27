package com.campustribune.beans;

import java.util.ArrayList;

/**
 * Created by sandyarathidas on 7/13/16.
 */
public class User {
    String id;
    String firstName;
    String lastName;
    String email;
    String token;
    String university;
    Boolean isNotifyFlag;
    Boolean isRecommendFlag;
    ArrayList<Post> postList= new ArrayList();
    ArrayList<String> subscriptionList = new ArrayList<>();
    ArrayList<Event> eventList= new ArrayList();

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<Event> eventList) {
        this.eventList = eventList;
    }

    public ArrayList<String> getSubscriptionList() {
        return subscriptionList;
    }

    public void setSubscriptionList(ArrayList<String> subscriptionList) {
        this.subscriptionList = subscriptionList;
    }







    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public ArrayList<Post> getPostList() {
        return postList;
    }

    public void setPostList(ArrayList<Post> postList) {
        this.postList = postList;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAuthToken(String authToken) {
        this.token = authToken;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }
}

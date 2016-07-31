package com.campustribune.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by aditi on 30/07/16.
 */
public class EventUser {

    private String userName;
    private List<UUID> followingEvents = new ArrayList<>();
    private List<UUID> upVotedEvents = new ArrayList<>();
    private List<UUID> downVotedEvents = new ArrayList<>();
    private List<UUID> reportedEvents = new ArrayList<>();
    private List<UUID> reportedComments = new ArrayList<>();
    private List<UUID> goingEvents = new ArrayList<>();
    private List<UUID> notgoingEvents = new ArrayList<>();
    private String lastUpdatedOn;

    public EventUser(){}

    public EventUser(String userName, List<UUID> followingEvents, List<UUID> upVotedEvents, List<UUID> downVotedEvents,
                     List<UUID> reportedEvents, List<UUID> reportedComments,
                     List<UUID> goingEvents, List<UUID> notgoingEvents, String lastUpdatedOn) {
        this.userName = userName;
        this.followingEvents = followingEvents;
        this.upVotedEvents = upVotedEvents;
        this.downVotedEvents = downVotedEvents;
        this.reportedEvents = reportedEvents;
        this.reportedComments = reportedComments;
        this.goingEvents = goingEvents;
        this.notgoingEvents = notgoingEvents;
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<UUID> getFollowingEvents() {
        return followingEvents;
    }

    public void setFollowingEvents(List<UUID> followingEvents) {
        this.followingEvents = followingEvents;
    }

    public List<UUID> getUpVotedEvents() {
        return upVotedEvents;
    }

    public void setUpVotedEvents(List<UUID> upVotedEvents) {
        this.upVotedEvents = upVotedEvents;
    }

    public List<UUID> getDownVotedEvents() {
        return downVotedEvents;
    }

    public void setDownVotedEvents(List<UUID> downVotedEvents) {
        this.downVotedEvents = downVotedEvents;
    }

    public List<UUID> getReportedEvents() {
        return reportedEvents;
    }

    public void setReportedEvents(List<UUID> reportedEvents) {
        this.reportedEvents = reportedEvents;
    }

    public List<UUID> getReportedComments() {
        return reportedComments;
    }

    public void setReportedComments(List<UUID> reportedComments) {
        this.reportedComments = reportedComments;
    }

    public List<UUID> getGoingEvents() {
        return goingEvents;
    }

    public void setGoingEvents(List<UUID> goingEvents) {
        this.goingEvents = goingEvents;
    }

    public List<UUID> getNotgoingEvents() {
        return notgoingEvents;
    }

    public void setNotgoingEvents(List<UUID> notgoingEvents) {
        this.notgoingEvents = notgoingEvents;
    }

    public String getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(String lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventUser that = (EventUser) o;

        if (userName != null ? !userName.equals(that.userName) : that.userName != null)
            return false;
        if (followingEvents != null ? !followingEvents.equals(that.followingEvents) : that.followingEvents != null)
            return false;
        if (upVotedEvents != null ? !upVotedEvents.equals(that.upVotedEvents) : that.upVotedEvents != null)
            return false;
        if (downVotedEvents != null ? !downVotedEvents.equals(that.downVotedEvents) : that.downVotedEvents != null)
            return false;
        if (reportedEvents != null ? !reportedEvents.equals(that.reportedEvents) : that.reportedEvents != null)
            return false;
        if (reportedComments != null ? !reportedComments.equals(that.reportedComments) : that.reportedComments != null)
            return false;
        if (goingEvents != null ? !goingEvents.equals(that.goingEvents) : that.goingEvents != null)
            return false;
        if (notgoingEvents != null ? !notgoingEvents.equals(that.notgoingEvents) : that.notgoingEvents != null)
            return false;
        return !(lastUpdatedOn != null ? !lastUpdatedOn.equals(that.lastUpdatedOn) : that.lastUpdatedOn != null);

    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (followingEvents != null ? followingEvents.hashCode() : 0);
        result = 31 * result + (upVotedEvents != null ? upVotedEvents.hashCode() : 0);
        result = 31 * result + (downVotedEvents != null ? downVotedEvents.hashCode() : 0);
        result = 31 * result + (reportedEvents != null ? reportedEvents.hashCode() : 0);
        result = 31 * result + (reportedComments != null ? reportedComments.hashCode() : 0);
        result = 31 * result + (goingEvents != null ? goingEvents.hashCode() : 0);
        result = 31 * result + (notgoingEvents != null ? notgoingEvents.hashCode() : 0);
        result = 31 * result + (lastUpdatedOn != null ? lastUpdatedOn.hashCode() : 0);
        return result;
    }
}

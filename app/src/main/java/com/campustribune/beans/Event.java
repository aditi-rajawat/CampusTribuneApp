package com.campustribune.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by aditi on 08/07/16.
 */
public class Event implements Serializable{

    private UUID id;
    private String title;
    private String description;
    private String category;
    private String url;
    private String startDate;
    private String endDate;
    private double latitude;
    private double longitude;
    private String address;
    private String eventImageS3URL;
    private String university;

    // Event actions
    private boolean upvoted;
    private boolean downvoted;
    private boolean follow;
    private boolean going;
    private boolean notGoing;
    private boolean reported;

    private Integer upVoteCount = new Integer(0);
    private Integer downVoteCount = new Integer(0);
    private Integer goingCount = new Integer(0);
    private Integer notGoingCount = new Integer(0);
    private Integer followCount = new Integer(0);

    private boolean updateEvent;
    private boolean updateComments;
    private boolean deleteComments;

    private String createdBy;
    private String updatedBy;
    private String createdOn;

    // Event list of comments
    private ArrayList<EventComment> listOfComments = new ArrayList<>();
    private ArrayList<EventComment> listOfDeletedComments = new ArrayList<>();

    public Event() {
    }

    public Event(UUID id, String title, String description, String category, String url, String startDate, String endDate,
                 double latitude, double longitude, String address, String eventImageS3URL, String university, boolean upvoted, boolean downvoted,
                 boolean follow, boolean going, boolean notGoing, boolean reported, Integer upVoteCount, Integer downVoteCount,
                 Integer goingCount, Integer notGoingCount, Integer followCount, boolean updateEvent, boolean updateComments, boolean deleteComments,
                 String createdBy, String updatedBy, String createdOn, ArrayList<EventComment> listOfComments, ArrayList<EventComment> listOfDeletedComments) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.url = url;
        this.startDate = startDate;
        this.endDate = endDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.eventImageS3URL = eventImageS3URL;
        this.university = university;
        this.upvoted = upvoted;
        this.downvoted = downvoted;
        this.follow = follow;
        this.going = going;
        this.notGoing = notGoing;
        this.reported = reported;
        this.upVoteCount = upVoteCount;
        this.downVoteCount = downVoteCount;
        this.goingCount = goingCount;
        this.notGoingCount = notGoingCount;
        this.followCount = followCount;
        this.updateEvent = updateEvent;
        this.updateComments = updateComments;
        this.deleteComments = deleteComments;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdOn = createdOn;
        this.listOfComments = listOfComments;
        this.listOfDeletedComments = listOfDeletedComments;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isUpvoted() {
        return upvoted;
    }

    public void setUpvoted(boolean upvoted) {
        this.upvoted = upvoted;
    }

    public boolean isDownvoted() {
        return downvoted;
    }

    public void setDownvoted(boolean downvoted) {
        this.downvoted = downvoted;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public boolean isGoing() {
        return going;
    }

    public void setGoing(boolean going) {
        this.going = going;
    }

    public boolean isNotGoing() {
        return notGoing;
    }

    public void setNotGoing(boolean notGoing) {
        this.notGoing = notGoing;
    }

    public boolean isReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }

    public Integer getUpVoteCount() {
        return upVoteCount;
    }

    public void setUpVoteCount(Integer upVoteCount) {
        this.upVoteCount = upVoteCount;
    }

    public Integer getDownVoteCount() {
        return downVoteCount;
    }

    public void setDownVoteCount(Integer downVoteCount) {
        this.downVoteCount = downVoteCount;
    }

    public Integer getGoingCount() {
        return goingCount;
    }

    public void setGoingCount(Integer goingCount) {
        this.goingCount = goingCount;
    }

    public Integer getNotGoingCount() {
        return notGoingCount;
    }

    public void setNotGoingCount(Integer notGoingCount) {
        this.notGoingCount = notGoingCount;
    }

    public Integer getFollowCount() {
        return followCount;
    }

    public void setFollowCount(Integer followCount) {
        this.followCount = followCount;
    }

    public boolean isUpdateEvent() {
        return updateEvent;
    }

    public void setUpdateEvent(boolean updateEvent) {
        this.updateEvent = updateEvent;
    }

    public boolean isUpdateComments() {
        return updateComments;
    }

    public void setUpdateComments(boolean updateComments) {
        this.updateComments = updateComments;
    }

    public boolean isDeleteComments() {
        return deleteComments;
    }

    public void setDeleteComments(boolean deleteComments) {
        this.deleteComments = deleteComments;
    }

    public String getEventImageS3URL() {
        return eventImageS3URL;
    }

    public void setEventImageS3URL(String eventImageS3URL) {
        this.eventImageS3URL = eventImageS3URL;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public ArrayList<EventComment> getListOfComments() {
        return listOfComments;
    }

    public void setListOfComments(ArrayList<EventComment> listOfComments) {
        this.listOfComments = listOfComments;
    }

    public ArrayList<EventComment> getListOfDeletedComments() {
        return listOfDeletedComments;
    }

    public void setListOfDeletedComments(ArrayList<EventComment> listOfDeletedComments) {
        this.listOfDeletedComments = listOfDeletedComments;
    }

    public void addComment(EventComment comment){
        this.listOfComments.add(comment);
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (Double.compare(event.latitude, latitude) != 0) return false;
        if (Double.compare(event.longitude, longitude) != 0) return false;
        if (upvoted != event.upvoted) return false;
        if (downvoted != event.downvoted) return false;
        if (follow != event.follow) return false;
        if (going != event.going) return false;
        if (notGoing != event.notGoing) return false;
        if (reported != event.reported) return false;
        if (updateEvent != event.updateEvent) return false;
        if (updateComments != event.updateComments) return false;
        if (deleteComments != event.deleteComments) return false;
        if (id != null ? !id.equals(event.id) : event.id != null) return false;
        if (title != null ? !title.equals(event.title) : event.title != null) return false;
        if (description != null ? !description.equals(event.description) : event.description != null)
            return false;
        if (category != null ? !category.equals(event.category) : event.category != null)
            return false;
        if (url != null ? !url.equals(event.url) : event.url != null) return false;
        if (startDate != null ? !startDate.equals(event.startDate) : event.startDate != null)
            return false;
        if (endDate != null ? !endDate.equals(event.endDate) : event.endDate != null) return false;
        if (address != null ? !address.equals(event.address) : event.address != null) return false;
        if (eventImageS3URL != null ? !eventImageS3URL.equals(event.eventImageS3URL) : event.eventImageS3URL != null)
            return false;
        if (university != null ? !university.equals(event.university) : event.university != null)
            return false;
        if (upVoteCount != null ? !upVoteCount.equals(event.upVoteCount) : event.upVoteCount != null)
            return false;
        if (downVoteCount != null ? !downVoteCount.equals(event.downVoteCount) : event.downVoteCount != null)
            return false;
        if (goingCount != null ? !goingCount.equals(event.goingCount) : event.goingCount != null)
            return false;
        if (notGoingCount != null ? !notGoingCount.equals(event.notGoingCount) : event.notGoingCount != null)
            return false;
        if (followCount != null ? !followCount.equals(event.followCount) : event.followCount != null)
            return false;
        if (createdBy != null ? !createdBy.equals(event.createdBy) : event.createdBy != null)
            return false;
        if (updatedBy != null ? !updatedBy.equals(event.updatedBy) : event.updatedBy != null)
            return false;
        if (createdOn != null ? !createdOn.equals(event.createdOn) : event.createdOn != null)
            return false;
        if (listOfComments != null ? !listOfComments.equals(event.listOfComments) : event.listOfComments != null)
            return false;
        return !(listOfDeletedComments != null ? !listOfDeletedComments.equals(event.listOfDeletedComments) : event.listOfDeletedComments != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (eventImageS3URL != null ? eventImageS3URL.hashCode() : 0);
        result = 31 * result + (university != null ? university.hashCode() : 0);
        result = 31 * result + (upvoted ? 1 : 0);
        result = 31 * result + (downvoted ? 1 : 0);
        result = 31 * result + (follow ? 1 : 0);
        result = 31 * result + (going ? 1 : 0);
        result = 31 * result + (notGoing ? 1 : 0);
        result = 31 * result + (reported ? 1 : 0);
        result = 31 * result + (upVoteCount != null ? upVoteCount.hashCode() : 0);
        result = 31 * result + (downVoteCount != null ? downVoteCount.hashCode() : 0);
        result = 31 * result + (goingCount != null ? goingCount.hashCode() : 0);
        result = 31 * result + (notGoingCount != null ? notGoingCount.hashCode() : 0);
        result = 31 * result + (followCount != null ? followCount.hashCode() : 0);
        result = 31 * result + (updateEvent ? 1 : 0);
        result = 31 * result + (updateComments ? 1 : 0);
        result = 31 * result + (deleteComments ? 1 : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (updatedBy != null ? updatedBy.hashCode() : 0);
        result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
        result = 31 * result + (listOfComments != null ? listOfComments.hashCode() : 0);
        result = 31 * result + (listOfDeletedComments != null ? listOfDeletedComments.hashCode() : 0);
        return result;
    }
}

package com.campustribune.beans;

/**
 * Created by snshr on 7/11/2016.
 */
public class PostComment {
    private int id;
    private int postId;
    private String commentContent;
    private String userId;
    private int reportScore;
    private String createdOn;
    private String lastEditedOn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getReportScore() {
        return reportScore;
    }

    public void setReportScore(int reportScore) {
        this.reportScore = reportScore;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getLastEditedOn() {
        return lastEditedOn;
    }

    public void setLastEditedOn(String lastEditedOn) {
        this.lastEditedOn = lastEditedOn;
    }
}

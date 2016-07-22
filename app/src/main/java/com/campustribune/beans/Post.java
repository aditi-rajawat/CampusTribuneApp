package com.campustribune.beans;

/**
 * Created by snshr on 7/11/2016.
 */
public class Post {
    private int id;
    private String headline;
    private String content;
    private String userId;
    private int voteScore;
    private boolean isAlert;
    private String category;
    private String webLink;
    private String imgURL;
    private int reportScore;
    private String createdOn;
    private String lastEditedOn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getVoteScore() {
        return voteScore;
    }

    public void setVoteScore(int voteScore) {
        this.voteScore = voteScore;
    }

    public boolean isAlert() {
        return isAlert;
    }

    public void setIsAlert(boolean isAlert) {
        this.isAlert = isAlert;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
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

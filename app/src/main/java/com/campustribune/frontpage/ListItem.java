package com.campustribune.frontpage;

import android.graphics.Bitmap;

/**
 * Created by sandyarathidas on 7/15/16.
 */
public class ListItem {

    private String headline;
    private String content;
    private int userId;
    private String image;
    private Bitmap bitMapImage;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Bitmap getBitMapImage() {
        return bitMapImage;
    }

    public void setBitMapImage(Bitmap bitMapImage) {
        this.bitMapImage = bitMapImage;
    }

    /* private int voteScore;
    private boolean isAlert;
    private String category;
    private String webLink;
    private int reportScore;
    private String createdOn;
    private String lastEditedOn;*/









}

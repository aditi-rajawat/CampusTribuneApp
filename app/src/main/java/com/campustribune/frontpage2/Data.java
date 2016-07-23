package com.campustribune.frontpage2;

/**
 * Created by sandyarathidas on 7/18/16.
 */
public class Data {
    public String title;
    public String description;
    public int imageId;

    Data(String title, String description, int imageId) {
        this.title = title;
        this.description = description;
        this.imageId = imageId;
    }

    Data(String title, String description){
        this.title = title;
        this.description = description;
    }

}
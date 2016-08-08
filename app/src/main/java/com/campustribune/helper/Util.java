package com.campustribune.helper;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

public class Util {

    //public static final String SERVER_URL="http://52.53.194.209:8080/";
    public static final String SERVER_URL="http://10.0.2.2:8080/";


    public static final String MY_ACCESS_KEY_ID = "";
    public static final String MY_SECRET_KEY = "";
    public static final String BUCKET = "";

    private static AmazonS3Client s3;



    public static AmazonS3Client getS3Client() {
        if (s3 == null) {
            s3 = new AmazonS3Client(new BasicAWSCredentials(MY_ACCESS_KEY_ID, MY_SECRET_KEY));


        }
        return s3;
    }

}

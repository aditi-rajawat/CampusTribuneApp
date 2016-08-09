package com.campustribune.helper;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

public class Util {
    /* EC2 public url*/
    public static final String SERVER_URL="http://52.53.194.209:8080/";
    /* local url*/
    //public static final String SERVER_URL="http://10.0.0.189:8080/";


    public static final String MY_ACCESS_KEY_ID = "ADD S3 ACCESS KEY ID HERE";
    public static final String MY_SECRET_KEY = "ADD S3 SECRET KEY HERE";
    public static final String BUCKET = "ADD BUCKET NAME HERE";

    private static AmazonS3Client s3;



    public static AmazonS3Client getS3Client() {
        if (s3 == null) {
            s3 = new AmazonS3Client(new BasicAWSCredentials(MY_ACCESS_KEY_ID, MY_SECRET_KEY));


        }
        return s3;
    }

}

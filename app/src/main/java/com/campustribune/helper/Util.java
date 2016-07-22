package com.campustribune.helper;

import android.content.Context;
import android.net.Uri;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

public class Util {

    public static final String MY_ACCESS_KEY_ID = "AKIAIPPAFYVI3NYPMHAA";
    public static final String MY_SECRET_KEY = "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";
    public static final String BUCKET = "ctpost";

    private static AmazonS3Client s3;



    public static AmazonS3Client getS3Client() {
        if (s3 == null) {
            s3 = new AmazonS3Client(new BasicAWSCredentials(MY_ACCESS_KEY_ID, MY_SECRET_KEY));


        }
        return s3;
    }

}

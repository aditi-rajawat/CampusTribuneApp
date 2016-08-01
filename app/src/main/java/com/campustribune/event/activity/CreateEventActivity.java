package com.campustribune.event.activity;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.services.s3.AmazonS3Client;
import com.campustribune.BaseActivity;
import com.campustribune.R;
import com.campustribune.beans.Event;
import com.campustribune.event.fragment.CreateEventMoreDetails;
import com.campustribune.event.fragment.CreateEventTitleNDesc;
import com.campustribune.event.fragment.DatePickerFragment;
import com.campustribune.event.fragment.TimePickerFragment;
import com.campustribune.event.utility.Constants;
import com.campustribune.helper.ImageUploader;
import com.campustribune.helper.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;


public class CreateEventActivity extends BaseActivity implements CreateEventTitleNDesc.NextButtonListener,
        CreateEventMoreDetails.EventTitleListener, CreateEventMoreDetails.AllButtonsListener, GoogleApiClient.OnConnectionFailedListener,
        AdapterView.OnItemSelectedListener{

    ProgressDialog progressDialog=null;
    String eventTitle;
    String currentImagePath;
    GoogleApiClient myGoogleAPIClient;
    static LatLng currentLocation = null;
    String category = new String();
    Event event = new Event();
    AmazonS3Client myS3Client=null;
    Uri selectedImgUri=null;
    String userId = null;
    String university=null;

    final static int PLACE_PICKER_REQUEST=1;
    final static int CAPTURE_IMAGE_REQUEST=2;
    final static int PICK_PHOTO_FROM_GALLERY_REQUEST=3;
    final static int LOCATION_PERMISSION_REQUEST=4;
    final static int WRITE_TO_STORAGE_PERMISSION_REQUEST=5;


    final static LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Disable create event menu
        invalidateOptionsMenu();

        // Retrieve the user Id
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.userId = new String(settings.getString("loggedInUserId", "").toString());
        this.university = new String(settings.getString("loggedInUserUniversity","").toString());

        //Intialize the GoogleAPIClient
        myGoogleAPIClient = new GoogleApiClient.Builder(this)
                .addApi(Places.PLACE_DETECTION_API).enableAutoManage(this, this).build();

        //Intialize AWS S3 Client
        myS3Client = Util.getS3Client();

        //Set Layout
        setContentView(R.layout.create_event_main);

        if(findViewById(R.id.fragment_container)!=null){

            if(savedInstanceState!=null)
                return;

            Fragment fragment_title_n_desc = new CreateEventTitleNDesc();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.add(R.id.fragment_container, fragment_title_n_desc, "create_event_first_page");
            transaction.addToBackStack(null);
            transaction.commit();

        }

        //Intialize progress dialog
        this.progressDialog = new ProgressDialog(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_frontpage, menu);
        MenuItem createevent = (MenuItem)menu.findItem(R.id.submenu_createevent);
        createevent.setVisible(false);
        MenuItem ref = (MenuItem) menu.findItem(R.id.action_refresh);
        ref.setVisible(false);
        return true;
    }

    /**
     * @param view
     * Method to navigate to the next fragment
     */
    @Override
    public void goToEventMoreDetails(View view) {

        ViewGroup viewGroup = (ViewGroup) view.getRootView();
        EditText editText = (EditText)viewGroup.findViewById(R.id.edit_event_title);
        this.eventTitle = editText.getText().toString();

        if(validateEventFirstPage(viewGroup) && findViewById(R.id.fragment_container)!=null){

            Fragment fragment_more_details = new CreateEventMoreDetails();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment_more_details, "create_event_second_page");
            transaction.addToBackStack(null);

            transaction.commit();

        }
    }

    private boolean validateEventFirstPage(ViewGroup view){

        CharSequence text = null;

        String title = new String();
        text = ((TextView)view.findViewById(R.id.edit_event_title)).getText();
        if(text != null)
            title = text.toString();

        String description = new String();
        text = ((TextView)view.findViewById(R.id.edit_event_desc)).getText();
        if(text!=null)
            description = text.toString();

        String url = new String();
        text = ((TextView)view.findViewById(R.id.edit_event_url)).getText();
        if(text!=null)
            url = text.toString();

        boolean isValid = true;

        if(title.isEmpty() || title.length()>Constants.MAX_LENGTH_EVENT_TITLE) {
            Toast.makeText(this, "Event title should not be empty and have max length as " + Constants.MAX_LENGTH_EVENT_TITLE, Toast.LENGTH_LONG).show();
            isValid = false;
        }
        else if(description.isEmpty() || description.length()>Constants.MAX_LENGTH_EVENT_DESC){
            Toast.makeText(this, "Event description should not be empty and have max length as " + Constants.MAX_LENGTH_EVENT_DESC, Toast.LENGTH_LONG).show();
            isValid = false;
        }
        else if(category.isEmpty()){
            Toast.makeText(this, "Select a category", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        else if(!url.isEmpty() && url.length()>=8) {
            if ((!url.substring(0, 7).equals("http://")) && (!url.substring(0, 8).equals("https://")))
            {
                Toast.makeText(this, "Enter a valid url starting with http:// or https://", Toast.LENGTH_LONG).show();
                isValid = false;
            }
        }
        else if(!url.isEmpty() && url.length()<8){
            Toast.makeText(this, "Enter a valid url starting with http:// or https://", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if(isValid){
            event.setTitle(title);
            event.setDescription(description);
            event.setCategory(category);
            event.setUrl(url);
        }

        return isValid;
    }

    public void datePickerListener(View view){
        if(findViewById(R.id.fragment_container)!=null){

            DatePickerFragment fragment_date_picker = new DatePickerFragment();
            Bundle args = new Bundle();
            args.putInt("clicked_date_button", view.getId());
            fragment_date_picker.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            fragment_date_picker.show(transaction, "datePicker");
        }
    }

    public void timePickerListener(View view){
        if(findViewById(R.id.fragment_container)!=null){

            TimePickerFragment fragment_time_picker = new TimePickerFragment();
            Bundle args = new Bundle();
            args.putInt("clicked_time_button", view.getId());
            fragment_time_picker.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            fragment_time_picker.show(transaction, "timePicker");

        }
    }

    public void placePickerListener(View view){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    CreateEventActivity.LOCATION_PERMISSION_REQUEST);
        }
        else{
           // getCurrentLocation();
            showPlacePicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == CreateEventActivity.LOCATION_PERMISSION_REQUEST){
            if(grantResults.length>0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //getCurrentLocation();
                showPlacePicker();
            }
        }
        else if(requestCode == CreateEventActivity.WRITE_TO_STORAGE_PERMISSION_REQUEST){
            if(grantResults.length>0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                pickPhotoAction();
            }
        }
    }

    private void getCurrentLocation(){
        try {

            progressDialog.show();
            progressDialog.setMessage("Fetching current location..");
            PendingResult<PlaceLikelihoodBuffer> latestLocations = Places.PlaceDetectionApi.getCurrentPlace(myGoogleAPIClient, null);

            float maxLikelihood = 0;
            Place currentLoc = null;

            for (PlaceLikelihood each : latestLocations.await()) {
                if (each.getLikelihood() > maxLikelihood) {
                    maxLikelihood = each.getLikelihood();
                    currentLoc = each.getPlace();
                }
            }

            if(currentLoc != null)
                CreateEventActivity.currentLocation = currentLoc.getLatLng();

            progressDialog.hide();

        }catch (SecurityException ex){
            System.out.println("Application is facing security issues due to "+ ex.getMessage());
        }
    }

    private void showPlacePicker(){

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        builder.setLatLngBounds(CreateEventActivity.BOUNDS_MOUNTAIN_VIEW);
        try {
            startActivityForResult(builder.build(this), CreateEventActivity.PLACE_PICKER_REQUEST);
        }catch(GooglePlayServicesNotAvailableException ex){

        }catch (GooglePlayServicesRepairableException ex){

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void takePhotoListener(View view) {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CreateEventActivity.WRITE_TO_STORAGE_PERMISSION_REQUEST);
        }
        else
            startTakePicFromCameraIntent();
    }

    private void startTakePicFromCameraIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            File imageFile = null;
            try{
                imageFile = createFileForImage();

            }catch (IOException ex){
                System.out.println("Exception : "+ ex.getMessage());
            }

            if(imageFile!=null){
                //Uri imageURI = Uri.fromFile(imageFile);
                try {
                    Uri imageURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", imageFile);
                    System.out.println("URI FROM FILE ---------> " + imageURI);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                    startActivityForResult(takePictureIntent, CreateEventActivity.CAPTURE_IMAGE_REQUEST);
                }catch (Exception ex){
                    System.out.println("ERROR DUE TO FILE PROVIDER IS "+ ex.getMessage());
                }
            }
        }
    }

    private File createFileForImage() throws IOException{

        StringBuilder imageFileName = new StringBuilder();
        imageFileName.append("IMG_");
        imageFileName.append(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //File imageFile = File.createTempFile(imageFileName.toString(), ".jpg", storageDir);
        File imageFile = new File(storageDir, imageFileName.toString()+".jpg");
        this.currentImagePath = imageFile.getAbsolutePath();
        System.out.println("Current File Path === " + this.currentImagePath);
        return imageFile;
    }

    private void saveImageToGallery(){

        Intent imageScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File image = new File(this.currentImagePath);
        Uri imageUri = Uri.fromFile(image);
        imageScanIntent.setData(imageUri);
        this.sendBroadcast(imageScanIntent);

    }

    @Override
    public void pickPhotoFromGalleryListener(View view) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CreateEventActivity.WRITE_TO_STORAGE_PERMISSION_REQUEST);
        }
        else
            pickPhotoAction();

    }

    private void pickPhotoAction(){
        Intent pickPhotoIntent = new Intent();
        pickPhotoIntent.setType("image/*");
        pickPhotoIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pickPhotoIntent, "Select picture"),
                CreateEventActivity.PICK_PHOTO_FROM_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Fragment fragment_create_event_details = getSupportFragmentManager().findFragmentByTag("create_event_second_page");
        View rootView = fragment_create_event_details.getView();
        ImageView imageView = (ImageView)rootView.findViewById(R.id.event_image);
        TextView location = (TextView)rootView.findViewById(R.id.chosen_event_location);

        if(requestCode == CreateEventActivity.CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK){
            // Save image to the photo gallery first
            saveImageToGallery();

            // Set image as the source of the image view of the layout

            try {
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap)extras.get("data");
                imageView.setImageBitmap(bitmap);
            }catch (Exception ex){
                System.out.println("Unable to save the image on the layout due to "+ ex.getMessage());
            }

        }
        else if(requestCode == CreateEventActivity.PICK_PHOTO_FROM_GALLERY_REQUEST && resultCode == RESULT_OK){
            try {
                selectedImgUri = data.getData();
                System.out.println("URI of selected image is  ---------> "+ selectedImgUri);
                imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImgUri));
            }catch (Exception ex){
                System.out.println("I am having this issue ----->  "+ ex.getMessage());
            }
        }
        else if(requestCode == CreateEventActivity.PLACE_PICKER_REQUEST && resultCode == RESULT_OK){
            Place place = PlacePicker.getPlace(data, this);
            System.out.println("Selected place is ========= " + place.getName());
            location.setText(place.getAddress());

            // Set address in Event type object
            event.setAddress(place.getAddress().toString());
            event.setLatitude(place.getLatLng().latitude);
            event.setLongitude(place.getLatLng().longitude);
        }
    }

    @Override
    public void previousPageListener(View view) {
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void createEventListener(View view) {
        View rootView = view.getRootView();
        String startDate, endDate, startTime, endTime;

        if(validateEventSecondPage(rootView)) {
            if(this.progressDialog!=null) {
                this.progressDialog.setMessage("Creating event..");
                this.progressDialog.show();
            }

            startDate = ((TextView) rootView.findViewById(R.id.chosen_start_date)).getText().toString();
            startTime = ((TextView) rootView.findViewById(R.id.chosen_start_time)).getText().toString();
            event.setStartDate(convertToDateTime(startDate, startTime));

            endDate = ((TextView) rootView.findViewById(R.id.chosen_end_date)).getText().toString();
            endTime = ((TextView) rootView.findViewById(R.id.chosen_end_time)).getText().toString();
            event.setEndDate(convertToDateTime(endDate, endTime));

            event.setCreatedBy(this.userId);
            DateTime dt = new DateTime(DateTimeZone.UTC);
            event.setCreatedOn(dt.toString(ISODateTimeFormat.dateTime().withZoneUTC()));
            event.setUniversity(this.university);

            if(this.selectedImgUri!=null) {
               try {
                    ImageUploader imageUploader = new ImageUploader(this.selectedImgUri, getApplicationContext(), getContentResolver());
                    imageUploader.beginUpload(imageUploader.getPath());

                    while (!imageUploader.isUploadComplete()) {
                        Thread.sleep(1000);
                    }
                    event.setEventImageS3URL(imageUploader.getImageS3URL().toString());
                   //event.setEventImageS3URL("https://ctpost.s3.amazonaws.com/ebec3a89-3c40-48c9-8b30-d2559b56d125?response-content-type=image%2Fjpeg&AWSAccessKeyId=AKIAJFQUSKEWLKRMA2OQ&Expires=1481871600&Signature=kmAg%2B1wCh675YgGTEIuJyZJ01jE%3D");
                }catch (URISyntaxException ex){
                    System.out.println("URI of the image file uploaded is having incorrect syntax!");
                }catch (InterruptedException ex){
                    System.out.println("Thread could not sleep while uploading the image");
                }
            }

            Intent viewEventIntent = new Intent(CreateEventActivity.this, ViewEventActivity.class);
            viewEventIntent.putExtra("new_event", event);
            viewEventIntent.putExtra("prev_activity", new String("CreateEventActivity"));

            if(this.progressDialog!=null)
                this.progressDialog.hide();

            CreateEventActivity.this.startActivity(viewEventIntent);
            CreateEventActivity.this.finish();
        }

    }

    private static String convertToDateTime(String date, String time){
        String[] dateArr;
        String[] timeArr;
        DateTime dt = new DateTime(DateTimeZone.UTC);

        dateArr = date.split("/");
        timeArr = time.split(":");
        if(dateArr.length==3 && timeArr.length==2)
            dt = new DateTime(Integer.parseInt(dateArr[2]),Integer.parseInt(dateArr[0]),
                    Integer.parseInt(dateArr[1]), Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), 0, DateTimeZone.UTC);
        else
            System.out.println("Problem occured while parsing event date and time");

        System.out.println("Event Date Check Point -----------> "+ dt.toString(ISODateTimeFormat.dateTime().withZoneUTC()));
        return dt.toString(ISODateTimeFormat.dateTime().withZoneUTC());
    }

    private boolean validateEventSecondPage(View view){
        TextView location = (TextView)view.findViewById(R.id.chosen_event_location);
        if(location.getText().toString().contains("No location selected")){
            Toast.makeText(this,"Select a location", Toast.LENGTH_LONG).show();
            return false;
        }
        else
            return true;
    }

    @Override
    public String getEventTitle() {
        return eventTitle;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(!parent.getItemAtPosition(position).toString().contains("-Select Category-")) {
            this.category = parent.getItemAtPosition(position).toString();
            System.out.println("Selected item is " + this.category);
        }
        else
            System.out.println("No category selected");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void uploadImageToS3(){

    }

}

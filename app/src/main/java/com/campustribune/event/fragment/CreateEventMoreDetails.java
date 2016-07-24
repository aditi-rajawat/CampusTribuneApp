package com.campustribune.event.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.campustribune.R;

import java.util.Calendar;

/**
 * Created by aditi on 24/06/16.
 */
public class CreateEventMoreDetails extends Fragment {

    EventTitleListener myEventTitleListener;
    AllButtonsListener myAllButtonsListener;

    public interface EventTitleListener{
        public String getEventTitle();
    }

    public interface AllButtonsListener{
        void datePickerListener(View view);
        void timePickerListener(View view);
        void placePickerListener(View view);
        void takePhotoListener(View view);
        void pickPhotoFromGalleryListener(View view);
        void previousPageListener(View view);
        void createEventListener(View view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_event_more_details, container, false);

        TextView eventTitleEntered = (TextView) view.findViewById(R.id.entered_event_title);
        if(myEventTitleListener.getEventTitle()!=null) {
            eventTitleEntered.setText(myEventTitleListener.getEventTitle());
        }
        else{
            eventTitleEntered.setText(R.string.default_entered_title);
        }

        Calendar today = Calendar.getInstance();
        int hour = today.get(Calendar.HOUR_OF_DAY);
        int minute = today.get(Calendar.MINUTE);
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH)+1;
        int day = today.get(Calendar.DAY_OF_MONTH);

        StringBuilder date = new StringBuilder();
        StringBuilder time = new StringBuilder();

        date.append(month+"/"+day+"/"+year);
        if(hour<10)
            time.append("0"+hour);
        else
            time.append(hour);

        time.append(":");

        if(minute<10)
            time.append("0"+minute);
        else
            time.append(minute);

        TextView defaultStartDate = (TextView) view.findViewById(R.id.chosen_start_date);
        defaultStartDate.setText(date.toString());

        TextView defaultEndDate = (TextView) view.findViewById(R.id.chosen_end_date);
        defaultEndDate.setText(date.toString());

        TextView defaultStartTime = (TextView) view.findViewById(R.id.chosen_start_time);
        defaultStartTime.setText(time.toString());

        TextView defaultEndTime = (TextView) view.findViewById(R.id.chosen_end_time);
        defaultEndTime.setText(time.toString());

        Button startDate = (Button) view.findViewById(R.id.pick_event_start_date);
        Button endDate = (Button) view.findViewById(R.id.pick_event_end_date);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDatePickerListener(view);
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDatePickerListener(view);
            }
        });

        Button startTime = (Button) view.findViewById(R.id.pick_event_start_time);
        Button endTime = (Button) view.findViewById(R.id.pick_event_end_time);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTimePickerListener(view);
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTimePickerListener(view);
            }
        });

        Button pickLocation = (Button) view.findViewById(R.id.pick_event_location);
        pickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlacePickerListener(view);
            }
        });

        TextView takePhoto = (TextView)view.findViewById(R.id.capture_image);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTakePhotoListener(view);
            }
        });

        TextView addPhoto = (TextView)view.findViewById(R.id.add_image);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPickPhotoListener(view);
            }
        });

        Button previous = (Button)view.findViewById(R.id.previous_button_eventsmoredetails);
        previous.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                addPreviousButtonListener(view);
            }
        });

        Button create = (Button)view.findViewById(R.id.create_button_eventsmoredetails);
        create.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                addCreateButtonListener(view);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            myEventTitleListener = (EventTitleListener) activity;
            myAllButtonsListener = (AllButtonsListener) activity;
        }catch(ClassCastException ex){
            throw new ClassCastException(activity.toString()
            + " must implement EventTitleListener and AllButtonsListener");
        }
    }

    public void addDatePickerListener(View view){
        this.myAllButtonsListener.datePickerListener(view);
    }

    public void addTimePickerListener(View view){
        this.myAllButtonsListener.timePickerListener(view);
    }

    public void addPlacePickerListener(View view){
        this.myAllButtonsListener.placePickerListener(view);
    }

    public void addTakePhotoListener(View view){
        this.myAllButtonsListener.takePhotoListener(view);
    }

    public void addPickPhotoListener(View view){
        this.myAllButtonsListener.pickPhotoFromGalleryListener(view);
    }

    public void addPreviousButtonListener(View view){
        this.myAllButtonsListener.previousPageListener(view);
    }

    public void addCreateButtonListener(View view){
        this.myAllButtonsListener.createEventListener(view);
    }

}

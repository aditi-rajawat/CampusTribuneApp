package com.campustribune.event.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.campustribune.R;

import java.util.Calendar;

/**
 * Created by aditi on 24/06/16.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    private FragmentManager fragmentManager=null;
    private int parentButtonId=0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if(this.getArguments() != null)
            this.parentButtonId = (int)this.getArguments().get("clicked_time_button");

        final Calendar today = Calendar.getInstance();
        int hour = today.get(Calendar.HOUR_OF_DAY);
        int minute = today.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.fragmentManager = ((FragmentActivity)activity).getSupportFragmentManager();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        StringBuilder pickedTime = new StringBuilder(3);

        if(hour<10)
            pickedTime.append("0"+hour);
        else
            pickedTime.append(hour);

        pickedTime.append(":");

        if(minute<10)
            pickedTime.append("0"+minute);
        else
            pickedTime.append(minute);

        Fragment parentFragment = this.fragmentManager.findFragmentByTag("create_event_second_page");
        View rootView = parentFragment.getView();
        TextView displayTime;

        if(this.parentButtonId == R.id.pick_event_start_time)
            displayTime = (TextView) rootView.findViewById(R.id.chosen_start_time);
        else
            displayTime = (TextView) rootView.findViewById(R.id.chosen_end_time);

        displayTime.setText(pickedTime.toString());


    }

}

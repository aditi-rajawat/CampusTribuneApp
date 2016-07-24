package com.campustribune.event.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.campustribune.R;

import java.util.Calendar;

/**
 * Created by aditi on 02/07/16.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private FragmentManager fragmentManager=null;
    private int parentButton=0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if(this.getArguments()!=null)
            this.parentButton = (int)this.getArguments().get("clicked_date_button");

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.fragmentManager = ((FragmentActivity)activity).getSupportFragmentManager();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        StringBuilder pickedDate = new StringBuilder(5);
        pickedDate.append(month+1);
        pickedDate.append("/");
        pickedDate.append(day);
        pickedDate.append("/");
        pickedDate.append(year);

        Fragment fragment = this.fragmentManager.findFragmentByTag("create_event_second_page");
        View rootView = fragment.getView();
        TextView displayText;

        if(this.parentButton == R.id.pick_event_start_date)
            displayText = (TextView) rootView.findViewById(R.id.chosen_start_date);
        else
            displayText = (TextView) rootView.findViewById(R.id.chosen_end_date);

        displayText.setText(pickedDate.toString());
    }
}

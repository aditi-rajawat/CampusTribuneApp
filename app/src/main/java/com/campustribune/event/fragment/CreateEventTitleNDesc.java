package com.campustribune.event.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.campustribune.R;

/**
 * Created by aditi on 17/06/16.
 */
public class CreateEventTitleNDesc extends Fragment {

    public interface NextButtonListener{
        void goToEventMoreDetails(View view);
    }

    private NextButtonListener nextButtonListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_event_title_n_desc, container, false);

        Spinner spinner = (Spinner)view.findViewById(R.id.edit_event_categories);
        String[] categories = getResources().getStringArray(R.array.categories);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, categories){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView)v).setTextSize(18);
                ((TextView)v).setTextColor(getResources().getColor(R.color.editTextHintColor));
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener)getActivity());

        Button nextButton = (Button) view.findViewById(R.id.edit_event_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoNextFragment(view);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            this.nextButtonListener = (NextButtonListener) activity;
        }catch (ClassCastException ex){
            throw new ClassCastException(activity.toString()+" must implement NextButtonListener");
        }
    }

    public void gotoNextFragment(View view){
        if(this.nextButtonListener!=null)
            this.nextButtonListener.goToEventMoreDetails(view);
    }


}

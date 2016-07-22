package com.campustribune.post.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.campustribune.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by snshr on 6/30/2016.
 */
public class ViewCommentFragment extends Fragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view =  inflater.inflate(R.layout.fragment_view_comment,
                container, false);
        view.setTag("show_comment");
        return view;
    }


}


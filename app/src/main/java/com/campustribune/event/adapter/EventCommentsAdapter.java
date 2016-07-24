package com.campustribune.event.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.campustribune.R;
import com.campustribune.beans.EventComment;

import java.util.ArrayList;

/**
 * Created by aditi on 16/07/16.
 */
public class EventCommentsAdapter extends ArrayAdapter<EventComment> {

    public interface CommentsUpdator{
        void updateEventComment(EventComment oldComment, EventComment newComment);
        void deleteEventComment(EventComment comment);
    }

    private Context ctx;
    private View convertView;
    private View parentActivityView;
    final private CommentsUpdator myUpdator;

    public EventCommentsAdapter(Context ctx, ArrayList<EventComment> comments, View parentActivityView,
                CommentsUpdator myUpdator){
        super(ctx, 0, comments);
        this.ctx = ctx;
        this.parentActivityView = parentActivityView;
        this.myUpdator = myUpdator;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final EventComment item = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.each_event_comment, parent, false);
        }

        this.convertView = convertView;

        TextView commentStr = (TextView)convertView.findViewById(R.id.each_event_comment_string);
        TextView commentCreator = (TextView)convertView.findViewById(R.id.each_event_comment_createdBy);

        commentStr.setText(item.getComment());
        commentCreator.setText("Added By "+ item.getCreatedBy());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final PopupWindow popupWindow = new PopupWindow(EventCommentsAdapter.this.ctx);
                    LayoutInflater inflater = (LayoutInflater) EventCommentsAdapter.this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View popupView = inflater.inflate(R.layout.edit_each_event_comment, null);
                    popupWindow.setContentView(popupView);
                    popupWindow.setFocusable(true);
                    popupWindow.setWidth(1000);
                    popupWindow.setHeight(500);
                    popupWindow.setBackgroundDrawable(EventCommentsAdapter.this.ctx.getDrawable(R.drawable.popupwindowbackground));
                    popupWindow.showAtLocation(EventCommentsAdapter.this.parentActivityView, Gravity.CENTER_HORIZONTAL, 0, 0);

                    EditText commentHint = (EditText)popupView.findViewById(R.id.edit_event_comment);
                    commentHint.setText(item.getComment());

                    Button save = (Button)popupView.findViewById(R.id.edit_event_comment_action);
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EditText newCommentString = (EditText)popupView.findViewById(R.id.edit_event_comment);
                            EventComment oldComment = item;
                            EventComment newComment = new EventComment(oldComment.getId(), oldComment.getEventId(), oldComment.getCreatedBy(), oldComment.getCreatedOn(),
                                    oldComment.getComment(), oldComment.getReportedBy());
                            newComment.setComment(newCommentString.getText().toString());
                            myUpdator.updateEventComment(oldComment, newComment);
                            popupWindow.dismiss();
                        }
                    });

                    Button delete = (Button)popupView.findViewById(R.id.delete_event_comment_action);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myUpdator.deleteEventComment(item);
                            popupWindow.dismiss();
                        }
                    });

                } catch (Exception ex) {
                    System.out.println("EDIT COMMENT EXCEPTION ----> " + ex.getMessage());
                }
            }
        });

        return convertView;
    }
}

package com.campustribune.frontpage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.campustribune.R;
import com.campustribune.beans.Post;

import java.util.Collections;
import java.util.List;

/**
 * Created by sandyarathidas on 7/18/16.
 */
public class Recycler_View_Adapter extends RecyclerView.Adapter<View_Holder> {



    private final OnItemClickListener listener;
    List<Data> list = Collections.emptyList();
    Context context;

    public interface OnItemClickListener {
        void onItemClick(Data data);
    }

    public Recycler_View_Adapter(List<Data> list, Context context, OnItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener=listener;
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_frontpage_row, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(View_Holder holder, int position) {

        holder.bind(list.get(position), listener);

    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void swap(List<Data> newlist){
        if (list != null) {
            list.clear();
            list.addAll(newlist);
        }
        else {
            list = newlist;
        }
        notifyDataSetChanged();
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, Data data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(Post data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }

    /*public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.anticipate_overshoot_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }*/

}
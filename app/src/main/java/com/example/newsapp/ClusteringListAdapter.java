package com.example.newsapp;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClusteringListAdapter extends RecyclerView.Adapter<ClusteringListAdapter.ClusteringListViewHolder> {
    List<EventClusterEntity> eventlist;
    Random rand = new Random();
    private static int[] pics = new int[]{
            R.drawable.random1,
            R.drawable.random2,
            R.drawable.random3,
            R.drawable.random4,
            R.drawable.random5,
            R.drawable.random6,
            R.drawable.random7};
    private static int length = pics.length;

    static public class ClusteringListViewHolder extends RecyclerView.ViewHolder {
        public CardView layout;
        public ClusteringListViewHolder(CardView v) {
            super(v);
            layout = v;
        }
    }
    ClusteringListAdapter() { eventlist = new ArrayList<>(); }
    ClusteringListAdapter(int type){
        eventlist=new ArrayList<>();
        eventlist.addAll(EventsClusterDataFetcher.fetchDataFromMem(type));
    }
    @NotNull
    @Override
    public ClusteringListAdapter.ClusteringListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_event_layout, parent, false);
        return new ClusteringListViewHolder(v);
    }
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ClusteringListViewHolder holder, int position) {
        Log.d("position", String.valueOf(position));
        TextView eventTextView = holder.layout.findViewById(R.id.event_title);
        TextView eventSourceView = holder.layout.findViewById(R.id.event_source);
        EventClusterEntity event = eventlist.get(position);
        String title = event.title;
        if(title.length() > 36) title = title.substring(0, 36) + "...";
        eventTextView.setText(title);
        eventSourceView.setText(event.date);
        ((ImageView)holder.layout.findViewById(R.id.event_img)).setImageResource(pics[rand.nextInt(length)]);
    }
    @Override
    public int getItemCount() { return eventlist.size(); }
}

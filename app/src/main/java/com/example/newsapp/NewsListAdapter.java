package com.example.newsapp;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsListViewHolder>{
    public List<NewsEntity> newslist;
    static public class NewsListViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout layout;
        public NewsListViewHolder(ConstraintLayout v) {
            super(v);
            layout = v;
        }
    }
    public NewsListAdapter() { newslist = new LinkedList<>(); }
    @Override
    public NewsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_news_layout, parent, false);
        NewsListViewHolder vh = new NewsListViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(NewsListViewHolder holder, int position) {
        ((TextView)holder.layout.findViewById(R.id.news_title)).setText(newslist.get(position).getmTitle());
        ((TextView)holder.layout.findViewById(R.id.news_source)).setText(newslist.get(position).getTime());
    }
    @Override
    public int getItemCount() { return newslist.size(); }
}

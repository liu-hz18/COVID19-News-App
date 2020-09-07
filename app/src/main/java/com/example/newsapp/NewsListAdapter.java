package com.example.newsapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsListViewHolder>{
    List<NewsEntity> newslist;
    static public class NewsListViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout layout;
        public NewsListViewHolder(ConstraintLayout v) {
            super(v);
            layout = v;
        }
    }
    NewsListAdapter() { newslist = new LinkedList<>(); }
    @Override
    public NewsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_news_layout, parent, false);
        NewsListViewHolder vh = new NewsListViewHolder(v);
        return vh;
    }
    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(NewsListViewHolder holder, int position) {
        final NewsEntity news = newslist.get(position);
        TextView newsTextView = holder.layout.findViewById(R.id.news_title);
        TextView newsSourceView = holder.layout.findViewById(R.id.news_source);
        newsTextView.setText(news.getmTitle());
        newsSourceView.setText(news.getTime() + " " + news.getmSource());
        if (news.viewed) {
            newsTextView.setTextColor(0xffaaaaaa);
        } else {
            newsTextView.setTextColor(R.color.titleItemUnselColor);
        }
        holder.layout.findViewById(R.id.news_title).setOnClickListener(v -> {
            Updater.logViewed(news);
            Bundle bundle = new Bundle();
            bundle.putSerializable("news", news);
            Navigation.findNavController(v).navigate(R.id.action_view_news_body, bundle);
        });
    }
    @Override
    public int getItemCount() { return newslist.size(); }
}

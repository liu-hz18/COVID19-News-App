package com.example.newsapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class NewsListAdapter extends XRecyclerView.Adapter<NewsListAdapter.NewsListViewHolder>{
    List<NewsEntity> newslist;
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

    static public class NewsListViewHolder extends XRecyclerView.ViewHolder {
        public View layout;
        public NewsListViewHolder(View v) {
            super(v);
            layout = v;
        }
    }
    NewsListAdapter() { newslist = new LinkedList<>(); }

    @NotNull
    @Override
    public NewsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_news_layout, parent, false);
        return new NewsListViewHolder(v);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(NewsListViewHolder holder, int position) {
        final NewsEntity news = newslist.get(position);
        TextView newsTextView = holder.layout.findViewById(R.id.news_title);
        TextView newsSourceView = holder.layout.findViewById(R.id.news_source);
        String title = news.getmTitle();
        if(title.length() > 40) title = title.substring(0, 40) + "...";
        newsTextView.setText(title);
        newsSourceView.setText(news.getTime() + " 来源:" + news.getmSource());
        if (news.viewed) {
            newsTextView.setTextColor(0xffaaaaaa);
        } else {
            newsTextView.setTextColor(R.color.titleItemUnselColor);
        }
        holder.layout.findViewById(R.id.news_title).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("news", news);
            Navigation.findNavController(v).navigate(R.id.action_view_news_body, bundle);
            Updater.logViewed(news);
        });
        ((ImageView)holder.layout.findViewById(R.id.news_img)).setImageResource(pics[rand.nextInt(length)]);
    }
    @Override
    public int getItemCount() { return newslist.size(); }
}

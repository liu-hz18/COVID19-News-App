package com.example.newsapp;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;


public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsListViewHolder>{
    private String[] data;
    static public class NewsListViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout layout;
        public NewsListViewHolder(ConstraintLayout v) {
            super(v);
            layout = v;
        }
    }
    public NewsListAdapter(String []_data) {
        data = _data;
    }
    @NotNull
    @Override
    public NewsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_news_layout, parent, false);
        return new NewsListViewHolder(v);
    }
    @Override
    public void onBindViewHolder(NewsListViewHolder holder, int position) {
        TextView textView = (TextView)holder.layout.findViewById(R.id.textView5);
        textView.setText(data[position]);
    }
    @Override
    public int getItemCount() {
        return data.length;
    }
}

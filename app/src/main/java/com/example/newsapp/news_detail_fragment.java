package com.example.newsapp;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
public class news_detail_fragment extends Fragment{
    public news_detail_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret_view = inflater.inflate(R.layout.news_detail_fragment, container, false);
        NewsEntity news = (NewsEntity) getArguments().getSerializable("news");
        ((TextView)ret_view.findViewById(R.id.news_body_title)).setText(news.getmTitle());
        ((TextView)ret_view.findViewById(R.id.news_body_content)).setText(news.getmContent());
        ((TextView)ret_view.findViewById(R.id.news_body_date)).setText(news.getTime());
        ((TextView)ret_view.findViewById(R.id.news_body_source)).setText(news.getmSource());
        return ret_view;
    }
}

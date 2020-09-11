package com.example.newsapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.jetbrains.annotations.NotNull;

public class main_clustering_fragment extends Fragment {
    public ClusteringListAdapter adapter;
    private int event_type = 0;
    public main_clustering_fragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret_view =  inflater.inflate(R.layout.clustering_main_fragment, container, false);
        XRecyclerView recyclerView = ret_view.findViewById(R.id.cluster_list_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager( getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.setPullRefreshEnabled(true);
        recyclerView.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
        recyclerView.getDefaultFootView().setLoadingDoneHint("加载完成");

        adapter = new ClusteringListAdapter(event_type);
        recyclerView.setAdapter(adapter);
        refresh_callback();

        ((TabLayout)ret_view.findViewById(R.id.cluster_tab_layout)).addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                event_type = tab.getPosition();
                if(event_type < 5) refresh_callback();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                event_type = tab.getPosition();
                if(event_type < 5) refresh_callback();
            }
        });

        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(recyclerView::refreshComplete, 500);
            }
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(recyclerView::loadMoreComplete, 500);
            }
        });
        return ret_view;
    }

    private void refresh_callback() {
        adapter.eventlist.clear();
        adapter.eventlist.addAll(EventsClusterDataFetcher.fetchDataFromMem(event_type));
        adapter.notifyDataSetChanged();
    }
}

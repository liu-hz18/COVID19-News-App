package com.example.newsapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;

import com.google.android.material.tabs.TabLayout;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class main_expert_fragment extends Fragment {
    class ExpertListUpdateHandler extends Handler {
        // 通过复写handlerMessage() 从而确定更新UI的操作
        @Override
        public void handleMessage(Message msg) {
            final int what = msg.what;
            if(what == ExpertsDataFetcher.UPDATE_EXPERTS){
                refresh_callback((List<ExpertEntity>) msg.obj);
            }
        }
    }

    public ExpertListAdapter adapter;
    private static boolean passed = false;
    public main_expert_fragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }


    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret_view =  inflater.inflate(R.layout.expert_main_fragment, container, false);
        XRecyclerView recyclerView = ret_view.findViewById(R.id.expert_list_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager( getActivity());
        recyclerView.setLayoutManager(layoutManager);

        Handler mhandler = new main_expert_fragment.ExpertListUpdateHandler();
        ExpertsDataFetcher.setHandler(mhandler);

        adapter = new ExpertListAdapter();
        recyclerView.setAdapter(adapter);
        ExpertsDataFetcher.fetchData(false, passed);

        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(recyclerView::refreshComplete, 400);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(recyclerView::refreshComplete, 400);
            }
        });

        TabLayout tablayout = (TabLayout)ret_view.findViewById(R.id.alive_tab_layout);
        if(passed)tablayout.getTabAt(1).select();
        else tablayout.getTabAt(0).select();
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    passed = false;
                    ExpertsDataFetcher.fetchData(false, false);
                } else {
                    passed = true;
                    ExpertsDataFetcher.fetchData(false, true);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    passed = false;
                    ExpertsDataFetcher.fetchData(false, false);
                } else {
                    passed = true;
                    ExpertsDataFetcher.fetchData(false, true);
                }
            }
        });
        return ret_view;
    }

    private void refresh_callback(List<ExpertEntity> expertList) {
        adapter.expertlist.clear();
        adapter.expertlist.addAll(expertList);
        adapter.notifyDataSetChanged();
    }
}

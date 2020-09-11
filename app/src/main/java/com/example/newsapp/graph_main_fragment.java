package com.example.newsapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public class graph_main_fragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "graph_main_fragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public graph_main_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment data_main_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static graph_main_fragment newInstance(String param1, String param2) {
        graph_main_fragment fragment = new graph_main_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private GraphListAdapter adapter;
    private ExpandableListView listView;
    private static Handler network_handler;
    private SearchView mSearchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        network_handler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
            @Override
            public void handleMessage(@NotNull Message msg) {
                if (msg.what == SearchEntityDataFetcher.UPDATE_GRAPH_ENTITY) {
                    // Log.d(TAG, "handleMessage: graph call complete");
                    adapter.group_list.clear();
                    List<SearchEntity> result = SearchEntityDataFetcher.getSearchResult();
                    if(result != null) adapter.group_list.addAll(result);
                    adapter.notifyDataSetChanged();
                }
            }
        };
        adapter = new GraphListAdapter(network_handler);
        SearchEntityDataFetcher.setHandler(network_handler);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret_view = inflater.inflate(R.layout.graph_main_fragment, container, false);

        mSearchView = ret_view.findViewById(R.id.search_graph_view);

        //mSearchView.setIconifiedByDefault(false);
        mSearchView.setQueryHint("Type and enjoy searching");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchEntityDataFetcher.fetchSearchEntities(query);
                adapter.notifyDataSetChanged();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //adapter.getFilter().filter(newText);
                return false;
            }
        });

        listView = ret_view.findViewById(R.id.expandable_graph_list);
        listView.setAdapter(adapter);
        return ret_view;
    }

    @Override
    public void onClick(View view) {
        //Log.d(TAG, "onClick: button");
    }
}


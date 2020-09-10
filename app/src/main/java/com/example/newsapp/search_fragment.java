package com.example.newsapp;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class search_fragment extends Fragment {

    private static LinkedHashSet<String> searchHistory = new LinkedHashSet<>();
    private static String historyPath = BaseDataFetcher.savePath + "search.log";
    private SearchView main_searchview;
    private ListView main_listview;

    public search_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate", "load history");
        try {
            searchHistory = (LinkedHashSet<String>) SerializeUtils.read(historyPath);
        } catch (Exception e) {
            searchHistory = new LinkedHashSet<>();
            Log.d("onCreate", "load history failed");
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret_view = inflater.inflate(R.layout.search_fragment, container, false);
        onCreateSearchView(ret_view);
        onCreateListView(ret_view);
        return ret_view;
    }

    @Override
    public void onPause() {
        Log.d("onPause", "save history");
        try {
            SerializeUtils.write(searchHistory, historyPath);
        } catch (Exception e) {
            Log.d("onPause", "save history failed");
            e.printStackTrace();
        }
        super.onPause();
    }

    private void onCreateSearchView(View ret_view) {
        main_searchview = ret_view.findViewById(R.id.main_searchview);
        main_searchview.setIconifiedByDefault(false);

        main_searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {//搜索时触发事件
                Log.d("onQueryTextSubmit", query);
                searchHistory.add(query);
                Bundle bundle = new Bundle();
                bundle.putSerializable("title", query);
                Navigation.findNavController(ret_view).navigate(R.id.action_return_home, bundle);
                main_news_fragment.searching = true;
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {//搜索时根据文本框动态改变搜索内容
                if (!TextUtils.isEmpty(newText)){
                    main_listview.setFilterText(newText);
                }   else {
                    main_listview.clearChoices();
                }
                return false;
            }
        });
    }

    private void onCreateListView(View ret_view) {
        String[] array = new String[searchHistory.size()];
        searchHistory.toArray(array);
        main_listview = ret_view.findViewById(R.id.main_listview);
        main_listview.setAdapter(new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, array));
        main_listview.setTextFilterEnabled(true);

        main_listview.setOnItemClickListener((parent, view, position, id) -> {
            String keyword = main_listview.getItemAtPosition(position).toString();
            //Toast.makeText(main_searchview.getContext(), "Searching for " + keyword, Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putSerializable("title", keyword);
            Navigation.findNavController(ret_view).navigate(R.id.action_return_home, bundle);
        });
    }
}

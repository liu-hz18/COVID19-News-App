package com.example.newsapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread netThread = new Thread(networkTask);
        netThread.start();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("My homepage");
        //Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_launcher_background);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @SuppressLint("RtlHardcoded")
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                ((DrawerLayout) findViewById(R.id.main_drawerlayout)).openDrawer(Gravity.LEFT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @SuppressLint("RtlHardcoded")
    public boolean main_drawer_button_click(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((DrawerLayout) findViewById(R.id.main_drawerlayout)).openDrawer(Gravity.LEFT);
                return true;
            case R.id.main_item_first:
                Navigation.findNavController(findViewById((R.id.main_fragment_host))).navigate(R.id.news_main_fragment);
                ((DrawerLayout) findViewById(R.id.main_drawerlayout)).closeDrawers();
                return true;
            case R.id.main_item_second:
                Navigation.findNavController(findViewById((R.id.main_fragment_host))).navigate(R.id.data_main_fragment);
                ((DrawerLayout) findViewById(R.id.main_drawerlayout)).closeDrawers();
                return true;
            case R.id.main_item_third:
                Navigation.findNavController(findViewById((R.id.main_fragment_host))).navigate(R.id.graph_main_fragment);
                ((DrawerLayout) findViewById(R.id.main_drawerlayout)).closeDrawers();
                return true;
            case R.id.main_item_fourth:
                return true;
            case R.id.main_item_fifth:
                return true;
            default:
                return false;
        }
    }

    Runnable networkTask = () -> {
        boolean update = false; //update from net or not
        long startTime =  System.currentTimeMillis();
        /*
        //EpidemicDataFetcher
        List<CountryEpidemicEntity> countryData = EpidemicDataFetcher.fetchCountryData(update);
        List<ChinaProvinceEpidemicEntity> chinaData = EpidemicDataFetcher.fetchChinaData(update);
        Log.d("country", Objects.requireNonNull(countryData).toString());
        Log.d("china", Objects.requireNonNull(chinaData).toString());
        */
        //EventsDataFetcher
        List<NewsEntity> allList = EventsDataFetcher.fetchAllData(update);
        //Log.d("data", Objects.requireNonNull(allList).toString());
        //Log.d("newsNumber", String.valueOf(allList.size()));
        List<NewsEntity> newsList = EventsDataFetcher.fetchNewsData(update);
        //Log.d("newsNumber", String.valueOf(newsList.size()));
        List<NewsEntity> paperList = EventsDataFetcher.fetchPaperData(update);
        //Log.d("newsNumber", String.valueOf(paperList.size()));

        //SearchEntityDataFetcher
        //List<SearchEntity> searchResult = SearchEntityDataFetcher.fetchSearchEntities("病毒");
        //Log.d("main", searchResult.toString());
        //List<SearchEntity> searchResult1 = SearchEntityDataFetcher.fetchSearchEntities("疫情");
        //Log.d("main", searchResult1.toString());
        /*
        //ExpertsDataFetcher
        List<ExpertEntity> expertList = ExpertsDataFetcher.fetchData(update);
        Log.d("data", Objects.requireNonNull(expertList).toString());
        */
        //SearchEngine
        SearchEngine.init(update);
        //Log.d("searchresult:", DataLoader.loadNewsList(SearchEngine.searchKeyWords(Arrays.asList("病毒"))).toString());
        //Log.d("search: ", DataLoader.loadNewsList(SearchEngine.searchString("武汉病毒")).toString());

        //Log.d("loadRelatedNews", DataLoader.loadRelatedNews(newsList.get(2).getmRelatedNews()).toString());
        //Log.d("loadRelatedNews", DataLoader.loadRelatedNews(newsList.get(1).getmRelatedNews()).toString());

        //DataLoader.loadExpertData("钟南山");
        //DataLoader.loadExpertsDataList();
        //DataLoader.loadSearchResult("病毒");

        //DataLoader.loadCountryEpidemicDataList();
        //DataLoader.loadChinaProvinceEpidemicData();

        //NewsEntity news = DataLoader.loadNewsList(SearchEngine.searchString("武汉病毒")).get(2);
        Updater.init();

        long endTime =  System.currentTimeMillis();
        long usedTime = (endTime-startTime)/1000;
        Log.d("totalTime","usedTime=" + usedTime + "s");
    };
}

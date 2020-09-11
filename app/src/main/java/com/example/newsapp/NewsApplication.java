package com.example.newsapp;

import android.app.Application;
import android.util.Log;

import com.mob.MobSDK;

import java.util.Arrays;
import java.util.List;

public class NewsApplication extends Application {
    private static NewsApplication instance;
    static boolean searching = false;

    @Override
    public void onCreate() {
        //MobSDK.init(this.getApplicationContext());
        super.onCreate();
        instance = this;
        Thread netThread = new Thread(networkTask);
        netThread.start();
    }

    public static NewsApplication getInstance(){
        return instance;
    }

    Runnable networkTask = () -> {
        boolean update = false; //update from net or not
        long startTime =  System.currentTimeMillis();
        EventsClusterDataFetcher.fetchDataFromJson("clustering.json", update);
        // ArrayList<EventClusterEntity> list = EventsClusterDataFetcher.fetchDataFromMem(1);
        //if(list != null) Log.d("NewsApplication", list.toString());

        //EpidemicDataFetcher
        List<CountryEpidemicEntity> countryData = EpidemicDataFetcher.fetchCountryData(update);
        List<ChinaProvinceEpidemicEntity> chinaData = EpidemicDataFetcher.fetchChinaData(update);
        //Log.d("country", Objects.requireNonNull(countryData).toString());
        //Log.d("china", Objects.requireNonNull(chinaData).toString());

        //EventsDataFetcher
        List<NewsEntity> allList = EventsDataFetcher.fetchAllData(update);
        //Log.d("data", Objects.requireNonNull(allList).toString());
        //Log.d("newsNumber", String.valueOf(allList.size()));
        List<NewsEntity> newsList = EventsDataFetcher.fetchNewsData(update);
        //Log.d("newsNumber", String.valueOf(newsList.size()));
        List<NewsEntity> paperList = EventsDataFetcher.fetchPaperData(update);
        //Log.d("newsNumber", String.valueOf(paperList.size()));

        //SearchEntityDataFetcher
        SearchEntityDataFetcher.fetchSearchEntities("病毒");
        //Log.d("main", searchResult.toString());
        //List<SearchEntity> searchResult1 = SearchEntityDataFetcher.fetchSearchEntities("疫情");
        //Log.d("main", searchResult1.toString());

        // ExpertsDataFetcher
        ExpertsDataFetcher.fetchData(update, false);
        ExpertsDataFetcher.fetchData(update, true);
        // Log.d("data", Objects.requireNonNull(expertList).toString());

        //SearchEngine
        SearchEngine.init(update);
        Log.d("searchresult:", DataLoader.loadNewsList(SearchEngine.searchKeyWords(Arrays.asList("病毒"))).toString());

        Updater.init();

        long endTime =  System.currentTimeMillis();
        long usedTime = (endTime-startTime)/1000;
        Log.d("totalTime","usedTime=" + usedTime + "s");
    };
}

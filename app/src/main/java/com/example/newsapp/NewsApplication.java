package com.example.newsapp;

import android.app.Application;
import android.util.Log;

import com.mob.MobSDK;

import java.util.List;

public class NewsApplication extends Application {
    private static NewsApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Thread netThread = new Thread(networkTask);
        netThread.start();
        MobSDK.init(this);
    }

    public static NewsApplication getInstance(){
        return instance;
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

        //ExpertsDataFetcher
        // ExpertsDataFetcher.fetchData(false, false);
        // ExpertsDataFetcher.fetchData(false, true);
        // Log.d("data", Objects.requireNonNull(expertList).toString());

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
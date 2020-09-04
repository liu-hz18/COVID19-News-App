package com.example.newsapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread netThread = new Thread(networkTask);
        netThread.start();
    }

    Runnable networkTask = () -> {
        try {
            long startTime =  System.currentTimeMillis();

            Map<String, CountryEpidemicEntity> temp = EpidemicDataFetcher.fetchData();
            Log.d("Main", temp.toString());

            List<NewsEntity> eventsList = EventsDataFetcher.fetchData();
            Log.d("newsNumber", String.valueOf(eventsList.size()));

            List<SearchEntity> searchResult = SearchEntityDataFetcher.fetchSearchEntities("病毒");
            Log.d("main", searchResult.toString());
            Log.d("size", String.valueOf(searchResult.size()));

            List<SearchEntity> searchResult1 = SearchEntityDataFetcher.fetchSearchEntities("疫情");
            Log.d("main", searchResult1.toString());
            Log.d("size", String.valueOf(searchResult1.size()));

            List<ExpertEntity> expertList = ExpertsDataFetcher.fetchExpertsList();
            for(ExpertEntity expert: expertList){
                Log.d("main", expert.toString());
            }
            long endTime =  System.currentTimeMillis();
            long usedTime = (endTime-startTime)/1000;
            Log.d("totalTime","usedTime=" + usedTime + "s");
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
}

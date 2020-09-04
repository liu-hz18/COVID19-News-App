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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Thread netThread = new Thread(networkTask);
        //netThread.start();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("My homepage");
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_launcher_background);// set drawable icon
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

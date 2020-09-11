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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.more_small);
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
                Navigation.findNavController(findViewById((R.id.main_fragment_host))).navigate(R.id.clustering_main_fragment);
                ((DrawerLayout) findViewById(R.id.main_drawerlayout)).closeDrawers();
                return true;
            case R.id.main_item_fifth:
                Navigation.findNavController(findViewById((R.id.main_fragment_host))).navigate(R.id.expert_main_fragment);
                ((DrawerLayout) findViewById(R.id.main_drawerlayout)).closeDrawers();
                return true;
            default:
                return false;
        }
    }
}

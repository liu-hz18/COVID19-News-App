package com.example.newsapp;

import android.util.Log;

import com.google.gson.internal.$Gson$Preconditions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//加载数据
class DataLoader {
    @NotNull
    public static List<NewsEntity> loadNewsList(@NotNull List<String> newsList) { //O(nlogn+klogn)
        List<NewsEntity> related = new ArrayList<>();
        List<NewsEntity> allNewsList = EventsDataFetcher.fetchAllData(false);
        Collections.sort(allNewsList, (left, right) -> left.getmEventId().compareTo(right.getmEventId()));
        int id;
        for (String newsId: newsList) {
            if((id = Collections.binarySearch(allNewsList, new NewsEntity(newsId), (left, right) -> left.getmEventId().compareTo(right.getmEventId()))) >= 0){
                related.add(allNewsList.get(id));
            }
        }
        return related;
    }
}

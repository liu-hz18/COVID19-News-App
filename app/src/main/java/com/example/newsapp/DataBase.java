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

class DataBase {
    public static void deleteDataBase(final String name) {
        File file = new File("/data/user/0/com.example.newsapp/databases/" + name + ".db");
        if(file.exists() && file.isFile()) {
            Log.d("delete db: ", String.valueOf(file.delete()));
        }
        File Jfile = new File("/data/user/0/com.example.newsapp/databases/" + name + ".db-journal");
        if(Jfile.exists() && Jfile.isFile()) {
            Log.d("delete db-journal: ", String.valueOf(Jfile.delete()));
        }
    }
}


//加载数据
class DataLoader {
    @NotNull
    public static List<NewsEntity> loadRelatedNews(@NotNull List<String> newsList) {
        return loadNewsList(newsList);
    }

    @NotNull
    public static List<NewsEntity> loadNewsList(@NotNull List<String> newsList) { //O(nlogn+klogn)
        List<NewsEntity> related = new ArrayList<>();
        NewsEntity news = null;
        List<NewsEntity> allNewsList = EventsDataFetcher.fetchAllData(false);
        Collections.sort(allNewsList, (left, right) -> {
            return left.getmEventId().compareTo(right.getmEventId());
        });
        int id;
        for (String newsId: newsList) {
            if((id = Collections.binarySearch(allNewsList, new NewsEntity(newsId), (left, right) -> left.getmEventId().compareTo(right.getmEventId()))) >= 0){
                related.add(allNewsList.get(id));
            }
        }
        return related;
    }

    public static List<ExpertEntity> loadExpertsDataList() {
        return ExpertsDataFetcher.fetchData(false);
    }

    @Nullable
    public static ExpertEntity loadExpertData(final String name) {
        List<ExpertEntity> experts = ExpertsDataFetcher.fetchData(false);
        for(ExpertEntity expert: experts) {
            if(expert.mZhName.equals(name) || expert.mEnName.equals(name)) {
                return expert;
            }
        }
        return null;
    }

    @NotNull
    public static List<SearchEntity> loadSearchResult(final String keyword) {
        return SearchEntityDataFetcher.fetchSearchEntities(keyword);
    }

    @NotNull
    public static List<CountryEpidemicEntity> loadCountryEpidemicDataList() {
        List<CountryEpidemicEntity> countryData = EpidemicDataFetcher.fetchCountryData(false);
        return countryData;
    }

    public static List<ChinaProvinceEpidemicEntity> loadChinaProvinceEpidemicData() {
        List<ChinaProvinceEpidemicEntity> china = EpidemicDataFetcher.fetchChinaData(false);
        Collections.sort(china, (left, right) -> (left.mConfirmed - right.mConfirmed));
        return china;
    }
}



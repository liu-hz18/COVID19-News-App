package com.example.newsapp;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


class BaseDataFetcher {
    private static final String TAG = "BaseDataFetcher";
    @SuppressLint("SdCardPath")
    protected static String savePath = "/data/user/0/com.example.newsapp/databases/";
    static JSONObject getJsonData(final String url_str) throws IOException {
        URL data_url = new URL(url_str);
        BufferedReader json_in = new BufferedReader(new InputStreamReader(data_url.openStream()));
        String input_json;
        StringBuilder total = new StringBuilder();
        while((input_json = json_in.readLine()) != null) {
            total.append(input_json);
        }
        json_in.close();
        return JSON.parseObject(total.toString());
    }
}

class EpidemicDataFetcher extends BaseDataFetcher {
    private static final String TAG = "EpidemicDataFetcher";
    private final static String url = "https://covid-dashboard.aminer.cn/api/dist/epidemic.json";
    private static String worldDataPath = savePath + "world.data";
    private static String chinaDataPath = savePath + "china.data";

    private static JSONObject getEpidemicJsonData() throws IOException {
        return BaseDataFetcher.getJsonData(url);
    }

    private static void fetchDataFromNet() throws IOException {
        JSONObject json_obj = EpidemicDataFetcher.getEpidemicJsonData();
        ArrayList<CountryEpidemicEntity> countryDataList = new ArrayList<>();
        ArrayList<ChinaProvinceEpidemicEntity> chinaDataList = new ArrayList<>();
        for(Map.Entry entry: json_obj.entrySet()) {
            String key = entry.getKey().toString();
            String[] locationInfo = key.split("\\|");
            JSONObject value = json_obj.getJSONObject(key);
            JSONArray dataArr = value.getJSONArray("data");
            List<Integer> data = Converter.StringListToList(dataArr.get(dataArr.size()-1).toString(), ",", 4);
            switch (locationInfo.length){
                case 1:
                    CountryEpidemicEntity countryData = new CountryEpidemicEntity(key, value.getString("begin"), data);
                    countryDataList.add(countryData);
                    break;
                case 2:
                    if (locationInfo[0].equals("China")){
                        ChinaProvinceEpidemicEntity provinceData = new ChinaProvinceEpidemicEntity(key, value.getString("begin"), data);
                        chinaDataList.add(provinceData);
                    }
                    break;
                default:
                    break;
            }
        }
        try{
            Log.d("after read", countryDataList.get(0).toString());
            SerializeUtils.write(countryDataList, worldDataPath);
            SerializeUtils.write(chinaDataList, chinaDataPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private static List<CountryEpidemicEntity> fetchCountryDataFromMem() throws IOException {
        try{
            return (ArrayList<CountryEpidemicEntity>) SerializeUtils.read(worldDataPath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    private static List<ChinaProvinceEpidemicEntity> fetchChinaDataFromMem() throws IOException {
        try{
            return (ArrayList<ChinaProvinceEpidemicEntity>) SerializeUtils.read(chinaDataPath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Nullable
    public static List<CountryEpidemicEntity> fetchCountryData(boolean update) {
        try {
            if(update) {
                fetchDataFromNet();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return fetchCountryDataFromMem();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static List<ChinaProvinceEpidemicEntity> fetchChinaData(boolean update) {
        try {
            if(update) {
                fetchDataFromNet();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return fetchChinaDataFromMem();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

class EventsDataFetcher extends BaseDataFetcher {
    private static final String TAG = "EventsDataFetcher";
    private final static String url = "https://covid-dashboard.aminer.cn/api/dist/events.json";
    private static String paperPath = savePath + "paper.data";
    private static String newsPath = savePath + "news.data";
    private static int topNews = 500;
    private static int topPapers = 200;
    private static boolean updated = false;
    private static List<NewsEntity> allEventsList = null;
    private static List<NewsEntity> allNewsList = null;
    private static List<NewsEntity> allPapersList = null;

    private static JSONObject getEventsJsonData() throws IOException {
        return BaseDataFetcher.getJsonData(url);
    }

    @NotNull
    private static void fetchDataFromNet() throws IOException {
        JSONArray eventsArray = getEventsJsonData().getJSONArray("datas");
        ArrayList<NewsEntity> newsList = new ArrayList<>();
        ArrayList<NewsEntity> paperList = new ArrayList<>();
        AtomicInteger countNews = new AtomicInteger();
        AtomicInteger countPaper = new AtomicInteger();
        eventsArray.parallelStream().forEach(
                jsonObj -> {
                    switch (((JSONObject) jsonObj).getString("type")) {
                        case "news":{
                            if(topNews > 0 && countNews.intValue() >= topNews)break;
                            NewsEntity temp = EventsDataFetcher.addEventToList((JSONObject) jsonObj);
                            synchronized (newsList) { newsList.add(temp); }
                            countNews.incrementAndGet();
                            break;
                        }
                        case "paper":{
                            if(topPapers > 0 && countPaper.intValue() >= topPapers)break;
                            NewsEntity temp = EventsDataFetcher.addEventToList((JSONObject) jsonObj);
                            synchronized (paperList) { paperList.add(temp); }
                            countPaper.incrementAndGet();
                            break;
                        }
                        default:
                    }
                }
        );
        Thread newsTask = new Thread(() -> {
            Collections.sort(newsList, (left, right) -> right.getTime().compareTo(left.getTime()));
            try{
                SerializeUtils.write(newsList, newsPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        newsTask.start();
        Thread paperTask = new Thread(() -> {
            Collections.sort(paperList, (left, right) -> right.getTime().compareTo(left.getTime()));
            try{
                SerializeUtils.write(paperList, paperPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        paperTask.start();
        //按时间排序
        try {
            newsTask.join();
            paperTask.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Nullable
    private static List<NewsEntity> fetchDataFromMem(final String dataPath) {
        try{
            return (ArrayList<NewsEntity>) SerializeUtils.read(dataPath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static void update() {
        if(!updated) {
            try{
                updated = true;
                fetchDataFromNet();
            } catch (IOException e) {
                updated = false;
                e.printStackTrace();
            }
        }
    }

    @NotNull
    public static List<NewsEntity> fetchAllData(final boolean update) {
        if(update) { update(); }
        if(allEventsList != null) return allEventsList;
        Log.d("update", "load from disk");
        ArrayList<NewsEntity> allList = new ArrayList<>();
        List<NewsEntity> temp;
        if((temp = fetchNewsData(false)) != null)allList.addAll(temp);
        if((temp = fetchPaperData(false)) != null)allList.addAll(temp);
        return allEventsList = allList;
    }

    @Nullable
    public static List<NewsEntity> fetchNewsData(final boolean update) {
        if(update) { update(); }
        if(allNewsList != null) return allNewsList;
        return (allNewsList = fetchDataFromMem(newsPath));
    }

    @Nullable
    public static List<NewsEntity> fetchPaperData(final boolean update) {
        if(update) { update(); }
        if(allPapersList != null) return allPapersList;
        return (allPapersList = fetchDataFromMem(paperPath));
    }

    @Contract("null -> null; !null -> new")
    private static NewsEntity addEventToList(JSONObject jsonObj) {
        if(jsonObj == null) return null;
        return new NewsEntity(
                jsonObj.getString("_id"),
                jsonObj.getString("type"),
                jsonObj.getString("title"),
                jsonObj.getString("category"),
                jsonObj.getString("time"),
                jsonObj.getString("lang")
        );
    }
}

class SearchEntityDataFetcher extends BaseDataFetcher {
    private static final String TAG = "SearchEntityDataFetcher";
    private final static String url = "https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity=";
    private static Map<String, List<SearchEntity>> searchHistory = new HashMap<>();
    private static List<SearchEntity> searchResult;
    public static int UPDATE_GRAPH_ENTITY = 0x3;
    private static Handler mHandler;

    private static JSONObject getSearchEntityJsonData(final String keyword) throws IOException {
        return BaseDataFetcher.getJsonData(url + keyword);
    }

    public static void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Nullable
    private static List<SearchEntity> fetchDataFromNet(final String keyword) {
        List<SearchEntity> entityList = new ArrayList<>();
        JSONObject entityJson;
        try {
            entityJson = SearchEntityDataFetcher.getSearchEntityJsonData(keyword);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("SearchEntityDataFetcher", "not found!");
            return null;
        }
        if (entityJson != null && entityJson.getJSONArray("data").size() > 0) {
            entityJson.getJSONArray("data").forEach(
                    jsonObj -> parseEntityJsonObject((JSONObject)jsonObj, entityList));
        }
        searchHistory.put(keyword, entityList);
        return entityList;
    }

    @NotNull
    public static void fetchSearchEntities(final String keyword) {
        Thread newsTask = new Thread(() -> {
            List<SearchEntity> result;
            if(searchHistory.containsKey(keyword)) {
                result = searchHistory.get(keyword);
            } else {
                result = fetchDataFromNet(keyword);
            }
            Message msg = Message.obtain(); // 实例化消息对象
            msg.what = UPDATE_GRAPH_ENTITY; // 消息标识
            if(mHandler != null) mHandler.sendMessage(msg);
            searchResult = result;
        });
        newsTask.start();
    }

    public static List<SearchEntity> getSearchResult() {
        return searchResult;
    }

    private static void parseEntityJsonObject(@NotNull JSONObject entityObj, List<SearchEntity> entityList) {
        JSONObject abstractInfo = entityObj.getJSONObject("abstractInfo");
        String intro = abstractInfo.getString("baidu");
        if (intro.equals("")) {
            intro = abstractInfo.getString("zhwiki");
        }
        if (intro.equals("")) {
            intro = abstractInfo.getString("enwiki");
        }
        SearchEntity entity = new SearchEntity(
                entityObj.getDouble("hot"),
                entityObj.getString("label"),
                entityObj.getString("url"),
                intro,
                entityObj.getString("img"),
                abstractInfo.getJSONObject("COVID")
        );
        entityList.add(entity);
        //entity.save();
    }
}

class ExpertsDataFetcher extends BaseDataFetcher {
    private static final String TAG = "ExpertsDataFetcher";
    private final static String url = "https://innovaapi.aminer.cn/predictor/api/v1/valhalla/highlight/get_ncov_expers_list?v=2";
    private static String dataPath = savePath + "experts.data";
    public static int UPDATE_EXPERTS = 0x4;

    private static JSONObject getExpertsJsonData() throws IOException {
        return BaseDataFetcher.getJsonData(url);
    }

    @NotNull
    private static List<ExpertEntity> fetchDataFromNet() throws IOException {
        ArrayList<ExpertEntity> expertList = new ArrayList<>();
        JSONObject experts = getExpertsJsonData();
        if(experts != null) {
            JSONArray expertsArr = experts.getJSONArray("data");
            expertsArr.parallelStream().forEach(expertJson->parseExpertJsonObj((JSONObject) expertJson, expertList));
        }
        try{
            SerializeUtils.write(expertList, dataPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return expertList;
    }

    @Nullable
    private static List<ExpertEntity> fetchDataFromMem() {
        try{
            return (ArrayList<ExpertEntity>) SerializeUtils.read(dataPath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static List<ExpertEntity> fetchData(boolean update) {
        if(update) {
            try{
                return fetchDataFromNet();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return fetchDataFromMem();
    }

    private static void parseExpertJsonObj(@NotNull JSONObject expertJson, @NotNull List<ExpertEntity> expertList) {
        ExpertEntity expert = new ExpertEntity(expertJson.getString("id"));
        expert.mImgURL = expertJson.getString("avatar");
        expert.mEnName = expertJson.getString("name");
        expert.mZhName = expertJson.getString("name_zh");
        if(expert.mZhName.equals("")) expert.mZhName = expert.mEnName;
        expert.hasPassedAway = expertJson.getBoolean("is_passedaway");

        JSONObject profile = expertJson.getJSONObject("profile");
        expert.mAssociation = profile.getString("affiliation") + "/" + profile.getString("affiliation_zh");
        expert.mBasicIntro = profile.getString("bio");
        if(expert.mBasicIntro != null) expert.mBasicIntro = expert.mBasicIntro.replace("<br>", "");
        expert.mEduIntro = profile.getString("edu");
        if(expert.mEduIntro != null) expert.mEduIntro = expert.mEduIntro.replace("<br>", "");
        expert.mHomePage = profile.getString("homepage");
        expert.mPosition = profile.getString("position");

        JSONObject indices = expertJson.getJSONObject("indices");
        expert.mActivityRate = indices.getDouble("activity");
        expert.mCitations = indices.getDouble("citations");
        expert.mDiversityRate = indices.getDouble("diversity");
        expert.mHindex = indices.getDouble("hindex");
        expert.mGindex = indices.getDouble("gindex");
        expert.mNewStar = indices.getDouble("newStar");
        expert.mSociability = indices.getDouble("sociability");
        expert.mPublication = indices.getInteger("pubs");
        synchronized (ExpertsDataFetcher.class){
            expertList.add(expert);
            //expert.save();
        }
    }
}
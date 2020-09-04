package com.example.newsapp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


class BaseDataFetcher {
    private static final String TAG = "BaseDataFetcher";
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
    private static JSONObject getJsonData() throws IOException {
        return BaseDataFetcher.getJsonData(url);
    }
    @NotNull
    public static Map<String, CountryEpidemicEntity> fetchData() throws IOException {
        JSONObject json_obj = getJsonData();
        Map<String, CountryEpidemicEntity> edataset = new HashMap<>();
        for(Map.Entry entry: json_obj.entrySet()) {
            String key = entry.getKey().toString();
            String[] locationInfo = key.split("\\|");
            if (locationInfo.length == 1) {
                JSONObject value = json_obj.getJSONObject(key);
                List<Integer> data = Converter.StringListToList(value.getString("data"), ",", 4);
                edataset.put(locationInfo[0], new CountryEpidemicEntity(key, value.getString("begin"), data));
            }
        }
        for(Map.Entry entry: json_obj.entrySet()) {
            String key = entry.getKey().toString();
            String[] locationInfo = key.split("\\|");
            if (locationInfo.length > 1 && edataset.containsKey(locationInfo[0])) {
                JSONObject value = json_obj.getJSONObject(key);
                List<Integer> data = Converter.StringListToList(value.getString("data"), ",", 4);
                Objects.requireNonNull(edataset.get(locationInfo[0])).addProvince(new ProvinceEpidemicEntity(key, value.getString("begin"), data));
            }
        }
        return edataset;
    }
}


class EventsDataFetcher extends BaseDataFetcher {
    private static final String TAG = "EventsDataFetcher";
    private final static String url = "https://covid-dashboard.aminer.cn/api/dist/events.json";
    private static JSONObject getJsonData() throws IOException {
        return BaseDataFetcher.getJsonData(url);
    }
    @NotNull
    public static List<NewsEntity> fetchData() throws IOException {
        JSONArray eventsArray = getJsonData().getJSONArray("datas");
        List<NewsEntity> eventsList = new ArrayList<>();
        eventsArray.parallelStream().forEach(
                jsonObj -> EventsDataFetcher.addEventToList((JSONObject) jsonObj, eventsList)
        );
        return eventsList;
    }

    private static void addEventToList(JSONObject jsonObj, @NotNull List<NewsEntity> eventsList) {
        if(jsonObj == null) return;
        NewsEntity event = new NewsEntity(
                jsonObj.getString("_id"),
                jsonObj.getString("type"),
                jsonObj.getString("title"),
                jsonObj.getString("category"),
                jsonObj.getString("time"),
                jsonObj.getString("lang")
        );
        synchronized (EventsDataFetcher.class) {
            eventsList.add(event);
        }
    }
}

class SearchEntityDataFetcher extends BaseDataFetcher {
    private static final String TAG = "SearchEntityDataFetcher";
    private final static String url = "https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity=";

    public static JSONObject getJsonData(final String keyword) throws IOException {
        return BaseDataFetcher.getJsonData(url + keyword);
    }

    @NotNull
    public static List<SearchEntity> fetchSearchEntities(final String keyword) throws IOException {
        List<SearchEntity> entityList = new ArrayList<>();
        JSONObject entityJson = SearchEntityDataFetcher.getJsonData(keyword);
        if (entityJson != null && entityJson.getJSONArray("data").size() > 0) {
            entityJson.getJSONArray("data").forEach(
                    jsonObj -> parseEntityJsonObject((JSONObject)jsonObj, entityList));
        }
        return entityList;
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
    }
}

class ExpertsDataFetcher extends BaseDataFetcher {
    private static final String TAG = "ExpertsDataFetcher";
    private final static String url = "https://innovaapi.aminer.cn/predictor/api/v1/valhalla/highlight/get_ncov_expers_list?v=2";

    private static JSONObject getJsonData() throws IOException {
        return BaseDataFetcher.getJsonData(url);
    }

    @NotNull
    public static List<ExpertEntity> fetchExpertsList() throws IOException {
        List<ExpertEntity> expertList = new ArrayList<>();
        JSONObject experts = getJsonData();
        if(experts != null) {
            JSONArray expertsArr = experts.getJSONArray("data");
            expertsArr.parallelStream().forEach(expertJson->parseExpertJsonObj((JSONObject) expertJson, expertList));
        }
        return expertList;
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
        }
    }
}
package com.example.newsapp;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import  org.jetbrains.annotations.Contract;
import  org.jetbrains.annotations.NotNull;
import  com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import  com.alibaba.fastjson.annotation.JSONField;
import  com.alibaba.fastjson.JSONObject;

import  java.io.IOException;
import  java.io.BufferedReader;
import  java.io.InputStreamReader;
import  java.net.URL;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import  java.util.Objects;
import  java.util.stream.Collectors;
import  java.util.Arrays;
import  java.util.HashMap;
import  java.util.Map;
import  java.util.List;


class Converter {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<Integer> StringListToList(@NotNull String listString, final String splitKey, final int topk) {
        String str = listString.toString().replace("[","").replace("]","");
        List<String> numbers = Arrays.asList(Arrays.copyOf(str.split(splitKey), topk));
        //Log.d("Converter", numbers.toString());
        return numbers.stream()
                .map(Converter::NullToInt)
                .collect(Collectors.toList());
    }

    @NotNull
    private static Integer NullToInt(@NotNull String numberlike) {
        return numberlike.equals("null") ? 0 : Integer.parseInt(numberlike);
    }
}


class EpidemicData {
    @JSONField(name = "begin")
    private String mBegin;

    @JSONField(name = "confirmed")
    private Integer mConfirmed;

    @JSONField(name = "suspected")
    private Integer mSuspected;

    @JSONField(name = "cured")
    private Integer mCured;

    @JSONField(name = "dead")
    private Integer mDead;

    public EpidemicData(final String begin, final int confirmed, final int suspected, final int cured, final int dead) {
        super();
        this.mBegin = begin;
        this.mConfirmed = confirmed;
        this.mCured = cured;
        this.mSuspected = suspected;
        this.mDead = dead;
    }

    public EpidemicData(final String begin, @NotNull final List<Integer> data) {
        this(begin, data.get(0), data.get(1), data.get(2), data.get(3));
    }

    @Contract(pure = true)
    public final int getDead() {
        return this.mDead;
    }

    @Contract(pure = true)
    public final int getConfirmed() {
        return this.mConfirmed;
    }

    @Contract(pure = true)
    public final int getSuspected() {
        return this.mSuspected;
    }

    @Contract(pure = true)
    public final int getCured() {
        return this.mCured;
    }

    @Contract(pure = true)
    public final String getBeginDate() {  //format: "YYYY-MM-DD"
        return this.mBegin;
    }
}

class CountryEpidemicData extends EpidemicData {
    @JSONField(name = "country")
    private String mCountry;

    private Map<String, ProvinceEpidemicData> mProvinceData = new HashMap<>();

    public CountryEpidemicData(final String country, final String begin, final int confirmed, final int suspected, final int cured, final int dead) {
        super(begin, confirmed, suspected, cured, dead);
        this.mCountry = country;
    }

    public CountryEpidemicData(final String province, final String begin, @NotNull final List<Integer> numbers) {
        this(province, begin, numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3));
    }

    public void addProvince(ProvinceEpidemicData provinceData) {
        if (provinceData == null || mProvinceData.containsKey(provinceData.getProvince())) {
            return;
        }
        mProvinceData.put(provinceData.getProvince(), provinceData);
    }
}

class ProvinceEpidemicData extends EpidemicData {
    @JSONField(name = "Province")
    private String mProvince;

    @Contract(pure = true)
    public final String getProvince() {
        return mProvince;
    }

    public ProvinceEpidemicData(final String province, final String begin, final int confirmed, final int suspected, final int cured, final int dead) {
        super(begin, confirmed, suspected, cured, dead);
        this.mProvince = province;
    }

    public ProvinceEpidemicData(final String province, final String begin, @NotNull final List<Integer> numbers) {
        this(province, begin, numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3));
    }
}


class NewsEntity {
    private static String url_prefix = "https://covid-dashboard-api.aminer.cn/event/";
    private String mEventId;
    private String mType;
    private String mTitle;
    private String mCategory;
    private String mTime;
    private String mLang;
    private String mContent;
    private String mSource;
    private String mURLSource;
    private List<String> mRelatedNews = new ArrayList<>();

    @NotNull
    @Override
    public String toString() {
        return "id:" + mEventId + ";title:" + mTitle + ";content:" + mContent + ";type:" + mType + ";category:" + mCategory + " time:" + mTime
                + ";lang:" + mLang + ";source:" + mSource + ";url:" + mURLSource + " related:" + mRelatedNews.toString();
    }

    public NewsEntity(final String id, final String type, final String title, final String category,
                      final String time, final String lang) {
        this.mEventId = id;
        this.mCategory = category;
        this.mTitle = title;
        this.mTime = time;
        this.mLang = lang;
        this.mType = type;
        this.readContent(this.mEventId);
    }

    private void readContent(final String eventId) {
        try{
            JSONObject json_obj = BaseDataParser.getJsonData(url_prefix + eventId).getJSONObject("data");
            this.mTitle = json_obj.getString("title");
            this.mContent = json_obj.getString("content");
            JSONArray urlArr = json_obj.getJSONArray("urls");
            this.mURLSource = urlArr.size() > 0 ? urlArr.getString(0) : null;
            this.initRelatedEvents(json_obj.getJSONArray("related_events"));
            this.mSource = json_obj.getString("source");
            if (this.mContent.equals("")) {
                this.mContent = json_obj.getString("seg_text").replace(" ","");
            }
        } catch (IOException e) {
            this.mContent = "Oh! You Found Nothing Here!";
            this.mURLSource = null;
            this.mSource = "Unkown";
        }
    }

    private void initRelatedEvents(@NotNull JSONArray relatedEventsJSONArray) {
        relatedEventsJSONArray.forEach(
                jsonObj -> this.mRelatedNews.add(((JSONObject)jsonObj).getString("id")));
    }
}


class BaseDataParser {

    public static JSONObject getJsonData(final String url_str) throws IOException {
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

class EpidemicDataParser extends BaseDataParser {
    private final static String url = "https://covid-dashboard.aminer.cn/api/dist/epidemic.json";
    protected static JSONObject getJsonData() throws IOException {
        return BaseDataParser.getJsonData(url);
    }
    @NotNull
    public static Map<String, CountryEpidemicData> fetchData() throws IOException {
        JSONObject json_obj = getJsonData();
        Map<String, CountryEpidemicData> edataset = new HashMap<>();
        for(Map.Entry entry: json_obj.entrySet()) {
            String key = entry.getKey().toString();
            String[] locationInfo = key.split("\\|");
            if (locationInfo.length == 1) {
                JSONObject value = json_obj.getJSONObject(key);
                List<Integer> data = Converter.StringListToList(value.getString("data"), ",", 4);
                edataset.put(locationInfo[0], new CountryEpidemicData(key, value.getString("begin"), data));
            }
        }
        for(Map.Entry entry: json_obj.entrySet()) {
            String key = entry.getKey().toString();
            String[] locationInfo = key.split("\\|");
            if (locationInfo.length > 1 && edataset.containsKey(locationInfo[0])) {
                JSONObject value = json_obj.getJSONObject(key);
                List<Integer> data = Converter.StringListToList(value.getString("data"), ",", 4);
                Objects.requireNonNull(edataset.get(locationInfo[0])).addProvince(new ProvinceEpidemicData(key, value.getString("begin"), data));
            }
        }
        return edataset;
    }
}


class EventsDataParser extends BaseDataParser {
    private final static String url = "https://covid-dashboard.aminer.cn/api/dist/events.json";
    public static JSONObject getJsonData() throws IOException {
        return BaseDataParser.getJsonData(url);
    }
    @NotNull
    public static List<NewsEntity> fetchData() throws IOException {
        JSONArray eventsArray = getJsonData().getJSONArray("datas");
        List<NewsEntity> eventsList = new ArrayList<>();
        eventsArray.forEach(
                jsonObj -> EventsDataParser.addEventToList((JSONObject) jsonObj, eventsList));
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
        Log.d("addEventToList", event.toString());
        eventsList.add(event);
    }
}

class SearchEntityDataParser extends BaseDataParser {
    private final static String url = "https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity=";
    public static JSONObject getJsonData(final String keyword) throws IOException {
        return BaseDataParser.getJsonData(url + keyword);
    }

}

class ExpertsDataParser extends BaseDataParser {
    private final static String url = "https://innovaapi.aminer.cn/predictor/api/v1/valhalla/highlight/get_ncov_expers_list?v=2";

    public static JSONObject getJsonData() throws IOException {
        return BaseDataParser.getJsonData(url);
    }
}

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
            //Map<String, CountryEpidemicData> temp = EpidemicDataParser.fetchData();
            //Log.d("Main", temp.toString());
            List<NewsEntity> eventsList = EventsDataParser.fetchData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
}

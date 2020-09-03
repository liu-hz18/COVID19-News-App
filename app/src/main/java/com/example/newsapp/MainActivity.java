package com.example.newsapp;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import  org.jetbrains.annotations.Contract;
import  org.jetbrains.annotations.NotNull;
import  com.alibaba.fastjson.JSON;
import  com.alibaba.fastjson.annotation.JSONField;
import  com.alibaba.fastjson.JSONObject;

import  java.io.IOException;
import  java.io.BufferedReader;
import  java.io.InputStreamReader;
import  java.net.URL;

import java.util.ArrayList;
import  java.util.Objects;
import  java.util.stream.Collectors;
import  java.util.Arrays;
import  java.util.HashMap;
import  java.util.Map;
import  java.util.List;


class Converter {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<Integer> StringToList(@NotNull String listString, final String splitKey, final int topk) {
        String str = listString.toString().replace("[","").replace("]","");
        List<String> numbers = Arrays.asList(Arrays.copyOf(str.split(splitKey), topk));
        Log.d("Converter", numbers.toString());
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
    private String mCatagory;
    private String mTime;
    private String mLang;
    private String mContent;
    private List<String> mRelatedNews = new ArrayList<>();

    public NewsEntity(final String id, final String type, final String title, final String catagory,
                      final String time, final String lang) {
        this.mEventId = id;
        this.mCatagory = catagory;
        this.mTitle = title;
        this.mTime = time;
        this.mLang = lang;
        this.mType = type;
        this.readContent();
    }

    private void readContent() {
        try{
            JSONObject json_obj = BaseDataParser.getJsonData(url_prefix + this.mEventId);
        } catch (IOException e) {
            this.mContent = "NULL";
        }
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
        //Log.d("DataParser", total.toString());
        //Log.d("DataParser", json_obj.toString());
        return JSON.parseObject(total.toString());
    }
}

class EpidemicDataParser extends BaseDataParser {
    private final static String url = "https://covid-dashboard.aminer.cn/api/dist/epidemic.json";
    protected static JSONObject getJsonData() throws IOException {
        return BaseDataParser.getJsonData(url);
    }
    @NotNull
    public static Map<String, CountryEpidemicData> jsonToList() throws IOException {
        JSONObject json_obj = getJsonData();
        Map<String, CountryEpidemicData> edataset = new HashMap<>();
        for(Map.Entry entry: json_obj.entrySet()) {
            String key = entry.getKey().toString();
            String[] locationInfo = key.split("\\|");
            if (locationInfo.length == 1) {
                JSONObject value = json_obj.getJSONObject(key);
                List<Integer> data = Converter.StringToList(value.getString("data"), ",", 4);
                edataset.put(locationInfo[0], new CountryEpidemicData(key, value.getString("begin"), data));
            }
        }
        for(Map.Entry entry: json_obj.entrySet()) {
            String key = entry.getKey().toString();
            String[] locationInfo = key.split("\\|");
            if (locationInfo.length > 1 && edataset.containsKey(locationInfo[0])) {
                JSONObject value = json_obj.getJSONObject(key);
                List<Integer> data = Converter.StringToList(value.getString("data"), ",", 4);
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
    public static
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
            Map<String, CountryEpidemicData> temp = EpidemicDataParser.jsonToList();
            Log.d("Main", temp.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
}

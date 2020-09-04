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

    @NotNull
    public static List<SearchEntity> fetchSearchEntities(final String keyword) throws IOException {
        List<SearchEntity> entityList = new ArrayList<>();
        JSONObject entityJson = BaseDataParser.getJsonData(url + keyword);
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

class RelationEntity {
    private String mRelation;
    private String mRelationURL;
    private String mLabel;
    private boolean isForward;

    public RelationEntity(final String relation, final String url, final String label, final boolean forward) {
        this.mRelation = relation;
        this.mRelationURL = url;
        this.mLabel = label;
        this.isForward = forward;
    }

    @NotNull
    public String toString() {
        return "relation:" + mRelation + " url:" + mRelationURL + " label:" + mLabel + " forward:" + isForward;
    }
}

class SearchEntity {
    private Double mHotRate;
    private String mLabel;
    private String mURL;
    private String mIntroduction;
    private String mImageURL;
    private Map<String, String> mPropertyMap = new HashMap<>();
    private List<RelationEntity> mRelationList = new ArrayList<>();

    public SearchEntity(final double hot, final String label, final String url, final String intro, final String imgurl, JSONObject graphJson) {
        this.mHotRate = hot;
        this.mLabel = label;
        this.mURL = url;
        this.mImageURL = imgurl;
        this.mIntroduction = intro;
        this.parseGraphJsonInfo(graphJson);
    }

    @NotNull
    public String toString() {
        return "hot: " + mHotRate + " label:" + mLabel + " url:" + mURL + " intro:" + mIntroduction
                + " img:" + mImageURL + " property:" + mPropertyMap + " relation:" + mRelationList;
    }

    private void parseGraphJsonInfo(@NotNull JSONObject graph) {
        JSONObject propertyJSON = graph.getJSONObject("properties");
        for(Map.Entry entry: propertyJSON.entrySet()) {
            mPropertyMap.put((String) entry.getKey(), (String) entry.getValue());
        }
        graph.getJSONArray("relations").forEach(
                relation -> parseRelationJsonObj((JSONObject) relation));
    }

    private void parseRelationJsonObj(@NotNull JSONObject relationObj){
        this.mRelationList.add(
                new RelationEntity(
                    relationObj.getString("relation"),
                    relationObj.getString("url"),
                    relationObj.getString("label"),
                    relationObj.getBoolean("forward")
                )
        );
    }
}

class ExpertEntity {
    public String mImgURL;       //照片链接
    private String mId;           //唯一标识
    public String mZhName;       //中文名
    public String mEnName;       //英文名
    public String mEduIntro;     //教育经历
    public String mBasicIntro;   //基本介绍
    public String mAssociation;  //所属单位
    public String mPosition;     //工作职位
    public String mHomePage;     //个人主页
    public Boolean hasPassedAway;//是追忆学者

    public Integer mPublication; //发表数量
    public Double mActivityRate; //活跃度
    public Double mDiversityRate;//多样性
    public Double mCitations;    //引用数量
    public Double mGindex;       //学术成就：G指数
    public Double mHindex;       //学术成就：H指数
    public Double mSociability;   //社会性
    public Double mNewStar;      //学术合作指数

    public ExpertEntity(final String id) {
        this.mId = id;
    }

    @NotNull
    public String toString() {
        return "id:" + mId + " name:" + mZhName + " enName:" + mEnName + " home:" + mHomePage + " img:" + mImgURL
                + " base:" + mBasicIntro + " edu:" + mEduIntro + " association:" + mAssociation + " position:" + mPosition
                + " passed:" + hasPassedAway + " pubs:" + mPublication + " activity:" + mActivityRate;
    }
}


class ExpertsDataParser extends BaseDataParser {
    private final static String url = "https://innovaapi.aminer.cn/predictor/api/v1/valhalla/highlight/get_ncov_expers_list?v=2";

    public static JSONObject getJsonData() throws IOException {
        return BaseDataParser.getJsonData(url);
    }

    @NotNull
    public static List<ExpertEntity> fetchExpertsList() throws IOException {
        List<ExpertEntity> expertList = new ArrayList<>();
        JSONObject experts = getJsonData();
        if(experts != null) {
            JSONArray expertsArr = experts.getJSONArray("data");
            expertsArr.forEach(expertJson->parseExpertJsonObj((JSONObject) expertJson, expertList));
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

        expertList.add(expert);
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

            //List<NewsEntity> eventsList = EventsDataParser.fetchData();

            List<SearchEntity> searchResult = SearchEntityDataParser.fetchSearchEntities("病毒");
            Log.d("main", searchResult.toString());
            Log.d("size", String.valueOf(searchResult.size()));

            //List<ExpertEntity> expertList = ExpertsDataParser.fetchExpertsList();
            //for(ExpertEntity expert: expertList){
            //    Log.d("main", expert.toString());
            //}
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
}

package com.example.newsapp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BaseEntity { }

class EpidemicEntity extends BaseEntity {
    private static final String TAG = "EpidemicEntity";
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

    public EpidemicEntity(final String begin, final int confirmed, final int suspected, final int cured, final int dead) {
        super();
        this.mBegin = begin;
        this.mConfirmed = confirmed;
        this.mCured = cured;
        this.mSuspected = suspected;
        this.mDead = dead;
    }

    public EpidemicEntity(final String begin, @NotNull final List<Integer> data) {
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

class CountryEpidemicEntity extends EpidemicEntity {
    private static final String TAG = "CountryEpidemicEntity";
    @JSONField(name = "country")
    private String mCountry;

    private Map<String, ProvinceEpidemicEntity> mProvinceData = new HashMap<>();

    public CountryEpidemicEntity(final String country, final String begin, final int confirmed, final int suspected, final int cured, final int dead) {
        super(begin, confirmed, suspected, cured, dead);
        this.mCountry = country;
    }

    public CountryEpidemicEntity(final String province, final String begin, @NotNull final List<Integer> numbers) {
        this(province, begin, numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3));
    }

    public void addProvince(ProvinceEpidemicEntity provinceData) {
        if (provinceData == null || mProvinceData.containsKey(provinceData.getProvince())) {
            return;
        }
        mProvinceData.put(provinceData.getProvince(), provinceData);
    }
}

class ProvinceEpidemicEntity extends EpidemicEntity {
    private static final String TAG = "ProvinceEpidemicEntity";
    @JSONField(name = "Province")
    private String mProvince;

    @Contract(pure = true)
    public final String getProvince() {
        return mProvince;
    }

    public ProvinceEpidemicEntity(final String province, final String begin, final int confirmed, final int suspected, final int cured, final int dead) {
        super(begin, confirmed, suspected, cured, dead);
        this.mProvince = province;
    }

    public ProvinceEpidemicEntity(final String province, final String begin, @NotNull final List<Integer> numbers) {
        this(province, begin, numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3));
    }
}

class NewsEntity extends BaseEntity {
    private static final String TAG = "NewsEntity";
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
            JSONObject json_obj = BaseDataFetcher.getJsonData(url_prefix + eventId).getJSONObject("data");
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

    private void initRelatedEvents(JSONArray relatedEventsJSONArray) {
        if(relatedEventsJSONArray!=null && relatedEventsJSONArray.size() > 0){
            relatedEventsJSONArray.forEach(
                    jsonObj -> this.mRelatedNews.add(((JSONObject)jsonObj).getString("id")));
        }
    }
}

class RelationEntity extends BaseEntity {
    private static final String TAG = "RelationEntity";
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

class SearchEntity extends BaseEntity {
    private static final String TAG = "SearchEntity";
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

    @NotNull
    public String toString() {
        return "hot: " + mHotRate + " label:" + mLabel + " url:" + mURL + " intro:" + mIntroduction
                + " img:" + mImageURL + " property:" + mPropertyMap + " relation:" + mRelationList;
    }
}

class ExpertEntity extends BaseEntity {
    private static final String TAG = "ExpertEntity";
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

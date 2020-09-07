package com.example.newsapp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

class BaseEntity extends LitePalSupport implements Serializable {
    private static final long serialVersionUID = 7356423530030029215L;
}

class EpidemicEntity extends BaseEntity implements Serializable {
    private static final String TAG = "EpidemicEntity";
    private static final long serialVersionUID = -855436446091606681L;
    @Column(unique = true)
    public String mRegion;
    public String  mBegin;  //format: "YYYY-MM-DD"
    public Integer mConfirmed;
    public Integer mSuspected;
    public Integer mCured;
    public Integer mDead;

    public EpidemicEntity(final String region, final String begin, final int confirmed, final int suspected, final int cured, final int dead) {
        super();
        this.mRegion = region;
        this.mBegin = begin;
        this.mConfirmed = confirmed;
        this.mCured = cured;
        this.mSuspected = suspected;
        this.mDead = dead;
    }

    public EpidemicEntity() {
        super();
        this.mRegion = "unknown";
        this.mBegin = "2020/1/1";
        this.mConfirmed = 0;
        this.mCured = 0;
        this.mSuspected = 0;
        this.mDead = 0;
    }

    public EpidemicEntity(final String region, final String begin, @NotNull final ArrayList<Integer> data) {
        this(region, begin, data.get(0), data.get(1), data.get(2), data.get(3));
    }

    @Override
    public String toString() {
        return "region:" + mRegion + " begin:" + mBegin + " confirmed:" + mConfirmed + " cured:" + mCured
                + " suspected:" + mSuspected + " dead:" + mDead + "\n";
    }

    @Contract(pure = true)
    public final int getmDead() { return this.mDead; }

    @Contract(pure = true)
    public final int getmConfirmed() { return this.mConfirmed; }

    @Contract(pure = true)
    public final int getmSuspected() { return this.mSuspected; }

    @Contract(pure = true)
    public final int getmCured() { return this.mCured; }

    @Contract(pure = true)
    public final String getmBeginDate() { return this.mBegin; }
}

class CountryEpidemicEntity extends EpidemicEntity implements Serializable {
    private static final String TAG = "CountryEpidemicEntity";
    private static final long serialVersionUID = -8691212280421542650L;

    public CountryEpidemicEntity() { super(); }

    public CountryEpidemicEntity(final String country, final String begin, final int confirmed, final int suspected, final int cured, final int dead) {
        super(country, begin, confirmed, suspected, cured, dead);
    }

    public CountryEpidemicEntity(final String province, final String begin, @NotNull final List<Integer> numbers) {
        this(province, begin, numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3));
    }

    @Contract(pure = true)
    public final String getmRegion() { return mRegion; }
}

class ChinaProvinceEpidemicEntity extends EpidemicEntity implements Serializable {
    private static final String TAG = "ChinaProvinceEpidemicEntity";
    private static final long serialVersionUID = -7860418769398381283L;

    public ChinaProvinceEpidemicEntity() { super(); }

    public ChinaProvinceEpidemicEntity(final String province, final String begin, final int confirmed, final int suspected, final int cured, final int dead) {
        super(province, begin, confirmed, suspected, cured, dead);
    }

    public ChinaProvinceEpidemicEntity(final String province, final String begin, @NotNull final List<Integer> numbers) {
        this(province, begin, numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3));
    }

    @Contract(pure = true)
    public final String getmRegion() { return mRegion; }
}

class NewsEntity extends BaseEntity implements Serializable {
    private static final String TAG = "NewsEntity";
    private static final long serialVersionUID = -3420032682480832882L;
    private static String url_prefix = "https://covid-dashboard-api.aminer.cn/event/";
    private static HashSet stopWords = new HashSet<>(
            Arrays.asList("的", "\'", "-", "``", "，", "”", "“", "：", ":", "(", ")", ",",
                    "和", "较", "。", "·", "）", "（", "I", "！", "!", "%", "、", "…", "--",
                    ".", "就", "已", "从", "例", "将", "与", "或", "了", "中", "用", "在", "据",
                    "有", "又", "不", "是", "《", "》", "—", "并", "向", "时", "了", "但", "正",
                    "？", "说", "上", "下", "并", "之", ")", "(", "'", "而", "者", "老", "%",
                    "|", "大", "过", "；", "给", "经", "and", "or", "but", "in", "the", "have",
                    "do"));
    private static String replaceWord = "<stop>";
    @Column(unique = true)
    private String mEventId;
    private String mType;
    private String mTitle;
    private String mCategory;
    private String mTime;
    private String mLang;
    private String mContent;
    private String mSource;
    private String mURLSource;

    public transient boolean viewed = false;

    private ArrayList<String> mRelatedNews = new ArrayList<>();
    private ArrayList<String> mTokens = new ArrayList<>();

    public String getmEventId() { return mEventId; }
    public ArrayList<String> getmRelatedNews() { return mRelatedNews; }
    public ArrayList<String> getmTokens() { return mTokens; }
    public String getType() { return mType; }
    public String getTime() { return mTime; }
    public String getmTitle() { return mTitle; }
    public String getmContent() { return mContent; }
    public String getmSource() { return "来源:" + mSource; }
    public String getmURLSource() { return mURLSource; }

    @NotNull
    @Override
    public String toString() {
        return "id:" + mEventId + ";title:" + mTitle + ";content:" + mContent + ";type:" + mType + ";category:" + mCategory + " time:" + mTime
                + ";lang:" + mLang + ";source:" + mSource + ";url:" + mURLSource + " related:" + mRelatedNews + " tokens:" + mTokens + "\n";
    }

    public NewsEntity() { super(); }

    public NewsEntity(final String id, final String type, final String title, final String category,
                      final String time, final String lang) {
        super();
        this.mEventId = id;
        this.mCategory = category;
        this.mTitle = title;
        this.mTime = time;
        this.mLang = lang;
        this.mType = type;
        this.readContent(this.mEventId);
    }

    public NewsEntity(final String id) {
        super();
        this.mEventId = id;
    }

    private void readContent(final String eventId) {
        try{
            JSONObject json_obj = BaseDataFetcher.getJsonData(url_prefix + eventId).getJSONObject("data");
            this.mTitle = json_obj.getString("title");
            this.mContent = json_obj.getString("content");
            JSONArray urlArr = json_obj.getJSONArray("urls");
            this.mURLSource = urlArr.size() > 0 ? urlArr.getString(0) : null;
            this.initRelatedEvents(json_obj.getJSONArray("related_events"), mRelatedNews);
            this.mSource = json_obj.getString("source");
            if(this.mSource.equals("")) this.mSource = "未知";
            String segText = json_obj.getString("seg_text");
            Collections.addAll(this.mTokens, segText.split(" "));
            int size = this.mTokens.size();
            for(int i = 0; i < size; i++) {
                if(stopWords.contains(this.mTokens.get(i)))
                    this.mTokens.set(i, replaceWord);
            }
            if (this.mContent.equals("") && this.mLang.equals("zh")) {
                this.mContent = segText.replace(" ","");
            }
        } catch (IOException e) {
            this.mContent = "Oh! You Found Nothing Here!";
            this.mURLSource = null;
            this.mSource = "unknown";
        }
    }

    private void initRelatedEvents(JSONArray relatedEventsJSONArray, List<String> mRelatedNews) {
        if(relatedEventsJSONArray!=null && relatedEventsJSONArray.size() > 0){
            relatedEventsJSONArray.forEach(
                    jsonObj -> mRelatedNews.add(((JSONObject)jsonObj).getString("id")));
        }
    }

}

class RelationEntity extends BaseEntity implements Serializable {
    private static final String TAG = "RelationEntity";
    private static final long serialVersionUID = -6118196070265765987L;

    public String mRelation;
    public String mRelationURL;
    @Column(unique = true)
    public String mLabel;
    public boolean isForward;

    public RelationEntity() { super(); }

    public RelationEntity(final String relation, final String url, final String label, final boolean forward) {
        super();
        this.mRelation = relation;
        this.mRelationURL = url;
        this.mLabel = label;
        this.isForward = forward;
    }

    @NotNull
    public String toString() {
        return "relation:" + mRelation + " url:" + mRelationURL + " label:" + mLabel + " forward:" + isForward + "\n";
    }
}

class SearchEntity extends BaseEntity implements Serializable {
    private static final String TAG = "SearchEntity";
    private static final long serialVersionUID = -8619878219055489349L;

    public String getmLabel() {
        return mLabel;
    }
    @Column(unique = true)
    public String mLabel;   //实体名
    public Double mHotRate;
    public String mURL;
    public String mIntroduction;
    public String mImageURL;
    public String mPropertyMapJsonStr;
    public String mRelationListJsonStr;

    public SearchEntity() { super(); }

    public SearchEntity(final double hot, final String label, final String url, final String intro, final String imgurl, JSONObject graphJson) {
        super();
        this.mHotRate = hot;
        this.mLabel = label;
        this.mURL = url;
        this.mImageURL = imgurl;
        this.mIntroduction = intro;
        this.parseGraphJsonInfo(graphJson);
    }

    public SearchEntity(final double hot, final String label, final String url, final String intro, final String imgurl, final String mPropertyMapJsonStr, final String mRelationListJsonStr) {
        super();
        this.mHotRate = hot;
        this.mLabel = label;
        this.mURL = url;
        this.mImageURL = imgurl;
        this.mIntroduction = intro;
        this.mRelationListJsonStr = mRelationListJsonStr;
        this.mPropertyMapJsonStr = mPropertyMapJsonStr;
    }
    
    private void parseGraphJsonInfo(@NotNull JSONObject graph) {
        JSONObject propertyJSON = graph.getJSONObject("properties");
        if(propertyJSON == null)return;
        Map<String, String> mPropertyMap = new HashMap<>();
        for(Map.Entry entry: propertyJSON.entrySet()) {
            mPropertyMap.put((String) entry.getKey(), (String) entry.getValue());
        }
        mPropertyMapJsonStr = JSON.toJSONString(mPropertyMap);

        ArrayList<RelationEntity> mRelationList = new ArrayList<>();
        graph.getJSONArray("relations").forEach(
                relation -> parseRelationJsonObj((JSONObject) relation, mRelationList));
        this.mRelationListJsonStr = JSONObject.toJSONString(mRelationList);
    }

    public Map<String, Object> getmPropertyMap() {
        return JSONObject.parseObject(this.mPropertyMapJsonStr);
    }

    public List<RelationEntity> getmRelationList() {
        return JSON.parseObject(this.mRelationListJsonStr, new TypeReference<ArrayList<RelationEntity>>(){});
    }

    private void parseRelationJsonObj(@NotNull JSONObject relationObj, @NotNull List<RelationEntity> mRelationList){
        RelationEntity relation = new RelationEntity(
                relationObj.getString("relation"),
                relationObj.getString("url"),
                relationObj.getString("label"),
                relationObj.getBoolean("forward")
        );
        mRelationList.add(relation);
    }

    @NotNull
    public String toString() {
        return "hot: " + mHotRate + " label:" + mLabel + " url:" + mURL + " intro:" + mIntroduction
                + " img:" + mImageURL + " property:" + getmPropertyMap() + " relation:" + getmRelationList() + "\n";
    }
}

class ExpertEntity extends BaseEntity implements Serializable {
    private static final String TAG = "ExpertEntity";
    private static final long serialVersionUID = 84249644034675389L;
    public String mImgURL;       //照片链接
    @Column(unique = true)
    private String mId;          //唯一标识

    public String getmZhName() {
        return mZhName;
    }

    public String mZhName;       //中文名

    public String getmEnName() {
        return mEnName;
    }

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

    public ExpertEntity(final String id) { super(); this.mId = id; }

    @NotNull
    public String toString() {
        return "id:" + mId + " name:" + mZhName + " enName:" + mEnName + " home:" + mHomePage + " img:" + mImgURL
                + " base:" + mBasicIntro + " edu:" + mEduIntro + " association:" + mAssociation + " position:" + mPosition
                + " passed:" + hasPassedAway + " pubs:" + mPublication + " activity:" + mActivityRate + "\n";
    }
}

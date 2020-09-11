package com.example.newsapp;

import android.annotation.SuppressLint;
import android.util.Log;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


class Tuple extends LitePalSupport implements Serializable {
    private static final long serialVersionUID = 1957059308529965192L;
    String first;
    Double second;
    Tuple(String f, Double s) {
        first = f;
        second = s;
    }
    @NotNull
    @Override
    public String toString() { return "(" + first + ", " + second + ")"; }
}


public class SearchEngine {
    @SuppressLint("SdCardPath")
    private static String savePath = "/data/user/0/com.example.newsapp/databases/searchengine.index";
    private static String saveCountPath = "/data/user/0/com.example.newsapp/databases/searchengine.count";
    private static HashMap<String, ArrayList<Tuple>> invertedIndex = new HashMap<>();
    private static HashMap<String, Double> tokenTotalTfIdf = new HashMap<>();
    private static Integer maxReturnNumber = 64;

    @Contract(pure = true)
    public static Map<String, ArrayList<Tuple>> getInvertedIndex() {
        return invertedIndex;
    }

    public static void init(boolean update) {
        if(update) {
            initFromList(EventsDataFetcher.fetchAllData(false));
        }
        try{
            invertedIndex = (HashMap<String, ArrayList<Tuple>>) SerializeUtils.read(savePath);
            tokenTotalTfIdf = (HashMap<String, Double>) SerializeUtils.read(saveCountPath);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("SearchEngine", "load failed!");
        }
    }

    private static void initFromList(@NotNull List<NewsEntity> newsList) {
        //词频统计
        HashMap< String, Map<String, Long> > tfTable = new HashMap<>();
        HashMap<String, Double> idfTable = new HashMap<>();
        //计算tf
        for(NewsEntity newsFile: newsList) {
            List<String> tokens = newsFile.getmTokens();
            List<String> titletokens = tokenize(newsFile.getmTitle());
            tokens.addAll(titletokens);
            tokens.addAll(titletokens);
            tokens.addAll(titletokens);
            Map<String, Long> countMap = tokens.parallelStream().collect(
                    Collectors.groupingBy(String::toString, Collectors.counting()));
            tfTable.put(newsFile.getmEventId(), countMap);
            countMap.keySet().forEach(token -> {
                Double idf;
                if( (idf = idfTable.get(token)) != null) {
                    idfTable.put(token, idf + 1.0);
                } else {
                    idfTable.put(token, 1.0);
                }
            });
        }
        //计算idf
        double totalFiles = newsList.size();
        idfTable.forEach((key, value) -> value = Math.log(totalFiles / (value + 1)));
        //计算tf-idf
        idfTable.forEach((key, value) -> {
            ArrayList<Tuple> filetfList = new ArrayList<>();
            newsList.forEach(
                    newsFile -> {
                        Long temp = null;
                        if((temp = Objects.requireNonNull(tfTable.get(newsFile.getmEventId())).get(key)) != null){
                            filetfList.add(new Tuple(newsFile.getmEventId(), temp.doubleValue() * value));
                        }
                    }
            );
            invertedIndex.put(key, filetfList);
            tokenTotalTfIdf.put(key, filetfList.stream().mapToDouble(tuple -> tuple.second).sum());
        });
        //排序得到倒排索引
        invertedIndex.forEach((token, fileMap) -> {
            Collections.sort(fileMap, (Tuple left, Tuple right) -> right.second.compareTo(left.second));
        });
        Log.d("invertedIndex",  invertedIndex.size() + "|" + invertedIndex.toString());
        try {
            SerializeUtils.write(invertedIndex, savePath);
            SerializeUtils.write(tokenTotalTfIdf, saveCountPath);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("initFromList", "save index failed");
        }
    }

    @NotNull
    public static List<String> searchKeyWords(@NotNull List<String> keyWordsList) {
        Map<String, Tuple> fileIdValues = new HashMap<>();
        for(String keyword: keyWordsList) {
            if (invertedIndex.containsKey(keyword)) {
                Double weight = tokenTotalTfIdf.get(keyword);
                Tuple newTuple;
                for(Tuple tuple: invertedIndex.get(keyword)) {
                    String token = tuple.first;
                    if((newTuple = fileIdValues.get(token)) != null) {
                        fileIdValues.put(token, new Tuple(token, newTuple.second + tuple.second/ weight));
                    } else {
                        fileIdValues.put(token, new Tuple(token, tuple.second / weight));
                    }
                }
            }
        }
        ArrayList<Tuple> ids = new ArrayList<>(fileIdValues.values());
        Collections.sort(ids, (Tuple left, Tuple right) -> right.second.compareTo(left.second));
        Log.d("result", ids.toString());
        ArrayList<String> finalFileIds = new ArrayList<>();
        ids.forEach(tuple -> finalFileIds.add(tuple.first));
        Log.d("number", String.valueOf(finalFileIds.size()));
        if(finalFileIds.size() > maxReturnNumber){
            return finalFileIds.subList(0, maxReturnNumber);
        }
        return finalFileIds;
    }

    private static List<String> tokenize(@NotNull String sentence) {
        return ToAnalysis.parse(sentence).getTerms().stream().map(Term::getName).collect(Collectors.toList());
    }

    @NotNull
    public static List<String> searchString(@NotNull String str) {
        return searchKeyWords(tokenize(str));
    }
}

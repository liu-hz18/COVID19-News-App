package com.example.newsapp;

import android.os.Handler;
import android.os.Message;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

class Updater {
    static int initNumber = 15;
    static int totalUpdateNumber = 7;
    static int relatedUpdateNumber = 5;

    static final int UPDATE_NEWS_LIST = 0x1;
    static final int UPDATE_PAPER_LIST = 0x2;
    static final int UPDATE_TOTAL_LIST = 0x3;

    static Random randEngine = new Random();
    private static Handler mHandler = null;

    static void update(final int action) {
        Message msg = Message.obtain(); // 实例化消息对象
        msg.what = action; // 消息标识
        if(mHandler != null) mHandler.sendMessage(msg);
    }

    static void setHandler(Handler handler) {
        mHandler = handler;
    }

    static List<NewsEntity> fetchData(@NotNull final String eventType) {
        switch (eventType) {
            case "news":
                return EventsDataFetcher.fetchNewsData(false);
            case "paper":
                return EventsDataFetcher.fetchPaperData(false);
            default:
                return EventsDataFetcher.fetchAllData(false);
        }
    }

    static void init(){
        NewsUpdater.initNews();
        PaperUpdater.initNews();
        AllUpdater.initNews();
    }

    static void updatePullUpNews(final String type) {
        switch (type) {
            case "news":
                NewsUpdater.updatePullUpNews();
            case "paper":
                PaperUpdater.updatePullUpNews();
            default:
                AllUpdater.updatePullUpNews();
        }
    }

    static void updatePullDownNews(final String type) {
        switch (type) {
            case "news":
                NewsUpdater.updatePullDownNews();
            case "paper":
                PaperUpdater.updatePullDownNews();
            default:
                AllUpdater.updatePullDownNews();
        }
    }

    static List<NewsEntity> getDisplayingNews (final String type) {
        switch (type) {
            case "news":
                return NewsUpdater.getDisplayingNews();
            case "paper":
                return PaperUpdater.getDisplayingNews();
            default:
                return AllUpdater.getDisplayingNews();
        }
    }
}


class NewsUpdater extends Updater {
    private static List<NewsEntity> displayingNews = new LinkedList<>();
    private static List<NewsEntity> viewedNews = new LinkedList<>();
    private static List<NewsEntity> pullUpNews = new LinkedList<>();
    private static List<NewsEntity> pullDownNews = new LinkedList<>();
    private static int cursor = 0;
    private static String type = "news";


    public static void initNews() {
        List<NewsEntity> allNewsList = Updater.fetchData(type);
        if(allNewsList != null) displayingNews.addAll(allNewsList.subList(0, initNumber));
        Thread newsTask = new Thread(() -> {
            cursor = initNumber;
            if(allNewsList != null){
                pullDownNews.addAll(allNewsList.subList(initNumber, initNumber + totalUpdateNumber));
                pullUpNews.addAll(allNewsList.subList(initNumber + totalUpdateNumber, initNumber + 2*totalUpdateNumber));
            }
        });
        newsTask.start();
        update(UPDATE_NEWS_LIST);
    }

    public static void updatePullUpNews() {
        List<NewsEntity> newData = pullUpNews.subList(0, totalUpdateNumber);
        Collections.shuffle(newData);
        displayingNews.addAll(0, newData);
        Thread newsTask = new Thread(() -> {
            int size = pullUpNews.size();
            List<NewsEntity> allNewsList = Updater.fetchData(type);
            if(allNewsList != null){
                int total = allNewsList.size();
                while(size < totalUpdateNumber && total > 0) {
                    pullUpNews.add(allNewsList.get(cursor + randEngine.nextInt(total - cursor)));
                }
            }
        });
        newsTask.start();
        update(UPDATE_NEWS_LIST);
    }

    public static void updatePullDownNews() {
        List<NewsEntity> newData = pullDownNews.subList(0, totalUpdateNumber);
        displayingNews.addAll(newData);
        Thread newsTask = new Thread(() -> {
            cursor += totalUpdateNumber;
            List<NewsEntity> allNewsList = Updater.fetchData(type);
            if(allNewsList != null) {
                if(cursor > allNewsList.size())cursor = 0;
                pullDownNews.addAll(allNewsList.subList(cursor, cursor + totalUpdateNumber));
            }
        });
        newsTask.start();
        update(UPDATE_NEWS_LIST);
    }

    public static void logViewed(NewsEntity news) {
        Thread newsTask = new Thread(() -> {
            //viewedNews.add(news);
            news.viewed = true;
            pullUpNews.addAll(0, DataLoader.loadRelatedNews(news.getmRelatedNews()));
            int size = pullUpNews.size();
            if (size < totalUpdateNumber) {
                List<NewsEntity> allNewsList = Updater.fetchData(type);
                if(allNewsList != null){
                    int total = allNewsList.size();
                    while(size < totalUpdateNumber && total > 0) {
                        pullUpNews.add(allNewsList.get(cursor + randEngine.nextInt(total - cursor)));
                    }
                }
            }
        });
        newsTask.start();
    }

    public static List<NewsEntity> getDisplayingNews() { return displayingNews; }

    public static List<NewsEntity> getViewedNews() { return viewedNews; }
}


class PaperUpdater extends Updater {
    private static List<NewsEntity> displayingNews = new LinkedList<>();
    private static List<NewsEntity> viewedNews = new LinkedList<>();
    private static List<NewsEntity> pullUpNews = new LinkedList<>();
    private static List<NewsEntity> pullDownNews = new LinkedList<>();
    private static int cursor = 0;
    private static String type = "paper";

    public static void initNews() {
        List<NewsEntity> allNewsList = Updater.fetchData(type);
        if(allNewsList != null) displayingNews.addAll(allNewsList.subList(0, initNumber));
        Thread newsTask = new Thread(() -> {
            cursor = initNumber;
            if(allNewsList != null){
                pullDownNews.addAll(allNewsList.subList(initNumber, initNumber + totalUpdateNumber));
                pullUpNews.addAll(allNewsList.subList(initNumber + totalUpdateNumber, initNumber + 2*totalUpdateNumber));
            }
        });
        newsTask.start();
        update(UPDATE_PAPER_LIST);
    }

    public static void updatePullUpNews() {
        List<NewsEntity> newData = pullUpNews.subList(0, totalUpdateNumber);
        Collections.shuffle(newData);
        displayingNews.addAll(0, newData);
        Thread newsTask = new Thread(() -> {
            int size = pullUpNews.size();
            List<NewsEntity> allNewsList = Updater.fetchData(type);
            if(allNewsList != null){
                int total = allNewsList.size();
                while(size < totalUpdateNumber && total > 0) {
                    pullUpNews.add(allNewsList.get(cursor + randEngine.nextInt(total - cursor)));
                }
            }
        });
        newsTask.start();
        update(UPDATE_PAPER_LIST);
    }

    public static void updatePullDownNews() {
        List<NewsEntity> newData = pullDownNews.subList(0, totalUpdateNumber);
        displayingNews.addAll(newData);
        Thread newsTask = new Thread(() -> {
            List<NewsEntity> allNewsList = Updater.fetchData(type);
            if(allNewsList != null) {
                cursor += totalUpdateNumber;
                if(cursor > allNewsList.size())cursor = 0;
                pullDownNews.addAll(allNewsList.subList(cursor, cursor + totalUpdateNumber));
            }
        });
        newsTask.start();
        update(UPDATE_PAPER_LIST);
    }

    public static void logViewed(NewsEntity news) {
        Thread newsTask = new Thread(() -> {
            //viewedNews.add(news);
            news.viewed = true;
            pullUpNews.addAll(0, DataLoader.loadRelatedNews(news.getmRelatedNews()));
            int size = pullUpNews.size();
            if (size < totalUpdateNumber) {
                List<NewsEntity> allNewsList = Updater.fetchData(type);
                if(allNewsList != null){
                    int total = allNewsList.size();
                    while(size < totalUpdateNumber && total > 0) {
                        pullUpNews.add(allNewsList.get(cursor + randEngine.nextInt(total - cursor)));
                    }
                }
            }
        });
        newsTask.start();
    }

    public static List<NewsEntity> getDisplayingNews() { return displayingNews; }

    public static List<NewsEntity> getViewedNews() { return viewedNews; }
}


class AllUpdater extends Updater {
    private static List<NewsEntity> displayingNews = new LinkedList<>();
    private static List<NewsEntity> viewedNews = new LinkedList<>();
    private static List<NewsEntity> pullUpNews = new LinkedList<>();
    private static List<NewsEntity> pullDownNews = new LinkedList<>();
    private static int cursor = 0;
    private static String type = "all";

    public static void initNews() {
        List<NewsEntity> allNewsList = Updater.fetchData(type);
        if(allNewsList != null) displayingNews.addAll(allNewsList.subList(0, initNumber));
        Thread newsTask = new Thread(() -> {
            cursor = initNumber;
            if(allNewsList != null){
                pullDownNews.addAll(allNewsList.subList(initNumber, initNumber + totalUpdateNumber));
                pullUpNews.addAll(allNewsList.subList(initNumber + totalUpdateNumber, initNumber + 2*totalUpdateNumber));
            }
        });
        newsTask.start();
        update(UPDATE_TOTAL_LIST);
    }

    public static void updatePullUpNews() {
        List<NewsEntity> newData = pullUpNews.subList(0, totalUpdateNumber);
        Collections.shuffle(newData);
        displayingNews.addAll(0, newData);
        Thread newsTask = new Thread(() -> {
            int size = pullUpNews.size();
            List<NewsEntity> allNewsList = Updater.fetchData(type);
            if(allNewsList != null){
                int total = allNewsList.size();
                while(size < totalUpdateNumber && total > 0) {
                    pullUpNews.add(allNewsList.get(cursor + randEngine.nextInt(total - cursor)));
                }
            }
        });
        newsTask.start();
        update(UPDATE_TOTAL_LIST);
    }

    public static void updatePullDownNews() {
        List<NewsEntity> newData = pullDownNews.subList(0, totalUpdateNumber);
        displayingNews.addAll(newData);
        Thread newsTask = new Thread(() -> {
            List<NewsEntity> allNewsList = Updater.fetchData(type);
            if(allNewsList != null) {
                cursor += totalUpdateNumber;
                if(cursor > allNewsList.size())cursor = 0;
                pullDownNews.addAll(allNewsList.subList(cursor, cursor + totalUpdateNumber));
            }
        });
        newsTask.start();
        update(UPDATE_TOTAL_LIST);
    }

    public static void logViewed(NewsEntity news) {
        Thread newsTask = new Thread(() -> {
            //viewedNews.add(news);
            news.viewed = true;
            pullUpNews.addAll(0, DataLoader.loadRelatedNews(news.getmRelatedNews()));
            int size = pullUpNews.size();
            if (size < totalUpdateNumber) {
                List<NewsEntity> allNewsList = Updater.fetchData(type);
                if(allNewsList != null){
                    int total = allNewsList.size();
                    while(size < totalUpdateNumber && total > 0) {
                        pullUpNews.add(allNewsList.get(cursor + randEngine.nextInt(total - cursor)));
                    }
                }
            }
        });
        newsTask.start();
    }

    public static List<NewsEntity> getDisplayingNews() { return displayingNews; }

    public static List<NewsEntity> getViewedNews() { return viewedNews; }
}

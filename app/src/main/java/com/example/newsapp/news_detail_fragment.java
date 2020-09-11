package com.example.newsapp;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class news_detail_fragment extends Fragment{
    public news_detail_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret_view = inflater.inflate(R.layout.news_detail_fragment, container, false);
        NewsEntity news = (NewsEntity) getArguments().getSerializable("news");
        ((TextView)ret_view.findViewById(R.id.news_body_title)).setText(news.getmTitle());
        ((TextView)ret_view.findViewById(R.id.news_body_content)).setText(news.getmContent());
        ((TextView)ret_view.findViewById(R.id.news_body_date)).setText(news.getTime());
        ((TextView)ret_view.findViewById(R.id.news_body_source)).setText("来源: " + news.getmSource());

        TabLayout bottomLayout = ret_view.findViewById(R.id.share_tab_layout);
        bottomLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                share("[标题]:\n" + news.getmTitle() + "\n[内容]:\n " + news.getmContent());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                share("[标题]:\n" + news.getmTitle() + "\n[内容]:\n " + news.getmContent());
            }
        });
        return ret_view;
    }

    public void share(final String content){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        List<ResolveInfo> resolveInfos = getActivity().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfos.isEmpty()) {
            return;
        }
        List<Intent> targetIntents = new ArrayList<>();
        for (ResolveInfo info : resolveInfos) {
            ActivityInfo ainfo = info.activityInfo;
            switch (ainfo.packageName) {
                case "com.tencent.mm":
                    addShareIntent(targetIntents, ainfo, content);
                    break;
                case "com.tencent.mobileqq":
                    addShareIntent(targetIntents, ainfo, content);
                    break;
                case "com.sina.weibo":
                    addShareIntent(targetIntents, ainfo, content);
                    break;
            }
        }
        if (targetIntents.size() == 0) {
            return;
        }
        Intent chooserIntent = Intent.createChooser(targetIntents.remove(0), "请选择分享平台");
        if (chooserIntent == null) {
            return;
        }
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(new Parcelable[]{}));
        try {
            startActivity(chooserIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "找不到该分享应用组件", Toast.LENGTH_SHORT).show();
        }
    }

    private void addShareIntent(List<Intent> list, ActivityInfo ainfo, final String content) {
        Intent target = new Intent(Intent.ACTION_SEND);
        target.setType("text/plain");
        target.putExtra(Intent.EXTRA_TEXT, content);
        target.setPackage(ainfo.packageName);
        target.setClassName(ainfo.packageName, ainfo.name);
        list.add(target);
    }
}

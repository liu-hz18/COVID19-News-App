package com.example.newsapp;
import android.annotation.SuppressLint;
import android.os.Bundle;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;


import org.jetbrains.annotations.NotNull;

public class main_news_fragment extends Fragment {

    @SuppressLint("HandlerLeak")
    class EventsListUpdateHandler extends Handler {
        // 通过复写handlerMessage() 从而确定更新UI的操作
        @Override
        public void handleMessage(Message msg) {
            final int what = msg.what;
            if(what == Updater.UPDATE_NEWS_LIST || what == Updater.UPDATE_PAPER_LIST || what == Updater.UPDATE_TOTAL_LIST){
                refresh_callback();
            }
        }
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static int ALL = 0;
    private static int NEWS = 1;
    private static int PAPER = 2;
    private int newsClass = ALL;

    static private String[] news_class_names = {"新闻", "论文"};
    static private boolean[] news_class_visible = {true, true};
    static private TabLayout tablayout;
    static private ClassHandler[] news_class_handler = {null, null};
    static private boolean from_dialog = false;
    static private String pre_tab;
    static private int unfinished_animations = 0;

    public NewsListAdapter adapter;
    private String news_type = "all";

    private boolean view_history = false;

    private XRecyclerView mRecyclerView;
    private TextView searchTextView;
    private TabLayout bottomLayout;

    public main_news_fragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment news_main_fragment.
     */
    @NotNull
    public static main_news_fragment newInstance(String param1, String param2) {
        main_news_fragment fragment = new main_news_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Updater.loadViewedFromMem();
    }

    @Override
    public void onPause() {
        EventsDataFetcher.saveEventsList();
        Updater.saveViewedToMem();
        super.onPause();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret_view = inflater.inflate(R.layout.news_main_fragment, container, false);
        pre_tab = "全部";
        onCreateRecyclerView(ret_view);
        onCreateHeaderTabLayout(ret_view);
        onCreateBottomTabLayout(ret_view);
        onCreateSearchEditText(ret_view);
        return ret_view;
    }

    private void onCreateBottomTabLayout(View ret_view) {
        bottomLayout = ret_view.findViewById(R.id.history_tab_layout);
        if(view_history) bottomLayout.getTabAt(1).select();
        else bottomLayout.getTabAt(0).select();
        bottomLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    view_history = true;
                    refresh_history();
                } else {
                    view_history = false;
                    changeType(news_type);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    view_history = true;
                    refresh_history();
                } else {
                    view_history = false;
                    changeType(news_type);
                }
            }
        });
    }

    private void onCreateHeaderTabLayout(View ret_view) {
        tablayout = ret_view.findViewById(R.id.news_tab_layout);
        tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        TabLayout.Tab temp_tab = tablayout.newTab().setText("全部");
        tablayout.addTab(temp_tab);
        LinearLayout linearLayout = temp_tab.view;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = 200;
        linearLayout.setLayoutParams(layoutParams);
        for(int i = 0; i < news_class_names.length; i++) {
            news_class_visible[i] = true;
            news_class_handler[i] = new ClassHandler(tablayout.newTab().setText(news_class_names[i]));
            tablayout.addTab(news_class_handler[i].tab);
            linearLayout = news_class_handler[i].tab.view;
            layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams.width = 200;
            linearLayout.setLayoutParams(layoutParams);
        }
        temp_tab = tablayout.newTab().setText("+");
        tablayout.addTab(temp_tab);
        linearLayout = temp_tab.view;
        layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = 200;
        linearLayout.setLayoutParams(layoutParams);
        tablayout.getTabAt(newsClass).select();
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            void change(TabLayout.Tab tab) {
                if(from_dialog) {
                    from_dialog = false;
                    return;
                }
                ((TabLayout)ret_view.findViewById(R.id.history_tab_layout)).getTabAt(0).select();
                CharSequence text = tab.getText();
                pre_tab = text.toString();
                Log.d("addOnTabSelectedListener", String.valueOf(NewsApplication.searching));
                if ("全部".contentEquals(text)) {
                    newsClass = ALL;
                    news_type = "all";
                    NewsApplication.searching = false;
                    view_history = false;
                    changeType(news_type);
                } else if ("新闻".contentEquals(text)) {
                    newsClass = NEWS;
                    news_type = "news";
                    NewsApplication.searching = false;
                    view_history = false;
                    changeType(news_type);
                } else if ("论文".contentEquals(text)) {
                    newsClass = PAPER;
                    news_type = "paper";
                    NewsApplication.searching = false;
                    view_history = false;
                    changeType(news_type);
                } else if ("+".contentEquals(text)) {
                    NewsApplication.searching = false;
                    view_history = false;
                    NewsClassDialog dialog = new NewsClassDialog();
                    dialog.show(getActivity().getSupportFragmentManager(), "choose_class_dialog");
                }
            }
            @Override
            public void onTabSelected(TabLayout.Tab tab) { change(tab); }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { change(tab); }
        });
    }


    private void onCreateRecyclerView(View ret_view) {
        mRecyclerView = ret_view.findViewById(R.id.news_list_recyclerview);
        //mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallPulse);
        LinearLayoutManager layoutManager = new LinearLayoutManager( getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setLoadingMoreEnabled(true);
        mRecyclerView.setPullRefreshEnabled(true);
        mRecyclerView.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
        mRecyclerView.getDefaultFootView().setLoadingDoneHint("加载完成");
        adapter = new NewsListAdapter();
        mRecyclerView.setAdapter(adapter);

        Handler mhandler = new EventsListUpdateHandler();
        NewsUpdater.setHandler(mhandler);

        if(view_history) refresh_history();
        else if(!NewsApplication.searching) refresh_callback();
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    Log.d("setLoadingListener", String.valueOf(NewsApplication.searching));
                    if(!view_history && !NewsApplication.searching) {
                        Updater.updatePullUpNews(news_type);
                    }
                    mRecyclerView.refreshComplete();
                }, 600);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(() -> {
                    if(!view_history && !NewsApplication.searching) {
                        Updater.updatePullDownNews(news_type);
                    }
                    mRecyclerView.loadMoreComplete();
                }, 600);
            }
        });
    }

    private void onCreateSearchEditText(View ret_view) {
        searchTextView = ret_view.findViewById(R.id.news_list_search_edittext);
        searchTextView.setOnClickListener(v-> {
            Navigation.findNavController(v).navigate(R.id.action_view_search_body);
        });
        String searchtitle = (String) getArguments().getSerializable("title");
        if(searchtitle!=null && !searchtitle.equals("")){
            searchUpdate(searchtitle);
        }
    }

    private void changeType(@NotNull final String eventType) {
        adapter.newslist.clear();
        switch (eventType) {
            case "news":
                adapter.newslist.addAll(NewsUpdater.getDisplayingNews());
            case "paper":
                adapter.newslist.addAll(PaperUpdater.getDisplayingNews());
            default:
                adapter.newslist.addAll(AllUpdater.getDisplayingNews());
        }
        adapter.notifyDataSetChanged();
    }

    private void searchUpdate(final String searchStr) {
        adapter.newslist.clear();
        adapter.newslist.addAll(DataLoader.loadNewsList(SearchEngine.searchString(searchStr)));
        adapter.notifyDataSetChanged();
    }

    private void refresh_callback() {
        adapter.newslist.clear();
        adapter.newslist.addAll(Updater.getDisplayingNews(news_type));
        adapter.notifyDataSetChanged();
    }

    private void refresh_history() {
        adapter.newslist.clear();
        adapter.newslist.addAll(Updater.getViewedNews());
        adapter.notifyDataSetChanged();
    }

    static public class ClassHandler {
        TabLayout.Tab tab;
        static private long animation_time = 250;
        ClassHandler(TabLayout.Tab _tab) { tab = _tab; }
        private void check_all_end() {
            if(unfinished_animations == 0) {
                for(int i = 0; i <news_class_visible.length; i++) {
                    if(news_class_visible[i]) {
                        if(Objects.equals(news_class_handler[i].tab.getText(), pre_tab)) {
                            from_dialog = true;
                            tablayout.selectTab(news_class_handler[i].tab);
                            return ;
                        }
                    }
                }
                tablayout.selectTab(tablayout.getTabAt(0));
            }
        }
        void create_animation() {
            unfinished_animations ++;
            ValueAnimator animation = ValueAnimator.ofInt(1, 200);
            animation.setDuration(animation_time);
            animation.start();
            animation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator updatedAnimation) {
                    unfinished_animations --;
                    // You can use the animated value in a property that uses the
                    // same type as the animation. In this case, you can use the
                    // float value in the translationX property.
                    check_all_end();
                }
                @Override
                public void onAnimationCancel(Animator updatedAnimation) {
                    // You can use the animated value in a property that uses the
                    // same type as the animation. In this case, you can use the
                    // float value in the translationX property.
                }
                @Override
                public void onAnimationRepeat(Animator updatedAnimation) {
                    // You can use the animated value in a property that uses the
                    // same type as the animation. In this case, you can use the
                    // float value in the translationX property.
                }
                @Override
                public void onAnimationStart(Animator updatedAnimation) {
                    // You can use the animated value in a property that uses the
                    // same type as the animation. In this case, you can use the
                    // float value in the translationX property.
                }
            });
            animation.addUpdateListener((ValueAnimator.AnimatorUpdateListener) updatedAnimation -> {
                // You can use the animated value in a property that uses the
                // same type as the animation. In this case, you can use the
                // float value in the translationX property.
                int animatedValue = (int)updatedAnimation.getAnimatedValue();
                LinearLayout linearLayout = tab.view;
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
                layoutParams.width = animatedValue;
                linearLayout.setLayoutParams(layoutParams);
            });
        }
        void delete_animation() {
            unfinished_animations ++;
            ValueAnimator animation = ValueAnimator.ofInt(200, 1);
            animation.setDuration(animation_time);
            animation.start();
            animation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator updatedAnimation) {
                    unfinished_animations --;
                    tablayout.removeTab(tab);
                    check_all_end();
                }
                @Override
                public void onAnimationCancel(Animator updatedAnimation) {
                    // You can use the animated value in a property that uses the
                    // same type as the animation. In this case, you can use the
                    // float value in the translationX property.
                }
                @Override
                public void onAnimationRepeat(Animator updatedAnimation) {
                    // You can use the animated value in a property that uses the
                    // same type as the animation. In this case, you can use the
                    // float value in the translationX property.
                }
                @Override
                public void onAnimationStart(Animator updatedAnimation) {
                    // You can use the animated value in a property that uses the
                    // same type as the animation. In this case, you can use the
                    // float value in the translationX property.
                }
            });
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                    // You can use the animated value in a property that uses the
                    // same type as the animation. In this case, you can use the
                    // float value in the translationX property.
                    int animatedValue = (int)updatedAnimation.getAnimatedValue();
                    LinearLayout linearLayout = tab.view;
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
                    layoutParams.width = animatedValue;
                    linearLayout.setLayoutParams(layoutParams);
                }
            });
        }
    }

    static public class NewsClassDialog extends DialogFragment {
        ArrayList selectedItems;
        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            from_dialog = true;
            tablayout.selectTab(tablayout.getTabAt(0));
            final boolean[] visible_update = new boolean[news_class_visible.length];
            System.arraycopy(news_class_visible, 0, visible_update, 0, visible_update.length);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Set the dialog title
            builder.setTitle("编辑分类")
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setMultiChoiceItems(news_class_names, visible_update,
                            (dialog, which, isChecked) -> visible_update[which] = isChecked)
                    // Set the action buttons
                    .setPositiveButton("ok", (dialog, id) -> {
                        // User clicked OK, so save the selectedItems results somewhere
                        // or return them to the component that opened the dialog
                        for(int i = 0; i < visible_update.length; i++) {
                            if(visible_update[i] != news_class_visible[i]) {
                                if(visible_update[i]) {
                                    main_news_fragment.news_class_handler[i] = new ClassHandler(tablayout.newTab().setText(news_class_names[i]));
                                    tablayout.addTab(main_news_fragment.news_class_handler[i].tab, 1, false);
                                    news_class_handler[i].create_animation();
                                }else {
                                    news_class_handler[i].delete_animation();
                                }
                            }
                            news_class_visible[i] = visible_update[i];
                            from_dialog = true;
                            tablayout.selectTab(tablayout.getTabAt(0));
                        }
                    })
                    .setNegativeButton("cancel", (dialog, id) -> { });
            return builder.create();
        }
    }
}

package com.example.newsapp;
import android.annotation.SuppressLint;
import android.os.Bundle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.android.material.tabs.TabLayout;
//import com.scwang.smart.refresh.footer.ClassicsFooter;
//import com.scwang.smart.refresh.header.ClassicsHeader;
//import com.scwang.smart.refresh.layout.api.RefreshLayout;
//import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
//import com.scwang.smart.refresh.layout.listener.OnRefreshListener;


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
    private String[] mStrs = {"aaa", "bbb", "ccc", "airsaid"};

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    static private String[] news_class_names = {"新闻", "论文"};
    static private boolean[] news_class_visible = {true, true};
    static private TabLayout tablayout;
    static private ClassHandler[] news_class_handler = {null, null};
    static private boolean from_dialog = false;
    static private String pre_tab;
    static private int unfinished_animations = 0;
    public NewsListAdapter adapter;
    private boolean scrolling_to_end = false;
    private String news_type = "all";
    private boolean view_history = false;
    private EditText search_edittext;

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
    // TODO: Rename and change types and number of parameters
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

    }
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret_view =  inflater.inflate(R.layout.news_main_fragment, container, false);
        RecyclerView recyclerView = (RecyclerView) ret_view.findViewById(R.id.news_list_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager( getActivity());
        recyclerView.setLayoutManager(layoutManager);

        Handler mhandler = new EventsListUpdateHandler();
        NewsUpdater.setHandler(mhandler);

        adapter = new NewsListAdapter();
        recyclerView.setAdapter(adapter);

        refresh_callback();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {//划到最低端时
                    if(!scrolling_to_end) {
                        Updater.updatePullDownNews(news_type);
                    }
                    scrolling_to_end = true;
                } else if (!recyclerView.canScrollVertically(-1)) {//划到最顶端时
                    if(!scrolling_to_end) {
                        Updater.updatePullUpNews(news_type);
                    }
                    scrolling_to_end = true;
                } else {
                    scrolling_to_end = false;
                }
            }
        });
        scrolling_to_end = true;

        pre_tab = "全部";
        unfinished_animations = 0;
        tablayout = (TabLayout)ret_view.findViewById(R.id.news_tab_layout);
        tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        TabLayout.Tab temp_tab = tablayout.newTab().setText("全部");
        tablayout.addTab(temp_tab);
        LinearLayout linearLayout = (LinearLayout)temp_tab.view;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = 200;
        linearLayout.setLayoutParams(layoutParams);
        for(int i = 0; i < news_class_names.length; i++) {
            news_class_visible[i] = true;
            news_class_handler[i] = new ClassHandler(tablayout.newTab().setText(news_class_names[i]));
            tablayout.addTab(news_class_handler[i].tab);
            linearLayout = (LinearLayout)news_class_handler[i].tab.view;
            layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams.width = 200;
            linearLayout.setLayoutParams(layoutParams);
        }
        temp_tab = tablayout.newTab().setText("+");
        tablayout.addTab(temp_tab);
        linearLayout = temp_tab.view;
        layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = 100;
        linearLayout.setLayoutParams(layoutParams);

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            void change(TabLayout.Tab tab) {
                if(from_dialog) {
                    from_dialog = false;
                    return;
                }
                CharSequence text = tab.getText();
                pre_tab = text.toString();
                if ("全部".contentEquals(text)) {
                    news_type = "all";
                    changeType(news_type);
                } else if ("新闻".contentEquals(text)) {
                    news_type = "news";
                    changeType(news_type);
                } else if ("论文".contentEquals(text)) {
                    news_type = "paper";
                    changeType(news_type);
                } else if ("+".contentEquals(text)) {
                    NewsClassDialog dialog = new NewsClassDialog();
                    dialog.show(getActivity().getSupportFragmentManager(), "choose_class_dialog");
                }
            }
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                change(tab);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //nop
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                change(tab);
            }
        });

        ((TabLayout)ret_view.findViewById(R.id.history_tab_layout)).addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    view_history = false;
                }
                if(tab.getPosition() == 1) {
                    view_history = true;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        search_edittext = ret_view.findViewById(R.id.news_list_search_edittext);
        ((ImageButton)ret_view.findViewById(R.id.search_button))
                .setOnClickListener(view -> searchUpdate(search_edittext.getText().toString()));

        return ret_view;
        //RefreshLayout refreshLayout = (RefreshLayout)ret_view.findViewById(R.id.refreshLayout);
        //refreshLayout.setRefreshHeader(new ClassicsHeader(this.getContext()));
        //refreshLayout.setRefreshFooter(new ClassicsFooter(this.getContext()));
        //refreshLayout.setOnRefreshListener(new OnRefreshListener() {
        //  @Override
        //public void onRefresh(RefreshLayout refreshlayout) {
        //  refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        //}
        //});
        //refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
        // @Override
        // public void onLoadMore(RefreshLayout refreshlayout) {
        //     refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
        //  }
        // });
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
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                    // You can use the animated value in a property that uses the
                    // same type as the animation. In this case, you can use the
                    // float value in the translationX property.
                    int animatedValue = (int)updatedAnimation.getAnimatedValue();
                    LinearLayout linearLayout = (LinearLayout)tab.view;
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
                    layoutParams.width = animatedValue;
                    linearLayout.setLayoutParams(layoutParams);
                }
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
                    LinearLayout linearLayout = (LinearLayout)tab.view;
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
                    layoutParams.width = animatedValue;
                    linearLayout.setLayoutParams(layoutParams);
                }
            });
        }
    }
    static public class NewsClassDialog extends DialogFragment {
        ArrayList selectedItems;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            from_dialog = true;
            tablayout.selectTab(tablayout.getTabAt(0));
            final boolean visible_update[] = new boolean[news_class_visible.length];
            for(int i = 0; i < visible_update.length; i++) {
                visible_update[i] = news_class_visible[i];
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Set the dialog title
            builder.setTitle("编辑分类")
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setMultiChoiceItems(news_class_names, visible_update,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which,
                                                    boolean isChecked) {
                                    visible_update[which] = isChecked;
                                }
                            })
                    // Set the action buttons
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK, so save the selectedItems results somewhere
                            // or return them to the component that opened the dialog
                            long animation_duration = 250;
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
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            return builder.create();
        }
    }
}

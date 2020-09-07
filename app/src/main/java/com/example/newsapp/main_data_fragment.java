package com.example.newsapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link main_data_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class main_data_fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private LinearLayout column;//柱状图绘制的地方
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public main_data_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment main_data_fragment.
     */
    // TODO: Rename and change types and number of parameters
    @NotNull
    public static main_data_fragment newInstance(String param1, String param2) {
        main_data_fragment fragment = new main_data_fragment();
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
        // return inflater.inflate(R.layout.data_main_fragment, container, false);
        View ret_view= inflater.inflate(R.layout.data_main_fragment, container, false);
        column = ret_view.findViewById(R.id.column);
        barChart();
        return ret_view;
    }

    // 初始化柱状图数据（可以根据自己需要插入数据）
    private void barChart() {
        //第一个为空，它需要占一个位置
        String[] transverse = {"","周一","周二","周三","周四","周五","周六","周日"};
        String[] vertical = {"0", "2h", "4h", "8h", "10h"};
        //这里的数据是根据你横列有几个来设的，如上面的横列星期有周一到周日，所以这里设置七个数据
        int[] data = {420 , 380, 340, 300, 260, 220, 180};
        //这里的颜色就对应线条、文字和柱状图（可以根据自己的需要到color里设置）
        List<Integer> color = new ArrayList<>();
        color.add(R.color.colorAccent);
        color.add(R.color.colorPrimary);
        color.add(R.color.colorPrimaryDark);
        column.addView(new DataDrawerView(this.getContext(), transverse, vertical, color, data));
    }

}
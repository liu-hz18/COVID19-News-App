package com.example.newsapp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private BarChart mBarChart1;
    private BarChart mBarChart2;
    private BarChart mBarChart3;
    private BarChart mBarChart4;
    private BarChart mBarChart5;
    private BarChart mBarChart6;
    private BarChart mBarChart7;
    private BarChart mBarChart8;
    private BarCharts mBarCharts;

    private String[] color = {"#C4FF8E", "#FFF88D", "#FFD38C", "#8CEBFF", "#FF8F9D", "#6BF3AD", "#C4FF8E", "#FFF88D", "#FFD38C", "#FFF88D", "#FFD38C", "#8CEBFF"};

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
        mBarCharts = new BarCharts();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret_view = inflater.inflate(R.layout.data_main_fragment, container, false);
        mBarChart1 = ret_view.findViewById(R.id.spreadBarChart1);
        mBarChart2 = ret_view.findViewById(R.id.spreadBarChart2);
        mBarChart3 = ret_view.findViewById(R.id.spreadBarChart3);
        mBarChart4 = ret_view.findViewById(R.id.spreadBarChart4);
        mBarChart5 = ret_view.findViewById(R.id.spreadBarChart5);
        mBarChart6 = ret_view.findViewById(R.id.spreadBarChart6);
        mBarChart7 = ret_view.findViewById(R.id.spreadBarChart7);
        mBarChart8 = ret_view.findViewById(R.id.spreadBarChart8);

        mBarCharts.showBarChart(mBarChart1, getCountryDeadBarData(), true);
        mBarCharts.showBarChart(mBarChart2, getCountryConfirmedBarData(), true);
        mBarCharts.showBarChart(mBarChart3, getCountryCuredBarData(), true);
        mBarCharts.showBarChart(mBarChart4, getCountrySuspectedBarData(), true);
        mBarCharts.showBarChart(mBarChart5, getChinaDeadBarData(), true);
        mBarCharts.showBarChart(mBarChart6, getChinaConfirmedBarData(), true);
        mBarCharts.showBarChart(mBarChart7, getChinaCuredBarData(), true);
        mBarCharts.showBarChart(mBarChart8, getChinaSuspectedBarData(), true);
        return ret_view;
    }

    public BarData getCountryDeadBarData() {
        List<CountryEpidemicEntity> countryList = EpidemicDataFetcher.fetchCountryData(false);
        HashMap<String,Integer> countryDead=new HashMap<>();
        for(CountryEpidemicEntity country: countryList) {
            countryDead.put(country.mRegion, country.getmDead());
        }
        List<Map.Entry<String, Integer>> list_Data = new ArrayList<>(countryDead.entrySet());
        Collections.sort(list_Data, (left, right) -> right.getValue().compareTo(left.getValue()));

        List<IBarDataSet> sets = new ArrayList<>();
        List<BarEntry> Values = new ArrayList<>();
        List<String> label_name=new ArrayList<>();
        int count = 1;
        for (Map.Entry<String,Integer> mapping : list_Data) {
            label_name.add(mapping.getKey());
            Values.add(new BarEntry(count, mapping.getValue()));
            count++;
        }

        XAxis xAxis = mBarChart1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // 设置x轴显示在下方，默认在上方
        xAxis.setDrawGridLines(false); // 将此设置为true，绘制该轴的网格线。
        xAxis.setLabelCount(countryDead.size());  // 设置x轴上的标签个数
        xAxis.setGranularity(1);
        // 设置x轴显示的值的格式
        xAxis.setValueFormatter((value, axis) -> {
            if ((int) value < label_name.size()) {
                return label_name.get((int)value);
            } else {
                return "";
            }
        });

        YAxis yAxis_left = mBarChart1.getAxisLeft();
        yAxis_left.setAxisMinimum(0f);  // 设置y轴的最小值
        yAxis_left.setValueFormatter(new LargeValueFormatter());

        BarDataSet barDataSet = new BarDataSet(Values, "各国死亡人数");
        barDataSet.setColor(Color.parseColor(color[0]));
        barDataSet.setDrawValues(true);
        sets.add(barDataSet);
        return new BarData(sets);
    }

    public BarData getCountryConfirmedBarData() {
        List<CountryEpidemicEntity> countryList = EpidemicDataFetcher.fetchCountryData(false);
        HashMap<String,Integer> countryConfirmed=new HashMap<>();
        for(CountryEpidemicEntity country: countryList) {
            countryConfirmed.put(country.mRegion, country.getmConfirmed());
        }
        List<Map.Entry<String, Integer>> list_Data = new ArrayList<>(countryConfirmed.entrySet());
        Collections.sort(list_Data, (left, right) -> right.getValue().compareTo(left.getValue()));

        List<IBarDataSet> sets = new ArrayList<>();
        List<BarEntry> Values = new ArrayList<>();
        List<String> label_name=new ArrayList<>();
        int count=1;
        for (Map.Entry<String,Integer> mapping : list_Data) {
            label_name.add(mapping.getKey());
            Values.add(new BarEntry(count, mapping.getValue()));
            count++;
        }

        XAxis xAxis = mBarChart2.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // 设置x轴显示在下方，默认在上方
        xAxis.setDrawGridLines(false); // 将此设置为true，绘制该轴的网格线。
        xAxis.setLabelCount(countryConfirmed.size());  // 设置x轴上的标签个数
        xAxis.setGranularity(1);
        // 设置x轴显示的值的格式
        xAxis.setValueFormatter((value, axis) -> {
            if ((int) value < label_name.size()) {
                return label_name.get((int)value);
            } else {
                return "";
            }
        });
        YAxis yAxis_left = mBarChart2.getAxisLeft();
        yAxis_left.setAxisMinimum(0f);  // 设置y轴的最小值
        yAxis_left.setValueFormatter(new LargeValueFormatter());
        BarDataSet barDataSet = new BarDataSet(Values, "各国确诊人数");
        barDataSet.setDrawValues(true);
        barDataSet.setColor(Color.parseColor(color[1]));
        sets.add(barDataSet);
        return new BarData(sets);
    }

    public BarData getCountryCuredBarData() {
        List<CountryEpidemicEntity> countryList = EpidemicDataFetcher.fetchCountryData(false);
        HashMap<String,Integer> countryCured = new HashMap<>();
        for(CountryEpidemicEntity country: countryList) {
            countryCured.put(country.mRegion, country.getmCured());
        }
        List<Map.Entry<String, Integer>> list_Data = new ArrayList<>(countryCured.entrySet());
        Collections.sort(list_Data, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        List<IBarDataSet> sets = new ArrayList<>();
        List<BarEntry> Values = new ArrayList<>();
        List<String> label_name=new ArrayList<>();
        int count = 1;
        for (Map.Entry<String,Integer> mapping : list_Data) {
            label_name.add(mapping.getKey());
            Values.add(new BarEntry(count, mapping.getValue()));
            count++;
        }

        XAxis xAxis = mBarChart3.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // 设置x轴显示在下方，默认在上方
        xAxis.setDrawGridLines(false); // 将此设置为true，绘制该轴的网格线。
        xAxis.setLabelCount(countryCured.size());  // 设置x轴上的标签个数
        xAxis.setGranularity(1);
        // 设置x轴显示的值的格式
        xAxis.setValueFormatter((value, axis) -> {
            if ((int) value < label_name.size()) {
                return label_name.get((int)value);
            } else {
                return "";
            }
        });
        YAxis yAxis_left = mBarChart3.getAxisLeft();
        yAxis_left.setAxisMinimum(0f);  // 设置y轴的最小值
        yAxis_left.setValueFormatter(new LargeValueFormatter());
        BarDataSet barDataSet = new BarDataSet(Values, "各国治愈人数");
        barDataSet.setDrawValues(true);
        barDataSet.setColor(Color.parseColor(color[2]));
        sets.add(barDataSet);
        return new BarData(sets);
    }

    public BarData getCountrySuspectedBarData() {
        List<CountryEpidemicEntity> countryList = EpidemicDataFetcher.fetchCountryData(false);
        HashMap<String,Integer> countrySuspected=new HashMap<>();
        for(CountryEpidemicEntity country: countryList) {
            countrySuspected.put(country.mRegion, country.getmSuspected());
        }
        List<Map.Entry<String, Integer>> list_Data = new ArrayList<>(countrySuspected.entrySet());
        Collections.sort(list_Data, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        List<IBarDataSet> sets = new ArrayList<>();
        List<BarEntry> Values = new ArrayList<>();
        List<String> label_name=new ArrayList<>();
        int count=1;
        for (Map.Entry<String,Integer> mapping : list_Data) {
            label_name.add(mapping.getKey());
            Values.add(new BarEntry(count, mapping.getValue()));
            count++;
        }

        XAxis xAxis = mBarChart4.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // 设置x轴显示在下方，默认在上方
        xAxis.setDrawGridLines(false); // 将此设置为true，绘制该轴的网格线。
        xAxis.setLabelCount(countrySuspected.size());  // 设置x轴上的标签个数
        xAxis.setGranularity(1);
        // 设置x轴显示的值的格式
        xAxis.setValueFormatter((value, axis) -> {
            if ((int) value < label_name.size()) {
                return label_name.get((int)value);
            } else {
                return "";
            }
        });
        YAxis yAxis_left = mBarChart4.getAxisLeft();
        yAxis_left.setAxisMinimum(0f);  // 设置y轴的最小值
        yAxis_left.setValueFormatter(new LargeValueFormatter());
        BarDataSet barDataSet = new BarDataSet(Values, "各国疑似人数");
        barDataSet.setDrawValues(true);
        barDataSet.setColor(Color.parseColor(color[3]));
        sets.add(barDataSet);
        return new BarData(sets);
    }


    public BarData getChinaDeadBarData() {
        List<ChinaProvinceEpidemicEntity> chinaList = EpidemicDataFetcher.fetchChinaData(false);
        HashMap<String,Integer> chinaDead=new HashMap<>();
        for(ChinaProvinceEpidemicEntity province: chinaList) {
            chinaDead.put(province.mRegion, province.getmDead());
        }
        List<Map.Entry<String, Integer>> list_Data = new ArrayList<>(chinaDead.entrySet());
        Collections.sort(list_Data, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        List<IBarDataSet> sets = new ArrayList<>();
        List<BarEntry> Values = new ArrayList<>();
        List<String> label_name=new ArrayList<>();
        int count=1;
        for (Map.Entry<String,Integer> mapping : list_Data) {
            label_name.add(mapping.getKey());
            Values.add(new BarEntry(count, mapping.getValue()));
            count++;
        }

        XAxis xAxis = mBarChart5.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // 设置x轴显示在下方，默认在上方
        xAxis.setDrawGridLines(false); // 将此设置为true，绘制该轴的网格线。
        xAxis.setLabelCount(chinaDead.size());  // 设置x轴上的标签个数
        xAxis.setGranularity(1);
        // 设置x轴显示的值的格式
        xAxis.setValueFormatter((value, axis) -> {
            if ((int) value < label_name.size()) {
                return label_name.get((int)value);
            } else {
                return "";
            }
        });

        YAxis yAxis_left = mBarChart5.getAxisLeft();
        yAxis_left.setAxisMinimum(0f);  // 设置y轴的最小值
        yAxis_left.setValueFormatter(new LargeValueFormatter());

        BarDataSet barDataSet = new BarDataSet(Values, "中国各省死亡人数");
        barDataSet.setDrawValues(true);
        barDataSet.setColor(Color.parseColor(color[6]));
        sets.add(barDataSet);
        return new BarData(sets);
    }

    public BarData getChinaConfirmedBarData() {
        List<ChinaProvinceEpidemicEntity> chinaList = EpidemicDataFetcher.fetchChinaData(false);
        HashMap<String,Integer> chinaConfirmed=new HashMap<>();
        for(ChinaProvinceEpidemicEntity province: chinaList) {
            chinaConfirmed.put(province.mRegion, province.getmConfirmed());
        }
        List<Map.Entry<String, Integer>> list_Data = new ArrayList<>(chinaConfirmed.entrySet());
        Collections.sort(list_Data, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        List<IBarDataSet> sets = new ArrayList<>();
        List<BarEntry> Values = new ArrayList<>();
        List<String> label_name=new ArrayList<>();
        int count=1;
        for (Map.Entry<String,Integer> mapping : list_Data) {
            label_name.add(mapping.getKey());
            Values.add(new BarEntry(count, mapping.getValue()));
            count++;
        }

        XAxis xAxis = mBarChart6.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // 设置x轴显示在下方，默认在上方
        xAxis.setDrawGridLines(false); // 将此设置为true，绘制该轴的网格线。
        xAxis.setLabelCount(chinaConfirmed.size());  // 设置x轴上的标签个数
        xAxis.setGranularity(1);
        // 设置x轴显示的值的格式
        xAxis.setValueFormatter((value, axis) -> {
            if ((int) value < label_name.size()) {
                return label_name.get((int)value);
            } else {
                return "";
            }
        });

        YAxis yAxis_left = mBarChart6.getAxisLeft();
        yAxis_left.setAxisMinimum(0f);  // 设置y轴的最小值
        yAxis_left.setValueFormatter(new LargeValueFormatter());

        BarDataSet barDataSet = new BarDataSet(Values, "中国各省确诊人数");
        barDataSet.setDrawValues(true);
        barDataSet.setColor(Color.parseColor(color[7]));
        sets.add(barDataSet);
        return new BarData(sets);
    }


    public BarData getChinaCuredBarData() {
        List<ChinaProvinceEpidemicEntity> chinaList = EpidemicDataFetcher.fetchChinaData(false);
        HashMap<String,Integer> chinaCured=new HashMap<>();
        for(ChinaProvinceEpidemicEntity province: chinaList) {
            chinaCured.put(province.mRegion, province.getmCured());
        }
        List<Map.Entry<String, Integer>> list_Data = new ArrayList<>(chinaCured.entrySet());
        Collections.sort(list_Data, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        List<IBarDataSet> sets = new ArrayList<>();
        List<BarEntry> Values = new ArrayList<>();
        List<String> label_name=new ArrayList<>();
        int count=1;
        for (Map.Entry<String,Integer> mapping : list_Data) {
            label_name.add(mapping.getKey());
            Values.add(new BarEntry(count, mapping.getValue()));
            count++;
        }

        XAxis xAxis = mBarChart7.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // 设置x轴显示在下方，默认在上方
        xAxis.setDrawGridLines(false); // 将此设置为true，绘制该轴的网格线。
        xAxis.setLabelCount(chinaCured.size());  // 设置x轴上的标签个数
        xAxis.setGranularity(1);
        // 设置x轴显示的值的格式
        xAxis.setValueFormatter((value, axis) -> {
            if ((int) value < label_name.size()) {
                return label_name.get((int)value);
            } else {
                return "";
            }
        });

        YAxis yAxis_left = mBarChart7.getAxisLeft();
        yAxis_left.setAxisMinimum(0f);  // 设置y轴的最小值
        yAxis_left.setValueFormatter(new LargeValueFormatter());

        BarDataSet barDataSet = new BarDataSet(Values, "中国各省治愈人数");
        barDataSet.setDrawValues(true);
        barDataSet.setColor(Color.parseColor(color[8]));
        sets.add(barDataSet);
        return new BarData(sets);
    }

    public BarData getChinaSuspectedBarData() {
        List<ChinaProvinceEpidemicEntity> chinaList = EpidemicDataFetcher.fetchChinaData(false);
        HashMap<String,Integer> chinaSuspected=new HashMap<>();
        for(ChinaProvinceEpidemicEntity privince: chinaList) {
            chinaSuspected.put(privince.mRegion, privince.getmSuspected());
        }
        List<Map.Entry<String, Integer>> list_Data = new ArrayList<>(chinaSuspected.entrySet());
        Collections.sort(list_Data, (o1, o2) -> {
            //o1 to o2升序   o2 to o1降序
            return o2.getValue().compareTo(o1.getValue());
        });

        List<IBarDataSet> sets = new ArrayList<>();
        List<BarEntry> Values = new ArrayList<>();
        List<String> label_name=new ArrayList<>();
        int count=1;
        for (Map.Entry<String,Integer> mapping : list_Data) {
            label_name.add(mapping.getKey());
            Values.add(new BarEntry(count, mapping.getValue()));
            count++;
        }

        XAxis xAxis = mBarChart8.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // 设置x轴显示在下方，默认在上方
        xAxis.setDrawGridLines(false); // 将此设置为true，绘制该轴的网格线。
        xAxis.setLabelCount(chinaSuspected.size());  // 设置x轴上的标签个数
        xAxis.setGranularity(1);
        // 设置x轴显示的值的格式
        xAxis.setValueFormatter((value, axis) -> {
            if ((int) value < label_name.size()) {
                return label_name.get((int)value);
            } else {
                return "";
            }
        });

        YAxis yAxis_left = mBarChart8.getAxisLeft();
        yAxis_left.setAxisMinimum(0f);  // 设置y轴的最小值
        yAxis_left.setValueFormatter(new LargeValueFormatter());

        BarDataSet barDataSet = new BarDataSet(Values, "中国各省疑似人数");
        barDataSet.setDrawValues(true);
        barDataSet.setColor(Color.parseColor(color[9]));
        sets.add(barDataSet);
        return new BarData(sets);
    }
}

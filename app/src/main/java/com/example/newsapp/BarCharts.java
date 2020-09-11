package com.example.newsapp;

import android.graphics.Matrix;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;

import org.jetbrains.annotations.NotNull;

public class BarCharts {
    /**
     * @param barChart  控件
     * @param barData   数据
     * @param isSlither 用来控制是否可以滑动
     */
    public void showBarChart(@NotNull BarChart barChart, BarData barData, boolean isSlither) {
        // 设置是否可以触摸
        barChart.setTouchEnabled(isSlither);
        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        // 是否可以拖拽
        barChart.setDragEnabled(isSlither);//放大可拖拽
        // 是否可以缩放
        barChart.setScaleEnabled(false);
        // 集双指缩放
        barChart.setPinchZoom(false);
        // 如果打开，背景矩形将出现在已经画好的绘图区域的后边。
        barChart.setDrawGridBackground(false);
        barChart.setData(barData);
        barChart.setVisibleXRangeMaximum(5);
        if (isSlither) {
            //当为true时,放大图
            // 为了使 柱状图成为可滑动的,将水平方向 放大 2.5倍
            barChart.invalidate();
            Matrix mMatrix = new Matrix();
            mMatrix.postScale(2f, 1f);
            barChart.getViewPortHandler().refresh(mMatrix, barChart, false);
            barChart.animateY(1000);
        } else {
            //当为false时 不对图进行操作
            barChart.animateY(1000);
        }
    }
}

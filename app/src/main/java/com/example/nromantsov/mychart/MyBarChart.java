package com.example.nromantsov.mychart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.utils.Utils;

/**
 * Created by n.romantsov on 05.12.2016.
 */

public class MyBarChart extends BarChart {

    public MyBarChart(Context context) {
        super(context);
    }

    public MyBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new MyBarChartRender(this, mAnimator, mViewPortHandler);
        mChartTouchListener = new MyBarChartListener(this, mViewPortHandler.getMatrixTouch(), 3f);
    }

    @Override
    protected void calcMinMax() {
        mXAxis.calculate(mData.getXMin(), mData.getXMax() + mData.getBarWidth() * 2f);

        // calculate axis range (min / max) according to provided data
        mAxisLeft.calculate(mData.getYMin(YAxis.AxisDependency.LEFT), mData.getYMax(YAxis.AxisDependency.LEFT));
    }


}

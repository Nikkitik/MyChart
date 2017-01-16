package com.example.nromantsov.mychart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.DataRenderer;

/**
 * Created by n.romantsov on 05.12.2016.
 */

public class MyBarChart extends BarChart {

    public interface Call {
        void doCall(float x);
    }

    Call ref;

    void setCall(Call o) {
        ref = o;
        ((MyBarChartListener) mChartTouchListener).setRef(o);
    }

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

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mRenderer = new MyBarChartRender(this, mAnimator, mViewPortHandler);
        mChartTouchListener = new MyBarChartListener(this, mViewPortHandler.getMatrixTouch(), 3f);
    }

    public void setColor(int[] color) {
        ((MyBarChartRender)mRenderer).setColor(color);
    }

    public void setColorDialog(int[] color) {
        ((MyBarChartDialogRender)mRenderer).setColor(color);
    }

    @Override
    protected void calcMinMax() {
        mXAxis.calculate(mData.getXMin(), mData.getXMax() + mData.getBarWidth() * 2f);

        // calculate axis range (min / max) according to provided data
        mAxisLeft.calculate(mData.getYMin(YAxis.AxisDependency.LEFT), mData.getYMax(YAxis.AxisDependency.LEFT));
    }

    public void setRender(com.github.mikephil.charting.renderer.DataRenderer render) {
        mRenderer = render;
    }

    public DataRenderer createRender() {
        return new MyBarChartDialogRender(this, mAnimator, mViewPortHandler);
    }
}

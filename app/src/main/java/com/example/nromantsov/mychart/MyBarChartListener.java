package com.example.nromantsov.mychart;

import android.graphics.Matrix;

import android.util.Log;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

/**
 * Created by n.romantsov on 08.12.2016.
 */

public class MyBarChartListener extends BarLineChartTouchListener {

    private MyBarChart.Call ref;

    public void setRef(MyBarChart.Call ref) {
        this.ref = ref;
    }

    /**
     * Constructor with initialization parameters.
     *
     * @param chart               instance of the chart
     * @param touchMatrix         the touch-matrix of the chart
     * @param dragTriggerDistance the minimum movement distance that will be interpreted as a "drag" gesture in dp (3dp equals)
     */
    public MyBarChartListener(BarLineChartBase<? extends BarLineScatterCandleBubbleData<? extends IBarLineScatterCandleBubbleDataSet<? extends Entry>>> chart, Matrix touchMatrix, float dragTriggerDistance) {
        super(chart, touchMatrix, dragTriggerDistance);
    }

    @Override
    public void onLongPress(MotionEvent e) {

        mLastGesture = ChartGesture.LONG_PRESS;

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartLongPressed(e);
        }

        Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());
        Log.d("...", "X: " + e.getX() + " Y: " + e.getY());
        performHighlight(h, e);
        ref.doCall();
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d("...", "Single X: " + e.getX() + " Y: " + e.getY());
        mLastGesture = ChartGesture.SINGLE_TAP;

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartSingleTapped(e);
        }

        performHighlight(null, e);

        return false;
    }
}




package com.example.nromantsov.mychart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by n.romantsov on 06.12.2016.
 */

public class MyXAxisRender extends XAxisRenderer {
    private MyBarChart chart;

    public MyXAxisRender(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans, MyBarChart chart) {
        super(viewPortHandler, xAxis, trans);
        this.chart = chart;
    }

    protected Path mRenderGridLinesPath = new Path();
    protected float[] mRenderGridLinesBuffer = new float[2];

    @Override
    public void renderGridLines(Canvas c) {
        List<Float> list = new ArrayList<>();
        if (!mXAxis.isDrawGridLinesEnabled() || !mXAxis.isEnabled())
            return;

        int clipRestoreCount = c.save();
        c.clipRect(getGridClippingRect());

        for (int i = 2; i + 2 < chart.getData().getEntryCount(); i += 6) {
            if (mViewPortHandler.isInBoundsLeft(chart.getData().getDataSetByIndex(0).getEntriesForXValue(i).get(0).getX())
                    || mViewPortHandler.isInBoundsRight(chart.getData().getDataSetByIndex(0).getEntriesForXValue(i + 2).get(0).getX())) {
                list.add(chart.getData().getDataSetByIndex(0).getEntriesForXValue(i).get(0).getX());
                list.add(chart.getData().getDataSetByIndex(0).getEntriesForXValue(i + 2).get(0).getX());
            }
        }

        float[] array = new float[list.size() * 2];
        for (int i = 0; i < list.size(); i++) {
            array[i * 2] = list.get(i);
            array[i * 2 + 1] = list.get(i);
        }

        mTrans.pointValuesToPixel(array);
        setupGridPaint();

        Path gridLinePath = mRenderGridLinesPath;
        gridLinePath.reset();

        for (int i = 0; i < array.length; i += 4) {

            drawGridLine(c, array[i], array[i + 2], gridLinePath);
        }

        c.restoreToCount(clipRestoreCount);
    }

    protected void drawGridLine(Canvas c, float x, float y, Path gridLinePath) {

        mGridPaint.setStyle(Paint.Style.FILL);

        gridLinePath.moveTo(x - 43, mViewPortHandler.contentBottom());
        gridLinePath.lineTo(x - 43, mViewPortHandler.contentTop());
        gridLinePath.lineTo(y + 43, mViewPortHandler.contentTop());
        gridLinePath.lineTo(y + 43, mViewPortHandler.contentBottom());
        gridLinePath.lineTo(x - 43, mViewPortHandler.contentBottom());

        // draw a path because lines don't support dashing on lower android versions
        c.drawPath(gridLinePath, mGridPaint);

        gridLinePath.reset();
    }
}

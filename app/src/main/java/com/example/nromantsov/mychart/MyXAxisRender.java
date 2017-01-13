package com.example.nromantsov.mychart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by n.romantsov on 06.12.2016.
 */

class MyXAxisRender extends XAxisRenderer {
    private MyBarChart chart;
    private float numX = 3.5f;
    private float getX = 0;
    private int startGridLine, endGridLine, countGridLine;
    private float widthGridLine = 14;

    MyXAxisRender(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans, MyBarChart chart) {
        super(viewPortHandler, xAxis, trans);
        this.chart = chart;
    }

    public void setNumX(float numX) {
        this.numX = numX;
    }

    public void setGetXY(float getX) {
        this.getX = getX;
    }

    public void setNumGridLines(int startGridLine, int endGridLine, float widthGridLine) {
        this.startGridLine = startGridLine;
        this.countGridLine = endGridLine * 2;
        this.endGridLine = endGridLine;
        this.widthGridLine = widthGridLine;
    }

    private Path mRenderGridLinesPath = new Path();

    @Override
    public void renderGridLines(Canvas c) {
        List<Float> list = new ArrayList<>();
        if (!mXAxis.isDrawGridLinesEnabled() || !mXAxis.isEnabled())
            return;

        int clipRestoreCount = c.save();
        c.clipRect(getGridClippingRect());

        for (int i = startGridLine; i + 2 < chart.getData().getEntryCount(); i += countGridLine) {
            if (mViewPortHandler.isInBoundsLeft(chart.getData().getDataSetByIndex(0).getEntriesForXValue(i).get(0).getX())
                    || mViewPortHandler.isInBoundsRight(chart.getData().getDataSetByIndex(0).getEntriesForXValue(i + 2).get(0).getX())) {
                list.add(chart.getData().getDataSetByIndex(0).getEntriesForXValue(i).get(0).getX());
                list.add(chart.getData().getDataSetByIndex(0).getEntriesForXValue(i + endGridLine).get(0).getX());
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

        gridLinePath.moveTo(x - widthGridLine * 1.3f, mViewPortHandler.contentBottom());
        gridLinePath.lineTo(x - widthGridLine * 1.3f, mViewPortHandler.contentTop());
        gridLinePath.lineTo(y - widthGridLine * 1.3f, mViewPortHandler.contentTop());
        gridLinePath.lineTo(y - widthGridLine * 1.3f, mViewPortHandler.contentBottom());
        gridLinePath.lineTo(x - widthGridLine * 1.3f, mViewPortHandler.contentBottom());

        // draw a path because lines don't support dashing on lower android versions
        c.drawPath(gridLinePath, mGridPaint);

        gridLinePath.reset();
    }

    @Override
    protected void drawLabels(Canvas c, float pos, MPPointF anchor) {
        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();

        float[] positions = new float[48];
        String[] strings = new String[48];
        int hour = 0;
        float k = numX;
        for (int i = 0; i < positions.length; i += 2) {

            if (getX == 0) {
                positions[i] = k; //mXAxis.mCenteredEntries[i / 2];
                positions[i + 1] = k; //mXAxis.mCenteredEntries[i / 2];
                strings[i] = (hour < 10 ? "0" : "") + hour + ":00"; //mXAxis.mCenteredEntries[i / 2];
                strings[i + 1] = (hour < 10 ? "0" : "") + hour + ":00"; //mXAxis.mCenteredEntries[i / 2];
                hour++;
                k += 6;
//            drawLabel(c, "ssss", x, pos, anchor, labelRotationAngleDegrees);
            } else {
                int h = (int) (getX / 6);
                positions[i] = k; //mXAxis.mCenteredEntries[i / 2];
                positions[i + 1] = k; //mXAxis.mCenteredEntries[i / 2];
                strings[i] = (h < 10 ? "0" : "") + h + ":" + (hour == 0 ? "0" : "") + hour; //mXAxis.mCenteredEntries[i / 2];
                strings[i + 1] = (h < 10 ? "0" : "") + h + ":" + (hour == 0 ? "0" : "") + hour; //mXAxis.mCenteredEntries[i / 2];
                hour+=10;
                k += 5;
            }
        }

        mTrans.pointValuesToPixel(positions);

        for (int i = 0; i < positions.length; i += 4) {
            if (mViewPortHandler.isInBoundsX(positions[i]))
                drawLabel(c, strings[i], positions[i], pos, anchor, labelRotationAngleDegrees);
        }
    }
}

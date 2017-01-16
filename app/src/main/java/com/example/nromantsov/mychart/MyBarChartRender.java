package com.example.nromantsov.mychart;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.Range;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by n.romantsov on 05.12.2016.
 */

class MyBarChartRender extends BarChartRenderer {

    private static final float CORNER_RADIUS_BIG = Utils.convertDpToPixel(4f);
    private static final float CORNER_RADIUS_SMALL = Utils.convertDpToPixel(2f);
    private static final int RADIUS_BARS_THRESHOLD = 10;
    private int[] color = new int[] {Color.rgb(0, 101, 105), Color.rgb(138, 217, 219), Color.rgb(0, 155, 161)};

    MyBarChartRender(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    public void setColor(int[] color) {
        this.color = color;
    }

    private RectF mBarShadowRectBuffer = new RectF();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        float cornerRadius;
        if (dataSet.getEntryCount() > RADIUS_BARS_THRESHOLD)
            cornerRadius = CORNER_RADIUS_SMALL;
        else
            cornerRadius = CORNER_RADIUS_BIG;

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));

        final boolean drawBorder = dataSet.getBarBorderWidth() > 0.f;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        // draw the bar shadow before the values
        if (mChart.isDrawBarShadowEnabled()) {
            mShadowPaint.setColor(dataSet.getBarShadowColor());

            BarData barData = mChart.getBarData();

            final float barWidth = barData.getBarWidth();
            final float barWidthHalf = barWidth / 2.0f;
            float x;

            for (int i = 0, count = Math.min((int) (Math.ceil((float) (dataSet.getEntryCount()) * phaseX)), dataSet.getEntryCount());
                 i < count;
                 i++) {

                BarEntry e = dataSet.getEntryForIndex(i);

                x = e.getX();

                mBarShadowRectBuffer.left = x - barWidthHalf;
                mBarShadowRectBuffer.right = x + barWidthHalf;

                trans.rectValueToPixel(mBarShadowRectBuffer);

                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left))
                    break;

                mBarShadowRectBuffer.top = mViewPortHandler.contentTop();
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom();

                c.drawRect(mBarShadowRectBuffer, mShadowPaint);
            }
        }

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());

        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        for (int j = 0; j < buffer.size(); j += 12) {

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                continue;

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break;


            if (buffer.buffer[j + 1] == buffer.buffer[j + 9]) {
                if (buffer.buffer[j + 1] == buffer.buffer[j + 5]) {
                    mRenderPaint.setColor(color[0]);
                    c.drawPath(getPathRoundRectTop(buffer.buffer[j], buffer.buffer[j + 1],
                            buffer.buffer[j + 2], buffer.buffer[j + 3], cornerRadius), mRenderPaint);
                } else {
                    mRenderPaint.setColor(color[0]);
                    c.drawPath(getPathRectTop(buffer.buffer[j], buffer.buffer[j + 1],
                            buffer.buffer[j + 2], buffer.buffer[j + 3]), mRenderPaint);
                }
            } else {
                mRenderPaint.setColor(color[0]);
                c.drawPath(getPathRectTop(buffer.buffer[j], buffer.buffer[j + 1],
                        buffer.buffer[j + 2], buffer.buffer[j + 3]), mRenderPaint);
            }

            if (buffer.buffer[j + 5] == buffer.buffer[j + 9]) {
                mRenderPaint.setColor(color[1]);
                c.drawPath(getPathRoundRectTop(buffer.buffer[j + 4], buffer.buffer[j + 5],
                        buffer.buffer[j + 6], buffer.buffer[j + 7], cornerRadius), mRenderPaint);
            } else {
                mRenderPaint.setColor(color[1]);
                c.drawPath(getPathRectTop(buffer.buffer[j + 4], buffer.buffer[j + 5],
                        buffer.buffer[j + 6], buffer.buffer[j + 7]), mRenderPaint);

                mRenderPaint.setColor(color[2]);
                c.drawPath(getPathRoundRectTop(buffer.buffer[j + 8], buffer.buffer[j + 9],
                        buffer.buffer[j + 10], buffer.buffer[j + 11], cornerRadius), mRenderPaint);
            }

            if (drawBorder) {
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mBarBorderPaint);
            }
        }
    }

    private Path getPathRoundRectTop(float left, float top, float right, float bottom,
                                     float radius) {

        Path path = new Path();
        if (top + radius * 2 > bottom) {
            radius = radius * ((bottom - top) / (2f * radius));
        }

        path.moveTo(right, bottom);
        path.lineTo(left, bottom);
        path.arcTo(new RectF(left, top, left + radius * 2, top + radius * 2), 180, 90);
        path.arcTo(new RectF(right - radius * 2, top, right, top + radius * 2), 270, 90);

        path.close();
        return path;
    }

    private Path getPathRectTop(float left, float top, float right, float bottom) {

        Path path = new Path();

        path.moveTo(right, bottom);
        path.lineTo(left, bottom);
        path.lineTo(left, top);
        path.lineTo(right, top);
        path.lineTo(right, bottom);
        path.close();
        return path;
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        BarData barData = mChart.getBarData();
        float xMin = 0, xMax = 0;

        for (Highlight high : indices) {

            IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            BarEntry e = set.getEntryForXValue(high.getX(), high.getY());

            if (!isInBoundsX(e, set))
                continue;

            if (e.getX() % 6 == 0) {
                xMin = e.getX() - 5;
                xMax = e.getX();
            } else if ((e.getX() + 1) % 6 == 0) {
                xMin = e.getX() - 4;
                xMax = e.getX() + 1;
            } else if ((e.getX() + 2) % 6 == 0) {
                xMin = e.getX() - 3;
                xMax = e.getX() + 2;
            } else if ((e.getX() + 3) % 6 == 0) {
                xMin = e.getX() - 2;
                xMax = e.getX() + 3;
            } else if ((e.getX() + 4) % 6 == 0) {
                xMin = e.getX() - 1;
                xMax = e.getX() + 4;
            } else if ((e.getX() + 5) % 6 == 0) {
                xMin = e.getX();
                xMax = e.getX() + 5;
            }

            Transformer trans = mChart.getTransformer(set.getAxisDependency());

            prepareBarHighlight(xMin, xMax, barData.getBarWidth(), trans, 1);

            mHighlightPaint.setShadowLayer(3, 0, 1, Color.BLACK);
            c.drawRect(mBarRect, mHighlightPaint);

            prepareBarHighlight(xMin, xMax, barData.getBarWidth(), trans, 2);
            setHighlightDrawPos(high, mBarRect);
            mHighlightPaint.setShadowLayer(0, 0, 0, Color.BLACK);
            c.drawRect(mBarRect, mHighlightPaint);

            drawData(c);
        }
    }

    private void prepareBarHighlight(float xMin, float xMax, float barWidthHalf, Transformer trans, int x) {
        float left = xMin - barWidthHalf;
        float right = xMax + barWidthHalf;

        if (x != 1) {

            mBarRect.set(left, Utils.convertDpToPixel(20), right, Utils.convertDpToPixel(16));
            trans.rectToPixelPhase(mBarRect, mAnimator.getPhaseY());
            mBarRect.set(mBarRect.left, Utils.convertDpToPixel(0), mBarRect.right, Utils.convertDpToPixel(4));
            mHighlightPaint.setColor(MyColor.BROWN_COLORS);
            mHighlightPaint.setAlpha(255);
        } else {
            mBarRect.set(left, Utils.convertDpToPixel(20), right, Utils.convertDpToPixel(0));
            trans.rectToPixelPhase(mBarRect, mAnimator.getPhaseY());
            mHighlightPaint.setColor(MyColor.WHITE_COLORS);
            mHighlightPaint.setAlpha(255);
        }


    }
}

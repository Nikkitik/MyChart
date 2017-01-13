package com.example.nromantsov.mychart;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by n.romantsov on 05.12.2016.
 */

class MyBarChartDialogRender extends MyBarChartRender {

    private static final float CORNER_RADIUS_BIG = Utils.convertDpToPixel(4f);
    private static final float CORNER_RADIUS_SMALL = Utils.convertDpToPixel(2f);
    private static final int RADIUS_BARS_THRESHOLD = 10;

    MyBarChartDialogRender(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    private RectF mBarShadowRectBuffer = new RectF();

    @Override
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

        for (int j = 0; j < buffer.size(); j += 4) {

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                continue;

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break;

            if (buffer.buffer[j + 1] <= 300)
                mRenderPaint.setColor(Color.rgb(0, 101, 105));
            else if (buffer.buffer[j + 1] < 500 && buffer.buffer[j + 1] > 300)
                mRenderPaint.setColor(Color.rgb(138, 217, 219));
            else
                mRenderPaint.setColor(Color.rgb(0, 155, 161));

            c.drawPath(getPathRoundRectTop(buffer.buffer[j], buffer.buffer[j + 1],
                    buffer.buffer[j + 2], buffer.buffer[j + 3], cornerRadius), mRenderPaint);

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
}

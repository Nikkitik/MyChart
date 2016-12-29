package com.example.nromantsov.mychart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MyBarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nik);

        mChart = (MyBarChart) findViewById(R.id.chart1);
        mChart.getDescription().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        MyXAxisRender myXAxisRender = new MyXAxisRender(mChart.getViewPortHandler(), xAxis, mChart.getTransformer(YAxis.AxisDependency.LEFT), mChart);
        myXAxisRender.setNumX(3.5f);
        myXAxisRender.setNumGridLines(7, 6, 14);
        mChart.setXAxisRenderer(myXAxisRender);

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setAxisMaximum(144.5f);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(6);


        IAxisValueFormatter custom = new MyAxisValueFormatter();
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        mChart.setVisibleXRange(1, 30f);

        Legend legend = mChart.getLegend();
        legend.setEnabled(false);

        setData(144, 17);

        mChart.setCall(new MyBarChart.Call() {
            @Override
            public void doCall(float x) {
                DialogFragmentPage dialogFragmentPage = new DialogFragmentPage();
                Bundle bundle = new Bundle();
                bundle.putFloat("X", x);
                dialogFragmentPage.setArguments(bundle);

                dialogFragmentPage.setDialogInter(new DialogFragmentPage.DialogInter() {
                    @Override
                    public void onDismiss() {
                        mChart.highlightValue(null);
                    }
                });

                dialogFragmentPage.show(getSupportFragmentManager(), "dialog");
            }
        });
    }

    private void setData(int count, float range) {

        float start = 1f;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = (int) start; i < start + count + 1; i++) {
            float mult = (range + 1);
            float walk = (float) Math.random() * mult;
            float aerobic = (float) Math.random() * mult;
            float run = (float) Math.random() * mult;


            yVals1.add(new BarEntry(i, new float[] {walk, aerobic, run}));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "The year 2017");
        set1.setColors(MyColor.THREE_COLORS);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.7f);
        data.setDrawValues(false);

        mChart.setData(data);
    }
}

package com.example.nromantsov.mychart;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;

public class DialogFragmentPage extends DialogFragment {

    MyBarChart mChart;
    float x;

    @Override
    public void onStart() {
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        super.onStart();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        Dialog dialog = new Dialog(getContext(), R.style.Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);

        x = getArguments().getFloat("X");

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_layout, container, false);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.close);
        toolbar.setBackgroundColor(getResources().getColor(R.color.gridTwo));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mChart = (MyBarChart) v.findViewById(R.id.chartFragment);
        mChart.setRender(mChart.createRender());
        mChart.getDescription().setEnabled(false);
        mChart.setViewPortOffsets(0, 0, 0, 50);
        mChart.setBackgroundColor(getResources().getColor(R.color.gridTwo));
        mChart.setColorDialog(new int[] {Color.rgb(0, 101, 105), Color.rgb(138, 217, 219), Color.rgb(0, 155, 161)});

        XAxis xAxis = mChart.getXAxis();
        MyXAxisRender myXAxisRender = new MyXAxisRender(mChart.getViewPortHandler(), xAxis, mChart.getTransformer(YAxis.AxisDependency.LEFT), mChart);
        myXAxisRender.setNumX(3f);
        myXAxisRender.setNumGridLines(6, 5, 13);
        myXAxisRender.setGetXY(x);
        mChart.setXAxisRenderer(myXAxisRender);

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setAxisMaximum(30.5f);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(6);
        xAxis.setGridColor(getResources().getColor(R.color.gridOne));

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setEnabled(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        mChart.setVisibleXRange(1, 30.5f);

        Legend legend = mChart.getLegend();
        legend.setEnabled(false);

        setData(30, 17);

        mChart.setCall(new MyBarChart.Call() {
            @Override
            public void doCall(float x) {
                dismiss();
            }
        });

        return v;
    }

    interface DialogInter {
        void onDismiss();
    }

    DialogInter dialogInter;

    public void setDialogInter(DialogInter dialogInter) {
        this.dialogInter = dialogInter;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (dialogInter != null){
            dialogInter.onDismiss();
        }
    }

    private void setData(int count, float range) {

        float start = 1f;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = (int) start; i < start + count + 1; i++) {
            float mult = (range + 1);
            float walk = (float) Math.random() * mult;
            float aerobic = (float) Math.random() * mult;
            float run = (float) Math.random() * mult;


            yVals1.add(new BarEntry(i, walk));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "The year 2017");

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.5f);
        data.setDrawValues(false);

        mChart.setData(data);
    }
}

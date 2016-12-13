package com.example.nromantsov.mychart;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

/**
 * Created by n.romantsov on 06.12.2016.
 */

public class MyBarDataSet extends BarDataSet {

    public MyBarDataSet(List<BarEntry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public int getColor(int index) {
        if(getEntryForIndex(index).getY() < 20) // less than 95 green
            return mColors.get(0);
        else if(getEntryForIndex(index).getY() > 40) // less than 100 orange
            return mColors.get(1);
        else // greater or equal than 100 red
            return mColors.get(2);
    }

}

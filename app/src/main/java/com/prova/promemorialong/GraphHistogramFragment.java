package com.prova.promemorialong;

import android.database.Cursor;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class GraphHistogramFragment extends Fragment {

    BarChart barChart;
    BarDataSet barDataSet;
    DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph_histogram, null);

        db = new DatabaseHelper(getContext());

        barChart = view.findViewById(R.id.barChart);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.getDescription().setEnabled(false);

        YAxis leftAxis = barChart.getAxisLeft();

        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        barChart.getAxisRight().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setEnabled(false);

        Cursor cursor = db.getStatsHist();

        ArrayList<BarEntry> values = new ArrayList<>();
        if (cursor != null) {
            int i = 0;
            //ArrayList<String> labels = new ArrayList<>();
            String [] labels = new String[4];
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                values.add(new BarEntry(i, cursor.getInt(0), cursor.getString(1)));
                labels[i]= (cursor.getString(1));
                Log.d("", cursor.getString(1) + cursor.getInt(0));
                i++;
            }
        }

        barDataSet = new BarDataSet(values,"");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);
        barData.setValueTextSize(15f);

        barChart.setData(barData);
        return view;
    }
}

package com.prova.promemorialong;

import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class GraphPieFragment extends Fragment {

    PieChart pieChart;
    PieDataSet dataSet;
    DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph_pie,null);

        db = new DatabaseHelper(getContext());

        pieChart = view.findViewById(R.id.pieChart);
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(60f);

        Cursor cursor = db.getStatsPie();

        ArrayList<PieEntry> values = new ArrayList <>();
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                values.add(new PieEntry(cursor.getInt(0), adapter(cursor.getString(1))));
                Log.d("", adapter(cursor.getString(1))+ cursor.getInt(0));
            }
        }

        dataSet = new PieDataSet(values,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(15f);
        dataSet.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        return view;
    }

    private String adapter(String inputString) {
        switch (inputString){
            case "pending":
                return getString(R.string.pending);
            case "ongoing":
                return getString(R.string.ongoing);
            case "complete":
                return getString(R.string.completed_task);
            default :return getString(R.string.pending);
        }
    }
}


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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class GraphLineFragment extends Fragment {

    LineChart lineChart;
    LineDataSet lineDataSet;
    DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_graph_line, null);

        db = new DatabaseHelper(getContext());

        Cursor cursor = db.getStatsLine();


        lineChart = view.findViewById(R.id.lineChart);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);

        ArrayList<Entry> values = new ArrayList<>();
        if (cursor != null) {
            int j = 1;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                while (j < cursor.getInt(1)){
                    values.add(new Entry(j,0));
                    Log.d("while interno j: ", ""+j);
                    j++;
                }
                values.add(new Entry(cursor.getInt(1), cursor.getInt(0)));
                Log.d("graphline stampa", "" + cursor.getFloat(1));
                Log.d("graphline stampa", "" + cursor.getFloat(0));
                j++;
            }
    }
        lineDataSet = new LineDataSet(values,"");
        lineDataSet.setColors(Color.RED);
        lineDataSet.setValueTextSize(11f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setCircleColor(Color.BLACK);

        LineData lineData = new LineData(lineDataSet);

        lineChart.setData(lineData);
        return view;
    }
}

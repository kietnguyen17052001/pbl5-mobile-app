package com.example.appsmarthome;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.appsmarthome.databinding.ActivityBedRoomLineChartBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class BedRoomLineChart extends AppCompatActivity {
    private ActivityBedRoomLineChartBinding binding;
    private List<Integer> listDays;
    private LineChart lineChart;
    private List<Entry> entryList;
    private List<ILineDataSet> iLineDataSets;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBedRoomLineChartBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        lineChart = binding.lineChart;
        Intent intent = getIntent();
        listDays = intent.getIntegerArrayListExtra("listDays");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Usage time of " + intent.getIntExtra("month", 0));
        entryList = new ArrayList<>();
        for (int i = 0; i < listDays.size(); i++){
            entryList.add(new Entry(i+1, listDays.get(i)));
        }
        LineDataSet lineDataSet = new LineDataSet(entryList, "minutes");
        iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);
        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}
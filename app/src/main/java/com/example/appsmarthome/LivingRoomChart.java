package com.example.appsmarthome;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.example.appsmarthome.databinding.ActivityKitchenChartBinding;
import com.example.appsmarthome.databinding.ActivityLivingRoomChartBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class LivingRoomChart extends AppCompatActivity {
    private ActivityLivingRoomChartBinding binding;
    private List<Integer> listTimes;
    private BarChart chart;
    private List<BarEntry> entryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLivingRoomChartBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Living room led chart");
        Intent intent = getIntent();
        entryList = new ArrayList<>();
        listTimes = intent.getIntegerArrayListExtra("listTimes");
        chart = binding.bcLivingRoom;
        int day = 2;
        for (int i = 0; i < listTimes.size(); i++) {
            entryList.add(new BarEntry(day, listTimes.get(i)));
            day++;
        }

        BarDataSet barDataSet = new BarDataSet(entryList, "minutes");
        barDataSet.setColor(Color.BLUE);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(15f);

        BarData barData = new BarData(barDataSet);

        chart.setData(barData);
        chart.setFitBars(true);
        chart.getDescription().setText("Led usage time chart");
    }
}
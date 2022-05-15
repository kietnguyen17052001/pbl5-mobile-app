package com.example.appsmarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.appsmarthome.databinding.ActivityLivingRoomChartBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LivingRoomChart extends AppCompatActivity {
    private ActivityLivingRoomChartBinding binding;
    private List<Integer> times;
    private BarChart chart;
    private List<BarEntry> entryList;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public void lineChart(int month) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        List<Integer> list = new ArrayList<>();
        databaseReference.child("leds/led_livingroom/details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i <= 30; i++) {
                    list.add(0);
                }
                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        Date dFirebase = dateFormat.parse(data.child("timeStart").getValue(String.class));
                        if (dFirebase.getYear() == new Date().getYear() && dFirebase.getMonth() + 1 == month) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(dFirebase);
                            int index = calendar.get(Calendar.DAY_OF_MONTH) - 1;
                            list.set(index, list.get(index) + (int) TimeUnit.SECONDS.toMinutes(data.child("seconds").getValue(Integer.class)));
                        }
                    } catch (Exception e) {
                        Log.d("Error", e.getMessage());
                    }
                }
                Intent intent = new Intent(LivingRoomChart.this, LivingRoomLineChart.class);
                intent.putExtra("month", month);
                intent.putIntegerArrayListExtra("listDays", (ArrayList<Integer>) list);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

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
        times = intent.getIntegerArrayListExtra("listMonth");
        chart = binding.barChart;
        for (int i = 0; i < times.size(); i++) {
            entryList.add(new BarEntry(i + 1, times.get(i)));
        }
        BarDataSet barDataSet = new BarDataSet(entryList, "minutes");
        barDataSet.setColor(Color.rgb(41, 128, 185));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(10f);
        BarData barData = new BarData(barDataSet);

        chart.setData(barData);
        chart.setFitBars(true);
        chart.getDescription().setText("Led usage time chart");

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int month = (int) e.getX();
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference();
                lineChart(month);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

}
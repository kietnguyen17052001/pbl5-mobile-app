package com.example.appsmarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.appsmarthome.databinding.ActivityLivingRoomLineChartBinding;
import com.github.mikephil.charting.charts.LineChart;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LivingRoomLineChart extends AppCompatActivity implements OnChartValueSelectedListener {

    private ActivityLivingRoomLineChartBinding binding;
    private List<Integer> listDays;
    private LineChart lineChart;
    private List<Entry> entryList;
    private List<ILineDataSet> iLineDataSets;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public void lineChart(int month) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        List<Integer> list = new ArrayList<>();
        databaseReference.child("leds/led_bedroom/details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i <= 30; i++) {
                    list.add(0);
                }
                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        Date dFirebase = dateFormat.parse(data.child("timeStart").getValue(String.class));
                        if (dFirebase.getYear() == new Date().getYear() && dFirebase.getMonth() == month) {
                            int index = dFirebase.getDay() - 1;
                            list.set(index, list.get(index) + (int) TimeUnit.SECONDS.toMinutes(data.child("seconds").getValue(Integer.class)));
                        }
                    } catch (Exception e) {
                        Log.d("Error", e.getMessage());
                    }
                }
                Intent intent = new Intent(getApplicationContext(), LivingRoomLineChart.class);
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
        binding = ActivityLivingRoomLineChartBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        lineChart = binding.lineChart;
        Intent intent = getIntent();
        listDays = intent.getIntegerArrayListExtra("listDays");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Usage time of " + intent.getIntExtra("month", 0));
        entryList = new ArrayList<>();
        for (int i = 0; i < listDays.size(); i++) {
            entryList.add(new Entry(i + 1, listDays.get(i)));
        }
        LineDataSet lineDataSet = new LineDataSet(entryList, "minutes");
        iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);
        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

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
}
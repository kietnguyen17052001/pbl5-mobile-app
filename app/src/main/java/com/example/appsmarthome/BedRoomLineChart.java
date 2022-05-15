package com.example.appsmarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.appsmarthome.databinding.ActivityBedRoomLineChartBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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

public class BedRoomLineChart extends AppCompatActivity {
    private static final String ledBedRoom = "ledBedRoom";
    private ActivityBedRoomLineChartBinding binding;
    private List<Integer> listDays;
    private LineChart lineChart;
    private List<Entry> entryList;
    private List<ILineDataSet> iLineDataSets;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public void chartMonths() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        List<Integer> list = new ArrayList<>();
        databaseReference.child("leds/" + ledBedRoom + "/details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i <= 11; i++) {
                    list.add(0);
                }
                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        Date dFirebase = dateFormat.parse(data.child("timeStart").getValue(String.class));
                        if (dFirebase.getYear() == new Date().getYear()) {
                            int index = dFirebase.getMonth();
                            list.set(index, list.get(index) + (int) TimeUnit.SECONDS.toMinutes(data.child("seconds").getValue(Integer.class)));
                        }
                    } catch (Exception e) {
                        Log.d("Error", e.getMessage());
                    }
                }
                Intent intent = new Intent();
                intent.putIntegerArrayListExtra("listMonth", (ArrayList<Integer>) list);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btn_back) {
            chartMonths();
        }
        return super.onOptionsItemSelected(item);
    }
}
package com.example.appsmarthome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import android.widget.Switch;
import android.widget.TextView;

import com.example.appsmarthome.Objects.Led;
import com.example.appsmarthome.Objects.TimeUsing;
import com.example.appsmarthome.databinding.ActivityRemoteBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RemoteActivity extends AppCompatActivity {
    private ActivityRemoteBinding binding;
    private Switch ledMode, ledLivingRoom, ledBedRoom, ledAll;
    private TextView timeOnOff, timeOnOffLivingRoom, timeOnOffBedRoom;
    private Button statisticLivingRoom, statisticBedRoom;
    private static final String LED_LIVING_ROOM = "led_livingroom", LED_BEDROOM = "led_bedroom";
    private StringBuilder builder;
    private SimpleDateFormat dateFormat;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private int minutes;
    private List<Integer> times;
    private boolean setup = true, isTurnOnAll = true;
    private Led led;
    private List<Led> lLed;
    private int iLed;

    // add TimeUsing into firebase
    public void addTimeUsing(String led, Date timeStart) {
        TimeUsing timeUsing = new TimeUsing();
        Date timeEnd = new Date();
        timeUsing.setTimeStart(dateFormat.format(timeStart));
        timeUsing.setTimeEnd(dateFormat.format(timeEnd));
        timeUsing.setSeconds((int) TimeUnit.MILLISECONDS.toSeconds(timeEnd.getTime() - timeStart.getTime()));
        databaseReference.child("leds/" + led + "/details").push().setValue(timeUsing);
    }

    public boolean onAll() {
        for (Led led : lLed) {
            if (led.getMode().equals("OFF")) {
                return false;
            }
        }
        return true;
    }

    public void switchEventAll(boolean isChecked) {
        if (isChecked) {
            if (!ledLivingRoom.isChecked()) {
                ledLivingRoom.setChecked(true);
            }
            if (!ledBedRoom.isChecked()) {
                ledBedRoom.setChecked(true);
            }
        } else {
            if (onAll()) {
                if (ledLivingRoom.isChecked()) {
                    ledLivingRoom.setChecked(false);
                }
                if (ledBedRoom.isChecked()) {
                    ledBedRoom.setChecked(false);
                }
            }
        }
    }

    public void switchEvent(boolean isChecked, Led led) {
        if (isChecked) {
            if (led.getMode().equals("OFF")) {
                led.setMode("ON");
                databaseReference.child("leds/" + led.getName() + "/mode").setValue("ON");
                databaseReference.child("leds/" + led.getName() + "/time_on_off").setValue(dateFormat.format(new Date()));
                ledAll.setChecked(onAll() ? true : false);
            }
        } else {
            if (led.getMode().equals("ON")) {
                led.setMode("OFF");
                addTimeUsing(led.getName(), led.getTimeOn());
                databaseReference.child("leds/" + led.getName() + "/mode").setValue("OFF");
                databaseReference.child("leds/" + led.getName() + "/time_on_off").setValue(dateFormat.format(new Date()));
                ledAll.setChecked(false);
            }
        }
    }

    // ----- Charts -----

    public void chartMonths(String led) {
        List<Integer> list = new ArrayList<>();
        databaseReference.child("leds/" + led + "/details").addListenerForSingleValueEvent(new ValueEventListener() {
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
                Intent intent = null;
                switch (led) {
                    case LED_BEDROOM:
                        intent = new Intent(RemoteActivity.this, BedRoomChart.class);
                        break;
                    case LED_LIVING_ROOM:
                        intent = new Intent(RemoteActivity.this, LivingRoomChart.class);
                        break;
                }
                intent.putIntegerArrayListExtra("listMonth", (ArrayList<Integer>) list);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // -----------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRemoteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        builder = new StringBuilder();
        dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        ledLivingRoom = binding.swLivingRoom;
        ledBedRoom = binding.swBedroom;
        ledAll = binding.swAll;
        timeOnOffLivingRoom = binding.tvTimeOnOffLedLivingRoom;
        timeOnOffBedRoom = binding.tvTimeOnOffLedBedRoom;
        statisticLivingRoom = binding.btnStatisticLivingroom;
        statisticBedRoom = binding.btnStatisticBedRoom;
        times = new ArrayList<>();
        lLed = new ArrayList<>();
        databaseReference.child("leds").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                led = new Led();
                led.setId(snapshot.child("id").getValue(Integer.class));
                led.setName((led.getId() == 0) ? LED_BEDROOM : LED_LIVING_ROOM);
                ledMode = ((led.getId() == 0) ? ledBedRoom : ledLivingRoom);
                timeOnOff = ((led.getId() == 0) ? timeOnOffBedRoom : timeOnOffLivingRoom);
                led.setMode(snapshot.child("mode").getValue(String.class));
                String time = snapshot.child("time_on_off").getValue(String.class);
                if (led.getMode().equals("OFF")) {
                    timeOnOff.setText("Turn off at: " + time);
                } else if (led.getMode().equals("ON")) {
                    try {
                        led.setTimeOn(dateFormat.parse(time));
                    } catch (Exception e) {
                    }
                    timeOnOff.setText("Turn on at: " + time);
                }
                lLed.add(led);
                ledMode.setChecked((led.getMode().equals("ON")) ? true : false);
                if (lLed.size() == 2) {
                    ledAll.setChecked(onAll() ? true : false);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                led = lLed.get(snapshot.child("id").getValue(Integer.class));
                led.setMode(snapshot.child("mode").getValue(String.class));
                String time = snapshot.child("time_on_off").getValue(String.class);
                ledMode = (led.getId() == 0) ? ledBedRoom : ledLivingRoom;
                timeOnOff = (led.getId() == 0) ? timeOnOffBedRoom : timeOnOffLivingRoom;
                if (led.getMode().equals("OFF")) {
                    timeOnOff.setText("Turn off at: " + time);
                } else if (led.getMode().equals("ON")) {
                    try {
                        led.setTimeOn(dateFormat.parse(time));
                    } catch (Exception e) {
                    }
                    timeOnOff.setText("Turn on at: " + time);
                }
                ledMode.setChecked((led.getMode().equals("ON")) ? true : false);
                ledAll.setChecked(onAll() ? true : false);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ledLivingRoom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Led led = (lLed != null && lLed.size() != 0) ? lLed.get(1) : null;
                switchEvent(isChecked, led);
            }
        });

        ledBedRoom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Led led = (lLed != null && lLed.size() != 0) ? lLed.get(0) : null;
                switchEvent(isChecked, led);
            }
        });

        ledAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                switchEventAll(isChecked);
            }
        });

        statisticLivingRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartMonths(LED_LIVING_ROOM);
            }
        });

        statisticBedRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartMonths(LED_BEDROOM);
            }
        });
    }
}
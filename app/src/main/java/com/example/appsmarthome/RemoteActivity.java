package com.example.appsmarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import android.widget.Switch;
import android.widget.TextView;

import com.example.appsmarthome.Objects.TimeUsing;
import com.example.appsmarthome.databinding.ActivityRemoteBinding;
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

public class RemoteActivity extends AppCompatActivity {
    private ActivityRemoteBinding binding;
    private Switch led_living_room, led_bedroom, led_kitchen, led_all;
    private TextView time_on_off_living_room, time_on_off_bedroom, time_on_off_kitchen,
            btn_show_chart_living_room, btn_show_chart_bedroom, btn_show_chart_kitchen;
    private static final String LED_LIVING_ROOM = "led_livingroom", LED_BEDROOM = "led_bedroom", LED_KITCHEN = "led_kitchen";
    private StringBuilder builder;
    private SimpleDateFormat dateFormat;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private String value_led_living_room, value_led_bedroom, value_led_kitchen;
    private String path;
    private Date preDateLedLivingRoom, preDateLedBedRoom, preDateLedKitchen;
    private int minutes;
    private boolean isTurnOn = true;
    private List<Integer> listTimes;

    // add TimeUsing into firebase
    public void addTimeUsing(String led, Date timeStart) {
        TimeUsing timeUsing = new TimeUsing();
        Date timeEnd = new Date();
        timeUsing.setTimeStart(dateFormat.format(timeStart));
        timeUsing.setTimeEnd(dateFormat.format(timeEnd));
        timeUsing.setSeconds((int) TimeUnit.MILLISECONDS.toSeconds(timeEnd.getTime() - timeStart.getTime()));
        databaseReference.child("leds/" + led + "/details").push().setValue(timeUsing);
    }

    // ----- Charts -----

    public void chartMonths(String led) {
        List<Integer> list = new ArrayList<>();
        databaseReference.child("leds/" + led + "/details").addValueEventListener(new ValueEventListener() {
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
                    case LED_KITCHEN:
                        intent = new Intent(RemoteActivity.this, KitchenChart.class);
                        break;
                    case LED_BEDROOM:
                        intent = new Intent(RemoteActivity.this, BedRoomChart.class);
                        break;
                    case LED_LIVING_ROOM:
                        intent = new Intent(RemoteActivity.this, LivingRoomChart.class);
                        break;
                }
                Log.d("list", String.valueOf(list.size()));
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
        led_living_room = binding.swLivingRoom;
        led_kitchen = binding.swKitchen;
        led_bedroom = binding.swBedroom;
        led_all = binding.swAll;
        time_on_off_living_room = binding.tvTimeOnOffLedLivingRoom;
        time_on_off_bedroom = binding.tvTimeOnOffLedBedroom;
        time_on_off_kitchen = binding.tvTimeOnOffLedKitchen;
        btn_show_chart_bedroom = binding.tvLedBedRoom;
        btn_show_chart_living_room = binding.tvLedLivingRoom;
        btn_show_chart_kitchen = binding.tvLedKitchen;
        listTimes = new ArrayList<>();
        databaseReference.child("leds/" + LED_LIVING_ROOM + "/mode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                value_led_living_room = snapshot.getValue(String.class);
                if (value_led_living_room.equals("OFF")) {
                    led_living_room.setChecked(false);
                    isTurnOn = false;
                } else {
                    led_living_room.setChecked(true);
                }
                databaseReference.child("leds/" + LED_LIVING_ROOM + "/time_on_off").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String time = snapshot.getValue(String.class);
                        if (value_led_living_room.equals("OFF")) {
                            time_on_off_living_room.setText("Turn off at: " + time);
                        } else {
                            time_on_off_living_room.setText("Turn on at: " + time);
                            try {
                                preDateLedLivingRoom = dateFormat.parse(time);
                            } catch (Exception e) {
                                Log.d("Error: ", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("leds/" + LED_BEDROOM + "/mode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                value_led_bedroom = snapshot.getValue(String.class);
                if (value_led_bedroom.equals("OFF")) {
                    led_bedroom.setChecked(false);
                    isTurnOn = false;
                } else {
                    led_bedroom.setChecked(true);
                }
                databaseReference.child("leds/" + LED_BEDROOM + "/time_on_off").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String time = snapshot.getValue(String.class);
                        if (value_led_bedroom.equals("OFF")) {
                            time_on_off_bedroom.setText("Turn off at: " + time);
                        } else {
                            time_on_off_bedroom.setText("Turn on at: " + time);
                            try {
                                preDateLedBedRoom = dateFormat.parse(time);
                            } catch (Exception e) {
                                Log.d("Error: ", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("leds/" + LED_KITCHEN + "/mode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                value_led_kitchen = snapshot.getValue(String.class);
                if (value_led_kitchen.equals("OFF")) {
                    led_kitchen.setChecked(false);
                    isTurnOn = false;
                } else {
                    led_kitchen.setChecked(true);
                }
                if (isTurnOn) {
                    led_all.setChecked(true);
                }
                databaseReference.child("leds/" + LED_KITCHEN + "/time_on_off").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String time = snapshot.getValue(String.class);
                        if (value_led_kitchen.equals("OFF")) {
                            time_on_off_kitchen.setText("Turn off at: " + time);
                        } else {
                            time_on_off_kitchen.setText("Turn on at: " + time);
                            try {
                                preDateLedKitchen = dateFormat.parse(time);
                            } catch (Exception e) {
                                Log.d("Error: ", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        led_living_room.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (value_led_living_room.equals("OFF")) {
                        databaseReference.child("leds/" + LED_LIVING_ROOM + "/mode").setValue("ON");
                        databaseReference.child("leds/" + LED_LIVING_ROOM + "/time_on_off").setValue(dateFormat.format(new Date()));
                        if (led_bedroom.isChecked() && led_kitchen.isChecked()) {
                            led_all.setChecked(true);
                        }
                    }
                } else {
                    if (value_led_living_room.equals("ON")) {
                        addTimeUsing(LED_LIVING_ROOM, preDateLedLivingRoom);
                        databaseReference.child("leds/" + LED_LIVING_ROOM + "/mode").setValue("OFF");
                        databaseReference.child("leds/" + LED_LIVING_ROOM + "/time_on_off").setValue(dateFormat.format(new Date()));
                    }
                }
            }
        });
        led_kitchen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (value_led_kitchen.equals("OFF")) {
                        databaseReference.child("leds/" + LED_KITCHEN + "/mode").setValue("ON");
                        databaseReference.child("leds/" + LED_KITCHEN + "/time_on_off").setValue(dateFormat.format(new Date()));
                        if (led_bedroom.isChecked() && led_living_room.isChecked()) {
                            led_all.setChecked(true);
                        }
                    }
                } else {
                    if (value_led_kitchen.equals("ON")) {
                        addTimeUsing(LED_KITCHEN, preDateLedKitchen);
                        databaseReference.child("leds/" + LED_KITCHEN + "/mode").setValue("OFF");
                        databaseReference.child("leds/" + LED_KITCHEN + "/time_on_off").setValue(dateFormat.format(new Date()));
                    }
                }
            }
        });
        led_bedroom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (value_led_bedroom.equals("OFF")) {
                        databaseReference.child("leds/" + LED_BEDROOM + "/mode").setValue("ON");
                        databaseReference.child("leds/" + LED_BEDROOM + "/time_on_off").setValue(dateFormat.format(new Date()));
                        if (led_living_room.isChecked() && led_kitchen.isChecked()) {
                            led_all.setChecked(true);
                        }
                    }
                } else {
                    if (value_led_bedroom.equals("ON")) {
                        addTimeUsing(LED_BEDROOM, preDateLedBedRoom);
                        databaseReference.child("leds/" + LED_BEDROOM + "/mode").setValue("OFF");
                        databaseReference.child("leds/" + LED_BEDROOM + "/time_on_off").setValue(dateFormat.format(new Date()));
                    }
                }
            }
        });

        led_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (!led_bedroom.isChecked()) {
                        databaseReference.child("leds/" + LED_BEDROOM + "/mode").setValue("ON");
                        databaseReference.child("leds/" + LED_BEDROOM + "/time_on_off").setValue(dateFormat.format(new Date()));
                    }
                    if (!led_living_room.isChecked()) {
                        databaseReference.child("leds/" + LED_LIVING_ROOM + "/mode").setValue("ON");
                        databaseReference.child("leds/" + LED_LIVING_ROOM + "/time_on_off").setValue(dateFormat.format(new Date()));
                    }
                    if (!led_kitchen.isChecked()) {
                        databaseReference.child("leds/" + LED_KITCHEN + "/mode").setValue("ON");
                        databaseReference.child("leds/" + LED_KITCHEN + "/time_on_off").setValue(dateFormat.format(new Date()));
                    }
                } else {
                    if (led_bedroom.isChecked()) {
                        databaseReference.child("leds/" + LED_BEDROOM + "/mode").setValue("OFF");
                        databaseReference.child("leds/" + LED_BEDROOM + "/time_on_off").setValue(dateFormat.format(new Date()));
                    }
                    if (led_living_room.isChecked()) {
                        databaseReference.child("leds/" + LED_LIVING_ROOM + "/mode").setValue("OFF");
                        databaseReference.child("leds/" + LED_LIVING_ROOM + "/time_on_off").setValue(dateFormat.format(new Date()));
                    }
                    if (led_kitchen.isChecked()) {
                        databaseReference.child("leds/" + LED_KITCHEN + "/mode").setValue("OFF");
                        databaseReference.child("leds/" + LED_KITCHEN + "/time_on_off").setValue(dateFormat.format(new Date()));
                    }
                }
            }
        });

        btn_show_chart_living_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartMonths(LED_LIVING_ROOM);
            }
        });

        btn_show_chart_bedroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartMonths(LED_BEDROOM);
            }
        });

        btn_show_chart_kitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartMonths(LED_KITCHEN);
            }
        });
    }
}
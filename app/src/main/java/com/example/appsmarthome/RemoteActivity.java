package com.example.appsmarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.appsmarthome.databinding.ActivityLoginBinding;
import com.example.appsmarthome.databinding.ActivityRemoteBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RemoteActivity extends AppCompatActivity {
    private ActivityRemoteBinding binding;
    private Switch led_living_room, led_bedroom, led_kitchen, led_all;
    private TextView time_on_off_living_room, time_on_off_bedroom, time_on_off_kitchen,
            time_use_living_room, time_use_bedroom, time_use_kitchen;
    private StringBuilder builder;
    private SimpleDateFormat dateFormat;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private Boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRemoteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        builder = new StringBuilder();
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        led_living_room = binding.swLivingRoom;
        led_kitchen = binding.swKitchen;
        led_bedroom = binding.swBedroom;
        led_all = binding.swAll;
        time_on_off_living_room = binding.tvTimeOnOffLedLivingRoom;
        time_on_off_bedroom = binding.tvTimeOnOffLedBedroom;
        time_on_off_kitchen = binding.tvTimeOnOffLedKitchen;
        time_use_living_room = binding.tvTimeUseLedLivingRoom;
        time_use_bedroom = binding.tvTimeUseLedBedroom;
        time_use_kitchen = binding.tvTimeUseLedKitchen;
        flag = false;
        databaseReference.child("led_livingroom").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (value.equals("OFF")) {
                    led_living_room.setChecked(false);
                } else {
                    led_living_room.setChecked(true);
                }
                databaseReference.child("time_on_off_led_livingroom").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String time = snapshot.getValue(String.class);
                        if (value.equals("OFF")) {
                            time_on_off_living_room.setText("Turn off at: " + time);
                        } else {
                            time_on_off_living_room.setText("Turn on at: " + time);
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
        databaseReference.child("led_bedroom").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (value.equals("OFF")) {
                    led_bedroom.setChecked(false);
                } else {
                    led_bedroom.setChecked(true);
                }
                databaseReference.child("time_on_off_led_bedroom").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String time = snapshot.getValue(String.class);
                        if (value.equals("OFF")) {
                            time_on_off_bedroom.setText("Turn off at: " + time);
                        } else {
                            time_on_off_bedroom.setText("Turn on at: " + time);
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
        databaseReference.child("led_kitchen").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (value.equals("OFF")) {
                    led_kitchen.setChecked(false);
                } else {
                    led_kitchen.setChecked(true);
                }
                databaseReference.child("time_on_off_led_kitchen").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String time = snapshot.getValue(String.class);
                        if (value.equals("OFF")) {
                            time_on_off_kitchen.setText("Turn off at: " + time);
                        } else {
                            time_on_off_kitchen.setText("Turn on at: " + time);
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
                    databaseReference.child("led_livingroom").setValue("ON");
                } else {
                    databaseReference.child("led_livingroom").setValue("OFF");
                }
                databaseReference.child("time_on_off_led_livingroom").setValue(dateFormat.format(new Date()));
            }
        });
        led_kitchen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    databaseReference.child("led_kitchen").setValue("ON");
                } else {
                    databaseReference.child("led_kitchen").setValue("OFF");
                }
                databaseReference.child("time_on_off_led_kitchen").setValue(dateFormat.format(new Date()));
            }
        });
        led_bedroom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    databaseReference.child("led_bedroom").setValue("ON");
                } else {
                    databaseReference.child("led_bedroom").setValue("OFF");
                }
                databaseReference.child("time_on_off_led_bedroom").setValue(dateFormat.format(new Date()));
            }
        });
        led_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (!led_bedroom.isChecked()) {
                        databaseReference.child("led_bedroom").setValue("ON");
                        databaseReference.child("time_on_off_led_bedroom").setValue(dateFormat.format(new Date()));
                    }
                    if (!led_living_room.isChecked()) {
                        databaseReference.child("led_livingroom").setValue("ON");
                        databaseReference.child("time_on_off_led_livingroom").setValue(dateFormat.format(new Date()));
                    }
                    if (!led_kitchen.isChecked()) {
                        databaseReference.child("led_kitchen").setValue("ON");
                        databaseReference.child("time_on_off_led_kitchen").setValue(dateFormat.format(new Date()));
                    }
                } else {
                    if (led_bedroom.isChecked()) {
                        databaseReference.child("led_bedroom").setValue("OFF");
                        databaseReference.child("time_on_off_led_bedroom").setValue(dateFormat.format(new Date()));
                    }
                    if (led_living_room.isChecked()) {
                        databaseReference.child("led_livingroom").setValue("OFF");
                        databaseReference.child("time_on_off_led_livingroom").setValue(dateFormat.format(new Date()));
                    }
                    if (led_kitchen.isChecked()) {
                        databaseReference.child("led_kitchen").setValue("OFF");
                        databaseReference.child("time_on_off_led_kitchen").setValue(dateFormat.format(new Date()));
                    }
                }
            }
        });
    }
}
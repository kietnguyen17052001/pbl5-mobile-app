package com.example.appsmarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.appsmarthome.databinding.ActivityLoginBinding;
import com.example.appsmarthome.databinding.ActivityRemoteBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RemoteActivity extends AppCompatActivity {
    private ActivityRemoteBinding binding;
    private Switch led_living_room, led_bedroom, led_kitchen;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRemoteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        led_living_room = binding.swLivingRoom;
        led_kitchen = binding.swKitchen;
        led_bedroom = binding.swBedroom;
        databaseReference.child("led_livingroom").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (value.equals("OFF")) {
                    led_living_room.setChecked(false);
                }
                else{
                    led_living_room.setChecked(true);
                }
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
                }
                else{
                    led_bedroom.setChecked(true);
                }
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
                }
                else{
                    led_kitchen.setChecked(true);
                }
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
            }
        });
    }
}
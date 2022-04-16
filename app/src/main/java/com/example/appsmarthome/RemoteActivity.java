package com.example.appsmarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private Long difference;
    private DayOfWeek dayOfWeek;
    private Date preDateLedLivingRoom, preDateLedBedRoom, preDateLedKitchen;
    private int preTime, minutes;
    private boolean isTurnOn = true;
    private List<Integer> listTimes;

    // convert milliseconds to minutes
    public int convertToMinutes(Long difference) {
        long hours = TimeUnit.MILLISECONDS.toHours(difference);
        difference -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
        return (int) (hours * 60 + minutes);
    }

    public String getPath(String led, DayOfWeek dayOfWeek) {
        switch (led) {
            case LED_LIVING_ROOM:
                path = "time_use_led_livingroom";
                break;
            case LED_BEDROOM:
                path = "time_use_led_bedroom";
                break;
            case LED_KITCHEN:
                path = "time_use_led_kitchen";
                break;
        }
        switch (dayOfWeek) {
            case MONDAY:
                path += "/1";
                break;
            case TUESDAY:
                path += "/2";
                break;
            case WEDNESDAY:
                path += "/3";
                break;
            case THURSDAY:
                path += "/4";
                break;
            case FRIDAY:
                path += "/5";
                break;
            case SATURDAY:
                path += "/6";
                break;
            case SUNDAY:
                path += "/7";
                break;
        }
        return path;
    }

    // return time use from turn on to turn off led
    public void timeUse(Date preDate, Date curDate, DatabaseReference databaseReference, String led) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(simpleDateFormat.format(preDate));
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        LocalDateTime currentDate = localDate.atTime(curDate.getHours(), curDate.getMinutes(), curDate.getSeconds());
        if (currentDate.isAfter(startOfDay) && currentDate.isBefore(endOfDay)) {
            difference = curDate.getTime() - preDate.getTime();
            dayOfWeek = currentDate.getDayOfWeek();
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    preTime = snapshot.child(getPath(led, dayOfWeek)).getValue(Integer.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReference.child(getPath(led, dayOfWeek)).setValue(preTime + convertToMinutes(difference));
        } else {
            long different_pre = LocalDateTime.of(preDate.getYear(), preDate.getMonth(), preDate.getDay(), preDate.getHours(), preDate.getMinutes(), preDate.getSeconds()).until(endOfDay, ChronoUnit.MILLIS);
            long different_cur = LocalDateTime.of(endOfDay.getYear(), endOfDay.getMonth(), endOfDay.getDayOfMonth(), 0, 0, 0).until(currentDate, ChronoUnit.MILLIS);
            // for previous time
            dayOfWeek = startOfDay.getDayOfWeek();
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    preTime = snapshot.child(getPath(led, dayOfWeek)).getValue(Integer.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReference.child(getPath(led, dayOfWeek)).setValue(preTime + convertToMinutes(different_pre));
            // for current time
            dayOfWeek = currentDate.getDayOfWeek();
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    preTime = snapshot.child(getPath(led, dayOfWeek)).getValue(Integer.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReference.child(getPath(led, dayOfWeek)).setValue(preTime + convertToMinutes(different_cur));
        }
    }

    // ----- Charts -----

    public List<Integer> resolve(DatabaseReference databaseReference, String path) {
        List<Integer> list = new ArrayList<>();
        databaseReference.child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    minutes = data.getValue(Integer.class);
                    list.add(minutes);
                }
                Intent intent = null;
                switch (path) {
                    case "time_use_led_kitchen":
                        intent = new Intent(RemoteActivity.this, KitchenChart.class);
                        break;
                    case "time_use_led_bedroom":
                        intent = new Intent(RemoteActivity.this, BedRoomChart.class);
                        break;
                    case "time_use_led_livingroom":
                        intent = new Intent(RemoteActivity.this, LivingRoomChart.class);
                        break;
                }
                intent.putIntegerArrayListExtra("listTimes", (ArrayList<Integer>) list);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return list;
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
        databaseReference.child(LED_LIVING_ROOM).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                value_led_living_room = snapshot.getValue(String.class);
                if (value_led_living_room.equals("OFF")) {
                    led_living_room.setChecked(false);
                    isTurnOn = false;
                } else {
                    led_living_room.setChecked(true);
                }
                databaseReference.child("time_on_off_led_livingroom").addValueEventListener(new ValueEventListener() {
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
        databaseReference.child(LED_BEDROOM).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                value_led_bedroom = snapshot.getValue(String.class);
                if (value_led_bedroom.equals("OFF")) {
                    led_bedroom.setChecked(false);
                    isTurnOn = false;
                } else {
                    led_bedroom.setChecked(true);
                }
                databaseReference.child("time_on_off_led_bedroom").addValueEventListener(new ValueEventListener() {
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
        databaseReference.child(LED_KITCHEN).addValueEventListener(new ValueEventListener() {
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
                databaseReference.child("time_on_off_led_kitchen").addValueEventListener(new ValueEventListener() {
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
                        databaseReference.child(LED_LIVING_ROOM).setValue("ON");
                        databaseReference.child("time_on_off_led_livingroom").setValue(dateFormat.format(new Date()));
                        if (led_bedroom.isChecked() && led_kitchen.isChecked()) {
                            led_all.setChecked(true);
                        }
                    }
                } else {
                    if (value_led_living_room.equals("ON")) {
                        timeUse(preDateLedLivingRoom, new Date(), databaseReference, LED_LIVING_ROOM);
                        databaseReference.child(LED_LIVING_ROOM).setValue("OFF");
                        databaseReference.child("time_on_off_led_livingroom").setValue(dateFormat.format(new Date()));
                    }
                }
            }
        });
        led_kitchen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (value_led_kitchen.equals("OFF")) {
                        databaseReference.child(LED_KITCHEN).setValue("ON");
                        databaseReference.child("time_on_off_led_kitchen").setValue(dateFormat.format(new Date()));
                        if (led_bedroom.isChecked() && led_living_room.isChecked()) {
                            led_all.setChecked(true);
                        }
                    }
                } else {
                    if (value_led_kitchen.equals("ON")) {
                        timeUse(preDateLedKitchen, new Date(), databaseReference, LED_KITCHEN);
                        databaseReference.child(LED_KITCHEN).setValue("OFF");
                        databaseReference.child("time_on_off_led_kitchen").setValue(dateFormat.format(new Date()));
                    }
                }
            }
        });
        led_bedroom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (value_led_bedroom.equals("OFF")) {
                        databaseReference.child(LED_BEDROOM).setValue("ON");
                        databaseReference.child("time_on_off_led_bedroom").setValue(dateFormat.format(new Date()));
                        if (led_living_room.isChecked() && led_kitchen.isChecked()) {
                            led_all.setChecked(true);
                        }
                    }
                } else {
                    if (value_led_bedroom.equals("ON")) {
                        timeUse(preDateLedBedRoom, new Date(), databaseReference, LED_BEDROOM);
                        databaseReference.child(LED_BEDROOM).setValue("OFF");
                        databaseReference.child("time_on_off_led_bedroom").setValue(dateFormat.format(new Date()));
                    }
                }
            }
        });

        led_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (!led_bedroom.isChecked()) {
                        databaseReference.child(LED_BEDROOM).setValue("ON");
                        databaseReference.child("time_on_off_led_bedroom").setValue(dateFormat.format(new Date()));
                    }
                    if (!led_living_room.isChecked()) {
                        databaseReference.child(LED_LIVING_ROOM).setValue("ON");
                        databaseReference.child("time_on_off_led_livingroom").setValue(dateFormat.format(new Date()));
                    }
                    if (!led_kitchen.isChecked()) {
                        databaseReference.child(LED_KITCHEN).setValue("ON");
                        databaseReference.child("time_on_off_led_kitchen").setValue(dateFormat.format(new Date()));
                    }
                } else {
                    if (led_bedroom.isChecked()) {
                        databaseReference.child(LED_BEDROOM).setValue("OFF");
                        databaseReference.child("time_on_off_led_bedroom").setValue(dateFormat.format(new Date()));
                    }
                    if (led_living_room.isChecked()) {
                        databaseReference.child(LED_LIVING_ROOM).setValue("OFF");
                        databaseReference.child("time_on_off_led_livingroom").setValue(dateFormat.format(new Date()));
                    }
                    if (led_kitchen.isChecked()) {
                        databaseReference.child(LED_KITCHEN).setValue("OFF");
                        databaseReference.child("time_on_off_led_kitchen").setValue(dateFormat.format(new Date()));
                    }
                }
            }
        });

        btn_show_chart_living_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolve(databaseReference, "time_use_led_livingroom");
            }
        });

        btn_show_chart_bedroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolve(databaseReference, "time_use_led_bedroom");
            }
        });

        btn_show_chart_kitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolve(databaseReference, "time_use_led_kitchen");
            }
        });
    }
}
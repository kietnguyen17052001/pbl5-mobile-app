package com.example.appsmarthome.Objects;


import java.util.Date;

public class Led {
    private int id;
    private String name;
    private String mode;
    private Date timeOn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Date getTimeOn() {
        return timeOn;
    }

    public void setTimeOn(Date timeOn) {
        this.timeOn = timeOn;
    }

}

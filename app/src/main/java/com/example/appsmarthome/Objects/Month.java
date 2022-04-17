package com.example.appsmarthome.Objects;

import java.util.List;
import java.util.Objects;

public class Month {
    private int month;
    private Long seconds;
    private Objects timeUsings;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Long getSeconds() {
        return seconds;
    }

    public void setSeconds(Long seconds) {
        this.seconds = seconds;
    }

    public Objects getTimeUsings() {
        return timeUsings;
    }

    public void setTimeUsings(Objects timeUsings) {
        this.timeUsings = timeUsings;
    }
}

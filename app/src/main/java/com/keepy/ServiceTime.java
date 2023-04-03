package com.keepy;

import java.util.Calendar;

public class ServiceTime {

    private long date;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;


    public ServiceTime(long date, int startHour, int startMinute, int endHour, int endMinute) {
        this.date = date;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    public long getDate() {
        return date;
    }


    public ServiceTime() {}

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public boolean sameDate(ServiceTime other) {
        return getDay() == other.getDay()
                && getMonth() == other.getMonth()
                && getYear() == other.getYear();
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }


    public int getMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return calendar.get(Calendar.MONTH);
    }
    public int getYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return calendar.get(Calendar.YEAR);
    }
    public int getDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

}

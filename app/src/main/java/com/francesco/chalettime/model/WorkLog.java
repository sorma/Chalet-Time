package com.francesco.chalettime.model;

public class WorkLog {
    private String userId;
    private String date;
    private double hours;

    // Costruttore vuoto necessario per Firebase
    public WorkLog() {
    }


    public WorkLog(String userId, String date, double hours) {
        this.userId = userId;
        this.date = date;
        this.hours = hours;
    }



    public String getDate() {
        return date;
    }

    public double getHours() {
        return hours;
    }

}

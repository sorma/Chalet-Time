package com.francesco.chalettime;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }
}

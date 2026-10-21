package com.francesco.chalettime.model;

import java.io.Serializable;

public class Employee implements Serializable {
    private String userId; // Aggiunto l'ID dell'utente
    private String name;
    private String hours;
    private String gender;
    private String birthday;

    public Employee(String userId, String name, String hours, String gender, String birthday) {
        this.userId = userId;
        this.name = name;
        this.hours = hours;
        this.gender = gender;
        this.birthday = birthday;
    }

    public String getUserId() {
        return userId;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getName() {
        return name;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getGender() {
        return gender;
    }

    // Potresti aggiungere dei setter se necessario
}
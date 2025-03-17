package com.francesco.chalettime;

import java.io.Serializable;

public class Recycler_item implements Serializable {
    String name;
    String hours;
    int image;
    String birthday;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Recycler_item(String name, String hours, int image, String birthday) {
        this.image = image;
        this.hours = hours;
        this.name = name;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}

package com.francesco.chalettime;

public class Recycler_item {
    String name;
    String hours;
    int image;

    public Recycler_item(String name, String hours, int image) {
        this.image = image;
        this.hours = hours;
        this.name = name;
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

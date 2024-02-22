package com.s19.spotbuy.Models;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class Country implements Serializable {
    String id;
    String name;
    boolean active;

    public Country(String name) {
        this.name = name;
        this.active = true;

    }

    public Country(String name, boolean active) {
        this.name = name;
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

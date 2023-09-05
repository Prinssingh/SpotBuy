package com.s19mobility.spotbuy.Models;

import java.io.Serializable;

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

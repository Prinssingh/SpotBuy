package com.s19.spotbuy.Models;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class FuelType implements Serializable {
    String id;
    boolean active;
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

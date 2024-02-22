package com.s19.spotbuy.Models;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class VehicleBrand implements Serializable {
    String id;
    boolean active;
    String categoryId;
    String name;

    public VehicleBrand() {
    }

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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

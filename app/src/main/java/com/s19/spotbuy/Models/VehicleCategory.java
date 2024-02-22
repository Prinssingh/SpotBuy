package com.s19.spotbuy.Models;

import androidx.annotation.Keep;

import java.io.Serializable;


@Keep
public class VehicleCategory implements Serializable {
    boolean active;
    String image;
    String id;
    String name;




    public VehicleCategory(){}


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

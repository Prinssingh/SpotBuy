package com.s19.spotbuy.Models;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class UserRole implements Serializable {
    boolean active;
    String type;
}

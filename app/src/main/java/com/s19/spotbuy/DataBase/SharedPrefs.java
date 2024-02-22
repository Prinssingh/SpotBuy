package com.s19.spotbuy.DataBase;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class SharedPrefs {
    public static String sharedPrefName = "spotbuyPrefs";
    Context context;

    public SharedPrefs(Context context) {
        this.context = context;
    }

    public void clearSharedData(){
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    //Login Preferences
    public boolean getLogin() {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return sp.getBoolean("isLoggedIn", false);
    }

    public void setLogin(Boolean login) {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isLoggedIn", login);
        editor.apply();

    }

    //Profile Preferences
    public boolean isProfileSet() {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return sp.getBoolean("isProfileSet", false);
    }

    public void setProfleStatus(Boolean profile) {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isProfileSet", profile);
        editor.apply();
    }

    //Profile Preferences
    public String getSharedMobile() {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return sp.getString("mobileNo", "");
    }

    public void setSharedMobile(String mobile) {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("mobileNo", mobile);
        editor.apply();
    }
    //Profile Preferences

    public String getSharedUID() {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return sp.getString("uid", "");
    }

    public void setSharedUID(String uid) {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("uid", uid);
        editor.apply();
    }

    public String getSharedImage() {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return sp.getString("image", "");
    }

    public void setSharedImage(String image) {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("image", image);
        editor.apply();
    }


    //City Preferences
    public String getSharedCity() {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return sp.getString("city", "");
    }

    public void setSharedCity(String city) {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("city", city);
        editor.apply();
    }

    //State Preferences
    public String getSharedState() {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return sp.getString("state", "");
    }

    public void setSharedState(String state) {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("state", state);
        editor.apply();
    }

    //Country Preferences
    public String getSharedCountry() {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return sp.getString("country", "India");
    }

    public void setSharedCountry(String country) {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("country", country);
        editor.apply();
    }


    //City Preferences
    public String getSharedName() {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        return sp.getString("name", "");
    }

    public void setSharedName(String name) {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name", name);
        editor.apply();
    }

    public void setLastUpdateDate(Date date){

        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("LastUpdate", date.getTime());
        editor.apply();

    }

    public Date getLastUpdateDate(){
        SharedPreferences sp = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        Long time = sp.getLong("LastUpdate", 0);

        if (time==0)
            return  null;

        return new Date(time);

    }

}

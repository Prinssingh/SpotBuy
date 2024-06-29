package com.s19.spotbuy.Others;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StateCityData {
    String jsonString;
    JSONObject json;
    public StateCityData(Context context){
        try {
            InputStream is = context.getAssets().open("Indian_State_City.json");

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
            json = new JSONObject(jsonString);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public List<String> getStateList() {
        List<String> states = new ArrayList<String>() {
        };

        for (int i = 0; i < json.names().length(); i++) {
            try {
                states.add((String) json.names().get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(states);

        return states;
    }

    public List<String> getCityList(String state) {
        List<String> cities = new ArrayList<String>() {
        };

        JSONArray tem = null;
        try {
            tem = (JSONArray) json.get(state);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < tem.length(); i++) {
            try {
                cities.add(tem.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(cities);
        return cities;
    }

    public List<String> getAllCityList() {
        List<String> cities = new ArrayList<String>() {
        };

        for (String state: getStateList()) {
            JSONArray tem = null;
            try {
                tem = (JSONArray) json.get(state);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < tem.length(); i++) {
                try {
                    cities.add(tem.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        Collections.sort(cities);
        return cities;
    }


    public List<String> getCountrylist() {
        List<String> country = new ArrayList<String>() {
        };

        country.add("India");

        return country;
    }

}

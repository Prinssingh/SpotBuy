package com.s19.spotbuy.Widgets;

import android.util.Log;

import com.s19.spotbuy.BuildConfig;

public class TestLog {
    public TestLog(String msg) {
        if(BuildConfig.DEBUG){
            Log.e("HERE",msg);
        }
    }
}

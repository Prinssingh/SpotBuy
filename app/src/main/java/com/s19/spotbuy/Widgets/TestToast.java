package com.s19.spotbuy.Widgets;


import android.content.Context;
import android.widget.Toast;

import com.s19.spotbuy.BuildConfig;

public class TestToast {

    public TestToast(Context context, String msg) {
      if(BuildConfig.DEBUG)
          Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}

package com.s19.spotbuy.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.s19.spotbuy.R;

public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initView();
    }


    private void initView(){
        back = findViewById(R.id.back);
        back.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view==back){
            finish();
        }

    }
}
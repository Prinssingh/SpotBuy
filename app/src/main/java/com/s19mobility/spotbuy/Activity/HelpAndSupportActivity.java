package com.s19mobility.spotbuy.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.s19mobility.spotbuy.R;

public class HelpAndSupportActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);
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
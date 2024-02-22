package com.s19.spotbuy.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.s19.spotbuy.Fragments.main.AdsFragment;
import com.s19.spotbuy.R;

public class MyAdActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ad);

        initView();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, AdsFragment.newInstance(false))
                    .commitNow();
        }
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
package com.s19mobility.spotbuy.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.s19mobility.spotbuy.R;

public class LanguageActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back, englishLanguageIndicator;
    MaterialCardView englishLanguage, hindiLanguage, bengaliLanguage, tamilLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        initView();
    }

    private void initView() {
        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        englishLanguage = findViewById(R.id.englishLanguage);
        englishLanguage.setOnClickListener(this);

        hindiLanguage = findViewById(R.id.hindiLanguage);
        hindiLanguage.setOnClickListener(this);

        bengaliLanguage = findViewById(R.id.bengaliLanguage);
        bengaliLanguage.setOnClickListener(this);

        tamilLanguage = findViewById(R.id.tamilLanguage);
        tamilLanguage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == back) {
            finish();
        }
        if (view == englishLanguage) {
            Toast.makeText(this, "Active", Toast.LENGTH_SHORT).show();
        }
        if (view == hindiLanguage) {
            Toast.makeText(this, "Not Available now", Toast.LENGTH_SHORT).show();
        }
        if (view == bengaliLanguage) {
            Toast.makeText(this, "Not Available now", Toast.LENGTH_SHORT).show();
        }
        if (view == tamilLanguage) {
            Toast.makeText(this, "Not Available now", Toast.LENGTH_SHORT).show();
        }
    }


}
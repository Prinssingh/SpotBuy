package com.s19.spotbuy.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.card.MaterialCardView;
import com.s19.spotbuy.R;

public class HelpAndSupportActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back;


    MaterialCardView termsConditions,privacyPolicy,aboutUs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);
        initView();
    }


    private void initView(){
        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        termsConditions = findViewById(R.id.termsConditions);
        termsConditions.setOnClickListener(this);

        privacyPolicy = findViewById(R.id.privacyPolicy);
        privacyPolicy.setOnClickListener(this);

        aboutUs = findViewById(R.id.aboutUs);
        aboutUs.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view==back){
            finish();
        }

        if(termsConditions== view){
            startActivity(new Intent(this,TermsConditionsActivity.class));
        }

        if(privacyPolicy== view){
            startActivity(new Intent(this,PrivacyPolicyActivity.class));
        }

        if(aboutUs== view){
            startActivity(new Intent(this,AboutUsActivity.class));
        }
    }


}
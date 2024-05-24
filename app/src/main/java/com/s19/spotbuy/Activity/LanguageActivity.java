package com.s19.spotbuy.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.card.MaterialCardView;
import com.s19.spotbuy.R;

public class LanguageActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back, englishLanguageIndicator;
    MaterialCardView englishLanguage, hindiLanguage, bengaliLanguage, tamilLanguage;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        initView();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
                initAds();
            }
        });


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

        mAdView = findViewById(R.id.adView);
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


    private void initAds(){
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                         interstitialAd.show(LanguageActivity.this);


                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {


                    }
                });
    }

}
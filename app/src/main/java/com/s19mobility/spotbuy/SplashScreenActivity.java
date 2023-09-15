package com.s19mobility.spotbuy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.s19mobility.spotbuy.Activity.HomeActivity;
import com.s19mobility.spotbuy.Activity.LoginActivity;
import com.s19mobility.spotbuy.Activity.ProfileActivity;
import com.s19mobility.spotbuy.DataBase.SharedPrefs;
import com.s19mobility.spotbuy.Others.Constants;
import com.s19mobility.spotbuy.Others.ReadBasicFireBaseData;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    SharedPrefs sharedPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefs = new SharedPrefs(this);

        if (sharedPrefs.getLogin())
            new ReadBasicFireBaseData(this);


        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {

            if (sharedPrefs.getLogin() && sharedPrefs.isProfileSet())
                startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
            else if (sharedPrefs.getLogin() && !sharedPrefs.isProfileSet()) {
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                profileIntent.putExtra(Constants.ProfileMode, "edit");
                startActivity(profileIntent);
            } else
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));

            finish();

        }, 3000);

        //initializeApp firebase
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance());

    }

}
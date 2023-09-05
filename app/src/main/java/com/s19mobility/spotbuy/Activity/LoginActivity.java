package com.s19mobility.spotbuy.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.s19mobility.spotbuy.Fragments.Login.LoginFragment;
import com.s19mobility.spotbuy.R;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, LoginFragment.newInstance())
                    .commitNow();
        }
    }

}
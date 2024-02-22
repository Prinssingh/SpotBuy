package com.s19.spotbuy.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.s19.spotbuy.Fragments.Login.LoginFragment;
import com.s19.spotbuy.R;

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
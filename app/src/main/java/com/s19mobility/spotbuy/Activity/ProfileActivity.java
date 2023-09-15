package com.s19mobility.spotbuy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.s19mobility.spotbuy.Fragments.Profile.EditProfileFragment;
import com.s19mobility.spotbuy.Fragments.Profile.ShowProfileFragment;
import com.s19mobility.spotbuy.Others.Constants;
import com.s19mobility.spotbuy.R;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout linearLayout;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();

        Intent intent = getIntent();
        String mode = intent.getStringExtra(Constants.ProfileMode);

        if (Objects.equals(mode, "edit")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, EditProfileFragment.newInstance())
                    .commitNow();
            linearLayout.setVisibility(View.GONE);
        }
        else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ShowProfileFragment.newInstance())
                    .commitNow();
        }


    }

    private void initView() {
        linearLayout = findViewById(R.id.linearLayout);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == back) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                getSupportFragmentManager().popBackStack();

            else finish();
        }
    }
}
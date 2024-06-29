package com.s19.spotbuy.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.s19.spotbuy.DataBase.SharedPrefs;
import com.s19.spotbuy.Fragments.main.AdsFragment;
import com.s19.spotbuy.Fragments.main.ChatsFragment;
import com.s19.spotbuy.Fragments.main.HomeFragment;
import com.s19.spotbuy.Fragments.main.SellFragment;
import com.s19.spotbuy.MainActivity;
import com.s19.spotbuy.Others.PermissionChecker;
import com.s19.spotbuy.R;
import com.s19.spotbuy.Widgets.TestToast;

public class HomeActivity extends MainActivity {
    ChipNavigationBar chipNavigationBar;
    SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPrefs = new SharedPrefs(this);

        chipNavigationBar = findViewById(R.id.bottom_nav_bar);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_home,
                true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,
                        HomeFragment.newInstance())
                .addToBackStack("Home")
                .commit();
        bottomMenu();

        new PermissionChecker(this, this);

    }

    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener
                (new ChipNavigationBar.OnItemSelectedListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public void onItemSelected(int i) {
                        Fragment fragment;
                        if (i == R.id.bottom_nav_home) {
                            fragment = HomeFragment.newInstance();
                        } else if (i == R.id.bottom_nav_messages) {
                            fragment = ChatsFragment.newInstance();
                        } else if (i == R.id.bottom_nav_ads) {
                            fragment = AdsFragment.newInstance(true);
                        } else if (i == R.id.bottom_nav_sell) {
                            fragment = SellFragment.newInstance();
                        } else {
                            fragment = HomeFragment.newInstance();
                        }
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container,
                                        fragment).commit();
                    }
                });
    }

    public void setSelectedChip(int i) {
        try {
            chipNavigationBar.setItemSelected(i, false);
        } catch (Exception e) {
            new TestToast(this,"Error: "+e);
        }
    }


    public void logoutFromApp() {
        sharedPrefs.clearSharedData();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void logoutConfirmationDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout ?")
                .setCancelable(false)
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        logoutFromApp();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Exit From SpotBuy");
        dialog.setCancelable(false);
        dialog.setMessage("Do you want to exit from spot buy app??");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
}
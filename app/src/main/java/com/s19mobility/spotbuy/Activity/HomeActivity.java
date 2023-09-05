package com.s19mobility.spotbuy.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.s19mobility.spotbuy.Fragments.main.AdsFragment;
import com.s19mobility.spotbuy.Fragments.main.HomeFragment;
import com.s19mobility.spotbuy.Fragments.main.MessagesFragment;
import com.s19mobility.spotbuy.Fragments.main.SellFragment;
import com.s19mobility.spotbuy.MainActivity;
import com.s19mobility.spotbuy.Others.GPSTracker;
import com.s19mobility.spotbuy.R;

public class HomeActivity extends MainActivity {
    ChipNavigationBar chipNavigationBar;
    GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        chipNavigationBar = findViewById(R.id.bottom_nav_bar);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_home,
                true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,
                        HomeFragment.newInstance()).commit();
        bottomMenu();

        checkRunTimePermission();
    }

    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener
                (new ChipNavigationBar.OnItemSelectedListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public void onItemSelected(int i) {
                        Fragment fragment;
                        switch (i) {
                            case R.id.bottom_nav_home:
                                fragment = HomeFragment.newInstance();
                                break;
                            case R.id.bottom_nav_messages:
                                fragment = MessagesFragment.newInstance();
                                break;
                            case R.id.bottom_nav_ads:
                                fragment = AdsFragment.newInstance(true);
                                break;

                            case R.id.bottom_nav_sell:
                                fragment = SellFragment.newInstance();
                                break;

                            default:
                                fragment = HomeFragment.newInstance();
                                break;
                        }
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container,
                                        fragment).commit();
                    }
                });
    }

    public void checkRunTimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                gpsTracker = new GPSTracker(this);

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        10);
            }
        } else {
            gpsTracker = new GPSTracker(this); //GPSTracker is class that is used for retrieve user current location
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gpsTracker = new GPSTracker(this);
            } else {
//                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                    // If User Checked 'Don't Show Again' checkbox for runtime permission, then navigate user to Settings
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//                    dialog.setTitle("Permission Required");
//                    dialog.setCancelable(false);
//                    dialog.setMessage("You have to Allow permission to access user location");
//                    dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",
//                                    getPackageName(), null));
//                            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivityForResult(i, 1001);
//                        }
//                    });
//                    AlertDialog alertDialog = dialog.create();
//                    alertDialog.show();
//                }
//                //code for deny
            }
        }
    }


}
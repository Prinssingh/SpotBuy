package com.s19.spotbuy.Others;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionChecker {
    private final Context context;
    private final Activity activity;

    public static final int INTERNET_CODE=101;
    public static final int LOCATION_CODE=201;
    public static final int FINE_LOCATION_CODE=202;
    public static final int CAMERA_CODE=301;
    public static final int READ_EXTERNAL_STORAGE_CODE=401;
    public static final int WRITE_EXTERNAL_CODE=402;
    public static final int CALL_PHONE_CODE=501;


    private  String[] allPerMissions=new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE
    };

    public  PermissionChecker(Context context,Activity activity){
        this.context =context;
        this.activity = activity;
        requestAllPermissions();

    }

    public void requestAllPermissions(){
            activity.requestPermissions(allPerMissions,100);
    }

    public boolean checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[] { permission }, requestCode);
            return false;
        }
        else {
            Log.d("TAG", "checkPermission: "+permission +" Granted");
            return true;

        }
    }



}

package com.s19mobility.spotbuy.Others;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Constants {
    public static String ProfileMode = "profileMode";


    //FireStore Collection names

    public static String UserCollection = "Users";
    public static String VehiclePostCollection = "VehiclePost";
    public static String FuelTypeCollection = "FuelType";
    public static String VehicleBrandCollection = "VehicleBrand";
    public static String VehicleBrandModelCollection = "VehicleBrandModel";
    public static String VehicleCategoryCollection = "VehicleCategory";
    public static String NotificationCollection = "Notifications";
    public static String UserChatsCollection = "Chats";
    public static String ChatRoomCollection = "ChatRoom";
    public static String ChatMessagesCollection = "Messages";

    public static List<String> getYearList() {
        Calendar cal = Calendar.getInstance();
        int currentyear = cal.get(Calendar.YEAR);
        List<String> years = new ArrayList<>();

        for (int i = currentyear; i >= 1990; i--) {
            years.add(String.valueOf(i));
        }

        return years;

    }

    public static List<String> getNumberOfOwners() {
        List<String> owners = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            owners.add(String.valueOf(i));
        }

        return owners;

    }

    public static List<String> getAllTransmissionModes() {
        List<String> modes = new ArrayList<String>();
        modes.add("Manual");
        modes.add("Automatic");
        modes.add("Semi-automatic");
        modes.add("Continuously Variable");
        modes.add("Dual Clutch");
        return modes;

    }
    public static List<String> getGenderList() {
        List<String> genderList = new ArrayList<String>();
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Transgender");
        genderList.add("Other");

        return genderList;
    }


    public static final int CAMERA_REQUEST = 100;
    public static final int STORAGE_REQUEST = 200;
    public static final int CALL_REQUEST = 200;
    public static final int CAMERA_ACTION_PICK_REQUEST_CODE = 101;
    public static final int PICK_IMAGE_GALLERY_REQUEST_CODE = 201;



    public  static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap byteToBitmap(byte[] imageBmp) {
        if (imageBmp==null)
            return null;
        return BitmapFactory.decodeByteArray(imageBmp, 0, imageBmp.length);
    }
}

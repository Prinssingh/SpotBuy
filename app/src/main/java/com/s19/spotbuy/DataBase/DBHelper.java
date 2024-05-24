package com.s19.spotbuy.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.s19.spotbuy.DataBase.VehicleDetails.FuelTypeManager;
import com.s19.spotbuy.DataBase.VehicleDetails.VehicleBrandManager;
import com.s19.spotbuy.DataBase.VehicleDetails.VehicleBrandModelManager;
import com.s19.spotbuy.DataBase.VehicleDetails.VehicleCategoryManager;


public class DBHelper extends SQLiteOpenHelper {

    // Database Information
    static final String DB_NAME = "system.db";

    // database version
    static final int DB_VERSION = 6;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Product
        sqLiteDatabase.execSQL(VehicleCategoryManager.CREATE_TABLE);
        sqLiteDatabase.execSQL(VehicleBrandManager.CREATE_TABLE);
        sqLiteDatabase.execSQL(VehicleBrandModelManager.CREATE_TABLE);
        sqLiteDatabase.execSQL(VehiclePostManager.CREATE_TABLE);
        sqLiteDatabase.execSQL(FuelTypeManager.CREATE_TABLE);
        sqLiteDatabase.execSQL(UserManager.CREATE_TABLE);
        sqLiteDatabase.execSQL(ImageManager.CREATE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VehicleCategoryManager.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VehicleBrandManager.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VehicleBrandModelManager.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VehiclePostManager.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FuelTypeManager.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserManager.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ImageManager.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}

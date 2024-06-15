package com.s19.spotbuy.DataBase.VehicleDetails;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.s19.spotbuy.DataBase.DBHelper;
import com.s19.spotbuy.Models.VehicleBrand;

import java.util.ArrayList;
import java.util.List;

public class VehicleBrandManager {
    public static final String TABLE_NAME = "VEHICLE_BRAND";


    // Table columns
    public static final String _ID = "_id";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String ACTIVE = "active";
    public static final String CATEGORY_ID = "categoryId";


    //Create Table Query
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID + " TEXT NOT NULL, "
            + NAME + " TEXT NOT NULL, "
            + ACTIVE + " INTEGER, "
            + CATEGORY_ID + " TEXT NOT NULL "
            + ");";


    private final Context context;
    private DBHelper helper;
    private SQLiteDatabase dbw;
    private SQLiteDatabase dbr;

    public VehicleBrandManager(Context context) {
        this.context = context;
        helper = new DBHelper(context);
        dbw = helper.getWritableDatabase();
        dbr = helper.getReadableDatabase();

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        helper.close();
    }

    public VehicleBrandManager open() throws SQLException {
        helper = new DBHelper(context);
        dbw = helper.getWritableDatabase();
        dbr = helper.getReadableDatabase();

        return this;
    }

    public void close() {
        helper.close();
    }

    //Operations
    public void insert(VehicleBrand brand) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, brand.getId());
        contentValues.put(NAME, brand.getName());
        contentValues.put(ACTIVE, brand.isActive());
        contentValues.put(CATEGORY_ID, brand.getCategoryId());
        dbw.insert(TABLE_NAME, null, contentValues);
        Log.d("TAG", "insert: Brand" + brand.getName());

    }

    public void update(VehicleBrand brand) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, brand.getId());
        contentValues.put(NAME, brand.getName());
        contentValues.put(ACTIVE, brand.isActive());
        contentValues.put(CATEGORY_ID, brand.getCategoryId());

        dbw.update(TABLE_NAME, contentValues, ID + " = " + brand.getId(), null);

    }

    public void delete(VehicleBrand brand) {
        dbw.delete(TABLE_NAME, ID + "=" + brand.getId(), null);

    }

    public void clearTable(){
//        String sqlQuery = " DELETE * FROM " + TABLE_NAME + ";";
        dbw.delete(TABLE_NAME, null,null);

    }

    @SuppressLint("Range")
    public ArrayList<VehicleBrand> listAllbyCategoryId(String categoryID) {
        ArrayList<VehicleBrand> categoryList = new ArrayList<>();

        String sqlQuery = " SELECT * FROM " + TABLE_NAME + " WHERE " + CATEGORY_ID + "='" + categoryID + "';";
        @SuppressLint("Recycle")
        Cursor cursor = dbr.rawQuery(sqlQuery, null);//        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_NAME,, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                VehicleBrand unit = new VehicleBrand();
                unit.setId(cursor.getString(cursor.getColumnIndex(ID)));
                unit.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                unit.setActive(cursor.getInt(cursor.getColumnIndex(ACTIVE))!=0);
                unit.setCategoryId(cursor.getString(cursor.getColumnIndex(CATEGORY_ID)));

                categoryList.add(unit);


            }
        }

        return categoryList;
    }


    @SuppressLint("Range")
    public List<String> getBrandByCategoryId(String categoryID) {
        List<String> temp = new ArrayList() {
        };

        String sqlQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + CATEGORY_ID + " = \"" + categoryID + "\";";


        @SuppressLint("Recycle")
        Cursor cursor = dbr.rawQuery(sqlQuery,null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(NAME));
                temp.add(name);
            }

        }


        return temp;
    }

   @SuppressLint("Range")
   public VehicleBrand getBrandById(String id){
        VehicleBrand brand= new VehicleBrand();
       String sqlQuery = " SELECT * FROM " + TABLE_NAME + " WHERE " + ID + "='" + id + "';";
       @SuppressLint("Recycle")
       Cursor cursor = dbr.rawQuery(sqlQuery, null);//        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_NAME,, null, null, null, null, null);

       if (cursor != null &&  cursor.moveToFirst()) {
               brand.setId(cursor.getString(cursor.getColumnIndex(ID)));
               brand.setName(cursor.getString(cursor.getColumnIndex(NAME)));
               brand.setActive(cursor.getInt(cursor.getColumnIndex(ACTIVE))!=0);
               brand.setCategoryId(cursor.getString(cursor.getColumnIndex(CATEGORY_ID)));

       }

        return brand;
   }

    public int getCount(){
        int count=0;
        count = (int) DatabaseUtils.queryNumEntries(dbr, TABLE_NAME);
        return  count;

        ///  String countQuery = "SELECT  * FROM " + TABLE_NAME;
        //    SQLiteDatabase db = this.getReadableDatabase();
        //    Cursor cursor = db.rawQuery(countQuery, null);
        //    int count = cursor.getCount();
    }

}

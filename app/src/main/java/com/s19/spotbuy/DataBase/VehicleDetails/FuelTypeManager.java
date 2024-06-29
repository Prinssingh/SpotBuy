package com.s19.spotbuy.DataBase.VehicleDetails;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.s19.spotbuy.DataBase.DBHelper;
import com.s19.spotbuy.Models.FuelType;

import java.util.ArrayList;
import java.util.List;

public class FuelTypeManager {
    public static final String TABLE_NAME = "FUEL_TYPE";


    // Table columns
    public static final String _ID = "_id";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String ACTIVE = "active";


    //Create Table Query
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID + " TEXT NOT NULL, "
            + NAME + " TEXT NOT NULL, "
            + ACTIVE + " INTEGER "
            + ");";


    private final Context context;
    private DBHelper helper;
    private SQLiteDatabase dbw;
    private SQLiteDatabase dbr;

    public FuelTypeManager(Context context) {
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

    public FuelTypeManager open() throws SQLException {
        helper = new DBHelper(context);
        dbw = helper.getWritableDatabase();
        dbr = helper.getReadableDatabase();

        return this;
    }

    public void close() {
        helper.close();
    }

    //Operations
    public void insert(FuelType fuelType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, fuelType.getId());
        contentValues.put(NAME, fuelType.getName());
        contentValues.put(ACTIVE, fuelType.isActive());
        dbw.insert(TABLE_NAME, null, contentValues);

    }

    public void update(FuelType fuelType) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, fuelType.getId());
        contentValues.put(NAME, fuelType.getName());
        contentValues.put(ACTIVE, fuelType.isActive());

        dbw.update(TABLE_NAME, contentValues, ID + " = '" + fuelType.getId()+"'", null);

    }

    public void delete(FuelType fuelType) {
        dbw.delete(TABLE_NAME, ID + "= '" + fuelType.getId()+"'", null);

    }

    public void clearTable(){
        String sqlQuery = " DELETE * FROM " + TABLE_NAME + ";";
        dbw.delete(TABLE_NAME, null,null);

    }

    @SuppressLint("Range")
    public ArrayList<FuelType> listAll() {
        ArrayList<FuelType> categoryList = new ArrayList<>();

        String sqlQuery = " SELECT * FROM " + TABLE_NAME + ";";
        @SuppressLint("Recycle")
        Cursor cursor = dbr.rawQuery(sqlQuery, null);//        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_NAME,, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                FuelType unit = new FuelType();
                unit.setId(cursor.getString(cursor.getColumnIndex(ID)));
                unit.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                unit.setActive(cursor.getInt(cursor.getColumnIndex(ACTIVE))!=0);

                categoryList.add(unit);


            }
        }

        return categoryList;
    }

    @SuppressLint("Range")
    public List<String> getStringList() {
        List<String> temp = new ArrayList() {};

        String sqlQuery = " SELECT * FROM " + TABLE_NAME + ";";

        @SuppressLint("Recycle")
        Cursor cursor = dbr.rawQuery(sqlQuery,null);
      //  Cursor cursor = dbr.query(TABLE_NAME, new String[]{NAME}, CATEGORY_ID + "=? AND " + ACTIVE + "='1'", new String[]{categoryID}, null, null, null, null);//        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_NAME,, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(NAME));
                temp.add(name);
            }
        }
        return temp;
    }

//    public void clearTable(){
//        dbw.delete(TABLE_NAME,null, null);
//    }

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

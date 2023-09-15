package com.s19mobility.spotbuy.DataBase.VehicleDetails;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.s19mobility.spotbuy.DataBase.DBHelper;
import com.s19mobility.spotbuy.Models.VehicleCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VehicleCategoryManager {
    public static final String TABLE_NAME = "VEHICLE_CATEGORY";


    // Table columns
    public static final String _ID = "_id";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final String ACTIVE = "active";


    //Create Table Query
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID + " TEXT NOT NULL, "
            + NAME + " TEXT NOT NULL, "
            + IMAGE + " TEXT, "
            + ACTIVE + " INTEGER  "
            + ");";

    private final Context context;
    private DBHelper helper;
    private SQLiteDatabase dbw;
    private SQLiteDatabase dbr;


    public VehicleCategoryManager(Context context) {
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

    public VehicleCategoryManager open() throws SQLException {
        helper = new DBHelper(context);
        dbw = helper.getWritableDatabase();
        dbr = helper.getReadableDatabase();

        return this;
    }

    public void close() {
        helper.close();
    }


    //Operations

    public void insert(VehicleCategory category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, category.getId());
        contentValues.put(NAME, category.getName());
        contentValues.put(ACTIVE, category.isActive());
        contentValues.put(IMAGE, category.getImage());
        dbw.insert(TABLE_NAME, null, contentValues);

    }

    public void update(VehicleCategory category) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, category.getId());
        contentValues.put(NAME, category.getName());
        contentValues.put(ACTIVE, category.isActive());
        contentValues.put(IMAGE, category.getImage());

        dbw.update(TABLE_NAME, contentValues, ID + " = " + category.getId(), null);

    }

    public void delete(VehicleCategory category) {
        dbw.delete(TABLE_NAME, ID + "=" + category.getId(), null);

    }

    public void clearTable(){
        String sqlQuery = " DELETE * FROM " + TABLE_NAME + ";";
        dbw.delete(TABLE_NAME, null,null);

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

    @SuppressLint("Range")
    public ArrayList<VehicleCategory> listAll() {
        ArrayList<VehicleCategory> categoryList = new ArrayList<>();

        String sqlQuery = " SELECT * FROM " + TABLE_NAME + ";";
        @SuppressLint("Recycle")
        Cursor cursor = dbr.rawQuery(sqlQuery, null);//        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_NAME,, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                VehicleCategory unit = new VehicleCategory();
                unit.setId(cursor.getString(cursor.getColumnIndex(ID)));
                unit.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                unit.setImage(cursor.getString(cursor.getColumnIndex(IMAGE)));
                unit.setActive(cursor.getInt(cursor.getColumnIndex(ACTIVE))!=0);


                categoryList.add(unit);


            }
        }

        return categoryList;
    }

    @SuppressLint("Range")
    public List<String> getStringList() {

        List<String> temp = new ArrayList() {
        };

        String sqlQuery = " SELECT * FROM " + TABLE_NAME + ";";
        @SuppressLint("Recycle")
        Cursor cursor = dbr.rawQuery(sqlQuery, null);//        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_NAME,, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(NAME)) ;
                temp.add(name);

            }

        }


        return temp;
    }

    @SuppressLint("Range")
    public VehicleCategory getCategoryByID(String id) {
        VehicleCategory category = new VehicleCategory();

        String Sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " = \"" + id + "\";";

        @SuppressLint("Recycle") Cursor cursor = dbr.rawQuery(Sql, null);

        if (cursor != null) {
            cursor.moveToFirst();
            category.setId(id);
            category.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            category.setImage(cursor.getString(cursor.getColumnIndex(IMAGE)));
            category.setActive(cursor.getInt(cursor.getColumnIndex(ACTIVE))!=0);

        }

        return category;
    }


}

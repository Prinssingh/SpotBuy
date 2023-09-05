package com.s19mobility.spotbuy.DataBase.VehicleDetails;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.s19mobility.spotbuy.DataBase.DBHelper;
import com.s19mobility.spotbuy.Models.VehicleBrand;
import com.s19mobility.spotbuy.Models.VehicleBrandModel;

import java.util.ArrayList;
import java.util.List;

public class VehicleBrandModelManager {
    public static final String TABLE_NAME = "VEHICLE_BRAND_MODEL";


    // Table columns
    public static final String _ID = "_id";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String ACTIVE = "active";
    public static final String CATEGORY_ID = "categoryId";
    public static final String BRAND_ID = "brandID";


    //Create Table Query
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID + " TEXT NOT NULL, "
            + NAME + " TEXT NOT NULL, "
            + ACTIVE + " INTEGER, "
            + CATEGORY_ID + " TEXT NOT NULL, "
            + BRAND_ID + " TEXT NOT NULL "
            + ");";


    private final Context context;
    private DBHelper helper;
    private SQLiteDatabase dbw;
    private SQLiteDatabase dbr;

    public VehicleBrandModelManager(Context context) {
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

    public VehicleBrandModelManager open() throws SQLException {
        helper = new DBHelper(context);
        dbw = helper.getWritableDatabase();
        dbr = helper.getReadableDatabase();

        return this;
    }

    public void close() {
        helper.close();
    }

    //Operations
    public void insert(VehicleBrandModel brandModel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, brandModel.getId());
        contentValues.put(NAME, brandModel.getName());
        contentValues.put(ACTIVE, brandModel.isActive());
        contentValues.put(CATEGORY_ID, brandModel.getCategoryId());
        contentValues.put(BRAND_ID, brandModel.getBrandId());
        dbw.insert(TABLE_NAME, null, contentValues);

    }

    public void update(VehicleBrandModel brandModel) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, brandModel.getId());
        contentValues.put(NAME, brandModel.getName());
        contentValues.put(ACTIVE, brandModel.isActive());
        contentValues.put(CATEGORY_ID, brandModel.getCategoryId());
        contentValues.put(BRAND_ID, brandModel.getBrandId());
        dbw.update(TABLE_NAME, contentValues, ID + " = " + brandModel.getId(), null);

    }

    public void delete(VehicleBrandModel brandModel) {
        dbw.delete(TABLE_NAME, ID + "=" + brandModel.getId(), null);

    }

    public void clearTable(){
        String sqlQuery = " DELETE * FROM " + TABLE_NAME + ";";
        dbw.delete(TABLE_NAME, null,null);

    }
    @SuppressLint("Range")
    public ArrayList<VehicleBrandModel> listAllbyBrandId(String brandId) {
        ArrayList<VehicleBrandModel> categoryList = new ArrayList<>();

        String sqlQuery = " SELECT * FROM " + TABLE_NAME + " WHERE " + BRAND_ID + "='" + brandId + "' AND " + ACTIVE + "='1';";
        @SuppressLint("Recycle")
        Cursor cursor = dbr.rawQuery(sqlQuery, null);//        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_NAME,, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                VehicleBrandModel unit = new VehicleBrandModel();
                unit.setId(cursor.getString(cursor.getColumnIndex(ID)));
                unit.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                unit.setActive(cursor.getInt(cursor.getColumnIndex(ACTIVE))!=0);
                unit.setCategoryId(cursor.getString(cursor.getColumnIndex(CATEGORY_ID)));
                unit.setBrandId(cursor.getString(cursor.getColumnIndex(BRAND_ID)));

                categoryList.add(unit);


            }
        }

        return categoryList;
    }



    @SuppressLint("Range")
    public List<String> getBrandModelByBrandId(String brandId) {
        List<String> temp = new ArrayList() {
        };

        String sqlQuery = " SELECT " + NAME + " FROM " + TABLE_NAME + " WHERE " + BRAND_ID + "='" + brandId + "' AND " + ACTIVE + "='1';";
        @SuppressLint("Recycle")
        Cursor cursor = dbr.query(TABLE_NAME, new String[]{NAME}, BRAND_ID + "=? AND " + ACTIVE + "='1'", new String[]{brandId}, null, null, null, null);//        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_NAME,, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(NAME));
                temp.add(name);
            }
        }
        return temp;
    }

}

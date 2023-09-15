package com.s19mobility.spotbuy.DataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.s19mobility.spotbuy.Models.ImageModel;

public class ImageManager {
    public static final String TABLE_NAME = "IMAGES";

    public static final String _ID = "_id";
    public static final String LINK = "link";
    public static final String BLOB_DATA = "blob_data";

    //Create Table Query
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + LINK + " TEXT NOT NULL, "
            + BLOB_DATA + " BLOB "
            + ");";


    private final Context context;
    private DBHelper helper;
    private SQLiteDatabase dbw;
    private SQLiteDatabase dbr;

    public ImageManager(Context context) {
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

    public ImageManager open() throws SQLException {
        helper = new DBHelper(context);
        dbw = helper.getWritableDatabase();
        dbr = helper.getReadableDatabase();

        return this;
    }

    public void close() {
        helper.close();
    }


    //Operations
    public void insert(ImageModel image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(LINK, image.getLink());
        contentValues.put(BLOB_DATA, image.getImageData());
        dbw.insert(TABLE_NAME, null, contentValues);

        Log.e("TAG", "insert: " + image);

    }

    public void update(ImageModel image) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(_ID, image.getId());
        contentValues.put(LINK, image.getLink());
        contentValues.put(BLOB_DATA, image.getImageData());

        dbw.update(TABLE_NAME, contentValues, LINK + " = " + image.getLink(), null);

    }

    public void delete(ImageModel image) {
        dbw.delete(TABLE_NAME, LINK + "=" + image.getLink(), null);
    }
    public void deleteByLink(String link) {
        dbw.delete(TABLE_NAME, LINK + "=" +link, null);
    }

    @SuppressLint("Range")
    public ImageModel getImageByLink(String image) {
        ImageModel imageModel = new ImageModel();
//        @SuppressLint("Recycle")
//        Cursor cursor = dbr.query(TABLE_NAME, new String[]{_ID,LINK,BLOB_DATA}, LINK + "=? ", new String[]{image}, null, null, null, null);//        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_NAME,, null, null, null, null, null);

        String sqlQuery = " SELECT * FROM " + TABLE_NAME + " WHERE " + LINK + " ='" + image + "'" + ";";
        Log.d("TAG", "getImageByLink: " + sqlQuery);
        @SuppressLint("Recycle") Cursor cursor = dbr.rawQuery(sqlQuery, null);//
        if (cursor != null && cursor.moveToFirst()) {
            imageModel.setImageData(cursor.getBlob(cursor.getColumnIndex(BLOB_DATA)));
            imageModel.setLink(cursor.getString(cursor.getColumnIndex(LINK)));
            imageModel.setId(cursor.getLong(cursor.getColumnIndex(_ID)));

        }
        Log.d("TAG", "getImageByLink: " + cursor.getCount());

        return imageModel;

    }

    public void clearTable() {
        dbw.delete(TABLE_NAME, null, null);
    }

    public int getCount() {
        int count = 0;
        count = (int) DatabaseUtils.queryNumEntries(dbr, TABLE_NAME);
        return count;

        ///  String countQuery = "SELECT  * FROM " + TABLE_NAME;
        //    SQLiteDatabase db = this.getReadableDatabase();
        //    Cursor cursor = db.rawQuery(countQuery, null);
        //    int count = cursor.getCount();
    }

}

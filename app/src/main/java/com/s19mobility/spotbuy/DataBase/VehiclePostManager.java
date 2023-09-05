package com.s19mobility.spotbuy.DataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.s19mobility.spotbuy.Models.FuelType;
import com.s19mobility.spotbuy.Models.VehiclePost;

import java.util.ArrayList;

public class VehiclePostManager {
    public static final String TABLE_NAME = "VEHICLE_POST";


    // Table columns
    public static final String _ID = "_id";
    public static final String ID = "id";
    public static final String ACTIVE = "active";
    public static final String BRAND_ID = "brandId";
    public static final String CATEGORY_ID = "categoryId";
    public static final String CITY = "city";
    public static final String COUNTRY = "country";
    public static final String DATE_TIME = "dateTime";
    public static final String DESCRIPTION = "description";
    public static final String FUEL_ID = "fuelId";
    public static final String IMAGE_LIST = "imageList";
    public static final String KMS_RIDDEN = "kmsRidden";
    public static final String BRAND_MODEL_ID = "modelId";
    public static final String MODEL_YEAR = "modelYear";
    public static final String NO_OF_OWNERS = "numberOfOwner";
    public static final String POST_TIME_END = "postTimeEnd";
    public static final String POST_TIME_START = "postTimeStart";
    public static final String PRICE = "price";
    public static final String STATE = "state";
    public static final String STATUS = "status";
    public static final String TRANSMISSION_MODE = "transmissionMode";
    public static final String TRIM_ID = "trimId";
    public static final String SELLER_ID = "sellerId";
    public static final String BUYER_ID = "buyerId";


    //Create Table Query
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID + " TEXT NOT NULL, "
            + ACTIVE + " INTEGER, "
            + BRAND_ID + " TEXT NOT NULL, "
            + CATEGORY_ID + " TEXT NOT NULL, "
            + CITY + " TEXT NOT NULL, "
            + COUNTRY + " TEXT NOT NULL, "
            + DATE_TIME + " TEXT , "
            + DESCRIPTION + " TEXT , "
            + FUEL_ID + " TEXT NOT NULL, "
            + IMAGE_LIST + " TEXT NOT NULL, "
            + KMS_RIDDEN + " INTEGER, "
            + BRAND_MODEL_ID + " TEXT NOT NULL, "
            + MODEL_YEAR + " INTEGER, "
            + NO_OF_OWNERS + " INTEGER, "
            + POST_TIME_END + " TEXT , "
            + POST_TIME_START + " TEXT , "
            + PRICE + " FLOAT NOT NULL, "
            + STATE + " TEXT , "
            + STATUS + " TEXT , "
            + TRANSMISSION_MODE + " TEXT , "
            + TRIM_ID + " TEXT NOT NULL, "
            + SELLER_ID + " TEXT NOT NULL, "
            + BUYER_ID + " TEXT "
            + ");";


    private final Context context;
    private DBHelper helper;
    private SQLiteDatabase dbw;
    private SQLiteDatabase dbr;

    public VehiclePostManager(Context context) {
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

    public VehiclePostManager open() throws SQLException {
        helper = new DBHelper(context);
        dbw = helper.getWritableDatabase();
        dbr = helper.getReadableDatabase();

        return this;
    }

    public void close() {
        helper.close();
    }


    //Operations
    public void insert(VehiclePost vehiclePost) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, vehiclePost.getId());
        contentValues.put(ACTIVE, vehiclePost.isActive());
        contentValues.put(BRAND_ID, vehiclePost.getBrandId());
        contentValues.put(CATEGORY_ID, vehiclePost.getCategoryId());
        contentValues.put(CITY, vehiclePost.getCity());
        contentValues.put(COUNTRY, vehiclePost.getCountry());
        contentValues.put(DATE_TIME, vehiclePost.getDateTimeString());
        contentValues.put(DESCRIPTION, vehiclePost.getDescription());
        contentValues.put(FUEL_ID, vehiclePost.getFuelId());
        contentValues.put(IMAGE_LIST, vehiclePost.jsonImageList());
        contentValues.put(BRAND_MODEL_ID, vehiclePost.getModelId());
        contentValues.put(KMS_RIDDEN, vehiclePost.getKmsRidden());
        contentValues.put(MODEL_YEAR, vehiclePost.getModelYear());
        contentValues.put(NO_OF_OWNERS, vehiclePost.getNumberOfOwner());
        contentValues.put(POST_TIME_START, vehiclePost.getPostTimeStartString());
        contentValues.put(POST_TIME_END, vehiclePost.getPostTimeEndString());
        contentValues.put(PRICE, vehiclePost.getPrice());
        contentValues.put(STATE, vehiclePost.getState());
        contentValues.put(STATUS, vehiclePost.getStatus());
        contentValues.put(TRANSMISSION_MODE, vehiclePost.getTransmissionMode());
        contentValues.put(TRIM_ID, vehiclePost.getTrimId());
        contentValues.put(SELLER_ID, vehiclePost.getSellerId());
        contentValues.put(BUYER_ID, vehiclePost.getBuyerId());


        dbw.insert(TABLE_NAME, null, contentValues);

    }

    public void update(VehiclePost vehiclePost) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(ACTIVE, vehiclePost.isActive());
        contentValues.put(BRAND_ID, vehiclePost.getBrandId());
        contentValues.put(CATEGORY_ID, vehiclePost.getCategoryId());
        contentValues.put(CITY, vehiclePost.getCity());
        contentValues.put(COUNTRY, vehiclePost.getCountry());
        contentValues.put(DATE_TIME, vehiclePost.getDateTimeString());
        contentValues.put(DESCRIPTION, vehiclePost.getDescription());
        contentValues.put(FUEL_ID, vehiclePost.getFuelId());
        contentValues.put(IMAGE_LIST, vehiclePost.jsonImageList());
        contentValues.put(BRAND_MODEL_ID, vehiclePost.getModelId());
        contentValues.put(KMS_RIDDEN, vehiclePost.getKmsRidden());
        contentValues.put(MODEL_YEAR, vehiclePost.getModelYear());
        contentValues.put(NO_OF_OWNERS, vehiclePost.getNumberOfOwner());
        contentValues.put(POST_TIME_START, vehiclePost.getPostTimeStartString());
        contentValues.put(POST_TIME_END, vehiclePost.getPostTimeEndString());
        contentValues.put(PRICE, vehiclePost.getPrice());
        contentValues.put(STATE, vehiclePost.getState());
        contentValues.put(STATUS, vehiclePost.getStatus());
        contentValues.put(TRANSMISSION_MODE, vehiclePost.getTransmissionMode());
        contentValues.put(TRIM_ID, vehiclePost.getTrimId());
        contentValues.put(SELLER_ID, vehiclePost.getSellerId());
        contentValues.put(BUYER_ID, vehiclePost.getBuyerId());

        dbw.update(TABLE_NAME, contentValues, ID + " = " + vehiclePost.getId(), null);

    }

    public void delete(VehiclePost vehiclePost) {
        dbw.delete(TABLE_NAME, ID + "=" + vehiclePost.getId(), null);

    }

    @SuppressLint("Range")
    public ArrayList<VehiclePost> listAll() {
        ArrayList<VehiclePost> vehiclePosts = new ArrayList<>();

        String sqlQuery = " SELECT * FROM " + TABLE_NAME + ";";
        @SuppressLint("Recycle")
        Cursor cursor = dbr.rawQuery(sqlQuery, null);//        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_NAME,, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                VehiclePost vehicle = new VehiclePost();

                vehicle.setId(cursor.getString(cursor.getColumnIndex(ID)));
                vehicle.setActive(cursor.getInt(cursor.getColumnIndex(ACTIVE))!=0);
                vehicle.setBrandId(cursor.getString(cursor.getColumnIndex(BRAND_ID)));
                vehicle.setCategoryId(cursor.getString(cursor.getColumnIndex(CATEGORY_ID)));
                vehicle.setCity(cursor.getString(cursor.getColumnIndex(CITY)));
                vehicle.setCountry(cursor.getString(cursor.getColumnIndex(COUNTRY)));
                vehicle.setDateTimeString(cursor.getString(cursor.getColumnIndex(DATE_TIME)));
                vehicle.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
                vehicle.setFuelId(cursor.getString(cursor.getColumnIndex(FUEL_ID)));
                vehicle.ImageListFromJson(cursor.getString(cursor.getColumnIndex(IMAGE_LIST)));
                vehicle.setKmsRidden(cursor.getInt(cursor.getColumnIndex(KMS_RIDDEN)));
                vehicle.setModelId(cursor.getString(cursor.getColumnIndex(BRAND_MODEL_ID)));
                vehicle.setModelYear(cursor.getInt(cursor.getColumnIndex(MODEL_YEAR)));
                vehicle.setNumberOfOwner(cursor.getInt(cursor.getColumnIndex(NO_OF_OWNERS)));
                vehicle.setPostTimeStartString(cursor.getString(cursor.getColumnIndex(POST_TIME_START)));
                vehicle.setPostTimeEndString(cursor.getString(cursor.getColumnIndex(POST_TIME_END)));
                vehicle.setPrice(cursor.getFloat(cursor.getColumnIndex(PRICE)));
                vehicle.setState(cursor.getString(cursor.getColumnIndex(STATE)));
                vehicle.setStatus(cursor.getString(cursor.getColumnIndex(STATUS)));
                vehicle.setTransmissionMode(cursor.getString(cursor.getColumnIndex(TRANSMISSION_MODE)));
                vehicle.setTrimId(cursor.getString(cursor.getColumnIndex(TRIM_ID)));
                vehicle.setSellerId(cursor.getString(cursor.getColumnIndex(SELLER_ID)));
                vehicle.setBuyerId(cursor.getString(cursor.getColumnIndex(BUYER_ID)));


                vehiclePosts.add(vehicle);


            }
        }

        return vehiclePosts;
    }



}


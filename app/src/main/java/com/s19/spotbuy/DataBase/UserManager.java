package com.s19.spotbuy.DataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.s19.spotbuy.Models.User;

import java.util.ArrayList;

public class UserManager {

    public static final String TABLE_NAME = "USERS";


    // Table columns
    public static final String _ID = "_id";
    public static final String ID = "id";
    public static final String ACTIVE = "active";
    public static final String ADDRESS = "address";
    public static final String ALT_MOBILE = "alt_mobile";
    public static final String AVAILABLE_POST = "availablePost";
    public static final String DATE_TIME = "dateTime";
    public static final String EMAIl = "email";
    public static final String GENDER = "gender";
    public static final String IMAGE = "image";
    public static final String MOBILE = "mobile";
    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String TOTAL_POST = "totalPost";
    public static final String UID = "uid";
    public static final String FOLLOWERS = "followers";
    public static final String FOLLOWING = "following";

    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String COUNTRY = "country";


    //Create Table Query
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID + " TEXT NOT NULL, "
            + ACTIVE + " INTEGER, "
            + ADDRESS + " TEXT , "
            + CITY + " TEXT , "
            + STATE + " TEXT , "
            + COUNTRY + " TEXT , "
            + ALT_MOBILE + " TEXT , "
            + AVAILABLE_POST + " INTEGER , "
            + DATE_TIME + " TEXT , "
            + EMAIl + " TEXT , "
            + GENDER + " TEXT , "
            + IMAGE + " TEXT , "
            + MOBILE + " TEXT NOT NULL, "
            + NAME + " TEXT, "
            + PASSWORD + " TEXT, "
            + TOTAL_POST + " INTEGER , "
            + UID + " TEXT , "
            + FOLLOWERS + " TEXT , "
            + FOLLOWING + " TEXT "
            + ");";


    private final Context context;
    private DBHelper helper;
    private SQLiteDatabase dbw;
    private SQLiteDatabase dbr;

    public UserManager(Context context) {
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

    public UserManager open() throws SQLException {
        helper = new DBHelper(context);
        dbw = helper.getWritableDatabase();
        dbr = helper.getReadableDatabase();

        return this;
    }

    public void close() {
        helper.close();
    }


    //Operations
    public void insert(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, user.getId());
        contentValues.put(ACTIVE, user.isActive());
        contentValues.put(ADDRESS, user.getAddress());
        contentValues.put(CITY, user.getCity());
        contentValues.put(STATE, user.getState());
        contentValues.put(COUNTRY, user.getCountry());
        contentValues.put(ALT_MOBILE, user.getAlt_mobile());
        contentValues.put(AVAILABLE_POST, user.getAvailablePost());
        contentValues.put(DATE_TIME, user.getDateTimeString());
        contentValues.put(EMAIl, user.getEmail());
        contentValues.put(GENDER, user.getGender());
        contentValues.put(IMAGE, user.getImage());
        contentValues.put(MOBILE, user.getMobile());
        contentValues.put(NAME, user.getName());
        contentValues.put(PASSWORD, user.getPassword());
        contentValues.put(TOTAL_POST, user.getTotalPost());
        contentValues.put(UID, user.getId());
        contentValues.put(FOLLOWERS, user.jsonFollowersList());
        contentValues.put(FOLLOWING, user.jsonFollowingList());


        dbw.insert(TABLE_NAME, null, contentValues);

    }

    public void update(User user) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, user.getId());
        contentValues.put(ACTIVE, user.isActive());
        contentValues.put(ADDRESS, user.getAddress());
        contentValues.put(CITY, user.getCity());
        contentValues.put(STATE, user.getState());
        contentValues.put(COUNTRY, user.getCountry());
        contentValues.put(ALT_MOBILE, user.getAlt_mobile());
        contentValues.put(AVAILABLE_POST, user.getAvailablePost());
        contentValues.put(DATE_TIME, user.getDateTimeString());
        contentValues.put(EMAIl, user.getEmail());
        contentValues.put(GENDER, user.getGender());
        contentValues.put(IMAGE, user.getImage());
        contentValues.put(MOBILE, user.getMobile());
        contentValues.put(NAME, user.getName());
        contentValues.put(PASSWORD, user.getPassword());
        contentValues.put(TOTAL_POST, user.getTotalPost());
        contentValues.put(UID, user.getId());

        contentValues.put(FOLLOWERS, user.jsonFollowersList());
        contentValues.put(FOLLOWING, user.jsonFollowingList());

        dbw.update(TABLE_NAME, contentValues, ID + " = '" + user.getId()+"'", null);

        Toast.makeText(context, "Local User Updated", Toast.LENGTH_SHORT).show();
    }
    public void updateFollowing(User user) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(FOLLOWING, user.jsonFollowingList());
        dbw.update(TABLE_NAME, contentValues, ID + " = " + user.getId(), null);

    }
    public void updateFollowers(User user) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(FOLLOWERS, user.jsonFollowersList());
        dbw.update(TABLE_NAME, contentValues, ID + " = '" + user.getId()+"'", null);

    }
    public void delete(User user) {
        dbw.delete(TABLE_NAME, ID + "='" + user.getId()+"'", null);

    }

    @SuppressLint("Range")
    public ArrayList<User> listAll() {
        ArrayList<User> users = new ArrayList<>();

        String sqlQuery = " SELECT * FROM " + TABLE_NAME + ";";
        @SuppressLint("Recycle")
        Cursor cursor = dbr.rawQuery(sqlQuery, null);//        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_NAME,, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                User user = new User();

                user.setId(cursor.getString(cursor.getColumnIndex(ID)));
                user.setActive(cursor.getInt(cursor.getColumnIndex(ACTIVE)) != 0);
                user.setAddress(cursor.getString(cursor.getColumnIndex(ADDRESS)));
                user.setCity(cursor.getString(cursor.getColumnIndex(CITY)));
                user.setState(cursor.getString(cursor.getColumnIndex(STATE)));
                user.setCountry(cursor.getString(cursor.getColumnIndex(COUNTRY)));
                user.setAlt_mobile(cursor.getString(cursor.getColumnIndex(ALT_MOBILE)));
                user.setAvailablePost(cursor.getInt(cursor.getColumnIndex(AVAILABLE_POST)));
                user.setDateTimeString(cursor.getString(cursor.getColumnIndex(DATE_TIME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(EMAIl)));
                user.setGender(cursor.getString(cursor.getColumnIndex(GENDER)));
                user.setImage(cursor.getString(cursor.getColumnIndex(IMAGE)));
                user.setMobile(cursor.getString(cursor.getColumnIndex(MOBILE)));
                user.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(PASSWORD)));
                user.setTotalPost(cursor.getInt(cursor.getColumnIndex(TOTAL_POST)));
                user.setId(cursor.getString(cursor.getColumnIndex(UID)));
                user.followersLisFromJson(cursor.getString(cursor.getColumnIndex(FOLLOWERS)));
                user.FollowingLisFromJson(cursor.getString(cursor.getColumnIndex(FOLLOWING)));


                users.add(user);


            }
        }

        return users;
    }

    @SuppressLint("Range")
    public User getUserById(String uid){
        User user = new User();
        String sqlQuery = " SELECT * FROM " + TABLE_NAME + " WHERE "+ID +" ='"+uid+"'"+";";
        @SuppressLint("Recycle") Cursor cursor = dbr.rawQuery(sqlQuery, null);//
        if (cursor != null && cursor.moveToFirst()) {
            user.setId(cursor.getString(cursor.getColumnIndex(ID)));
            user.setActive(cursor.getInt(cursor.getColumnIndex(ACTIVE)) != 0);
            user.setAddress(cursor.getString(cursor.getColumnIndex(ADDRESS)));
            user.setCity(cursor.getString(cursor.getColumnIndex(CITY)));
            user.setState(cursor.getString(cursor.getColumnIndex(STATE)));
            user.setCountry(cursor.getString(cursor.getColumnIndex(COUNTRY)));
            user.setAlt_mobile(cursor.getString(cursor.getColumnIndex(ALT_MOBILE)));
            user.setAvailablePost(cursor.getInt(cursor.getColumnIndex(AVAILABLE_POST)));
            user.setDateTimeString(cursor.getString(cursor.getColumnIndex(DATE_TIME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(EMAIl)));
            user.setGender(cursor.getString(cursor.getColumnIndex(GENDER)));
            user.setImage(cursor.getString(cursor.getColumnIndex(IMAGE)));
            user.setMobile(cursor.getString(cursor.getColumnIndex(MOBILE)));
            user.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(PASSWORD)));
            user.setTotalPost(cursor.getInt(cursor.getColumnIndex(TOTAL_POST)));
            user.setId(cursor.getString(cursor.getColumnIndex(UID)));
            user.followersLisFromJson(cursor.getString(cursor.getColumnIndex(FOLLOWERS)));
            user.FollowingLisFromJson(cursor.getString(cursor.getColumnIndex(FOLLOWING)));


        }
        return user;

    }

    public void clearTable(){
        dbw.delete(TABLE_NAME,null, null);
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

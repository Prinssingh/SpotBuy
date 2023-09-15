package com.s19mobility.spotbuy.Models;

import static com.s19mobility.spotbuy.Others.Constants.bitmapToByte;
import static com.s19mobility.spotbuy.Others.Constants.byteToBitmap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.Exclude;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {
    String id;
    boolean active;
    String address;
    String alt_mobile;
    int availablePost;
    Date dateTime;
    String email;
    String gender;
    String image;
    String mobile;
    String name;
    String password;
    String role;
    int totalPost;
    List<String> followers = new ArrayList<>();
    List<String> following=  new ArrayList<>();

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAlt_mobile() {
        return alt_mobile;
    }

    public void setAlt_mobile(String alt_mobile) {
        this.alt_mobile = alt_mobile;
    }

    public int getAvailablePost() {
        return availablePost;
    }

    public void setAvailablePost(int availablePost) {
        this.availablePost = availablePost;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getTotalPost() {
        return totalPost;
    }

    public void setTotalPost(int totalPost) {
        this.totalPost = totalPost;
    }


    public int getFollowerCount() {
        return followers.size();
    }

    public int getFollowingCount() {
        return following.size();
    }

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    ///IMPORTANT FUNCTIONS
    public String jsonFolloersList() {
        Gson gson = new Gson();
        String inputString = gson.toJson(followers);
        return inputString;
    }

    public String[] FolloersLisFromJson(String jsonList) {

        Type type = new TypeToken<String[]>() {
        }.getType();
        Gson gson = new Gson();
        String[] finalOutputString = gson.fromJson(jsonList, type);
        followers = Arrays.asList(finalOutputString);
        return finalOutputString;
    }

    public String jsonFollowingList() {
        Gson gson = new Gson();
        String inputString = gson.toJson(following);
        return inputString;
    }

    public String[] FollowingLisFromJson(String jsonList) {

        Type type = new TypeToken<String[]>() {
        }.getType();
        Gson gson = new Gson();
        String[] finalOutputString = gson.fromJson(jsonList, type);
        following = Arrays.asList(finalOutputString);
        return finalOutputString;
    }

    public String getDateTimeString() {
        return dateFormat.format(dateTime);
    }
    public void setDateTimeString(String date) {
        try {
            dateTime= dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            dateTime=null;
        }
    }

    public Map<String,Object> toUpdateMap(){
        Map<String,Object> temp = new HashMap<String,Object>(){};
         temp.put("name",name);
         temp.put("alt_mobile",alt_mobile);
         temp.put("email",email);
         temp.put("address",address);
         temp.put("gender",gender);
         temp.put("image",image);

        return  temp;
    }

    @Exclude
    private byte[] imageBmp;

    public byte[] getImageBmp() {
        return imageBmp;
    }

    public void setImageBmp(byte[] imageBmp) {
        this.imageBmp = imageBmp;
    }

    public void setImageBitmap(Bitmap bitmap)
    {
        this.imageBmp=bitmapToByte(bitmap);
    }

    public Bitmap getImageBitmap(){
        return byteToBitmap(this.imageBmp);
    }

}

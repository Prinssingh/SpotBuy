package com.s19.spotbuy.Models;

import android.annotation.SuppressLint;

import androidx.annotation.Keep;

import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.Exclude;
import com.google.gson.Gson;
import com.s19.spotbuy.Others.STATUS;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Keep
public class VehiclePost implements Serializable {
    String id;
    boolean active;
    String brandId;
    String categoryId;
    String city;
    String country;
    Date dateTime;
    String description;
    String fuelId;
    List<String> imageList;
    int kmsRidden;
    String modelId;
    int modelYear;
    int numberOfOwner;
    Date postTimeEnd;
    Date postTimeStart;
    float price;
    String state;
    String status;
    String transmissionMode;
    String trimId;
    String sellerId;
    String buyerId;
    String title;
    String fuelType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Exclude
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public  VehiclePost(){}


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

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFuelId() {
        return fuelId;
    }

    public void setFuelId(String fuelId) {
        this.fuelId = fuelId;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public int getKmsRidden() {
        return kmsRidden;
    }

    public void setKmsRidden(int kmsRidden) {
        this.kmsRidden = kmsRidden;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public int getNumberOfOwner() {
        return numberOfOwner;
    }

    public void setNumberOfOwner(int numberOfOwner) {
        this.numberOfOwner = numberOfOwner;
    }

    public Date getPostTimeEnd() {
        return postTimeEnd;
    }

    public void setPostTimeEnd(Date postTimeEnd) {
        this.postTimeEnd = postTimeEnd;
    }

    public Date getPostTimeStart() {
        return postTimeStart;
    }

    public void setPostTimeStart(Date postTimeStart) {
        this.postTimeStart = postTimeStart;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransmissionMode() {
        return transmissionMode;
    }

    public void setTransmissionMode(String transmissionMode) {
        this.transmissionMode = transmissionMode;
    }

    public String getTrimId() {
        return trimId;
    }

    public void setTrimId(String trimId) {
        this.trimId = trimId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }


    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getModelYear() {
        return modelYear;
    }

    public void setModelYear(int modelYear) {
        this.modelYear = modelYear;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }




    ///IMPORTANT FUNCTIONS
    public String jsonImageList() {
        Gson gson = new Gson();
        String inputString = gson.toJson(imageList);
        return inputString;
    }

    public List<String> ImageListFromJson(String jsonList) {

        Type type = new TypeToken<List<String>>() {}.getType();
        Gson gson = new Gson();
        List<String>  finalOutputString = gson.fromJson(jsonList, type);
        imageList = finalOutputString;
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

    public String getPostTimeEndString() {
        return dateFormat.format(postTimeEnd);
    }

    public void setPostTimeEndString(String date) {
        try {
            postTimeEnd= dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            dateTime=null;
        }
    }


    public String getPostTimeStartString() {
        return dateFormat.format(postTimeStart);
    }

    public void setPostTimeStartString(String date) {
        try {
            postTimeStart= dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            dateTime=null;
        }
    }


    public Map<String, Object> toUpdateMap() {
        Map<String, Object> vehicle = new HashMap<>();
        vehicle.put("active",false);
        vehicle.put("status",String.valueOf(STATUS.PENDING));
        vehicle.put("brandId",brandId);
        vehicle.put("categoryId",categoryId);
        vehicle.put("city",city);
        vehicle.put("country",country);
        //vehicle.put("dateTime", Calendar.getInstance().getTime());
        vehicle.put("description", description);
        vehicle.put("fuelId", fuelId);
        vehicle.put("imageList", imageList);
        vehicle.put("kmsRidden", kmsRidden);
        vehicle.put("modelId", modelId);
        vehicle.put("modelYear", modelYear);
        vehicle.put("numberOfOwner", numberOfOwner);
        vehicle.put("price", price);
        vehicle.put("state", state);
        vehicle.put("transmissionMode", transmissionMode);
        //vehicle.put("trimId", trimId);
        vehicle.put("title", title);
        vehicle.put("fuelType", fuelType);



        return vehicle;
    }

    @Exclude
    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }
    @Exclude
    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }
}

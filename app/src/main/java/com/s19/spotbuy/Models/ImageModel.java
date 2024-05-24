package com.s19.spotbuy.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.Keep;

import java.io.ByteArrayOutputStream;

@Keep
public class ImageModel {
    long id;
    String link;
    byte[] imageData;
    Bitmap imageBitmap;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        if (imageData == null)
            return;
        this.imageData = imageData;
        this.imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        this.imageData = stream.toByteArray();
        Log.d("TAG", "setImageBitmap: "+imageData);

    }
}

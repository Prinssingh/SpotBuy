package com.s19.spotbuy.Others;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.content.Context;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.ByteArrayBuffer;
import com.s19.spotbuy.DataBase.ImageManager;
import com.s19.spotbuy.Models.ImageModel;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class SaveImageByteToDatabase extends AsyncTask<String, Void, byte[]> {
    @SuppressLint("StaticFieldLeak")
    Context context;
    String url;

    public SaveImageByteToDatabase (Context context){
        this.context=context;
    }

    @Override
    protected byte[] doInBackground(String... strings) {
        url =strings[0];

        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(500);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            return baf.toByteArray();
        } catch (Exception e) {
            Log.d("ImageManager", "Error: " + e.toString());
        }
        return null;

    }

    protected void onPostExecute(byte[] imageBytes) {
        ImageModel imageModel=new ImageModel();
        imageModel.setImageData(imageBytes);
        imageModel.setLink(url);
        try {
            new ImageManager(context).insert(imageModel);
        } catch (Exception e) {
            e.printStackTrace();
            new ImageManager(context).update(imageModel);
        }
    }
}

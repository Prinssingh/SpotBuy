package com.s19.spotbuy.Others;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.ByteArrayBuffer;
import com.s19.spotbuy.DataBase.ImageManager;
import com.s19.spotbuy.Models.ImageModel;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadImage extends AsyncTask<String, Void, byte[]> {
    @SuppressLint("StaticFieldLeak")
    ImageView imageView;
    @SuppressLint("StaticFieldLeak")
    ProgressBar loadingIndicator;
    @SuppressLint("StaticFieldLeak")
    Context context;
    String url;

    public DownloadImage(Context context, ImageView imageView, ProgressBar loadingIndicator) {
        this.imageView = imageView;
        this.loadingIndicator = loadingIndicator;
        this.context=context;

        loadingIndicator.setVisibility(View.VISIBLE);
    }

    protected  byte[]  doInBackground(String... urls) {
        url = urls[0];

        try {
            if(url==null || url ==""){
                Toast.makeText(context, "empty Link", Toast.LENGTH_SHORT).show();
            }
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

        if (imageBytes == null) {
            loadingIndicator.setVisibility(View.GONE);
            return;
        }

        ImageModel imageModel = new ImageModel();
        imageModel.setImageData(imageBytes);
        imageModel.setLink(url);
        imageView.setImageBitmap(imageModel.getImageBitmap());
        loadingIndicator.setVisibility(View.GONE);
        try {
            new ImageManager(context).insert(imageModel);
        } catch (Exception e) {
            e.printStackTrace();
            new ImageManager(context).update(imageModel);
        }
    }





}
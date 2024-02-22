package com.s19.spotbuy.Others;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.InputStream;

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
    @SuppressLint("StaticFieldLeak")
    ImageView bmImage;
    @SuppressLint("StaticFieldLeak")
    ProgressBar loadingIndicator;

    public DownloadImage(ImageView bmImage, ProgressBar loadingIndicator) {
        this.bmImage = bmImage;
        this.loadingIndicator= loadingIndicator;
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        }
        catch (Exception e) {
            Log.e("Error Image", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {

        if(result!=null)
            bmImage.setImageBitmap(result);
        loadingIndicator.setVisibility(View.GONE);
    }
}
package com.s19mobility.spotbuy.Dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.s19mobility.spotbuy.Others.DownloadImage;
import com.s19mobility.spotbuy.Widgets.ZoomableImageView;
import com.s19mobility.spotbuy.R;

public class ShowImageDialog {
    ZoomableImageView vehicleimage;
    ImageView close;
    ProgressBar loadingIndicator;
    AlertDialog dialog;
    AlertDialog.Builder builder;
    @SuppressLint("MissingInflatedId")

    public ShowImageDialog(Activity activity,String imageUrl) {
        builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_show_image, null);
        builder.setView(dialogView);

        vehicleimage =dialogView.findViewById(R.id.vehicleImage);
        close =dialogView.findViewById(R.id.close);
        loadingIndicator =dialogView.findViewById(R.id.loadingIndicator);

        new DownloadImage(vehicleimage,loadingIndicator).execute(imageUrl);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

    }

    public void show() {
        if (dialog != null)
            dialog.show();
    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

}
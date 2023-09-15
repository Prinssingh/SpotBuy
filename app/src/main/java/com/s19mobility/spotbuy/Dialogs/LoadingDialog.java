package com.s19mobility.spotbuy.Dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.s19mobility.spotbuy.R;

public class LoadingDialog {

    TextView msg;
    AlertDialog dialog;
    AlertDialog.Builder builder;


    @SuppressLint("MissingInflatedId")
    public LoadingDialog(Activity activity) {
        builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_loading, null);
        builder.setView(dialogView);

        msg =dialogView.findViewById(R.id.message);

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

    public void setMessage(String message) {
        if (dialog != null)
            try {
                msg.setText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}

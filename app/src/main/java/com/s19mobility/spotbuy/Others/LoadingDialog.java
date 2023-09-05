package com.s19mobility.spotbuy.Others;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.s19mobility.spotbuy.R;

public class LoadingDialog {

    TextView msg;
    AlertDialog dialog;
    AlertDialog.Builder builder;


    @SuppressLint("MissingInflatedId")
    public LoadingDialog(Activity activity) {
        builder = new AlertDialog.Builder(activity, R.style.FullScreenDialog);
        builder.setView(R.layout.dialog_loading);
        builder.setCancelable(false);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_loading, null);
        ViewGroup.LayoutParams params =activity.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialogView.setLayoutParams(params);
        builder.setView(dialogView);




        msg = dialogView.findViewById(R.id.message);


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
            msg.setText(message);
    }
}

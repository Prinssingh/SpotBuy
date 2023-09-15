package com.s19mobility.spotbuy.Adapters.FireBase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.s19mobility.spotbuy.Activity.NotificationsActivity;
import com.s19mobility.spotbuy.Models.Notification;
import com.s19mobility.spotbuy.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class NotificationListAdapter extends FirestoreRecyclerAdapter<Notification, NotificationListAdapter.ViewHolderData> {
    private final Context mContext;


    public NotificationListAdapter(@NonNull FirestoreRecyclerOptions<Notification> options, Context mContext) {
        super(options);
        this.mContext = mContext;
        if (getItemCount()<1)
            ((NotificationsActivity) mContext).showEmptyIndicator();
        else
            ((NotificationsActivity) mContext).hideEmptyIndicator();

    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationListAdapter.ViewHolderData holder, int position, @NonNull Notification model) {

        holder.bindData(model, position);
    }

    @NonNull
    @Override
    public NotificationListAdapter.ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderData(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_notification, parent, false), mContext);
    }

    @Override
    public void onDataChanged() {
        if (getItemCount()<1)
            ((NotificationsActivity) mContext).showEmptyIndicator();
        else
            ((NotificationsActivity) mContext).hideEmptyIndicator();
    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
        Log.e("HERE", "onError: ", e);
    }

    public static class ViewHolderData extends RecyclerView.ViewHolder {
        Context context;
        TextView title, dateTime, message;
        MaterialCardView categoryColor;


        public ViewHolderData(@NonNull View itemView, Context context) {
            super(itemView);

            this.context = context;
            title = itemView.findViewById(R.id.title);
            dateTime = itemView.findViewById(R.id.dateTime);
            categoryColor = itemView.findViewById(R.id.categoryColor);
            message = itemView.findViewById(R.id.message);

        }

        @SuppressLint({"SetTextI18n", "ResourceAsColor"})
        public void bindData(Notification notification, int i) {

            title.setText("" + notification.getTitle());
            message.setText("" + notification.getMessage());
            if (Objects.equals(notification.getCategory(), "RED"))
                categoryColor.setCardBackgroundColor(R.color.red);
            else if (Objects.equals(notification.getCategory(), "GREEN"))
                categoryColor.setCardBackgroundColor(R.color.green);
            else
                categoryColor.setCardBackgroundColor(R.color.yellow);


            dateTime.setText(dateDifference(notification.getDateTime(), Calendar.getInstance().getTime()));
        }

        private String dateDifference(Date d1, Date d2) {

            long difference_In_Time
                    = d2.getTime() - d1.getTime();
            long difference_In_Seconds
                    = (difference_In_Time
                    / 1000)
                    % 60;

            long difference_In_Minutes
                    = (difference_In_Time
                    / (1000 * 60))
                    % 60;

            long difference_In_Hours
                    = (difference_In_Time
                    / (1000 * 60 * 60))
                    % 24;

            long difference_In_Years
                    = (difference_In_Time
                    / (1000L * 60 * 60 * 24 * 365));

            long difference_In_Days
                    = (difference_In_Time
                    / (1000 * 60 * 60 * 24))
                    % 365;

            if (difference_In_Years != 0)
                return "" + difference_In_Years + "Years ago";

            else if (difference_In_Days != 0)
                return "" + difference_In_Days + "Days ago";

            else if (difference_In_Hours != 0)
                return "" + difference_In_Hours + "Hours ago";

            else if (difference_In_Minutes != 0)
                return "" + difference_In_Minutes + "Minutes ago";
            else
                return "" + difference_In_Seconds + "Seconds ago";

        }

    }
}

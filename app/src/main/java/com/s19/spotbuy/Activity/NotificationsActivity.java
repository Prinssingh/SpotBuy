package com.s19.spotbuy.Activity;

import static com.s19.spotbuy.Others.Constants.NotificationCollection;
import static com.s19.spotbuy.Others.Constants.UserCollection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.s19.spotbuy.Adapters.FireBase.NotificationListAdapter;
import com.s19.spotbuy.DataBase.SharedPrefs;
import com.s19.spotbuy.Models.Notification;
import com.s19.spotbuy.Widgets.WrapContentLinearLayoutManager;
import com.s19.spotbuy.R;

public class NotificationsActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView back;
    private AdView mAdView;
    LinearLayout emptyIndicator;
    RecyclerView notificationList;
    NotificationListAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        sharedPrefs = new SharedPrefs(this);
        initView();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        });

    }

    private void initView() {

        //banner Ad
        mAdView = findViewById(R.id.adView);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        notificationList = findViewById(R.id.notificationList);
        notificationList.setLayoutManager(new WrapContentLinearLayoutManager(this));
        emptyIndicator = findViewById(R.id.emptyIndicator);
        emptyIndicator.setVisibility(View.GONE);

        Query query = db.collection(UserCollection)
                .document(sharedPrefs.getSharedUID())
                .collection(NotificationCollection);
        FirestoreRecyclerOptions<Notification> options = new FirestoreRecyclerOptions.Builder<Notification>().setQuery(query, Notification.class).build();
        adapter = new NotificationListAdapter(options, this);
        notificationList.setAdapter(adapter);


    }

    @Override
    public void onClick(View view) {
        if (view == back) {
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            adapter.startListening();
        } catch (Exception e) {
            Toast.makeText(this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        try {
            adapter.stopListening();
        } catch (Exception e) {
            Toast.makeText(this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void showEmptyIndicator() {

        try {
            emptyIndicator.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideEmptyIndicator() {
        try {
            emptyIndicator.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
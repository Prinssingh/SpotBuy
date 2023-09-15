package com.s19mobility.spotbuy.Activity;

import static com.s19mobility.spotbuy.Others.Constants.CALL_REQUEST;
import static com.s19mobility.spotbuy.Others.Constants.UserCollection;
import static com.s19mobility.spotbuy.Others.Constants.VehiclePostCollection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.api.LogDescriptor;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.s19mobility.spotbuy.Adapters.FireBase.SellersAdsListAdapter;
import com.s19mobility.spotbuy.DataBase.SharedPrefs;
import com.s19mobility.spotbuy.Models.User;
import com.s19mobility.spotbuy.Models.VehiclePost;
import com.s19mobility.spotbuy.Others.DownloadImage;
import com.s19mobility.spotbuy.R;
import com.s19mobility.spotbuy.Widgets.WrapContentLinearLayoutManager;

import java.util.Objects;

public class SellerProfileActivity extends AppCompatActivity implements View.OnClickListener {
    User seller;
    ImageView back;
    ShapeableImageView sellerImage;
    ProgressBar imageProgressIndicator;
    TextView username,
            post,
            followers,
            following;
    MaterialButton message, call;
    RecyclerView sellerPosts;
    SellersAdsListAdapter sellerPostsAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ExtendedFloatingActionButton follow;

    SharedPrefs sharedPrefs;
    String[] callPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);
        seller = (User) getIntent().getSerializableExtra("seller");
        sharedPrefs = new SharedPrefs(this);
        callPermissions = new String[]{Manifest.permission.CALL_PHONE};
        initView();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        sellerImage = findViewById(R.id.sellerImage);
        imageProgressIndicator = findViewById(R.id.imageProgressIndicator);
        if (seller.getImage() != null)
            new DownloadImage(sellerImage, imageProgressIndicator).execute(seller.getImage());

        username = findViewById(R.id.username);
        username.setText(seller.getName());

        post = findViewById(R.id.post);
        post.setText("" + seller.getTotalPost());

        followers = findViewById(R.id.followers);
        followers.setText("" + seller.getFollowerCount());

        following = findViewById(R.id.following);
        following.setText("" + seller.getFollowingCount());


        message = findViewById(R.id.message);
        message.setOnClickListener(this);

        call = findViewById(R.id.call);
        call.setOnClickListener(this);

        follow = findViewById(R.id.fab_Follow);
        follow.setOnClickListener(this);
        if (Objects.equals(seller.getId(), sharedPrefs.getSharedUID()))
            follow.setVisibility(View.GONE);

        //TODO Add Check for already following

        db.collection(UserCollection)
                .document(sharedPrefs.getSharedUID());





        sellerPosts = findViewById(R.id.sellerPosts);
        Query query = db.collection(VehiclePostCollection).
                whereEqualTo("sellerId", seller.getId())
                .whereEqualTo("active", true)
                .whereEqualTo("status", "APPROVED")
                .orderBy("dateTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<VehiclePost> options = new FirestoreRecyclerOptions.Builder<VehiclePost>()
                .setQuery(query, VehiclePost.class)
                .build();
        sellerPosts.setLayoutManager(new WrapContentLinearLayoutManager(this));
        sellerPostsAdapter = new SellersAdsListAdapter(options, this, this);
        sellerPosts.setAdapter(sellerPostsAdapter);
        sellerPostsAdapter.startListening();
    }

    @Override
    public void onClick(View view) {
        if (view == back) {
            onBackPressed();
        }
        if (view == message) {
            if (seller != null && seller.getId() != "") {
                User temp = new User();
                temp.setId(seller.getId());
                temp.setName(seller.getName());
                temp.setImage(seller.getImage());


                Intent chatIntent = new Intent(SellerProfileActivity.this, ChatActivity.class);
                chatIntent.putExtra("User", temp);
                startActivity(chatIntent);
            } else
                Toast.makeText(this, "Sellers information is not available right now", Toast.LENGTH_LONG).show();

        }
        if (view == call) {
            if (seller != null && seller.getMobile() != "") {
                makeCall();
            } else
                Toast.makeText(this, "Sellers information is not available right now", Toast.LENGTH_LONG).show();


        }
        if (view == follow) {
            db.collection(UserCollection)
                    .document(sharedPrefs.getSharedUID())
                    .update("followingCount", FieldValue.increment(1), "following", FieldValue.arrayUnion(seller.getId()))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            db.collection(UserCollection)
                                    .document(seller.getId())
                                    .update("followerCount", FieldValue.increment(1), "followers", FieldValue.arrayUnion(sharedPrefs.getSharedUID()))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            follow.setVisibility(View.GONE);
                                        }
                                    });
                        }
                    });

        }
    }

    public void OnItemClickListener(VehiclePost vehicle) {
        Intent showVehicle= new Intent(SellerProfileActivity.this,VehicleDetailsActivity.class);
        showVehicle.putExtra("vehicle",vehicle);
        startActivity(showVehicle);

    }

    private void makeCall() {
        if (!checkCallPermission()) {
            requestCallPermission();
        } else {
            callSeller();
        }

    }

    private void callSeller() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + seller.getMobile()));
        startActivity(intent);

        //TODO Make a dialog and let the user choose the calling number
    }

    // checking storage permissions
    private Boolean checkCallPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == (PackageManager.PERMISSION_GRANTED);
    }

    // Requesting  gallery permission
    private void requestCallPermission() {
        requestPermissions(callPermissions, CALL_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_REQUEST) {
            if (grantResults.length > 0) {
                boolean call_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (call_accepted) {
                    callSeller();

                } else {
                    Toast.makeText(this, "Please Enable Call Permissions", Toast.LENGTH_LONG).show();
                }

            }
        }
    }


}
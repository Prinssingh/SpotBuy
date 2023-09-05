package com.s19mobility.spotbuy.Activity;

import static com.s19mobility.spotbuy.Others.Constants.UserCollection;
import static com.s19mobility.spotbuy.Others.Constants.VehiclePostCollection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.s19mobility.spotbuy.Adapters.FireBase.SellersAdsListAdapter;
import com.s19mobility.spotbuy.DataBase.SharedPrefs;
import com.s19mobility.spotbuy.Models.SellerModel;
import com.s19mobility.spotbuy.Models.User;
import com.s19mobility.spotbuy.Models.VehiclePost;
import com.s19mobility.spotbuy.Others.DownloadImage;
import com.s19mobility.spotbuy.Others.WrapContentLinearLayoutManager;
import com.s19mobility.spotbuy.R;

import java.util.Objects;

public class SellerProfileActivity extends AppCompatActivity implements View.OnClickListener {
    User seller;
    ImageView back;
    ShapeableImageView sellerImage;
    TextView username,
            post,
            followers,
            following;
    MaterialButton message,call;
    RecyclerView sellerPosts;
    FirestoreRecyclerAdapter sellerPostsAdapter;
    FirebaseFirestore db =FirebaseFirestore.getInstance();
    ExtendedFloatingActionButton follow;

    SharedPrefs sharedPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);
        seller =(User) getIntent().getSerializableExtra("seller");
        sharedPrefs = new SharedPrefs(this);

        initView();
    }

    @SuppressLint("SetTextI18n")
    private void initView(){
        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        sellerImage = findViewById(R.id.sellerImage);
        if(seller.getImage()!=null)
            new DownloadImage(sellerImage).execute(seller.getImage());

        username = findViewById(R.id.username);
        username.setText(seller.getName());

        post = findViewById(R.id.post);
        post.setText(""+seller.getTotalPost());

        followers = findViewById(R.id.followers);
        followers.setText(""+seller.getFollowerCount());

        following = findViewById(R.id.following);
        following.setText(""+seller.getFollowingCount());


        message = findViewById(R.id.message);
        message.setOnClickListener(this);

        call = findViewById(R.id.call);
        call.setOnClickListener(this);

        follow = findViewById(R.id.fab_Follow);
        follow.setOnClickListener(this);
        if(Objects.equals(seller.getId(), sharedPrefs.getSharedID()))
            follow.setVisibility(View.GONE);

        //TODO Add Check for already following


        sellerPosts= findViewById(R.id.sellerPosts);
        Query query =db.collection(VehiclePostCollection).
                whereEqualTo("sellerId",seller.getId())
                .whereEqualTo("active", true)
                .whereEqualTo("status", "APPROVED")
                .orderBy("dateTime");
        FirestoreRecyclerOptions<VehiclePost> options = new FirestoreRecyclerOptions.Builder<VehiclePost>()
                .setQuery(query, VehiclePost.class)
                .build();
        sellerPosts.setLayoutManager(new WrapContentLinearLayoutManager(this));
        sellerPostsAdapter = new SellersAdsListAdapter(options,this,this);
        sellerPosts.setAdapter(sellerPostsAdapter);
    }

    @Override
    public void onClick(View view) {
        if(view==back){
           onBackPressed();
        }
        if(view == message){
            //todo
        }
        if(view == call){

            //TODO Check permission
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + seller.getMobile()));
            //startActivity(intent);
        }
        if(view == follow){
            db.collection(UserCollection)
                    .document(sharedPrefs.getSharedID())
                    .update("followingCount", FieldValue.increment(1),"following",FieldValue.arrayUnion(seller.getId()))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    db.collection(UserCollection)
                            .document(seller.getId())
                            .update("followerCount", FieldValue.increment(1),"followers",FieldValue.arrayUnion(sharedPrefs.getSharedID()))
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

    public void OnItemClickListener(VehiclePost vehicle){}
}
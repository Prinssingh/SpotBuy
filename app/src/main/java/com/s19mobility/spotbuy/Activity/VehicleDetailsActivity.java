package com.s19mobility.spotbuy.Activity;

import static com.s19mobility.spotbuy.Others.Constants.UserCollection;
import static com.s19mobility.spotbuy.Others.Constants.VehiclePostCollection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.s19mobility.spotbuy.Adapters.FireBase.RelatedPostListAdapter;
import com.s19mobility.spotbuy.Adapters.ScrollingImageAdapter;
import com.s19mobility.spotbuy.Models.User;
import com.s19mobility.spotbuy.Models.VehiclePost;
import com.s19mobility.spotbuy.Others.DownloadImage;
import com.s19mobility.spotbuy.Others.WrapContentLinearLayoutManager;
import com.s19mobility.spotbuy.R;


public class VehicleDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back;

    ViewPager viewPager;
    //SpringDotsIndicator dotsIndicator;
    ScrollingImageAdapter imageAdapter;
    VehiclePost vehicle;


    TextView brandName, price, fuelType, kmRidden, owners, city, description, sellerName;
    Chip yearModel;
    ShapeableImageView sellerImage;

    MaterialCardView sellerCard;
    RecyclerView relatedPosts;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirestoreRecyclerAdapter relatedPostsAdapter;
    User seller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);
        vehicle = (VehiclePost) getIntent().getSerializableExtra("vehicle");

        initView();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {

        back = findViewById(R.id.back);
        back.setOnClickListener(this);


        viewPager = findViewById(R.id.vehiclesImages);
        // dotsIndicator= findViewById(R.id.dotsIndicator);
        imageAdapter = new ScrollingImageAdapter(this, vehicle.getImageList());
        viewPager.setAdapter(imageAdapter);
        //dotsIndicator.setViewPager(viewPager); //TODO

        brandName = findViewById(R.id.brandName);
        brandName.setText(vehicle.getTitle());
        yearModel = findViewById(R.id.yearModel);
        yearModel.setText(vehicle.getModelYear() + " model");

        price = findViewById(R.id.price);
        price.setText("₹" + vehicle.getPrice());

        fuelType = findViewById(R.id.fuelType);
        fuelType.setText(vehicle.getFuelType());

        kmRidden = findViewById(R.id.kmRidden);
        kmRidden.setText(vehicle.getKmsRidden() + "KM");

        owners = findViewById(R.id.owners);
        owners.setText("Owner No.  " + vehicle.getNumberOfOwner());

        city = findViewById(R.id.city);
        city.setText(vehicle.getCity());

        description = findViewById(R.id.description);
        description.setText(vehicle.getDescription());

        sellerName = findViewById(R.id.sellerName);
        sellerImage = findViewById(R.id.sellerImage);
        sellerCard = findViewById(R.id.sellerCard);
        sellerCard.setOnClickListener(this);


        relatedPosts = findViewById(R.id.relatedPosts);
        relatedPosts.setLayoutManager(new WrapContentLinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        Query query = db.collection(VehiclePostCollection)
                .whereNotEqualTo("id", vehicle.getId())
                .whereEqualTo("active", true)
                .whereEqualTo("status", "APPROVED")
                .whereEqualTo("categoryId", vehicle.getCategoryId());
        FirestoreRecyclerOptions<VehiclePost> options = new FirestoreRecyclerOptions.Builder<VehiclePost>()
                .setQuery(query, VehiclePost.class)
                .build();
        relatedPostsAdapter = new RelatedPostListAdapter(options, this, this);
        relatedPosts.setAdapter(relatedPostsAdapter);


        db.collection(UserCollection)
                .document(vehicle.getSellerId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        seller = documentSnapshot.toObject(User.class);
                        if (seller != null) {
                            sellerName.setText(seller.getName());

                            if (seller.getImage()!=null)
                                new DownloadImage(sellerImage).execute(seller.getImage());

                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if (view == back) {
            finish();
        }

        if (view == sellerCard) {
            if (seller != null) {
                Intent sellerIntent = new Intent(this, SellerProfileActivity.class);
                sellerIntent.putExtra("seller", seller);
                startActivity(sellerIntent);
            }


        }
    }


    //Related Vehicle
    public void onItemClickListener(VehiclePost vehicle) {
    }


    @Override
    protected void onStart() {
        super.onStart();
        relatedPostsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        relatedPostsAdapter.stopListening();
    }
}
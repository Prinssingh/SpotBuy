package com.s19mobility.spotbuy.Activity;

import static com.s19mobility.spotbuy.Others.Constants.CALL_REQUEST;
import static com.s19mobility.spotbuy.Others.Constants.CAMERA_REQUEST;
import static com.s19mobility.spotbuy.Others.Constants.STORAGE_REQUEST;
import static com.s19mobility.spotbuy.Others.Constants.UserCollection;
import static com.s19mobility.spotbuy.Others.Constants.VehiclePostCollection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import com.s19mobility.spotbuy.Widgets.WrapContentLinearLayoutManager;
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
    ProgressBar loadingIndicator;

    MaterialCardView sellerCard;
    RecyclerView relatedPosts;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RelatedPostListAdapter relatedPostsAdapter;
    User seller;

    Button message,call;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);
        vehicle = (VehiclePost) getIntent().getSerializableExtra("vehicle");
        callPermissions =new String[]{Manifest.permission.CALL_PHONE};
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
        loadingIndicator = findViewById(R.id.loadingIndicator);
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
                                new DownloadImage(sellerImage,loadingIndicator).execute(seller.getImage());

                        }
                    }
                });

        message = findViewById(R.id.message);
        message.setOnClickListener(this);

        call = findViewById(R.id.call);
        call.setOnClickListener(this);

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
            else
                Toast.makeText(this, "Seller Information is not available at this movement. please try again", Toast.LENGTH_SHORT).show();


        }

        if(view ==message){
            if(seller !=null && seller.getId()!="") {
                Intent chatIntent = new Intent(VehicleDetailsActivity.this,ChatActivity.class);
                chatIntent.putExtra("User",seller);
                startActivity(chatIntent);
            }
              else
                Toast.makeText(this, "Sellers information is not available right now...please wait", Toast.LENGTH_LONG).show();
        }

        if(view == call){
            if(seller !=null && seller.getMobile()!="") {
                makeCall();
            }
            else
                Toast.makeText(this, "Sellers information is not available right now...please wait", Toast.LENGTH_LONG).show();

        }
    }

    private void makeCall(){
        if (!checkCallPermission()) {
            requestCallPermission();
        } else {
           callSeller();
        }

    }

    private void callSeller(){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + seller.getMobile()));
        startActivity(intent);

        //TODO Make a dialog and let the user choose the calling number
    }

    //Related Vehicle
    public void onItemClickListener(VehiclePost vehicle) {
        Intent recreate= new Intent(VehicleDetailsActivity.this,VehicleDetailsActivity.class);
        recreate.putExtra("vehicle",vehicle);
        startActivity(recreate);
        this.finish();
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

    // checking storage permissions
    private Boolean checkCallPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == (PackageManager.PERMISSION_GRANTED);
    }
    String[] callPermissions ;

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
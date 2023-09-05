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
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.s19mobility.spotbuy.Activity.SellerProfileActivity;
import com.s19mobility.spotbuy.Adapters.ScrollingImageAdapter;
import com.s19mobility.spotbuy.Fragments.main.AdsFragment;
import com.s19mobility.spotbuy.Models.VehiclePost;
import com.s19mobility.spotbuy.R;

public class SellersAdsListAdapter extends FirestoreRecyclerAdapter<VehiclePost, SellersAdsListAdapter.ViewHolderData> {

    private final SellerProfileActivity sellerProfileActivity;
    private final Context mContext;


    public SellersAdsListAdapter(@NonNull FirestoreRecyclerOptions options, SellerProfileActivity sellerProfileActivity, Context mContext) {
        super(options);
        this.sellerProfileActivity = sellerProfileActivity;
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull SellersAdsListAdapter.ViewHolderData holder, int position, @NonNull VehiclePost model) {

        ((SellersAdsListAdapter.ViewHolderData) holder).bindData(model, position);
    }

    @NonNull
    @Override
    public SellersAdsListAdapter.ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SellersAdsListAdapter.ViewHolderData(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_my_ads, parent, false), mContext);
    }

    @Override
    public void onDataChanged() {

    }

    @Override
    public void onError(FirebaseFirestoreException e) {
        Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
        Log.e("HERE", "onError: ", e);
    }

    public class ViewHolderData extends RecyclerView.ViewHolder {
        Context context;
        ViewPager viewPager;
        //SpringDotsIndicator dotsIndicator;
        ScrollingImageAdapter imageAdapter;

        TextView brandName,price,yearModel,description;
        Chip chip1,chip2,chip3;



        public ViewHolderData(@NonNull View itemView, Context context) {
            super(itemView);

            this.context = context;
            viewPager = itemView.findViewById(R.id.vehiclesImages);
            //dotsIndicator= itemView.findViewById(R.id.dotsIndicator);

            brandName= itemView.findViewById(R.id.brandName);
            price= itemView.findViewById(R.id.price);
            yearModel= itemView.findViewById(R.id.yearModel);

            chip1= itemView.findViewById(R.id.chip1);
            chip2= itemView.findViewById(R.id.chip2);
            chip3= itemView.findViewById(R.id.chip3);

            description= itemView.findViewById(R.id.description);

        }

        @SuppressLint("SetTextI18n")
        public void bindData(VehiclePost vehicle, int i) {
            imageAdapter=new ScrollingImageAdapter(context,vehicle.getImageList());
            viewPager.setAdapter(imageAdapter);
            //dotsIndicator.setViewPager(viewPager); Todo

            brandName.setText(vehicle.getTitle());
            price.setText("₹" + vehicle.getPrice());
            yearModel.setText("" + vehicle.getModelYear()+" Model");
            chip1.setText(""+vehicle.getNumberOfOwner()+" Owners");
            chip2.setText(""+vehicle.getKmsRidden()+" KM");
            chip2.setText(""+vehicle.getFuelType());
            description.setText(""+vehicle.getDescription());


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sellerProfileActivity.OnItemClickListener(vehicle);
                }
            });
        }

    }

}

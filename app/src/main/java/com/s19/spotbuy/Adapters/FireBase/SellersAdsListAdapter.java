package com.s19.spotbuy.Adapters.FireBase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.s19.spotbuy.Activity.SellerProfileActivity;
import com.s19.spotbuy.Adapters.ScrollingImageAdapter;
import com.s19.spotbuy.Models.VehiclePost;
import com.s19.spotbuy.R;

public class SellersAdsListAdapter extends FirestoreRecyclerAdapter<VehiclePost, SellersAdsListAdapter.ViewHolderData> {

    private final SellerProfileActivity sellerProfileActivity;
    private final Context mContext;


    public SellersAdsListAdapter(@NonNull FirestoreRecyclerOptions<VehiclePost> options, SellerProfileActivity sellerProfileActivity, Context mContext) {
        super(options);
        this.sellerProfileActivity = sellerProfileActivity;
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull SellersAdsListAdapter.ViewHolderData holder, int position, @NonNull VehiclePost model) {

        holder.bindData(model, position);
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
    public void onError(@NonNull FirebaseFirestoreException e) {
        Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
        Log.e("HERE", "onError: ", e);
    }

    public class ViewHolderData extends RecyclerView.ViewHolder {
        Context context;
        ViewPager viewPager;
        //SpringDotsIndicator dotsIndicator;
        ScrollingImageAdapter imageAdapter;

        TextView brandName, price, yearModel, description;
        Chip chip1, chip2, chip3;
        ImageView statusIndicator;


        public ViewHolderData(@NonNull View itemView, Context context) {
            super(itemView);

            this.context = context;
            viewPager = itemView.findViewById(R.id.vehiclesImages);
            //dotsIndicator= itemView.findViewById(R.id.dotsIndicator);

            brandName = itemView.findViewById(R.id.brandName);
            price = itemView.findViewById(R.id.price);
            yearModel = itemView.findViewById(R.id.yearModel);

            chip1 = itemView.findViewById(R.id.chip1);
            chip2 = itemView.findViewById(R.id.chip2);
            chip3 = itemView.findViewById(R.id.chip3);

            description = itemView.findViewById(R.id.description);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);

        }

        @SuppressLint("SetTextI18n")
        public void bindData(VehiclePost vehicle, int i) {
            imageAdapter = new ScrollingImageAdapter(context, vehicle.getImageList());
            viewPager.setAdapter(imageAdapter);
            //dotsIndicator.setViewPager(viewPager); Todo

            brandName.setText(vehicle.getTitle());
            price.setText("₹" + vehicle.getPrice());
            yearModel.setText("" + vehicle.getModelYear() + " Model");
            chip1.setText("" + vehicle.getNumberOfOwner() + " Owners");
            chip2.setText("" + vehicle.getKmsRidden() + " KM");
            chip2.setText("" + vehicle.getFuelType());
            description.setText("" + vehicle.getDescription());
            switch (vehicle.getStatus()) {
                case "PENDING":
                    statusIndicator.setImageResource(R.drawable.ic_pending);
                    break;
                case "APPROVED":
                    statusIndicator.setImageResource(R.drawable.ic_approved);
                    break;
                case "REJECTED":
                    statusIndicator.setImageResource(R.drawable.ic_rejected);
                    break;
                case "DELETED":
                    statusIndicator.setImageResource(R.drawable.ic_deleted);
                    break;
                case "EXPIRED":
                    statusIndicator.setImageResource(R.drawable.ic_expired);
                    break;
            }


            itemView.setOnClickListener(view -> sellerProfileActivity.OnItemClickListener(vehicle));
        }

    }

}

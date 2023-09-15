package com.s19mobility.spotbuy.Adapters.FireBase;

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
import com.s19mobility.spotbuy.Adapters.ScrollingImageAdapter;
import com.s19mobility.spotbuy.Fragments.main.AdsFragment;
import com.s19mobility.spotbuy.Models.VehiclePost;
import com.s19mobility.spotbuy.R;

public class MyAdsAdapter extends FirestoreRecyclerAdapter<VehiclePost, MyAdsAdapter.ViewHolderData> {

    private final AdsFragment adsFragment;
    private final Context mContext;


    public MyAdsAdapter(@NonNull FirestoreRecyclerOptions<VehiclePost> options, AdsFragment adsFragment, Context mContext) {
        super(options);
        this.adsFragment = adsFragment;
        this.mContext = mContext;
        if (getItemCount()<1)
            adsFragment.showEmptyIndicator();
        else
            adsFragment.hideEmptyIndicator();

    }

    @Override
    protected void onBindViewHolder(@NonNull MyAdsAdapter.ViewHolderData holder, int position, @NonNull VehiclePost model) {

        holder.bindData(model, position);
    }

    @NonNull
    @Override
    public MyAdsAdapter.ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyAdsAdapter.ViewHolderData(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_my_ads, parent, false), mContext);
    }

    @Override
    public void onDataChanged() {
        if (getItemCount()<1)
            adsFragment.showEmptyIndicator();
        else
            adsFragment.hideEmptyIndicator();
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
        ImageView statusIndicator;

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
            statusIndicator= itemView.findViewById(R.id.statusIndicator);

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

            itemView.setOnClickListener(view -> MyAdsAdapter.this.adsFragment.OnItemClickListener(vehicle));
        }

    }
}

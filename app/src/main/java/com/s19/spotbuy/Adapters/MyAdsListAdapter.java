package com.s19.spotbuy.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.chip.Chip;
import com.s19.spotbuy.Fragments.main.AdsFragment;
import com.s19.spotbuy.Models.VehiclePost;
import com.s19.spotbuy.R;

import java.util.List;

public class MyAdsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    List<VehiclePost> vehiclePostList;
    private final AdsFragment adsFragment;
    private final Context mContext;


    public MyAdsListAdapter(List<VehiclePost> vehiclePostList, AdsFragment adsFragment, Context mContext) {
        this.vehiclePostList=vehiclePostList;
        this.adsFragment = adsFragment;
        this.mContext = mContext;
        if (getItemCount()<1)
            adsFragment.showEmptyIndicator();
        else
            adsFragment.hideEmptyIndicator();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyAdsListAdapter.ViewHolderData(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_my_ads, parent, false), mContext);
    }

    public int getItemViewType(int i) {
        return super.getItemViewType(i);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((MyAdsListAdapter.ViewHolderData) viewHolder).bindData(this.vehiclePostList.get(i), i);
    }

    public int getItemCount() {
        return this.vehiclePostList.size();
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

            brandName.setText(""+vehicle.getTitle());
            price.setText("₹" + vehicle.getPrice());
            yearModel.setText("" + vehicle.getModelYear()+" Model");
            chip1.setText(""+vehicle.getNumberOfOwner()+" Owners");
            chip2.setText(""+vehicle.getKmsRidden()+" KM");
            chip3.setText(""+vehicle.getFuelType());
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

            itemView.setOnClickListener(view -> MyAdsListAdapter.this.adsFragment.OnItemClickListener(vehicle));
        }

    }


}

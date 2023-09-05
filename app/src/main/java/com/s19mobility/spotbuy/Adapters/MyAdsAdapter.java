package com.s19mobility.spotbuy.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.chip.Chip;
import com.s19mobility.spotbuy.Fragments.main.AdsFragment;
import com.s19mobility.spotbuy.Models.VehiclePost;
import com.s19mobility.spotbuy.R;
//import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import java.util.List;

public class MyAdsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private final List<VehiclePost> myAdsModelList;
    private final AdsFragment adsFragment;
    private final Context mContext;

    public MyAdsAdapter(List<VehiclePost> myAdsModelList, AdsFragment adsFragment, Context mContext) {
        this.myAdsModelList = myAdsModelList;
        this.adsFragment = adsFragment;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderData(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_my_ads, parent, false),mContext);
    }

    public int getItemViewType(int i) {
        return super.getItemViewType(i);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((ViewHolderData) viewHolder).bindData(this.myAdsModelList.get(i), i);
    }

    public int getItemCount() {
        return this.myAdsModelList.size();
    }



    public class ViewHolderData extends RecyclerView.ViewHolder {
        Context context;
        ViewPager viewPager;
        //SpringDotsIndicator dotsIndicator;

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
        public void bindData(VehiclePost ad, int i) {
            //imageAdapter=new ScrollingImageAdapter(context,ad.getImages());
            //viewPager.setAdapter(imageAdapter);
            //dotsIndicator.setViewPager(viewPager);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyAdsAdapter.this.adsFragment.OnItemClickListener(ad);
                }
            });
        }

    }
}

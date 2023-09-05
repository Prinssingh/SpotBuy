package com.s19mobility.spotbuy.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.s19mobility.spotbuy.Fragments.main.HomeFragment;
import com.s19mobility.spotbuy.Models.VehicleCategory;
import com.s19mobility.spotbuy.R;

import java.util.List;

public class VehicleCategoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final List<VehicleCategory> vehicleCategoryList;
    private final HomeFragment homeFragment;
    private final Context mContext;



    public VehicleCategoryListAdapter(List<VehicleCategory> vehicleModelList, HomeFragment homeFragment, Context mContext) {
        this.vehicleCategoryList = vehicleModelList;
        this.homeFragment = homeFragment;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VehicleCategoryListAdapter.ViewHolderData(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_vehicle_category, parent, false), mContext);
    }

    public int getItemViewType(int i) {
        return super.getItemViewType(i);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((VehicleCategoryListAdapter.ViewHolderData) viewHolder).bindData(this.vehicleCategoryList.get(i), i);
    }

    public int getItemCount() {
        return this.vehicleCategoryList.size();
    }


    public class ViewHolderData extends RecyclerView.ViewHolder {
        Context context;
        ImageView image;
        TextView title;



        public ViewHolderData(@NonNull View itemView, Context context) {
            super(itemView);

            this.context = context;
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);



        }

        @SuppressLint("SetTextI18n")
        public void bindData(VehicleCategory vehicleCategory, int i) {
            //Binding here

           // image.setImageResource(vehicleCategory.getImageId());
            title.setText(vehicleCategory.getName());


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VehicleCategoryListAdapter.this.homeFragment.onCategoryClickListener(vehicleCategory);
                }
            });
        }

    }



}

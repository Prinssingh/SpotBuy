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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.s19mobility.spotbuy.Fragments.main.HomeFragment;
import com.s19mobility.spotbuy.Models.VehiclePost;
import com.s19mobility.spotbuy.R;

import java.util.List;

public class VehicleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<VehiclePost> vehiclePostList;
    private final HomeFragment homeFragment;
    private final Context mContext;


    public VehicleListAdapter(List<VehiclePost> vehiclePostList, HomeFragment homeFragment, Context mContext) {
        this.vehiclePostList = vehiclePostList;
        this.homeFragment = homeFragment;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VehicleListAdapter.ViewHolderData(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_vehicle_list, parent, false), mContext);
    }

    public int getItemViewType(int i) {
        return super.getItemViewType(i);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((VehicleListAdapter.ViewHolderData) viewHolder).bindData(this.vehiclePostList.get(i), i);
    }

    public int getItemCount() {
        return this.vehiclePostList.size();
    }


    public class ViewHolderData extends RecyclerView.ViewHolder {
        Context context;
        ImageView image;
        TextView brandName;
        Chip price,yearModel,fuelType;
        MaterialButton message;


        public ViewHolderData(@NonNull View itemView, Context context) {
            super(itemView);

            this.context = context;
            image = itemView.findViewById(R.id.vehicleImage);
            brandName = itemView.findViewById(R.id.brandName);

            price = itemView.findViewById(R.id.price);
            yearModel = itemView.findViewById(R.id.yearModel);
            fuelType = itemView.findViewById(R.id.fuelType);
            message = itemView.findViewById(R.id.message);


        }

        @SuppressLint("SetTextI18n")
        public void bindData(VehiclePost vehicle, int i) {
            //Binding here
           // image.setImageResource(vehicle.getImages()[0]);
           // brandName.setText(vehicle.getBrandName());
            price.setText("₹"+vehicle.getPrice());
            yearModel.setText(""+vehicle.getModelYear());
            fuelType.setText(""+vehicle.getFuelType());


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VehicleListAdapter.this.homeFragment.onItemClickListener(vehicle);
                }
            });
        }

    }


}

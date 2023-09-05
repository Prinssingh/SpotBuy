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

import com.s19mobility.spotbuy.Activity.VehicleDetailsActivity;
import com.s19mobility.spotbuy.Models.VehiclePost;
import com.s19mobility.spotbuy.R;

import java.util.List;

public class RelatedPostListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<VehiclePost> vehiclePostList;
    private final Context mContext;
    private final VehicleDetailsActivity activity;


    public RelatedPostListAdapter(List<VehiclePost> vehiclePostList, Context mContext, VehicleDetailsActivity activity) {
        this.vehiclePostList = vehiclePostList;
        this.mContext = mContext;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RelatedPostListAdapter.ViewHolderData(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_related_posts, parent, false), mContext);
    }

    public int getItemViewType(int i) {
        return super.getItemViewType(i);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((RelatedPostListAdapter.ViewHolderData) viewHolder).bindData(this.vehiclePostList.get(i), i);
    }

    public int getItemCount() {
        return this.vehiclePostList.size();
    }


    public class ViewHolderData extends RecyclerView.ViewHolder {
        Context context;
        ImageView image;
        TextView brandName;
        TextView price;


        public ViewHolderData(@NonNull View itemView, Context context) {
            super(itemView);

            this.context = context;
            image = itemView.findViewById(R.id.vehicleImage);
            brandName = itemView.findViewById(R.id.brandName);
            price = itemView.findViewById(R.id.price);


        }

        @SuppressLint("SetTextI18n")
        public void bindData(VehiclePost vehicle, int i) {
            //Binding here


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RelatedPostListAdapter.this.activity.onItemClickListener(vehicle);
                }
            });
        }

    }
}
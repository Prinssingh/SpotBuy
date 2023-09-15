package com.s19mobility.spotbuy.Adapters.FireBase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.s19mobility.spotbuy.Activity.SearchActivity;
import com.s19mobility.spotbuy.Models.VehiclePost;
import com.s19mobility.spotbuy.Others.DownloadImage;
import com.s19mobility.spotbuy.R;

public class SearchAdapter extends FirestoreRecyclerAdapter<VehiclePost, SearchAdapter.ViewHolderData> {

    private final Activity mContext;


    public SearchAdapter(@NonNull FirestoreRecyclerOptions<VehiclePost> options, Activity mContext) {
        super(options);
        this.mContext = mContext;
        if(getItemCount()<1)
            ((SearchActivity) mContext).showEmptyIndicator();
        else
            ((SearchActivity) mContext).hideEmptyIndicator();

    }

    @Override
    protected void onBindViewHolder(@NonNull SearchAdapter.ViewHolderData holder, int position, @NonNull VehiclePost model) {

        holder.bindData(model, position);
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderData(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_vehicle_list, parent, false), mContext);
    }

    @Override
    public void onDataChanged() {
        if(getItemCount()<1)
            ((SearchActivity) mContext).showEmptyIndicator();
        else
            ((SearchActivity) mContext).hideEmptyIndicator();
    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        Toast.makeText(mContext, "Error"+e, Toast.LENGTH_SHORT).show();
        e.printStackTrace();

    }

    public class ViewHolderData extends RecyclerView.ViewHolder {
        Context context;
        ImageView image;
        TextView brandName;
        Chip price, yearModel, fuelType;
        MaterialButton message;
        ProgressBar imageEmptyIndicator;


        public ViewHolderData(@NonNull View itemView, Context context) {
            super(itemView);

            this.context = context;
            image = itemView.findViewById(R.id.vehicleImage);
            imageEmptyIndicator = itemView.findViewById(R.id.imageEmptyIndicator);
            brandName = itemView.findViewById(R.id.brandName);

            price = itemView.findViewById(R.id.price);
            yearModel = itemView.findViewById(R.id.yearModel);
            fuelType = itemView.findViewById(R.id.fuelType);
            message = itemView.findViewById(R.id.message);


        }

        @SuppressLint("SetTextI18n")
        public void bindData(VehiclePost vehicle, int i) {

            new DownloadImage(image,imageEmptyIndicator).execute(vehicle.getImageList().get(0));
            brandName.setText(vehicle.getTitle());
            price.setText("₹" + vehicle.getPrice());
            yearModel.setText("" + vehicle.getModelYear());
            fuelType.setText("" + vehicle.getFuelType());
            //TODO message

            itemView.setOnClickListener(view -> ((SearchActivity) mContext).onItemClickListener(vehicle));

            message.setOnClickListener(view -> ((SearchActivity) mContext).sendMessage(vehicle.getSellerId()));
        }

    }
}

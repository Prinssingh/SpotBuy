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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.s19mobility.spotbuy.Fragments.main.HomeFragment;
import com.s19mobility.spotbuy.Models.VehiclePost;
import com.s19mobility.spotbuy.Others.DownloadImage;
import com.s19mobility.spotbuy.R;


public class VehicleListAdapter extends FirestoreRecyclerAdapter<VehiclePost, VehicleListAdapter.ViewHolderData> {
    private final Context mContext;
    private final HomeFragment homeFragment;

    public VehicleListAdapter(@NonNull FirestoreRecyclerOptions options, Context mContext, HomeFragment homeFragment) {
        super(options);
        this.mContext = mContext;
        this.homeFragment = homeFragment;

    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderData holder, int position, @NonNull VehiclePost model) {

        ((ViewHolderData) holder).bindData(model, position);
    }

    @NonNull
    @Override
    public ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderData(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_vehicle_list, parent, false), mContext);
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
        ImageView image;
        TextView brandName;
        Chip price, yearModel, fuelType;
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

            new DownloadImage(image).execute(vehicle.getImageList().get(0));
            brandName.setText(vehicle.getTitle());
            price.setText("₹" + vehicle.getPrice());
            yearModel.setText("" + vehicle.getModelYear());
            fuelType.setText("" + vehicle.getFuelType());
            //TODO message

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VehicleListAdapter.this.homeFragment.onItemClickListener(vehicle);
                }
            });
        }

    }

}

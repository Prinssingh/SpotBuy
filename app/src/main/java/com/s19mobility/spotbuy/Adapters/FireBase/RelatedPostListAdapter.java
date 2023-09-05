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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.s19mobility.spotbuy.Activity.VehicleDetailsActivity;
import com.s19mobility.spotbuy.Models.VehiclePost;
import com.s19mobility.spotbuy.Others.DownloadImage;
import com.s19mobility.spotbuy.R;

public class RelatedPostListAdapter extends FirestoreRecyclerAdapter<VehiclePost, RelatedPostListAdapter.ViewHolderData> {

    private final VehicleDetailsActivity vehicleDetailsActivity;
    private final Context mContext;


    public RelatedPostListAdapter(@NonNull FirestoreRecyclerOptions options, VehicleDetailsActivity vehicleDetailsActivity, Context mContext) {
        super(options);
        this.vehicleDetailsActivity = vehicleDetailsActivity;
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull RelatedPostListAdapter.ViewHolderData holder, int position, @NonNull VehiclePost model) {
        ((RelatedPostListAdapter.ViewHolderData) holder).bindData(model, position);
    }

    @NonNull
    @Override
    public RelatedPostListAdapter.ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RelatedPostListAdapter.ViewHolderData(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_related_posts, parent, false), mContext);
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
            new DownloadImage(image).execute(vehicle.getImageList().get(0));
            brandName.setText(vehicle.getTitle());
            price.setText("₹" + vehicle.getPrice());


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vehicleDetailsActivity.onItemClickListener(vehicle);
                }
            });
        }

    }
}

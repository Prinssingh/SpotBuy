package com.s19.spotbuy.Adapters.FireBase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.s19.spotbuy.DataBase.ImageManager;
import com.s19.spotbuy.Fragments.main.HomeFragment;
import com.s19.spotbuy.Models.ImageModel;
import com.s19.spotbuy.Models.VehicleCategory;
import com.s19.spotbuy.Others.DownloadImage;
import com.s19.spotbuy.R;


public class CategoriesListAdapter extends FirestoreRecyclerAdapter<VehicleCategory, CategoriesListAdapter.ViewHolderData> {
    private final Context mContext;
    private final HomeFragment homeFragment;

    public CategoriesListAdapter(@NonNull FirestoreRecyclerOptions<VehicleCategory> options, Context mContext, HomeFragment homeFragment) {
        super(options);
        this.mContext = mContext;
        this.homeFragment = homeFragment;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderData holder, int position, @NonNull VehicleCategory model) {

        holder.bindData(model, position);
    }

    @NonNull
    @Override
    public ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderData(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_vehicle_category, parent, false), mContext);
    }

    @Override
    public void onDataChanged() {

    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        Toast.makeText(mContext, "Error Firebase Cat", Toast.LENGTH_SHORT).show();
        Log.e("HERE", "onError: ", e);
    }

    public class ViewHolderData extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView title;
        Context context;
        ProgressBar imageProgressIndicator;

        public ViewHolderData(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            imageProgressIndicator = itemView.findViewById(R.id.imageProgressIndicator);


        }

        @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
        public void bindData(VehicleCategory category, int i) {

            ImageModel imageModel = new ImageManager(context).getImageByLink(category.getImage());
            if(imageModel!=null)
                image.setImageBitmap(imageModel.getImageBitmap());
            else  new DownloadImage(context,image,imageProgressIndicator).execute(category.getImage());
            //Older version -- new DownloadImage(image,imageProgressIndicator).execute(category.getImage());

            title.setText(category.getName());
            itemView.setOnClickListener(view -> CategoriesListAdapter.this.homeFragment.onCategoryClickListener(category));


        }

    }

}

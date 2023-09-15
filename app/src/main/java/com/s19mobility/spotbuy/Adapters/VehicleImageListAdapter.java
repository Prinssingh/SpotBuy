package com.s19mobility.spotbuy.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.s19mobility.spotbuy.Activity.AddNewPostActivity;
import com.s19mobility.spotbuy.Dialogs.ShowImageDialog;
import com.s19mobility.spotbuy.MainActivity;
import com.s19mobility.spotbuy.Others.DownloadImage;
import com.s19mobility.spotbuy.R;

import java.util.List;

public class VehicleImageListAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final  List<String> imageList;
    private final MainActivity mainActivity;
    private final Context mContext;

    public VehicleImageListAdapter(List<String> imageList, MainActivity mainActivity, Context mContext) {
        this.imageList = imageList;
        this.mainActivity = mainActivity;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VehicleImageListAdapter.ViewHolderData(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_vehicle_image_list, parent, false), mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((VehicleImageListAdapter.ViewHolderData) viewHolder).bindData(this.imageList.get(position), position);

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }


    public class ViewHolderData extends RecyclerView.ViewHolder {
        Context context;
        ImageView image_view,close;
        ProgressBar loadingIndicator;




        public ViewHolderData(@NonNull View itemView, Context context) {
            super(itemView);

            this.context = context;
            image_view = itemView.findViewById(R.id.image_view);
            close = itemView.findViewById(R.id.close);
            loadingIndicator = itemView.findViewById(R.id.loadingIndicator);


        }

        @SuppressLint("SetTextI18n")
        public void bindData(String imageUrl, int i) {
            //Binding here
            new DownloadImage(image_view,loadingIndicator).execute(imageUrl);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivity.deleteImage(imageUrl,i);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ShowImageDialog(mainActivity,imageUrl);
                }
            });

        }

    }


}

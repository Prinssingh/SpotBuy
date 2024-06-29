package com.s19.spotbuy.Adapters.FireBase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.s19.spotbuy.DataBase.ImageManager;
import com.s19.spotbuy.Fragments.main.HomeFragment;
import com.s19.spotbuy.Models.ImageModel;
import com.s19.spotbuy.Models.VehiclePost;
import com.s19.spotbuy.Others.DownloadImage;
import com.s19.spotbuy.R;


public class VehicleListAdapter extends FirestoreRecyclerAdapter<VehiclePost, RecyclerView.ViewHolder> {
    private final Context mContext;
    private final HomeFragment homeFragment;
    FirestoreRecyclerOptions<VehiclePost> options;

    private final int ADS_AFTER = 2 ;//frequency of ads in list
    private final int ITEM_VIEW= R.layout.item_view_vehicle_list ;//regular item view layout
    private final int AD_VIEW = R.layout.item_view_vehicle_list_ads ; //ad view layout

    @Override
    public int getItemViewType(int position) {
        if(position!= 0 && position % ADS_AFTER == 0){
            return AD_VIEW;
        }

        return ITEM_VIEW;
    }
    

    @Override
    public int getItemCount() {
        int s = super.getItemCount();
        int t = s + s / ADS_AFTER;
        return t;
    }

    @NonNull
    @Override
    public VehiclePost getItem(int position) {
        if(position!= 0 && position % ADS_AFTER == 0)
            return null;
        else {
            position =position - position/ADS_AFTER;
            return super.getItem(position);
        }
    }

    public VehicleListAdapter(@NonNull FirestoreRecyclerOptions<VehiclePost> options, Context mContext, HomeFragment homeFragment) {
        super(options);
        this.mContext = mContext;
        this.homeFragment = homeFragment;
        this.options = options;
        if (getItemCount() < 1)
            homeFragment.showEmptyIndicator();
        else
            homeFragment.hideEmptyIndicator();

    }



    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull VehiclePost model) {
        if(getItemViewType(position)==ITEM_VIEW)
            ((ViewHolderData)holder).bindData(model,position);


    }

    @NonNull
    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType ==ITEM_VIEW)
              return new ViewHolderData(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_vehicle_list, parent, false), mContext);
        else
              return new AdsViewHolderData(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_vehicle_list_ads, parent, false));

    }

    @Override
    public void onDataChanged() {
        if (getItemCount() < 1)
            homeFragment.showEmptyIndicator();
        else
            homeFragment.hideEmptyIndicator();
    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
//        Toast.makeText(mContext, "Firebase Error Vehicle list" +e.getMessage(), Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }


    public class ViewHolderData extends RecyclerView.ViewHolder {
        Context context;
        ShapeableImageView image;
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
            String url = vehicle.getImageList().get(0);

            ImageModel imageModel = new ImageManager(mContext).getImageByLink(url);
            if (imageModel != null && imageModel.getImageBitmap() != null)
                image.setImageBitmap(imageModel.getImageBitmap());
            else new DownloadImage(mContext, image, imageEmptyIndicator).execute(url);

            //Old Version --new DownloadImage(image,imageEmptyIndicator).execute(vehicle.getImageList().get(0));
            brandName.setText(vehicle.getTitle());
            price.setText("₹" + vehicle.getPrice());
            yearModel.setText("" + vehicle.getModelYear());
            fuelType.setText("" + vehicle.getFuelType());
            //TODO message

            itemView.setOnClickListener(view -> VehicleListAdapter.this.homeFragment.onItemClickListener(vehicle));

            message.setOnClickListener(view -> homeFragment.sendMessage(vehicle.getSellerId()));
        }


    }

    public class AdsViewHolderData extends RecyclerView.ViewHolder {
        //Ads
        NativeAdView adView;
        NativeAd mNativeAd;

        public AdsViewHolderData(@NonNull View itemView) {
            super(itemView);
            adView = itemView.findViewById(R.id.native_ad_view);
            loadNativeAd();
        }


        public void bindData() {

        }


        private void loadNativeAd() {
            AdLoader.Builder adBuilder = new AdLoader.Builder(mContext, mContext.getResources().getString(R.string.native_2));
            adBuilder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                @Override
                public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {

                    if (mNativeAd != null)
                        mNativeAd.destroy();
                    mNativeAd = nativeAd;
                    adView.setVisibility(View.VISIBLE);
                    populateNativeAdView(nativeAd, adView);

                }
            });

            VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
            NativeAdOptions nativeAdOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

            adBuilder.withNativeAdOptions(nativeAdOptions);

            AdLoader adLoader = adBuilder.withAdListener(new AdListener() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    // Toast.makeText(mContext, "Failed to load Error" + loadAdError, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    // Toast.makeText(mContext, "Ad loaded", Toast.LENGTH_SHORT).show();
                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }

        private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
            // Set the media view.
            adView.setMediaView(adView.findViewById(R.id.ad_media));

            // Set other ad assets.
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            adView.setStoreView(adView.findViewById(R.id.ad_store));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

            // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
            adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }

            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((MaterialButton) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }

            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(
                        nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getPrice() == null) {
                adView.getPriceView().setVisibility(View.INVISIBLE);
            } else {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
            }

            if (nativeAd.getStore() == null) {
                adView.getStoreView().setVisibility(View.INVISIBLE);
            } else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            }

            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) adView.getStarRatingView())
                        .setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            adView.setNativeAd(nativeAd);
        }

    }


}

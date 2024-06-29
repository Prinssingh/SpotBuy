package com.s19.spotbuy.Fragments.main;

import static com.s19.spotbuy.Others.Constants.UserCollection;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.s19.spotbuy.Activity.AddNewPostActivity;
import com.s19.spotbuy.Activity.HomeActivity;
import com.s19.spotbuy.DataBase.SharedPrefs;
import com.s19.spotbuy.DataBase.UserManager;
import com.s19.spotbuy.Models.User;
import com.s19.spotbuy.R;

import java.util.Objects;


public class SellFragment extends Fragment implements View.OnClickListener {

    View Root;
    FragmentActivity activity;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView totalPosts, postsLeft;
    Button button_topup, button_add_post, button_show_packages;
    int postLeft = 0;

    SharedPrefs sharedPrefs;
    AlertDialog alertPlans;

    UserManager userManager;


    //Ads
    NativeAdView adView;
    NativeAd mNativeAd;

    public SellFragment() {
        // Required empty public constructor
    }

    public static SellFragment newInstance() {

        return new SellFragment();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity= requireActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Root = inflater.inflate(R.layout.fragment_sell, container, false);
        sharedPrefs = new SharedPrefs(activity);
        userManager = new UserManager(activity);

        initView();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                loadNativeAd();
            }
        });
        return Root;
    }

    @SuppressLint({"SetTextI18n", "SuspiciousIndentation"})
    private void initView() {
        adView = Root.findViewById(R.id.native_ad_view);
        adView.setVisibility(View.GONE);

        totalPosts = Root.findViewById(R.id.totalPosts);
        postsLeft = Root.findViewById(R.id.postsLeft);
        //totalPosts.setText("My Post's  :" + );
        User user = userManager.getUserById(sharedPrefs.getSharedUID());

        if (user.getId() != null) {
            postLeft = user.getAvailablePost();
            totalPosts.setText("My Post's  : " + user.getTotalPost());
            postsLeft.setText("Post's Left  : " + user.getAvailablePost());
        } else
            db.collection(UserCollection).document(sharedPrefs.getSharedUID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    postLeft = Objects.requireNonNull(documentSnapshot.toObject(User.class)).getAvailablePost();
                    totalPosts.setText("My Post's  : " + documentSnapshot.get("totalPost"));
                    postsLeft.setText("Post's Left  : " + documentSnapshot.get("availablePost"));
                }
            });

        button_topup = Root.findViewById(R.id.button_topup);
        button_topup.setOnClickListener(this);

        button_add_post = Root.findViewById(R.id.button_add_post);
        button_add_post.setOnClickListener(this);

        button_show_packages = Root.findViewById(R.id.button_show_packages);
        button_show_packages.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == button_topup) {
            showTopupDialog();
        }
        if (view == button_add_post) {
            if (postLeft > 0)
                startActivity(new Intent(requireActivity(), AddNewPostActivity.class));
            else
                showTopupDialog();
        }

        if (view == button_show_packages) {
            Toast.makeText(activity, "Coming Soon...", Toast.LENGTH_SHORT).show();
        }

    }

    private void showTopupDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activity);
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.dialog_plans, null);
        builder.setView(dialogView);

        MaterialCardView close = dialogView.findViewById(R.id.close);
        close.setOnClickListener(view -> alertPlans.dismiss());

        MaterialCardView plan1 = dialogView.findViewById(R.id.plan1);
        plan1.setOnClickListener(view -> {
            alertPlans.dismiss();
            Toast.makeText(activity, "plan 1 will be activate soon", Toast.LENGTH_SHORT).show();
        });
        MaterialCardView plan2 = dialogView.findViewById(R.id.plan2);
        plan2.setOnClickListener(view -> {
            alertPlans.dismiss();
            Toast.makeText(activity, "plan 2 will be activate soon", Toast.LENGTH_SHORT).show();

        });
        MaterialCardView plan3 = dialogView.findViewById(R.id.plan3);
        plan3.setOnClickListener(view -> {
            alertPlans.dismiss();
            Toast.makeText(activity, "plan 3 will be activate soon", Toast.LENGTH_SHORT).show();

        });


        alertPlans = builder.create();
        alertPlans.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertPlans.show();

    }

    private void loadNativeAd() {
        AdLoader.Builder adBuilder = new AdLoader.Builder(activity, activity.getResources().getString(R.string.native_1));
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


        AdLoader adLoader = adBuilder.withAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                //Toast.makeText(activity, "Failed to load Error" + loadAdError, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                //Toast.makeText(activity, "Ad loaded", Toast.LENGTH_SHORT).show();
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
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
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


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((HomeActivity)context).setSelectedChip(3);
    }
}
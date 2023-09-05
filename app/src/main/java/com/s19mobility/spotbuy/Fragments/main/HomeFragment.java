package com.s19mobility.spotbuy.Fragments.main;

import static com.s19mobility.spotbuy.Others.Constants.VehiclePostCollection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.s19mobility.spotbuy.Activity.HelpAndSupportActivity;
import com.s19mobility.spotbuy.Activity.LanguageActivity;
import com.s19mobility.spotbuy.Activity.MyAdActivity;
import com.s19mobility.spotbuy.Activity.NotificationsActivity;
import com.s19mobility.spotbuy.Activity.ProfileActivity;
import com.s19mobility.spotbuy.Activity.VehicleDetailsActivity;
import com.s19mobility.spotbuy.Adapters.FireBase.CategoriesListAdapter;
import com.s19mobility.spotbuy.Adapters.FireBase.VehicleListAdapter;
import com.s19mobility.spotbuy.Adapters.VehicleCategoryListAdapter;
import com.s19mobility.spotbuy.BuildConfig;
import com.s19mobility.spotbuy.Models.VehicleCategory;
import com.s19mobility.spotbuy.Models.VehiclePost;
import com.s19mobility.spotbuy.Others.Constants;
import com.s19mobility.spotbuy.Others.GPSTracker;
import com.s19mobility.spotbuy.Others.WrapContentGridlayoutManager;
import com.s19mobility.spotbuy.Others.WrapContentLinearLayoutManager;
import com.s19mobility.spotbuy.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment implements View.OnClickListener {

    View Root;
    DrawerLayout drawer_layout;
    NavigationView navigationView;
    View headerView;

    //Drawer View
    TextView editProfile, userName;
    ShapeableImageView userImage;
    LinearLayout notifications, yourAds, language, share, helpSupport, logout;

    //Page
    MaterialCardView drawer_open;
    TextView location;
    RecyclerView VehicleCategory, vehiclesList;
    TextView title;
    LinearLayout emptyIndicator;

    GPSTracker gpsTracker;
    FirestoreRecyclerAdapter adapterCategory,adapterVehicle;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Root = inflater.inflate(R.layout.fragment_home, container, false);


        initView();

        return Root;
    }

    private void initView() {
        //Drawer
        drawer_layout = Root.findViewById(R.id.drawer_layout);
        navigationView = Root.findViewById(R.id.navigationView);

        headerView = navigationView.getHeaderView(0);

        editProfile = headerView.findViewById(R.id.editProfile);
        editProfile.setOnClickListener(this);

        userImage = headerView.findViewById(R.id.userImage);
        userImage.setOnClickListener(this);

        userName = headerView.findViewById(R.id.userName);
        userName.setOnClickListener(this);

        notifications = headerView.findViewById(R.id.notifications);
        notifications.setOnClickListener(this);

        yourAds = headerView.findViewById(R.id.yourAds);
        yourAds.setOnClickListener(this);

        language = headerView.findViewById(R.id.language);
        language.setOnClickListener(this);

        share = headerView.findViewById(R.id.share);
        share.setOnClickListener(this);

        helpSupport = headerView.findViewById(R.id.helpSupport);
        helpSupport.setOnClickListener(this);

        logout = headerView.findViewById(R.id.logout);
        logout.setOnClickListener(this);

        //Page
        drawer_open = Root.findViewById(R.id.drawer_open);
        drawer_open.setOnClickListener(this);

        location = Root.findViewById(R.id.location);
        location.setOnClickListener(this);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    gpsTracker = new GPSTracker(requireContext());
                    if (gpsTracker.canGetLocation()) {

                        location.setText(gpsTracker.getLocality());
                    }
                }


            }
        });

        VehicleCategory = Root.findViewById(R.id.VehicleCategory);
        VehicleCategory.setLayoutManager(new WrapContentLinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        setCategoryAdapter();


        title = Root.findViewById(R.id.title);

        vehiclesList = Root.findViewById(R.id.vehiclesList);

        vehiclesList.setLayoutManager(new WrapContentGridlayoutManager(requireContext(), 2));
        setVehicleAdapter(db.collection(VehiclePostCollection).whereEqualTo("active",true).whereEqualTo("status","APPROVED"));

        emptyIndicator= Root.findViewById(R.id.emptyIndicator);
        emptyIndicator.setVisibility(View.GONE);
    }

    @SuppressLint("NotifyDataSetChanged")
    protected void setCategoryAdapter() {
       Query query =db.collection("VehicleCategory").orderBy("preference");

        FirestoreRecyclerOptions<VehicleCategory> options = new FirestoreRecyclerOptions.Builder<VehicleCategory>()
                .setQuery(query, VehicleCategory.class)
                .build();

        adapterCategory = new CategoriesListAdapter(options, requireContext(),this);
        VehicleCategory.setAdapter(adapterCategory);
        adapterCategory.notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    protected void setVehicleAdapter( Query query) {
        FirestoreRecyclerOptions<VehiclePost> options = new FirestoreRecyclerOptions.Builder<VehiclePost>()
                .setQuery(query, VehiclePost.class)
                .build();

        adapterVehicle = new VehicleListAdapter(options, requireContext(),this);
        vehiclesList.setAdapter(adapterVehicle);
        adapterVehicle.notifyDataSetChanged();
    }


    public void onItemClickListener(VehiclePost vehiclePost) {
        Intent details = new Intent(requireActivity(), VehicleDetailsActivity.class);
        details.putExtra("vehicle", vehiclePost);

        startActivity(details);
    }

    @SuppressLint("SetTextI18n")
    public void onCategoryClickListener(VehicleCategory vehicleCategory) {
        if (Objects.equals(vehicleCategory.getName(), "All")){
            title.setText("All Vehicles");
            setVehicleAdapter(db.collection(VehiclePostCollection).whereEqualTo("active",true).whereEqualTo("status","APPROVED"));
        }
        else{
            title.setText(vehicleCategory.getName());
            setVehicleAdapter(db.collection(VehiclePostCollection).whereEqualTo("active",true).whereEqualTo("status","APPROVED").whereEqualTo("categoryId",vehicleCategory.getId()));
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {

        if (view == drawer_open) {
            drawer_layout.open();
        }

        if (view == editProfile) {
            Intent profileIntent = new Intent(requireActivity(), ProfileActivity.class);
            profileIntent.putExtra(Constants.ProfileMode, "edit");
            startActivity(profileIntent);
        }

        if (userImage == view || userName == view) {
            Intent profileIntent = new Intent(requireActivity(), ProfileActivity.class);
            profileIntent.putExtra(Constants.ProfileMode, "show");
            startActivity(profileIntent);
        }

        if (notifications == view) {
            drawer_layout.closeDrawers();
            Intent profileIntent = new Intent(requireActivity(), NotificationsActivity.class);
            startActivity(profileIntent);
        }
        if (yourAds == view) {
            drawer_layout.closeDrawers();
            Intent profileIntent = new Intent(requireActivity(), MyAdActivity.class);
            startActivity(profileIntent);
        }

        if (language == view) {
            drawer_layout.closeDrawers();
            Intent profileIntent = new Intent(requireActivity(), LanguageActivity.class);
            startActivity(profileIntent);
        }
        if (share == view) {
            drawer_layout.closeDrawers();
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                String shareMessage = "\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                ShareCompat.IntentBuilder.from(requireActivity())
                        .setType("text/plain")
                        .setChooserTitle("Chooser title")
                        .setText("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                        .startChooser();
            }
        }


        if (helpSupport == view) {
            drawer_layout.closeDrawers();
            Intent profileIntent = new Intent(requireActivity(), HelpAndSupportActivity.class);
            startActivity(profileIntent);
        }
        if (logout == view) {
            drawer_layout.closeDrawers();
            //TODO Logout Dialog

        }


        if (location == view) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                gpsTracker = new GPSTracker(requireContext());
                if (gpsTracker.canGetLocation()) {

                    Log.i("here", "startActivityForResult: " + gpsTracker.getLatitude()
                            + "  " + gpsTracker.getLongitude());
                    location.setText("" + gpsTracker.getLatitude());
                    location.setText(gpsTracker.getLocality());
                }
            }


        }
    }


    ///Sample Data
    @Override
    public void onStart() {
        super.onStart();
        adapterCategory.startListening();
        adapterVehicle.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        //adapterCategory.stopListening();
        adapterVehicle.stopListening();
    }

    public void showEmptyIndicator(){

        try {
            emptyIndicator.setVisibility(View.VISIBLE);
        } catch (Exception e) {

        }
    }
    public void hideEmptyIndicator(){
        try {
            emptyIndicator.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
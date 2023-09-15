package com.s19mobility.spotbuy.Fragments.main;

import static com.s19mobility.spotbuy.Others.Constants.VehiclePostCollection;
import static com.s19mobility.spotbuy.Others.Constants.byteToBitmap;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.api.LogDescriptor;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.s19mobility.spotbuy.Activity.ChatActivity;
import com.s19mobility.spotbuy.Activity.HelpAndSupportActivity;
import com.s19mobility.spotbuy.Activity.HomeActivity;
import com.s19mobility.spotbuy.Activity.LanguageActivity;
import com.s19mobility.spotbuy.Activity.MyAdActivity;
import com.s19mobility.spotbuy.Activity.NotificationsActivity;
import com.s19mobility.spotbuy.Activity.ProfileActivity;
import com.s19mobility.spotbuy.Activity.SearchActivity;
import com.s19mobility.spotbuy.Activity.VehicleDetailsActivity;
import com.s19mobility.spotbuy.Adapters.FireBase.CategoriesListAdapter;
import com.s19mobility.spotbuy.Adapters.FireBase.VehicleListAdapter;
import com.s19mobility.spotbuy.Adapters.VehicleCategoryListAdapter;
import com.s19mobility.spotbuy.BuildConfig;
import com.s19mobility.spotbuy.DataBase.ImageManager;
import com.s19mobility.spotbuy.DataBase.SharedPrefs;
import com.s19mobility.spotbuy.DataBase.UserManager;
import com.s19mobility.spotbuy.DataBase.VehicleDetails.VehicleCategoryManager;
import com.s19mobility.spotbuy.Models.ImageModel;
import com.s19mobility.spotbuy.Models.User;
import com.s19mobility.spotbuy.Models.VehicleCategory;
import com.s19mobility.spotbuy.Models.VehiclePost;
import com.s19mobility.spotbuy.Others.Constants;
import com.s19mobility.spotbuy.Others.GPSTracker;
import com.s19mobility.spotbuy.R;
import com.s19mobility.spotbuy.Widgets.WrapContentGridlayoutManager;
import com.s19mobility.spotbuy.Widgets.WrapContentLinearLayoutManager;

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
    FirestoreRecyclerAdapter adapterCategory, adapterVehicle;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPrefs sharedPrefs;

    ImageView search;

    UserManager userManager;
    VehicleCategoryManager vehicleCategoryManager;
    ImageManager imageManager;

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
        sharedPrefs = new SharedPrefs(requireContext());
        userManager = new UserManager(requireContext());
        imageManager = new ImageManager(requireContext());
        vehicleCategoryManager = new VehicleCategoryManager(requireContext());
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
        try {
            User user =userManager.getUserById(sharedPrefs.getSharedUID());
            ImageModel imageModel =imageManager.getImageByLink(user.getImage());
            userImage.setImageBitmap(imageModel.getImageBitmap());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        userName = headerView.findViewById(R.id.userName);
        userName.setText(sharedPrefs.getSharedName());
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

        new Handler().post(() -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                gpsTracker = new GPSTracker(requireContext());
                if (gpsTracker.canGetLocation()) {

                    location.setText(gpsTracker.getLocality());
                }
            }


        });

        VehicleCategory = Root.findViewById(R.id.VehicleCategory);
        VehicleCategory.setLayoutManager(new WrapContentLinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        setCategoryAdapter();


        title = Root.findViewById(R.id.title);

        vehiclesList = Root.findViewById(R.id.vehiclesList);
        emptyIndicator = Root.findViewById(R.id.emptyIndicator);
        emptyIndicator.setVisibility(View.GONE);

        vehiclesList.setLayoutManager(new WrapContentGridlayoutManager(requireContext(), 2));
        setVehicleAdapter(db.collection(VehiclePostCollection).whereEqualTo("active", true).whereEqualTo("status", "APPROVED"));


        search = Root.findViewById(R.id.search);
        search.setOnClickListener(this);
    }


    @SuppressLint("NotifyDataSetChanged")
    protected void setCategoryAdapter() {
        if(vehicleCategoryManager.getCount()>1){
            VehicleCategoryListAdapter categoryListAdapter =new VehicleCategoryListAdapter(vehicleCategoryManager.listAll(),this,requireContext());
            VehicleCategory.setAdapter(categoryListAdapter);
        }
        else {
            Query query = db.collection("VehicleCategory").orderBy("preference");
            FirestoreRecyclerOptions<VehicleCategory> options = new FirestoreRecyclerOptions.Builder<VehicleCategory>()
                    .setQuery(query, VehicleCategory.class)
                    .build();
            adapterCategory = new CategoriesListAdapter(options, requireContext(), this);
            VehicleCategory.setAdapter(adapterCategory);
            adapterCategory.startListening();
            adapterCategory.notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    protected void setVehicleAdapter(Query query) {
        FirestoreRecyclerOptions<VehiclePost> options = new FirestoreRecyclerOptions.Builder<VehiclePost>()
                .setQuery(query, VehiclePost.class)
                .setLifecycleOwner(this)
                .build();

        adapterVehicle = new VehicleListAdapter(options, requireContext(), this);
        vehiclesList.setAdapter(adapterVehicle);
        adapterVehicle.startListening();
        adapterVehicle.notifyDataSetChanged();

    }

    @SuppressLint("NotifyDataSetChanged")
    protected void updateVehicleAdapter(Query query) {
        FirestoreRecyclerOptions<VehiclePost> options = new FirestoreRecyclerOptions.Builder<VehiclePost>()
                .setQuery(query, VehiclePost.class)
                .setLifecycleOwner(this)
                .build();

        adapterVehicle.updateOptions(options);
        adapterVehicle.notifyDataSetChanged();
    }


    public void onItemClickListener(VehiclePost vehiclePost) {
        Intent details = new Intent(requireActivity(), VehicleDetailsActivity.class);
        details.putExtra("vehicle", vehiclePost);

        startActivity(details);
    }

    @SuppressLint("SetTextI18n")
    public void onCategoryClickListener(VehicleCategory vehicleCategory) {
        if (Objects.equals(vehicleCategory.getName(), "All")) {
            title.setText("All Vehicles");
            updateVehicleAdapter(db.collection(VehiclePostCollection).whereEqualTo("active", true).whereEqualTo("status", "APPROVED"));
        } else {
            title.setText(vehicleCategory.getName());
            updateVehicleAdapter(db.collection(VehiclePostCollection).whereEqualTo("active", true).whereEqualTo("status", "APPROVED").whereEqualTo("categoryId", vehicleCategory.getId()));
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
            ((HomeActivity) requireActivity()).logoutConfirmationDialog();

        }

        if (view == search) {
            startActivity(new Intent(requireActivity(), SearchActivity.class));
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
        try {
            adapterCategory.startListening();
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapterVehicle.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            adapterCategory.stopListening();
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapterVehicle.stopListening();
    }

    public void showEmptyIndicator() {

        try {
            emptyIndicator.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideEmptyIndicator() {
        try {
            emptyIndicator.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String id) {
        if (Objects.equals(id, sharedPrefs.getSharedUID())) {
            Toast.makeText(requireContext(), "This is your post.", Toast.LENGTH_LONG).show();
            return;
        }
        User otherUser = new User();
        otherUser.setId(id);
        Intent intent = new Intent(requireActivity(), ChatActivity.class);
        intent.putExtra("User", otherUser);
        startActivity(intent);

    }


}
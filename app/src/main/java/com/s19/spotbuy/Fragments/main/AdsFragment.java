package com.s19.spotbuy.Fragments.main;

import static com.s19.spotbuy.Others.Constants.VehiclePostCollection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.s19.spotbuy.Activity.HomeActivity;
import com.s19.spotbuy.Activity.UpdatePostActivity;
import com.s19.spotbuy.Adapters.FireBase.MyAdsAdapter;
import com.s19.spotbuy.Adapters.MyAdsListAdapter;
import com.s19.spotbuy.DataBase.SharedPrefs;
import com.s19.spotbuy.DataBase.VehiclePostManager;
import com.s19.spotbuy.Models.VehiclePost;
import com.s19.spotbuy.R;
import com.s19.spotbuy.Widgets.WrapContentLinearLayoutManager;


public class AdsFragment extends Fragment {

    View Root;
    boolean main;
    LinearLayout header_title, emptyIndicator;

    RecyclerView myAdList;
    MyAdsAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPrefs sharedPrefs;
    VehiclePostManager vehiclePostManager;

    public AdsFragment(boolean main) {
        this.main = main;
    }


    public static AdsFragment newInstance(boolean main) {
        return new AdsFragment(main);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Root = inflater.inflate(R.layout.fragment_ads, container, false);
        sharedPrefs = new SharedPrefs(requireContext());
        vehiclePostManager = new VehiclePostManager(requireContext());

        initView();
        setAdapter();

        return Root;
    }

    private void setAdapter() {

        if (vehiclePostManager.getCount() > 1) {
            MyAdsListAdapter myAdsListAdapter = new MyAdsListAdapter(vehiclePostManager.listAll(), this, requireContext());
            myAdList.setAdapter(myAdsListAdapter);

        } else {
            Query query = db.collection(VehiclePostCollection)
                    .whereEqualTo("sellerId", sharedPrefs.getSharedUID()).orderBy("dateTime");
            FirestoreRecyclerOptions<VehiclePost> options = new FirestoreRecyclerOptions.Builder<VehiclePost>()
                    .setQuery(query, VehiclePost.class)
                    .build();
            adapter = new MyAdsAdapter(options, this, requireContext());
            myAdList.setAdapter(adapter);
            adapter.startListening();
        }

    }


    private void initView() {
        header_title = Root.findViewById(R.id.header_title);
        if (!main) header_title.setVisibility(View.GONE);

        emptyIndicator = Root.findViewById(R.id.emptyIndicator);
        emptyIndicator.setVisibility(View.GONE);

        myAdList = Root.findViewById(R.id.myAdList);
        myAdList.setLayoutManager(new WrapContentLinearLayoutManager(requireContext()));

    }


    public void OnItemClickListener(VehiclePost vehicle) {
        Intent intent = new Intent(requireActivity(), UpdatePostActivity.class);
        intent.putExtra("vehicle", vehicle);
        startActivity(intent);

    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            adapter.startListening();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            adapter.stopListening();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            ((HomeActivity) context).setSelectedChip(2);
        } catch (Exception ignored) {

        }
    }


}
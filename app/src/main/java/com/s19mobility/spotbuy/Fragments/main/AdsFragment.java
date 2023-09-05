package com.s19mobility.spotbuy.Fragments.main;

import static com.s19mobility.spotbuy.Others.Constants.VehiclePostCollection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.s19mobility.spotbuy.Adapters.FireBase.MyAdsAdapter;
import com.s19mobility.spotbuy.DataBase.SharedPrefs;
import com.s19mobility.spotbuy.Models.VehicleCategory;
import com.s19mobility.spotbuy.Models.VehiclePost;
import com.s19mobility.spotbuy.Others.WrapContentLinearLayoutManager;
import com.s19mobility.spotbuy.R;


public class AdsFragment extends Fragment {

    View Root;
    boolean main;
    LinearLayout header_title, emptyIndicator;

    RecyclerView myAdList;
    FirestoreRecyclerAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPrefs sharedPrefs;

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

        Query query = db.collection(VehiclePostCollection)
                .whereEqualTo("sellerId", sharedPrefs.getSharedID()).orderBy("dateTime");
        FirestoreRecyclerOptions<VehiclePost> options = new FirestoreRecyclerOptions.Builder<VehiclePost>()
                .setQuery(query, VehiclePost.class)
                .build();
        adapter = new MyAdsAdapter(options, this, requireContext());

        initView();

        return Root;
    }

    private void initView() {
        header_title = Root.findViewById(R.id.header_title);
        if (!main) header_title.setVisibility(View.GONE);

        emptyIndicator = Root.findViewById(R.id.emptyIndicator);
        emptyIndicator.setVisibility(View.GONE);

        myAdList = Root.findViewById(R.id.myAdList);
        myAdList.setLayoutManager(new WrapContentLinearLayoutManager(requireContext()));
        myAdList.setAdapter(adapter);
    }


    public void OnItemClickListener(VehiclePost ad) {

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
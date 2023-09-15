package com.s19mobility.spotbuy.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.s19mobility.spotbuy.Adapters.FireBase.SearchAdapter;
import com.s19mobility.spotbuy.DataBase.SharedPrefs;
import com.s19mobility.spotbuy.Models.User;
import com.s19mobility.spotbuy.Models.VehiclePost;
import com.s19mobility.spotbuy.Others.Constants;
import com.s19mobility.spotbuy.R;
import com.s19mobility.spotbuy.Widgets.WrapContentGridlayoutManager;

import java.util.Objects;

public class SearchActivity extends AppCompatActivity {
    ImageView back;
    EditText search;
    RecyclerView searchResults;
    LinearLayout emptyIndicator;
    SearchAdapter adapter;

    SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        sharedPrefs = new SharedPrefs(this);


        initView();
    }

    private void initView() {

        back = findViewById(R.id.back);
        back.setOnClickListener(view -> onBackPressed());

        emptyIndicator = findViewById(R.id.emptyIndicator);
        emptyIndicator.setVisibility(View.GONE);

        search = findViewById(R.id.search);
        search.requestFocus();
        searchResults = findViewById(R.id.searchResults);
        searchResults.setLayoutManager(new WrapContentGridlayoutManager(this, 2));

        setSearchAdapter();

        search.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchKey =search.getText().toString().trim();
                Query query = FirebaseFirestore.getInstance().collection(Constants.VehiclePostCollection)
                        .orderBy("title")
                        .whereEqualTo("active", true)
                        .whereEqualTo("status", "APPROVED")
                        .whereGreaterThanOrEqualTo("title",searchKey.toUpperCase())
                        .whereLessThanOrEqualTo("title",searchKey.toLowerCase()+"\uf8ff");

                updateSearchQuery(query);


                return true;
            }
            return false;
        });


    }

    private void setSearchAdapter() {
        String searchKey=search.getText().toString().trim();
        Query query = FirebaseFirestore.getInstance().collection(Constants.VehiclePostCollection)
                .orderBy("title")
                .whereEqualTo("active", true)
                .whereEqualTo("status", "APPROVED")
                .whereGreaterThanOrEqualTo("title",searchKey.toUpperCase())
                .whereLessThanOrEqualTo("title",searchKey.toLowerCase()+"\uf8ff");

        FirestoreRecyclerOptions<VehiclePost> options = new FirestoreRecyclerOptions.Builder<VehiclePost>()
                .setQuery(query, VehiclePost.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new SearchAdapter(options, this);
        searchResults.setAdapter(adapter);
        adapter.startListening();


    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateSearchQuery(Query query) {

        FirestoreRecyclerOptions<VehiclePost> options = new FirestoreRecyclerOptions.Builder<VehiclePost>()
                .setQuery(query, VehiclePost.class)
                .setLifecycleOwner(this)
                .build();
        adapter.updateOptions(options);
        adapter.notifyDataSetChanged();
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
            Toast.makeText(this, "This is your post.", Toast.LENGTH_LONG).show();
            return;
        }
        User otherUser = new User();
        otherUser.setId(id);
        Intent intent = new Intent(SearchActivity.this, ChatActivity.class);
        intent.putExtra("User", otherUser);
        startActivity(intent);

    }


    public void onItemClickListener(VehiclePost vehiclePost) {
        Intent details = new Intent(SearchActivity.this, VehicleDetailsActivity.class);
        details.putExtra("vehicle", vehiclePost);
        startActivity(details);
    }
}
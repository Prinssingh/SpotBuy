package com.s19mobility.spotbuy.Fragments.main;

import static com.s19mobility.spotbuy.Others.Constants.UserCollection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.s19mobility.spotbuy.Activity.AddNewPostActivity;
import com.s19mobility.spotbuy.DataBase.SharedPrefs;
import com.s19mobility.spotbuy.Models.User;
import com.s19mobility.spotbuy.R;


public class SellFragment extends Fragment implements View.OnClickListener {

    View Root;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView totalPosts, postsLeft;
    Button button_topup, button_add_post;

    SharedPrefs sharedPrefs;

    public SellFragment() {
        // Required empty public constructor
    }


    public static SellFragment newInstance() {

        return new SellFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Root = inflater.inflate(R.layout.fragment_sell, container, false);
        sharedPrefs = new SharedPrefs(requireContext());

        initView();

        return Root;
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        totalPosts = Root.findViewById(R.id.totalPosts);
        postsLeft = Root.findViewById(R.id.postsLeft);
        //totalPosts.setText("My Post's  :" + );
        db.collection(UserCollection).document(sharedPrefs.getSharedID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                totalPosts.setText("My Post's  : " + documentSnapshot.get("totalPost"));
                postsLeft.setText("Post's Left  : "+documentSnapshot.get("availablePost"));
            }
        });

        button_topup = Root.findViewById(R.id.button_topup);
        button_topup.setOnClickListener(this);

        button_add_post = Root.findViewById(R.id.button_add_post);
        button_add_post.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == button_topup) {
            showTopupDialog();
        }
        if (view == button_add_post) {
            startActivity(new Intent(requireActivity(), AddNewPostActivity.class));
        }

    }

    private void showTopupDialog() {

    }
}
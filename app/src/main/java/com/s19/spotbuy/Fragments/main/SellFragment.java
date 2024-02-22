package com.s19.spotbuy.Fragments.main;

import static com.s19.spotbuy.Others.Constants.UserCollection;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.s19.spotbuy.Activity.AddNewPostActivity;
import com.s19.spotbuy.DataBase.SharedPrefs;
import com.s19.spotbuy.DataBase.UserManager;
import com.s19.spotbuy.Models.User;
import com.s19.spotbuy.R;

import java.util.Objects;


public class SellFragment extends Fragment implements View.OnClickListener {

    View Root;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView totalPosts, postsLeft;
    Button button_topup, button_add_post,button_show_packages;
    int postLeft = 0;

    SharedPrefs sharedPrefs;
    AlertDialog alertPlans;

    UserManager userManager;

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
        userManager = new UserManager(requireContext());

        initView();

        return Root;
    }

    @SuppressLint({"SetTextI18n", "SuspiciousIndentation"})
    private void initView() {
        totalPosts = Root.findViewById(R.id.totalPosts);
        postsLeft = Root.findViewById(R.id.postsLeft);
        //totalPosts.setText("My Post's  :" + );
        User user= userManager.getUserById(sharedPrefs.getSharedUID());

        if(user.getId()!=null)
        {
            postLeft=user.getAvailablePost();
            totalPosts.setText("My Post's  : " + user.getTotalPost());
            postsLeft.setText("Post's Left  : " + user.getAvailablePost());
        }
        else
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
        
        if(view == button_show_packages)
        {
            Toast.makeText(requireContext(), "Coming Soon...", Toast.LENGTH_SHORT).show();
        }

    }

    private void showTopupDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(requireContext());
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.dialog_plans, null);
        builder.setView(dialogView);

        MaterialCardView close = dialogView.findViewById(R.id.close);
        close.setOnClickListener(view -> alertPlans.dismiss());

        MaterialCardView plan1 = dialogView.findViewById(R.id.plan1);
        plan1.setOnClickListener(view -> {
            alertPlans.dismiss();
            Toast.makeText(requireContext(), "plan 1 will be activate soon", Toast.LENGTH_SHORT).show();
        });
        MaterialCardView plan2 = dialogView.findViewById(R.id.plan2);
        plan2.setOnClickListener(view -> {
            alertPlans.dismiss();
            Toast.makeText(requireContext(), "plan 2 will be activate soon", Toast.LENGTH_SHORT).show();

        });
        MaterialCardView plan3 = dialogView.findViewById(R.id.plan3);
        plan3.setOnClickListener(view -> {
            alertPlans.dismiss();
            Toast.makeText(requireContext(), "plan 3 will be activate soon", Toast.LENGTH_SHORT).show();

        });


        alertPlans = builder.create();
        alertPlans.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertPlans.show();

    }
}
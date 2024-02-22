package com.s19.spotbuy.Fragments.Profile;

import static com.s19.spotbuy.Others.Constants.UserCollection;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.s19.spotbuy.DataBase.ImageManager;
import com.s19.spotbuy.DataBase.SharedPrefs;
import com.s19.spotbuy.DataBase.UserManager;
import com.s19.spotbuy.Models.User;
import com.s19.spotbuy.Others.DownloadImage;
import com.s19.spotbuy.R;

import java.util.Objects;


public class ShowProfileFragment extends Fragment implements View.OnClickListener {
    View Root;
    User curentUser;
    ShapeableImageView userImage;
    ProgressBar imageProgressIndicator;

    TextView username, post, followers, following, userName, userMobile, userMobileAlt, userEmail, userGender, userAddress;

    Button editProfile;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPrefs sharedPrefs;
    UserManager userManager;
    ImageManager imageManager;

    public ShowProfileFragment() {
        // Required empty public constructor
    }


    public static ShowProfileFragment newInstance() {

        return new ShowProfileFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Root = inflater.inflate(R.layout.fragment_show_profile, container, false);
        sharedPrefs = new SharedPrefs(requireContext());
        userManager= new UserManager(requireContext());
        imageManager= new ImageManager(requireContext());

        initView();

        return Root;
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        curentUser = userManager.getUserById(sharedPrefs.getSharedUID());

        editProfile = Root.findViewById(R.id.editProfile);
        editProfile.setOnClickListener(this);

        userImage = Root.findViewById(R.id.userImage);
        imageProgressIndicator = Root.findViewById(R.id.imageProgressIndicator);
        username = Root.findViewById(R.id.username);
        post = Root.findViewById(R.id.post);
        followers = Root.findViewById(R.id.followers);
        following = Root.findViewById(R.id.following);
        userName = Root.findViewById(R.id.userName);
        userMobile = Root.findViewById(R.id.userMobile);
        userMobileAlt = Root.findViewById(R.id.userMobileAlt);
        userEmail = Root.findViewById(R.id.userEmail);
        userGender = Root.findViewById(R.id.userGender);
        userAddress = Root.findViewById(R.id.userAddress);

        if(curentUser.getId()!=null){
            if(curentUser!=null){
                if(curentUser.getImage()!=null || Objects.equals(curentUser.getImage(), "")) {
                    Bitmap temp = imageManager.getImageByLink(curentUser.getImage()).getImageBitmap();
                    if(temp!=null)
                        userImage.setImageBitmap(temp);
                    else
                        new DownloadImage(userImage, imageProgressIndicator).execute(curentUser.getImage());
                }

                username.setText(""+curentUser.getName());
                userName.setText(""+curentUser.getName());
                post.setText(""+curentUser.getTotalPost());
                followers.setText(""+curentUser.getFollowerCount());
                following.setText(""+curentUser.getFollowingCount());
                userMobile.setText(""+curentUser.getMobile());
                userMobileAlt.setText(""+curentUser.getAlt_mobile());
                userEmail.setText(""+curentUser.getEmail());
                userGender.setText(""+curentUser.getGender());
                userAddress.setText(""+curentUser. getAddress());
            }
            else
                initView();
        }
        else{
            db.collection(UserCollection)
                    .document(sharedPrefs.getSharedUID())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            curentUser= documentSnapshot.toObject(User.class);
                            if(curentUser!=null){
                                if(curentUser.getImage()!=null || Objects.equals(curentUser.getImage(), ""))
                                    new DownloadImage(userImage,imageProgressIndicator).execute(curentUser.getImage());

                                username.setText(""+curentUser.getName());
                                userName.setText(""+curentUser.getName());
                                post.setText(""+curentUser.getTotalPost());
                                followers.setText(""+curentUser.getFollowerCount());
                                following.setText(""+curentUser.getFollowingCount());
                                userMobile.setText(""+curentUser.getMobile());
                                userMobileAlt.setText(""+curentUser.getAlt_mobile());
                                userEmail.setText(""+curentUser.getEmail());
                                userGender.setText(""+curentUser.getGender());
                                userAddress.setText(""+curentUser. getAddress());
                            }
                            else
                                initView();
                        }
                    });
        }


    }

    @Override
    public void onClick(View view) {
        if (editProfile == view) {
            // requireActivity().getSupportFragmentManager().popBackStack();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, EditProfileFragment.newInstance())
                    .addToBackStack("Data")
                    .commit();

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }
}
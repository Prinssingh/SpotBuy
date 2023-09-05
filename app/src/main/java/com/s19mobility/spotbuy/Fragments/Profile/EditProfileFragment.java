package com.s19mobility.spotbuy.Fragments.Profile;

import static com.s19mobility.spotbuy.Others.Constants.UserCollection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.s19mobility.spotbuy.Activity.HomeActivity;
import com.s19mobility.spotbuy.DataBase.SharedPrefs;
import com.s19mobility.spotbuy.DataBase.UserManager;
import com.s19mobility.spotbuy.Models.User;
import com.s19mobility.spotbuy.Others.ImagePicker;
import com.s19mobility.spotbuy.Others.LoadingDialog;
import com.s19mobility.spotbuy.Others.NetworkUtil;
import com.s19mobility.spotbuy.R;

import java.util.Date;
import java.util.Objects;


public class EditProfileFragment extends Fragment implements View.OnClickListener {
    View Root;
    ImageButton editUserImage;
    ImageView userImage;
    Button updateProfile;
    EditText userName, userMobileNo, userAltMobile, userEmail, userAddress;
    LinearLayout linearBG;
    Spinner gender;

    Activity activity;
    LoadingDialog loadingDialog;
    SharedPrefs sharedPrefs;
    UserManager userManager;
    User user = new User();

    FirebaseFirestore   db = FirebaseFirestore.getInstance();


    public EditProfileFragment() {
        // Required empty public constructor
    }


    public static EditProfileFragment newInstance() {

        return new EditProfileFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Root = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        activity = requireActivity();
        sharedPrefs = new SharedPrefs(activity);
        loadingDialog = new LoadingDialog(activity);
        userManager = new UserManager(activity);
        initView();
        return Root;
    }

    private void initView() {
        userImage = Root.findViewById(R.id.userImage);
        userImage.setOnClickListener(this);
        editUserImage = Root.findViewById(R.id.editUserImage);
        editUserImage.setOnClickListener(this);

        userName = Root.findViewById(R.id.userName);
        userMobileNo = Root.findViewById(R.id.userMobileNo);
        userMobileNo.setText(sharedPrefs.getSharedMobile());
        userMobileNo.setActivated(false);
        userAltMobile = Root.findViewById(R.id.userAltMobile);
        userEmail = Root.findViewById(R.id.userEmail);
        userAddress = Root.findViewById(R.id.userAddress);
        linearBG = Root.findViewById(R.id.linearBG);
        gender = Root.findViewById(R.id.gender);

        gender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    linearBG.setBackgroundResource(R.drawable.custom_active_background);
                else
                    linearBG.setBackgroundResource(R.drawable.custom_input_design);
            }
        });
        user.setGender("Male");
        gender.getSelectedItem().toString();


        updateProfile = Root.findViewById(R.id.updateProfile);
        updateProfile.setOnClickListener(this);

        setFirebaseData();
    }


    @Override
    public void onClick(View view) {
        if (view == updateProfile) {
            loadingDialog.show();
            profileUpdate();
        }

        if (view == userImage || view == editUserImage) {
            new ImagePicker(requireActivity(), userImage);
        }
    }

    private boolean validateEnteredData() {
        boolean isValid = true;
        if (userName.getText().toString().length() == 0 || userName.getText().toString().trim() == "") {
            isValid = false;
            userName.setError("Empty");
        }
        if (userEmail.getText().toString().length() == 0 || userEmail.getText().toString().trim() == "") {
            isValid = false;
            userEmail.setError("Empty");
        }


        return isValid;
    }

    private void getEnteredData() {


        user.setUid(sharedPrefs.getSharedUID());
        user.setName(userName.getText().toString().trim());
        user.setMobile(sharedPrefs.getSharedMobile());
        user.setAlt_mobile(userAltMobile.getText().toString().trim());
        user.setEmail(userEmail.getText().toString().trim());
        user.setAddress(userAddress.getText().toString().trim());
        //TODO set image

        if (!sharedPrefs.isProfileSet()) {
            //set default Value
            user.setActive(true);
            user.setAvailablePost(100); //TODO HERE
            user.setDateTime(new Date());
            user.setRole("User");
            user.setTotalPost(0);
            user.setPassword("");//TODO
        }


    }

    private void profileUpdate() {
        if (NetworkUtil.getConnectivityStatus(requireActivity()) != 0 && validateEnteredData()) {
            getEnteredData();
            if (sharedPrefs.getSharedID() == null || Objects.equals(sharedPrefs.getSharedID(), ""))
                db.collection(UserCollection)
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(activity, "Success!", Toast.LENGTH_SHORT).show();

                                documentReference.update("id",documentReference.getId());
                                user.setId(documentReference.getId());
                                sharedPrefs.setSharedID(documentReference.getId());
                                userManager.insert(user);
                                sharedPrefs.setProfleStatus(true);

                                startActivity(new Intent(requireActivity(), HomeActivity.class));
                                requireActivity().finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error writing document", e);
                                Toast.makeText(activity, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(requireActivity(), HomeActivity.class));
                                requireActivity().finish();
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                loadingDialog.dismiss();
                            }
                        });

            else
                db.collection(UserCollection)
                        .document(sharedPrefs.getSharedID())
                        .update(user.toUpdateMap())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(activity, "Update Success!", Toast.LENGTH_LONG).show();
                                requireActivity().getSupportFragmentManager().popBackStack();

                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingDialog.dismiss();
                            }
                        });


        }
    }

    private void setFirebaseData(){
        loadingDialog.show();
        db.collection(UserCollection).whereEqualTo("uid",sharedPrefs.getSharedUID())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            user= queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                            assert user != null;
                            sharedPrefs.setSharedID(user.getId());
                            userName.setText(user.getName());
                            userAltMobile.setText(user.getAlt_mobile());
                            userEmail.setText(user.getEmail());
                            userAddress.setText(user.getAddress());
                            userMobileNo.setText(user.getMobile());
                        }

                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        loadingDialog.dismiss();
                    }
                });

    }
}
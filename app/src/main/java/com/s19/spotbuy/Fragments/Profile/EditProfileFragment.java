package com.s19.spotbuy.Fragments.Profile;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.os.Build.VERSION.SDK_INT;
import static android.view.View.GONE;
import static com.s19.spotbuy.Activity.ImagePickerActivity.IMAGE_REQUEST_CODE;
import static com.s19.spotbuy.Activity.ImagePickerActivity.RATIO_X;
import static com.s19.spotbuy.Activity.ImagePickerActivity.RATIO_Y;
import static com.s19.spotbuy.Activity.ImagePickerActivity.RESULT_HEIGHT;
import static com.s19.spotbuy.Activity.ImagePickerActivity.RESULT_WIDTH;
import static com.s19.spotbuy.Others.Constants.CAMERA_ACTION_PICK_REQUEST_CODE;
import static com.s19.spotbuy.Others.Constants.CAMERA_REQUEST;
import static com.s19.spotbuy.Others.Constants.DEFAULT_POST_COUNT;
import static com.s19.spotbuy.Others.Constants.PICK_IMAGE_GALLERY_REQUEST_CODE;
import static com.s19.spotbuy.Others.Constants.STORAGE_REQUEST;
import static com.s19.spotbuy.Others.Constants.UserCollection;
import static com.s19.spotbuy.Others.Constants.getGenderList;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.s19.spotbuy.Activity.HomeActivity;
import com.s19.spotbuy.Activity.ImagePickerActivity;
import com.s19.spotbuy.Activity.UpdatePostActivity;
import com.s19.spotbuy.DataBase.ImageManager;
import com.s19.spotbuy.DataBase.SharedPrefs;
import com.s19.spotbuy.DataBase.UserManager;
import com.s19.spotbuy.Dialogs.LoadingDialog;
import com.s19.spotbuy.Models.ImageModel;
import com.s19.spotbuy.Models.User;
import com.s19.spotbuy.Others.DownloadImage;
import com.s19.spotbuy.Others.NetworkUtil;
import com.s19.spotbuy.Others.OnImageUploadListener;
import com.s19.spotbuy.Others.ReadBasicFireBaseData;
import com.s19.spotbuy.Others.SaveImageByteToDatabase;
import com.s19.spotbuy.Others.StateCityData;
import com.s19.spotbuy.Others.Utils;
import com.s19.spotbuy.R;
import com.s19.spotbuy.Widgets.TestToast;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class EditProfileFragment extends Fragment implements View.OnClickListener {

    View Root;
    ImageButton editUserImage;
    ImageView userImage;
    ProgressBar imageProgressIndicator;
    Button updateProfile;
    EditText userName, userMobileNo, userAltMobile, userEmail, userAddress;
    AutoCompleteTextView city,gender;
    Activity activity;
    LoadingDialog loadingDialog;
    SharedPrefs sharedPrefs;
    UserManager userManager;
    ImageManager imageManager;
    User user = new User();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();


    String oldImage;

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
        imageManager = new ImageManager(activity);


        initView();
        return Root;
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        imageProgressIndicator = Root.findViewById(R.id.imageProgressIndicator);
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
        //Setting City list
        city = Root.findViewById(R.id.city);
        ArrayAdapter<String> cityAdapter;
        cityAdapter = new ArrayAdapter<String>(requireActivity(),
                android.R.layout.simple_list_item_1, new Utils(requireContext()).getAllCityList());
        city.setAdapter(cityAdapter);
        city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sharedPrefs.setSharedCity(city.getText().toString().trim());
            }
        });
        city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!new Utils(requireContext()).getAllCityList().contains(city.getText().toString().trim())) {
                    city.setText("");
                }
                else {
                    sharedPrefs.setSharedCity(city.getText().toString().trim());
                }
            }
        });
        city.setText(requireContext().getString(R.string.select_your_city), false);


        //Setting Gender List
        gender = Root.findViewById(R.id.gender);
        ArrayAdapter<String> genderAdapter;
        genderAdapter = new ArrayAdapter<String>(requireActivity(),
                android.R.layout.simple_list_item_1, getGenderList());
        gender.setAdapter(genderAdapter);
        gender.setOnItemClickListener((adapterView, view, i, l) -> user.setGender(getGenderList().get(i)));
        gender.setFreezesText(false);
        gender.setText("Male", false);
        updateProfile = Root.findViewById(R.id.updateProfile);
        updateProfile.setOnClickListener(this);

        setFirebaseData();
    }

    @Override
    public void onClick(View view) {
        if (view == updateProfile) {
            updateProfile.requestFocus();
            loadingDialog.show();
            getEnteredData();
            try {
                profileUpdate();
            } catch (Exception e) {
                loadingDialog.dismiss();
                Toast.makeText(activity, "Error :" + e, Toast.LENGTH_SHORT).show();
            }
        }

        if (view == userImage || view == editUserImage) {
            showImagePicDialog();
        }
    }

    private boolean validateEnteredData() {
        boolean isValid = true;
        if (userName.getText().toString().length() == 0 || userName.getText().toString().trim() == "") {
            isValid = false;
            userName.setError("Empty");
        }
        else if(!isValidName(userName.getText().toString())){
            isValid = false;
            userName.setError("Invalid");
        }
        if (userEmail.getText().toString().length() != 0 && !isValidEmail(userEmail.getText().toString().trim())) {
            isValid = false;
            userEmail.setError("Invalid");
        }

        if (userAltMobile.getText().toString().length() != 0 && userAltMobile.getText().toString().length() != 10) {
            isValid = false;
            userAltMobile.setError("Invalid");
        }
        if(city.getText().toString().trim().length()==0) {
            isValid = false;
            city.setError("Empty");
        }
        else if(!new Utils(requireContext()).getAllCityList().contains(city.getText().toString().trim())) {
            isValid = false;
            city.setError("Invalid");
        }


        return isValid;
    }

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    public static boolean isValidName(String name) {
        return name.matches("[a-zA-Z\\s]+");
    }

    private void getEnteredData() {

        user.setId(sharedPrefs.getSharedUID());
        user.setName(capitalizeName(userName.getText().toString().trim()));
        user.setMobile(sharedPrefs.getSharedMobile());
        user.setAlt_mobile(userAltMobile.getText().toString().trim());
        user.setEmail(userEmail.getText().toString().trim());
        user.setAddress(userAddress.getText().toString().trim());
        user.setCity(city.getText().toString().trim());


        if (user.getDateTime() == null) {
            //set default Value
            user.setActive(true);
            user.setAvailablePost(DEFAULT_POST_COUNT);
            user.setDateTime(new Date());
            user.setRole("User");
            user.setTotalPost(0);
            user.setPassword("");//TODO
        }


    }

    public static String capitalizeName(String input) {
        // Split input by spaces to handle multiple parts
        String[] parts = input.trim().split("\\s+");

        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (!part.isEmpty()) {
                // Capitalize first letter of the part and append
                result.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    // Append the rest of the part in lowercase
                    result.append(part.substring(1).toLowerCase());
                }
                // Append space if there were multiple parts
                result.append(" ");
            }
        }

        // Remove trailing space and return the result
        return result.toString().trim();
    }

    private void profileUpdate() {
        if (NetworkUtil.getConnectivityStatus(requireActivity()) != 0) {
            if (validateEnteredData()) {
                if (sharedPrefs.getSharedName() == null || Objects.equals(sharedPrefs.getSharedName(), ""))
                    db.collection(UserCollection)
                            .document(sharedPrefs.getSharedUID())
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(activity, "Success!", Toast.LENGTH_SHORT).show();

                                    sharedPrefs.setSharedName(user.getName());
                                    if (user.getImage() != null || !Objects.equals(user.getImage(), ""))
                                        sharedPrefs.setSharedImage(user.getImage());
                                    userManager.insert(user);
                                    sharedPrefs.setProfleStatus(true);
                                    //setLocal Data
                                    new ReadBasicFireBaseData(requireActivity());

                                    startActivity(new Intent(requireActivity(), HomeActivity.class));
                                    requireActivity().finish();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.w("TAG", "Error writing document", e);
                                Toast.makeText(activity, "Something went wrong... Try Again!!", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(requireActivity(), HomeActivity.class));
                                //requireActivity().finish();
                            })
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    loadingDialog.dismiss();
                                }
                            });

                else
                    db.collection(UserCollection)
                            .document(sharedPrefs.getSharedUID())
                            .update(user.toUpdateMap())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    sharedPrefs.setSharedName(user.getName());
                                    sharedPrefs.setProfleStatus(true);
                                    if (user.getImage() != null || user.getImage() != "")
                                        sharedPrefs.setSharedImage(user.getImage());
                                    Toast.makeText(activity, "Update Success!", Toast.LENGTH_LONG).show();
                                    //Updating local db
                                    userManager.update(user);
                                    new SaveImageByteToDatabase(requireActivity()).execute(user.getImage());
//                                    try{
//                                        userManager.update(user);
//                                        new SaveImageByteToDatabase(requireActivity()).execute(user.getImage());
//                                    }
//                                    catch(SQLiteException e){
//                                        userManager.insert(user);
//                                        new SaveImageByteToDatabase(requireActivity()).execute(user.getImage());
//                                    }
                                    //catch(Exception e){e.printStackTrack}


                                    if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() == 0) {
                                        startActivity(new Intent(requireActivity(), HomeActivity.class));
                                        requireActivity().finish();
                                    } else
                                        requireActivity().getSupportFragmentManager().popBackStack();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("TAG", "onFailure: ", e);
                                    Toast.makeText(activity, "Error on update" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    loadingDialog.dismiss();
                                }
                            });


            } else {
                Toast.makeText(activity, "Invalid Data", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }

        } else {
            Toast.makeText(activity, "No Internet", Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
        }

    }

    private void setFirebaseData() {
        user = userManager.getUserById(sharedPrefs.getSharedUID());
        if (user.getId() != null) {

            sharedPrefs.setSharedName(user.getName());
            if (user.getImage() != null || user.getImage() != "") {
                sharedPrefs.setSharedImage(user.getImage());

                ImageModel  temp = imageManager.getImageByLink(user.getImage());
                if (temp != null && temp.getImageBitmap()!=null) {
                    userImage.setImageBitmap(temp.getImageBitmap());
                } else {
                        new DownloadImage(activity, userImage, imageProgressIndicator).execute(user.getImage());
                }
            }
            userName.setText(user.getName());
            userAltMobile.setText(user.getAlt_mobile());
            userEmail.setText(user.getEmail());
            userAddress.setText(user.getAddress());
            userMobileNo.setText(user.getMobile());
            gender.setText(user.getGender(), false);
            if(user.getCity()!=null ) {
                city.setText(user.getCity());
            }
            else if (sharedPrefs.getSharedCity().equals("location")) {
                city.setText(sharedPrefs.getSharedCity());
            }

            oldImage = user.getImage();
        } else {
            loadingDialog.show();
            db.collection(UserCollection)
                    .document(sharedPrefs.getSharedUID())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot != null) {
                                User temp = documentSnapshot.toObject(User.class);
                                if (temp == null)
                                    return;

                                user = temp;
                                sharedPrefs.setSharedName(user.getName());
                                if (user.getImage() != null || user.getImage() != "") {
                                    sharedPrefs.setSharedImage(user.getImage());
                                    ImageModel imageModel = new ImageManager(activity).getImageByLink(user.getImage());
                                    if (imageModel != null && imageModel.getImageBitmap() != null)
                                        userImage.setImageBitmap(imageModel.getImageBitmap());
                                    else
                                        new DownloadImage(activity, userImage, imageProgressIndicator).execute(user.getImage());

                                    //Older Version --new DownloadImage(userImage, imageProgressIndicator).execute(user.getImage());
                                }
                                userName.setText(user.getName());
                                userAltMobile.setText(user.getAlt_mobile());
                                userEmail.setText(user.getEmail());
                                userAddress.setText(user.getAddress());
                                userMobileNo.setText(user.getMobile());
                                gender.setText(user.getGender(), false);


                            }

                        }
                    })
                    .addOnCompleteListener(task -> loadingDialog.dismiss());
        }

    }

    private void setProfileImageFromUri(Uri uri) {
        imageProgressIndicator.setVisibility(View.VISIBLE);
        Bitmap selectedImageBitmap;
        try {
            selectedImageBitmap
                    = MediaStore.Images.Media.getBitmap(activity.getContentResolver(),uri);
            if (selectedImageBitmap != null)
                uploadFireBaseImage(selectedImageBitmap, new OnImageUploadListener() {
                    @Override
                    public void onSuccess() {
                        imageProgressIndicator.setVisibility(GONE);
                        updateProfile.setActivated(true);
                        userImage.setImageBitmap(selectedImageBitmap);
                        user.setImageBitmap(selectedImageBitmap);
                        deleteImageFromFirebaseAndLocalDB();
                        try {
                            //delete from mobile
                            deleteImageFromLocalStorage(uri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onStart() {
                        imageProgressIndicator.setVisibility(View.VISIBLE);
                        updateProfile.setActivated(false);
                    }

                    @Override
                    public void onFailure() {
                        updateProfile.setActivated(true);
                        imageProgressIndicator.setVisibility(GONE);
                        Toast.makeText(activity, "image uploading failed", Toast.LENGTH_SHORT).show();
                    }
                });

            else new TestToast(activity,"Bitmap Empty");

        }
        catch (IOException e) {
            e.printStackTrace();
            new TestToast(activity,"IO Exception");
        }
    }

    private void uploadFireBaseImage(Bitmap bitmap, OnImageUploadListener listener) {
        listener.onStart();
        StorageReference imagesRef = storage.getReference().child("UserImages/" + sharedPrefs.getSharedUID() + ".png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return imagesRef.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        sharedPrefs.setSharedImage(downloadUri.toString());
                        user.setImage(downloadUri.toString());
                        new SaveImageByteToDatabase(requireActivity()).execute(downloadUri.toString());
                        listener.onSuccess();
                    } else {
                        // Handle failures
                        // ...
                        listener.onFailure();
                    }
                });
    }


    private void deleteImageFromLocalStorage(Uri imageUri) {
        try {
            File fdelete = new File(getFilePath(imageUri));

            if (fdelete != null && fdelete.exists()) {
                if (fdelete.delete()) {
                    System.out.println("file Deleted :");
                } else {
                    System.out.println("file not Deleted :");
                }
            }
        } catch (Exception ignored) {

        }
    }

    private String getFilePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = requireActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(projection[0]);
            String picturePath = cursor.getString(columnIndex); // returns null
            cursor.close();
            return picturePath;
        }
        return null;
    }

    private void showImagePicDialog() {
        Intent imagePicker = new Intent(activity, ImagePickerActivity.class);
        imagePicker.putExtra(RATIO_X, 1f);
        imagePicker.putExtra(RATIO_Y, 1f);
        imagePicker.putExtra(RESULT_WIDTH, 200);
        imagePicker.putExtra(RESULT_HEIGHT, 200);
        startActivityForResult(imagePicker, IMAGE_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                setProfileImageFromUri(uri);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(activity, "Operation Failed !!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteImageFromFirebaseAndLocalDB() {
        if (!Objects.equals(oldImage, user.getImage())) {
            imageManager.deleteByLink(oldImage);
            try {
                FirebaseStorage.getInstance().getReferenceFromUrl(oldImage).delete();
            } catch (Exception ignored) {

            }

        }
    }


}
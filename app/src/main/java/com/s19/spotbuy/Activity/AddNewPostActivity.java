package com.s19.spotbuy.Activity;

import static com.s19.spotbuy.Activity.ImagePickerActivity.IMAGE_REQUEST_CODE;
import static com.s19.spotbuy.Activity.ImagePickerActivity.RATIO_X;
import static com.s19.spotbuy.Activity.ImagePickerActivity.RATIO_Y;
import static com.s19.spotbuy.Activity.ImagePickerActivity.RESULT_HEIGHT;
import static com.s19.spotbuy.Activity.ImagePickerActivity.RESULT_WIDTH;
import static com.s19.spotbuy.Others.Constants.UserCollection;
import static com.s19.spotbuy.Others.Constants.VehiclePostCollection;
import static com.s19.spotbuy.Others.Constants.getAllTransmissionModes;
import static com.s19.spotbuy.Others.Constants.getNumberOfOwners;
import static com.s19.spotbuy.Others.Constants.getYearList;
import static com.s19.spotbuy.Others.Utils.deleteImageFromFirebase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.s19.spotbuy.Adapters.VehicleImageListAdapter;
import com.s19.spotbuy.DataBase.SharedPrefs;
import com.s19.spotbuy.DataBase.UserManager;
import com.s19.spotbuy.DataBase.VehicleDetails.FuelTypeManager;
import com.s19.spotbuy.DataBase.VehicleDetails.VehicleBrandManager;
import com.s19.spotbuy.DataBase.VehicleDetails.VehicleBrandModelManager;
import com.s19.spotbuy.DataBase.VehicleDetails.VehicleCategoryManager;
import com.s19.spotbuy.DataBase.VehiclePostManager;
import com.s19.spotbuy.Dialogs.LoadingDialog;
import com.s19.spotbuy.MainActivity;
import com.s19.spotbuy.Models.FuelType;
import com.s19.spotbuy.Models.User;
import com.s19.spotbuy.Models.VehicleBrand;
import com.s19.spotbuy.Models.VehicleBrandModel;
import com.s19.spotbuy.Models.VehiclePost;
import com.s19.spotbuy.Others.OnImageUploadListener;
import com.s19.spotbuy.Others.STATUS;
import com.s19.spotbuy.Others.SaveImageByteToDatabase;
import com.s19.spotbuy.Others.Utils;
import com.s19.spotbuy.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddNewPostActivity extends MainActivity implements View.OnClickListener {
    ImageView back;

    List<String> imageList = new ArrayList<>(); //DownloadAbe Links
    ProgressBar imageProgressIndicator;
    AutoCompleteTextView vehicleCategoryList,
            vehicleBrandList,
            vehicleBrandModelList,
            fuelTypeList,
            yearModelList,
            ownerNumberList,
            transmissionModeList,
            countryList,
            stateList,
            cityList;
    Button saveButton;

    TextInputEditText KMSRidden,
            description,
            price;


    //Local String  List
    List<String> categoryList = new ArrayList<>();
    List<String> fuelList = new ArrayList<>();


    //DB
    VehicleCategoryManager categoryManager;
    VehicleBrandManager brandManager;
    VehicleBrandModelManager brandModelManager;
    FuelTypeManager fuelTypeManager;
    VehiclePostManager vehiclePostManager;
    UserManager userManager;
    User user;
    SharedPrefs sharedPrefs;

    VehiclePost currentVehicle;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();


    Utils utils;//For json Data Sates and cities
    LoadingDialog loadingDialog;

    RecyclerView croppedImageList;
    Button addImage;
    ImageView TestImage;
    VehicleImageListAdapter ImageAdapter;

    //Ads
    private RewardedAd rewardedAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);

        //Ads
        MobileAds.initialize(this);
        loadRewardedVideoAd();

        currentVehicle = new VehiclePost();
        sharedPrefs = new SharedPrefs(this);
        categoryManager = new VehicleCategoryManager(this);
        categoryList = categoryManager.getStringList();
        if (!categoryList.isEmpty())
            categoryList.remove(0);


        brandManager = new VehicleBrandManager(this);
        brandModelManager = new VehicleBrandModelManager(this);
        vehiclePostManager = new VehiclePostManager(this);
        userManager = new UserManager(this);
        user = userManager.getUserById(sharedPrefs.getSharedUID());

        fuelTypeManager = new FuelTypeManager(this);
        fuelList = fuelTypeManager.getStringList();


        utils = new Utils(this);
        loadingDialog = new LoadingDialog(this);
        ImageAdapter = new VehicleImageListAdapter(imageList, this, this);
        initView();
    }

    private void initView() {
        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        croppedImageList = findViewById(R.id.croppedImageList);
        croppedImageList.setLayoutManager(new GridLayoutManager(this, 2));
        croppedImageList.setAdapter(ImageAdapter);
        addImage = findViewById(R.id.addImage);
        addImage.setVisibility(View.GONE);
        addImage.setOnClickListener(view -> showImagePicDialog());

        TestImage = findViewById(R.id.TestImage);
        TestImage.setOnClickListener(view -> showImagePicDialog());

        imageProgressIndicator = findViewById(R.id.imageProgressIndicator);


        vehicleCategoryList = findViewById(R.id.vehicleCategoryList);
        vehicleBrandList = findViewById(R.id.vehicleBrandList);
        vehicleBrandModelList = findViewById(R.id.vehicleBrandModelList);


        //Set Category Adapter
        ArrayAdapter<String> adapterCategory;
        adapterCategory = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, categoryList);
        vehicleCategoryList.setAdapter(adapterCategory);
        vehicleCategoryList.setOnItemClickListener((adapterView, view, i, l) -> {
            String CategoryId = categoryManager.listAll().get(i + 1).getId();
            currentVehicle.setCategoryId(CategoryId);
            unsetBrandAdapter();

            List<String> brandList = brandManager.getBrandByCategoryId(CategoryId);
            setBrandAdapter(brandList);

        });

        //Set Fuel Adapter Adapter
        fuelTypeList = findViewById(R.id.fuelTypeList);
        ArrayAdapter<String> adapterFuel;
        adapterFuel = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, fuelList);
        fuelTypeList.setAdapter(adapterFuel);
        fuelTypeList.setOnItemClickListener((adapterView, view, i, l) -> {
            FuelType tempFuel = fuelTypeManager.listAll().get(i);
            currentVehicle.setFuelId(tempFuel.getId());
            currentVehicle.setFuelType(tempFuel.getName());


        });

        //Set Year Model Adapter
        yearModelList = findViewById(R.id.yearModelList);
        ArrayAdapter<String> adapterYear;
        adapterYear = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getYearList());
        yearModelList.setAdapter(adapterYear);
        yearModelList.setOnItemClickListener((adapterView, view, i, l) -> currentVehicle.setModelYear(Integer.parseInt(getYearList().get(i))));
        //Set owner number Adapter
        ownerNumberList = findViewById(R.id.ownerNumberList);
        ArrayAdapter<String> adapterOwner;
        adapterOwner = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getNumberOfOwners());
        ownerNumberList.setAdapter(adapterOwner);
        ownerNumberList.setOnItemClickListener((adapterView, view, i, l) -> currentVehicle.setNumberOfOwner(Integer.parseInt(getNumberOfOwners().get(i))));

        //Set owner number Adapter
        transmissionModeList = findViewById(R.id.transmissionModeList);
        ArrayAdapter<String> adapterTransmissionMode;
        adapterTransmissionMode = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getAllTransmissionModes());
        transmissionModeList.setAdapter(adapterTransmissionMode);
        transmissionModeList.setOnItemClickListener((adapterView, view, i, l) -> currentVehicle.setTransmissionMode(getAllTransmissionModes().get(i)));

        KMSRidden = findViewById(R.id.KMSRidden);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);


        countryList = findViewById(R.id.countryList);
        stateList = findViewById(R.id.stateList);
        cityList = findViewById(R.id.cityList);
        //Country adapter
        ArrayAdapter<String> adapterCountry = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, utils.getCountrylist());
        countryList.setAdapter(adapterCountry);
        countryList.setOnItemClickListener((adapterView, view, i, l) -> currentVehicle.setCountry("India"));

        //State

        ArrayAdapter<String> adapterSatet = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, utils.getStateList());
        stateList.setAdapter(adapterSatet);
        stateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentVehicle.setState(utils.getStateList().get(i));
                setCityAdapter(utils.getStateList().get(i));
            }
        });

        saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(this);


    }

    private void setBrandAdapter(List<String> brandList) {
        ArrayAdapter<String> adapterBrand = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, brandList);
        vehicleBrandList.setAdapter(adapterBrand);
        vehicleBrandList.setOnItemClickListener((adapterView, view, i, l) -> {
            VehicleBrand Brand = brandManager.listAllbyCategoryId(currentVehicle.getCategoryId()).get(i);
            currentVehicle.setBrandId(Brand.getId());
            currentVehicle.setTitle(Brand.getName());
            unsetBrandModelAdapter();

            List<String> brandModelList = brandModelManager.getBrandModelByBrandId(Brand.getId());

            setBrandModelAdapter(brandModelList);

        });

    }

    private void unsetBrandAdapter() {
        vehicleBrandList.setText("");
        vehicleBrandList.setAdapter(null);
        unsetBrandModelAdapter();

    }

    private void setBrandModelAdapter(List<String> brandModelList) {
        ArrayAdapter<String> adapterBrandModel;
        adapterBrandModel = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, brandModelList);
        vehicleBrandModelList.setAdapter(adapterBrandModel);
        vehicleBrandModelList.setOnItemClickListener((adapterView, view, i, l) -> {
            VehicleBrandModel model = brandModelManager.listAllbyBrandId(currentVehicle.getBrandId()).get(i);
            currentVehicle.setModelId(model.getId());
            currentVehicle.setTitle(currentVehicle.getTitle() + " " + model.getName());
        });
    }

    private void unsetBrandModelAdapter() {
        vehicleBrandModelList.setText("");
        vehicleBrandModelList.setAdapter(null);

    }

    private void setCityAdapter(String state) {
        ArrayAdapter<String> adapterCity;
        adapterCity = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, utils.getCityList(state));
        cityList.setAdapter(adapterCity);
        cityList.setOnItemClickListener((adapterView, view, i, l) -> currentVehicle.setCity(utils.getCityList(state).get(i)));
    }

    @Override
    public void onClick(View view) {
        if (view == back) {
            onBackPressed();
        }


        if (view == saveButton) {
            loadingDialog.show();
            getEnteredData();
            if (validateEnteredData()) {
                db.collection(VehiclePostCollection)
                        .add(currentVehicle)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.update("id", documentReference.getId());

                                //Update Local Data Base
                                currentVehicle.setId(documentReference.getId());
                                vehiclePostManager.insert(currentVehicle);
                                user.setAvailablePost(user.getAvailablePost() - 1);
                                user.setTotalPost(user.getTotalPost() + 1);
                                userManager.update(user);
                                for (String url : currentVehicle.getImageList())
                                    new SaveImageByteToDatabase(AddNewPostActivity.this).execute(url);

                                db.collection(UserCollection)
                                        .document(sharedPrefs.getSharedUID())
                                        .update("availablePost", FieldValue.increment(-1), "totalPost", FieldValue.increment(1));

                                showRewardedVideoAd();
                                showSuccessDialog();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddNewPostActivity.this, "Operation Failed,Please try again!", Toast.LENGTH_LONG).show();
                                showFailureDialog();
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {

                            }
                        });


            } else {
                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        }
    }

    private void getEnteredData() {

        try {
            currentVehicle.setKmsRidden(Integer.parseInt(KMSRidden.getText().toString()));
            currentVehicle.setDescription(description.getText().toString().trim());
            currentVehicle.setPrice(Float.parseFloat(price.getText().toString().trim()));

            currentVehicle.setActive(false);
            Calendar cal = Calendar.getInstance();
            currentVehicle.setDateTime(cal.getTime());
            currentVehicle.setPostTimeStart(cal.getTime());
            cal.add(Calendar.MONTH, 12); //TODO :- post Expires in 12 months
            currentVehicle.setPostTimeEnd(cal.getTime());
            currentVehicle.setImageList(imageList);
            currentVehicle.setStatus(String.valueOf(STATUS.PENDING));
            currentVehicle.setSellerId(sharedPrefs.getSharedUID());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    private boolean validateEnteredData() {
        boolean isValid = true;

        if (imageList.isEmpty()) {
            isValid = false;
            TestImage.requestFocus();
            Toast.makeText(this, "Please select at least image of your vehicle.", Toast.LENGTH_LONG).show();
        }

        if (currentVehicle.getCategoryId() == null || currentVehicle.getCategoryId().isEmpty() || currentVehicle.getCategoryId() == "") {
            isValid = false;
            vehicleCategoryList.setError("Empty");
            vehicleCategoryList.requestFocus();
        }

        if (currentVehicle.getBrandId() == null || currentVehicle.getBrandId().isEmpty() || currentVehicle.getBrandId() == "") {
            isValid = false;
            vehicleBrandList.setError("Empty");
            vehicleBrandList.requestFocus();
        }

        if (currentVehicle.getModelId() == null || currentVehicle.getModelId().isEmpty() || currentVehicle.getModelId() == "") {
            isValid = false;
            vehicleBrandModelList.setError("Empty");
            vehicleBrandModelList.requestFocus();
        }

        if (currentVehicle.getFuelType() == null || currentVehicle.getFuelType().isEmpty() || currentVehicle.getFuelType() == "") {
            isValid = false;
            fuelTypeList.setError("Empty");
            fuelTypeList.requestFocus();
        }

        if (currentVehicle.getModelYear() < 1990) {
            isValid = false;
            yearModelList.setError("Empty");
            yearModelList.requestFocus();
        }

        if (currentVehicle.getNumberOfOwner() < 1 || currentVehicle.getNumberOfOwner() > 10) {
            isValid = false;
            ownerNumberList.setError("Empty");
            ownerNumberList.requestFocus();
        }

        if (currentVehicle.getTransmissionMode() == null || currentVehicle.getTransmissionMode().isEmpty() || currentVehicle.getTransmissionMode() == "") {
            isValid = false;
            transmissionModeList.setError("Empty");
            transmissionModeList.requestFocus();
        }

        if (currentVehicle.getKmsRidden() < 1 || currentVehicle.getKmsRidden() > 100000000) {
            isValid = false;
            KMSRidden.setError("Invalid");
            KMSRidden.requestFocus();
        }

        if (currentVehicle.getDescription() == null || currentVehicle.getDescription().isEmpty() || currentVehicle.getDescription() == "") {
            isValid = false;
            description.setError("Empty");
            description.requestFocus();
        }

        if (currentVehicle.getPrice() < 1 || currentVehicle.getPrice() > 100000000) {
            isValid = false;
            price.setError("Invalid");
            price.requestFocus();
        }

        if (currentVehicle.getCountry() == null || currentVehicle.getCountry().isEmpty() || currentVehicle.getCountry() == "") {
            isValid = false;
            countryList.setError("Empty");
            countryList.requestFocus();
        }
        if (currentVehicle.getState() == null || currentVehicle.getState().isEmpty() || currentVehicle.getState() == "") {
            isValid = false;
            stateList.setError("Empty");
            stateList.requestFocus();
        }
        if (currentVehicle.getCity() == null || currentVehicle.getCity().isEmpty() || currentVehicle.getCity() == "") {
            isValid = false;
            cityList.setError("Empty");
            cityList.requestFocus();
        }


        return isValid;
    }

    public void setTestImageVisibility() {
        if (imageList.isEmpty()) {
            TestImage.setVisibility(View.VISIBLE);
            addImage.setVisibility(View.GONE);
        } else {
            TestImage.setVisibility(View.GONE);
            addImage.setVisibility(View.VISIBLE);
        }

        if (imageList.size() == 0) {
            TestImage.setVisibility(View.VISIBLE);
            addImage.setVisibility(View.GONE);
        } else {
            TestImage.setVisibility(View.GONE);
            addImage.setVisibility(View.VISIBLE);
        }


    }

    private void setVehicleImageFromUri(Uri uri) {
        imageProgressIndicator.setVisibility(View.VISIBLE);
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            uploadFireBaseImage(bmp, new OnImageUploadListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onSuccess() {
                    ImageAdapter.notifyDataSetChanged();
                    ImageAdapter.notifyItemInserted(imageList.size() - 1);
                    setTestImageVisibility();
                    imageProgressIndicator.setVisibility(View.GONE);

                }

                @Override
                public void onStart() {
                    imageProgressIndicator.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure() {
                    imageProgressIndicator.setVisibility(View.GONE);
                    Toast.makeText(AddNewPostActivity.this, "Failed to add image!!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            imageProgressIndicator.setVisibility(View.GONE);

        }
    }

    private void uploadFireBaseImage(Bitmap bitmap, OnImageUploadListener listener) {
        listener.onStart();
        StorageReference imagesRef = storage.getReference().child("VehicleImages/" + sharedPrefs.getSharedUID() + "/" + System.currentTimeMillis() + ".png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return imagesRef.getDownloadUrl();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            imageList.add(String.valueOf(downloadUri));
//                            // Set Local Image
//                            ImageModel img =new ImageModel();
//                            img.setLink(String.valueOf(downloadUri));
//                            img.setImageBitmap(bitmap);
//                            new ImageManager(AddNewPostActivity.this).insert(img);
                            listener.onSuccess();
                        } else {
                            // Handle failures
                            // ...
                            listener.onFailure();
                        }
                    }
                });
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Success")
                .setMessage("Your post has been placed successfully. Let our team examine your post and approve it.\n\n\nUsually it take 24 hrs. but due to heavy traffic it may take 7 days. \n\n Thanking you!!")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        loadingDialog.dismiss();
                        finish();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        dialogInterface.dismiss();
                        loadingDialog.dismiss();
                        finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showFailureDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Failure!!")
                .setMessage("Sorry your post failed to registered. \n\nPlease try again...")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.setCancelable(false);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void deleteImage(String imageUrl, int position) {
        imageList.remove(position);
        deleteImageFromFirebase(imageUrl);
        ImageAdapter.notifyItemRemoved(position);
        setTestImageVisibility();


    }

    //Setting Croped Image

    private void showImagePicDialog() {
        Intent imagePicker = new Intent(AddNewPostActivity.this, ImagePickerActivity.class);
        imagePicker.putExtra(RATIO_X, 4f);
        imagePicker.putExtra(RATIO_Y, 3f);
        imagePicker.putExtra(RESULT_WIDTH, 1050);
        imagePicker.putExtra(RESULT_HEIGHT, 850);
        startActivityForResult(imagePicker, IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                setVehicleImageFromUri(uri);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Operation Failed !!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Ads
    void loadRewardedVideoAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, this.getString(R.string.rewarded_1), adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                rewardedAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                super.onAdLoaded(ad);
                rewardedAd = ad;
                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        // Called when a click is recorded for an ad.

                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdImpression() {
                        // Called when an impression is recorded for an ad.

                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        loadRewardedVideoAd();
                    }
                });

            }
        });


    }

    void showRewardedVideoAd() {
        if (rewardedAd != null) {

            rewardedAd.show(this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    // Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                }
            });
        } else {
            // Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }
    }

}

package com.s19mobility.spotbuy.Activity;

import static com.s19mobility.spotbuy.Others.Constants.UserCollection;
import static com.s19mobility.spotbuy.Others.Constants.VehiclePostCollection;

import android.content.ClipData;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

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
import com.s19mobility.spotbuy.Adapters.ImageListAdapter;
import com.s19mobility.spotbuy.DataBase.SharedPrefs;
import com.s19mobility.spotbuy.DataBase.VehicleDetails.FuelTypeManager;
import com.s19mobility.spotbuy.DataBase.VehicleDetails.VehicleBrandManager;
import com.s19mobility.spotbuy.DataBase.VehicleDetails.VehicleBrandModelManager;
import com.s19mobility.spotbuy.DataBase.VehicleDetails.VehicleCategoryManager;
import com.s19mobility.spotbuy.Models.FuelType;
import com.s19mobility.spotbuy.Models.VehicleBrand;
import com.s19mobility.spotbuy.Models.VehicleBrandModel;
import com.s19mobility.spotbuy.Models.VehiclePost;
import com.s19mobility.spotbuy.Others.LoadingDialog;
import com.s19mobility.spotbuy.Others.OnImageUploadListener;
import com.s19mobility.spotbuy.Others.STATUS;
import com.s19mobility.spotbuy.Others.Utils;
import com.s19mobility.spotbuy.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNewPostActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView back;

    ImageView sampleImage;
    ViewPager vehicleImages;
    List<String> imageList = new ArrayList<String>();
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
    SharedPrefs sharedPrefs;

    VehiclePost currentVehicle;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    int PICK_IMAGE_MULTIPLE = 1;
    ArrayList<Uri> mArrayUri = new ArrayList<>();
    Utils utils;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);
        currentVehicle = new VehiclePost();

        categoryManager = new VehicleCategoryManager(this);
        categoryList = categoryManager.getStringList();

        brandManager = new VehicleBrandManager(this);
        brandModelManager = new VehicleBrandModelManager(this);

        fuelTypeManager = new FuelTypeManager(this);
        fuelList = fuelTypeManager.getStringList();

        sharedPrefs = new SharedPrefs(this);
        utils = new Utils(this);
        loadingDialog = new LoadingDialog(this);
        initView();
    }

    private void initView() {
        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        sampleImage = findViewById(R.id.sampleImage);
        sampleImage.setOnClickListener(this);

        vehicleImages = findViewById(R.id.vehicleImages);
        vehicleImages.setOnClickListener(this);   // showing all images in imageswitcher

        imageProgressIndicator = findViewById(R.id.imageProgressIndicator);

        vehicleCategoryList = findViewById(R.id.vehicleCategoryList);
        vehicleBrandList = findViewById(R.id.vehicleBrandList);
        vehicleBrandModelList = findViewById(R.id.vehicleBrandModelList);


        //Set Category Adapter
        ArrayAdapter<String> adapterCategory;
        adapterCategory = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, categoryList);
        vehicleCategoryList.setAdapter(adapterCategory);
        vehicleCategoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String CategoryId = categoryManager.listAll().get(i).getId();
                currentVehicle.setCategoryId(CategoryId);
                List<String> brandList = brandManager.getBrandByCategoryId(CategoryId);
                setBrandAdapter(brandList);

            }
        });

        //Set Fuel Adapter Adapter
        fuelTypeList = findViewById(R.id.fuelTypeList);
        ArrayAdapter<String> adapterFuel;
        adapterFuel = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, fuelList);
        fuelTypeList.setAdapter(adapterFuel);
        fuelTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FuelType tempFuel = fuelTypeManager.listAll().get(i);
                currentVehicle.setFuelId(tempFuel.getId());
                currentVehicle.setFuelType(tempFuel.getName());


            }
        });

        //Set Year Model Adapter
        yearModelList = findViewById(R.id.yearModelList);
        ArrayAdapter<String> adapterYear;
        adapterYear = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getYearList());
        yearModelList.setAdapter(adapterYear);
        yearModelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentVehicle.setModelYear(Integer.parseInt(getYearList().get(i)));
            }
        });
        //Set owner number Adapter
        ownerNumberList = findViewById(R.id.ownerNumberList);
        ArrayAdapter<String> adapterOwner;
        adapterOwner = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getNumberOfOwners());
        ownerNumberList.setAdapter(adapterOwner);
        ownerNumberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentVehicle.setNumberOfOwner(Integer.parseInt(getNumberOfOwners().get(i)));
            }
        });

        //Set owner number Adapter
        transmissionModeList = findViewById(R.id.transmissionModeList);
        ArrayAdapter<String> adapterTransmissionMode;
        adapterTransmissionMode = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getAllTransmissionModes());
        transmissionModeList.setAdapter(adapterTransmissionMode);
        transmissionModeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentVehicle.setTransmissionMode(getAllTransmissionModes().get(i));
            }
        });

        KMSRidden = findViewById(R.id.KMSRidden);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);


        countryList = findViewById(R.id.countryList);
        stateList = findViewById(R.id.stateList);
        cityList = findViewById(R.id.cityList);
        //Country adapter
        ArrayAdapter adapterCountry = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, utils.getCountrylist());
        countryList.setAdapter(adapterCountry);
        countryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentVehicle.setCountry("India");
            }
        });

        //State

        ArrayAdapter adapterSatet = new ArrayAdapter<String>(this,
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
        ArrayAdapter adapterBrand = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, brandList);
        vehicleBrandList.setAdapter(adapterBrand);
        vehicleBrandList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VehicleBrand Brand = brandManager.listAllbyCategoryId(currentVehicle.getCategoryId()).get(i);
                currentVehicle.setBrandId(Brand.getId());
                currentVehicle.setTitle("" + Brand.getName());
                List<String> brandModelList = brandModelManager.getBrandModelByBrandId(Brand.getId());
                setBrandModelAdapter(brandModelList);
            }
        });

    }

    private void setBrandModelAdapter(List<String> brandModelList) {
        ArrayAdapter<String> adapterBrandModel;
        adapterBrandModel = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, brandModelList);
        vehicleBrandModelList.setAdapter(adapterBrandModel);
        vehicleBrandModelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VehicleBrandModel model = brandModelManager.listAllbyBrandId(currentVehicle.getBrandId()).get(i);
                currentVehicle.setModelId(model.getId());
                currentVehicle.setTitle(currentVehicle.getTitle() + " " + model.getName());
            }
        });
    }

    private void setCityAdapter(String state) {
        ArrayAdapter<String> adapterCity;
        adapterCity = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, utils.getCityList(state));
        cityList.setAdapter(adapterCity);
        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentVehicle.setCity(utils.getCityList(state).get(i));
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == back) {
            finish();
        }

        if (view == sampleImage || view == vehicleImages) {
            sampleImage.setVisibility(View.GONE);
            imageProgressIndicator.setVisibility(View.VISIBLE);

            Intent intent = new Intent();

            // setting type to select to be image
            intent.setType("image/*");

            // allowing multiple image to be selected
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Pictures ( upto 5"), PICK_IMAGE_MULTIPLE);

        }

        if (view == saveButton) {
            if (validateEnteredData()) {
                loadingDialog.show();
                getEnteredData();
                db.collection(VehiclePostCollection)
                        .add(currentVehicle)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.update("id", documentReference.getId());

                                db.collection(UserCollection)
                                        .document(sharedPrefs.getSharedID())
                                        .update("availablePost",FieldValue.increment(-1),"totalPost",FieldValue.increment(1));

                                Toast.makeText(AddNewPostActivity.this, "Post Added!", Toast.LENGTH_SHORT).show();
                                onBackPressed();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddNewPostActivity.this, "Operation Failed,Please try again!", Toast.LENGTH_LONG).show();

                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {

                            }
                        });


            }
        }
    }

    private void getEnteredData() {
        currentVehicle.setKmsRidden(Integer.parseInt(KMSRidden.getText().toString()));
        currentVehicle.setDescription(description.getText().toString().trim());
        currentVehicle.setPrice(Float.parseFloat(price.getText().toString().trim()));

        currentVehicle.setActive(false);
        Calendar cal = Calendar.getInstance();
        currentVehicle.setDateTime(cal.getTime());
        currentVehicle.setPostTimeStart(cal.getTime());
        cal.add(Calendar.MONTH, 12);
        currentVehicle.setPostTimeEnd(cal.getTime());
        currentVehicle.setImageList(imageList);
        currentVehicle.setStatus(String.valueOf(STATUS.PENDING));
        currentVehicle.setSellerId(sharedPrefs.getSharedID());


    }

    private boolean validateEnteredData() {
        //TODO
        return true;
    }

    private List<String> getYearList() {
        Calendar cal = Calendar.getInstance();
        int currentyear = cal.get(Calendar.YEAR);
        List<String> years = new ArrayList<>();

        for (int i = currentyear; i >= 1990; i--) {
            years.add(String.valueOf(i));
        }

        return years;

    }

    private List<String> getNumberOfOwners() {
        List<String> owners = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            owners.add(String.valueOf(i));
        }

        return owners;

    }

    private List<String> getAllTransmissionModes() {
        List<String> modes = new ArrayList<String>();
        modes.add("Manual");
        modes.add("Automatic");
        modes.add("Semi-automatic");
        modes.add("Continuously Variable");
        modes.add("Dual Clutch");
        return modes;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
            // Get the Image from data
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int cout = data.getClipData().getItemCount();
                for (int i = 0; i < cout; i++) {
                    // adding imageuri in array
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
                    mArrayUri.add(imageurl);
                }

            } else {
                Uri imageurl = data.getData();
                mArrayUri.add(imageurl);

            }

            vehicleImages.setAdapter(new ImageListAdapter(this, mArrayUri));
            for (Uri uri : mArrayUri) {
                try {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    uploadFireBaseImage(bmp, new OnImageUploadListener() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            sampleImage.setVisibility(View.GONE);
            imageProgressIndicator.setVisibility(View.GONE);


        } else {
            // show this if no image is selected
            sampleImage.setVisibility(View.VISIBLE);
            imageProgressIndicator.setVisibility(View.GONE);
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }


    }


//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == MultiImagePicker.REQUEST_PICK_MULTI_IMAGES && resultCode == RESULT_OK) {
//            MultiImagePicker.Result result = new MultiImagePicker.Result(data);
//            if (result.isSuccess()) {
//                ArrayList<Uri> imageListInUri = result.getImageList(); // List os selected images as content Uri format
//
//                for (Uri uri : imageListInUri) {
//                    try {
//                        Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                        uploadFireBaseImage(bmp, new OnImageUploadListener() {
//                            @Override
//                            public void onSuccess() {
//
//                            }
//
//                            @Override
//                            public void onStart() {
//
//                            }
//
//                            @Override
//                            public void onFailure() {
//
//                            }
//                        });
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                vehicleImages.setAdapter(new ScrollingImageAdapter(this, imageList));
//                sampleImage.setVisibility(View.GONE);
//            } else {
//                Toast.makeText(this, "Error!! Try Again!!", Toast.LENGTH_LONG).show();
//                sampleImage.setVisibility(View.VISIBLE);
//            }
//
//
//            imageProgressIndicator.setVisibility(View.GONE);
//        }
//    }


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
                            listener.onSuccess();
                        } else {
                            // Handle failures
                            // ...
                            listener.onFailure();
                        }
                    }
                });
    }
}



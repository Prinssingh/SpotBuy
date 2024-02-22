package com.s19.spotbuy.Activity;

import static com.s19.spotbuy.Others.Constants.CAMERA_ACTION_PICK_REQUEST_CODE;
import static com.s19.spotbuy.Others.Constants.CAMERA_REQUEST;
import static com.s19.spotbuy.Others.Constants.PICK_IMAGE_GALLERY_REQUEST_CODE;
import static com.s19.spotbuy.Others.Constants.STORAGE_REQUEST;
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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.s19.spotbuy.Adapters.VehicleImageListAdapter;
import com.s19.spotbuy.DataBase.ImageManager;
import com.s19.spotbuy.DataBase.SharedPrefs;
import com.s19.spotbuy.DataBase.VehicleDetails.FuelTypeManager;
import com.s19.spotbuy.DataBase.VehicleDetails.VehicleBrandManager;
import com.s19.spotbuy.DataBase.VehicleDetails.VehicleBrandModelManager;
import com.s19.spotbuy.DataBase.VehicleDetails.VehicleCategoryManager;
import com.s19.spotbuy.DataBase.VehiclePostManager;
import com.s19.spotbuy.MainActivity;
import com.s19.spotbuy.Models.FuelType;
import com.s19.spotbuy.Models.VehicleBrand;
import com.s19.spotbuy.Models.VehicleBrandModel;
import com.s19.spotbuy.Models.VehiclePost;
import com.s19.spotbuy.Dialogs.LoadingDialog;
import com.s19.spotbuy.Others.OnImageUploadListener;
import com.s19.spotbuy.Others.STATUS;
import com.s19.spotbuy.Others.SaveImageByteToDatabase;
import com.s19.spotbuy.Others.Utils;
import com.s19.spotbuy.R;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class UpdatePostActivity extends MainActivity implements View.OnClickListener {
    VehiclePost currentVehicle;
    ImageView back;

    List<String> imageList = new ArrayList<>();
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
    VehiclePostManager vehiclePostManager;
    ImageManager imageManager;
    FuelTypeManager fuelTypeManager;
    SharedPrefs sharedPrefs;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Utils utils;
    LoadingDialog loadingDialog;

    RecyclerView croppedImageList;
    Button addImage;
    ImageView TestImage;
    VehicleImageListAdapter ImageAdapter;
    String currentPhotoPath = "";
    String[] cameraPermission;
    String[] storagePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_post);
        currentVehicle = (VehiclePost) getIntent().getSerializableExtra("vehicle");
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,  Manifest.permission.READ_EXTERNAL_STORAGE};

        categoryManager = new VehicleCategoryManager(this);
        categoryList = categoryManager.getStringList();

        brandManager = new VehicleBrandManager(this);
        brandModelManager = new VehicleBrandModelManager(this);

        fuelTypeManager = new FuelTypeManager(this);
        fuelList = fuelTypeManager.getStringList();

        vehiclePostManager = new VehiclePostManager(this);
        imageManager = new ImageManager(this);

        sharedPrefs = new SharedPrefs(this);
        utils = new Utils(this);
        loadingDialog = new LoadingDialog(this);

        initView();
        setVehicleData();
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
            String CategoryId = categoryManager.listAll().get(i).getId();
            currentVehicle.setCategoryId(CategoryId);
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
        ArrayAdapter adapterCountry = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, utils.getCountrylist());
        countryList.setAdapter(adapterCountry);
        countryList.setOnItemClickListener((adapterView, view, i, l) -> currentVehicle.setCountry("India"));

        //State

        ArrayAdapter adapterSatet = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, utils.getStateList());
        stateList.setAdapter(adapterSatet);
        stateList.setOnItemClickListener((adapterView, view, i, l) -> {
            currentVehicle.setState(utils.getStateList().get(i));
            setCityAdapter(utils.getStateList().get(i));
        });

        saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(this);


    }

    private void setBrandAdapter(List<String> brandList) {
        ArrayAdapter adapterBrand = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, brandList);
        vehicleBrandList.setAdapter(adapterBrand);
        vehicleBrandList.setOnItemClickListener((adapterView, view, i, l) -> {
            VehicleBrand Brand = brandManager.listAllbyCategoryId(currentVehicle.getCategoryId()).get(i);
            currentVehicle.setBrandId(Brand.getId());
            currentVehicle.setTitle("" + Brand.getName());
            List<String> brandModelList = brandModelManager.getBrandModelByBrandId(Brand.getId());
            setBrandModelAdapter(brandModelList);
        });

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
            finish();
        }

        if (view == saveButton) {
            if (validateEnteredData()) {
                loadingDialog.show();
                getEnteredData();
                db.collection(VehiclePostCollection)
                        .document(currentVehicle.getId())
                        .update(currentVehicle.toUpdateMap())
                        .addOnSuccessListener(unused -> {
                            //update local
                            vehiclePostManager.update(currentVehicle);
                            showDialog();
                        })
                        .addOnCompleteListener(task -> loadingDialog.dismiss());


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
            cal.add(Calendar.MONTH, 12);
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
    
    private void uploadFireBaseImage(Bitmap bitmap, OnImageUploadListener listener) {
        listener.onStart();
        StorageReference imagesRef = storage.getReference().child("VehicleImages/" + sharedPrefs.getSharedUID() + "/" + System.currentTimeMillis() + ".png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    // Continue with the task to get the download URL
                    return imagesRef.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        imageList.add(String.valueOf(downloadUri));
                        //Save local Image
                        new SaveImageByteToDatabase(this).execute(String.valueOf(downloadUri));
                        listener.onSuccess();
                    } else {
                        // Handle failures
                        // ...
                        listener.onFailure();
                    }
                });
    }

    public void setTestImageVisibility(){
        if(imageList.isEmpty()) {
            TestImage.setVisibility(View.VISIBLE);
            addImage.setVisibility(View.GONE);
        }
        else {
            TestImage.setVisibility(View.GONE);
            addImage.setVisibility(View.VISIBLE);
        }

        if(imageList.size()==0) {
            TestImage.setVisibility(View.VISIBLE);
            addImage.setVisibility(View.GONE);
        }
        else {
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
                    Toast.makeText(UpdatePostActivity.this, "Failed to add image!!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (IOException e) {
            e.printStackTrace();
            imageProgressIndicator.setVisibility(View.GONE);

        }
        finally {
            imageProgressIndicator.setVisibility(View.GONE);
        }
    }


    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void setVehicleData() {
        imageProgressIndicator.setVisibility(View.VISIBLE);

        imageList = currentVehicle.getImageList();
        ImageAdapter = new VehicleImageListAdapter(imageList, this, this);
        croppedImageList.setAdapter(ImageAdapter);
        setTestImageVisibility();
        ImageAdapter.notifyDataSetChanged();
        ImageAdapter.notifyItemInserted(imageList.size() - 1);
        imageProgressIndicator.setVisibility(View.GONE);

        vehicleCategoryList.setText(categoryManager.getCategoryByID(currentVehicle.getCategoryId()).getName(), false);
        vehicleBrandList.setText(brandManager.getBrandById(currentVehicle.getBrandId()).getName(), false);
        vehicleBrandModelList.setText(brandModelManager.getBrandModelById(currentVehicle.getModelId()).getName(), false);
        fuelTypeList.setText(currentVehicle.getFuelType(), false);
        yearModelList.setText("" + currentVehicle.getModelYear(), false);
        ownerNumberList.setText("" + currentVehicle.getNumberOfOwner(), false);
        transmissionModeList.setText("" + currentVehicle.getTransmissionMode(), false);
        KMSRidden.setText("" + currentVehicle.getKmsRidden());
        description.setText("" + currentVehicle.getDescription());
        price.setText("" + currentVehicle.getPrice());
        countryList.setText("" + currentVehicle.getCountry(), false);
        stateList.setText("" + currentVehicle.getState(), false);
        cityList.setText("" + currentVehicle.getCity(), false);


    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Success!!");
        builder.setMessage("your request to update post has been registered, it will be published after the review. It can take up to 24 hours.");
        builder.setCancelable(false);
        builder.setPositiveButton("ok", (DialogInterface.OnClickListener) (dialog, which) -> {
            finish();
        });


        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }


    public void deleteImage(String imageUrl, int position) {
        imageList.remove(position);
        deleteImageFromFirebase(imageUrl);
        ImageAdapter.notifyItemRemoved(position);
        setTestImageVisibility();
        //Delete local Image
        imageManager.deleteByLink(imageUrl);


    }


    private void showImagePicDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        openCamera();
                    }
                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        openImagesDocument();
                    }
                }
            }
        });
        builder.create().show();
    }

    // checking storage permissions
    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    // Requesting  gallery permission
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    // checking camera permissions
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // Requesting camera permission
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageaccepted) {
                        openCamera();
                    } else {
                        Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaccepted) {
                        openImagesDocument();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    private void openCamera() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getImageFile(); // 1
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) // 2
            uri = FileProvider.getUriForFile(this, "com.s19mobility.spotbuy.provider", file);
        else
            uri = Uri.fromFile(file); // 3
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // 4
        startActivityForResult(pictureIntent, CAMERA_ACTION_PICK_REQUEST_CODE);
    }

    private File getImageFile() {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        File file = null;
        try {
            file = File.createTempFile(
                    imageFileName, ".jpg", storageDir

            );
            currentPhotoPath = "file:" + file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.Options options = new UCrop.Options();
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(1050, 825)
                .withAspectRatio(12f, 8f)
                .withOptions(options)
                .start(this);
    }

    private void openImagesDocument() {
        Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pictureIntent.setType("image/*");  // 1
        pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);  // 2
        String[] mimeTypes = new String[]{"image/jpeg", "image/png"};  // 3
        pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(pictureIntent, "Select Picture"), PICK_IMAGE_GALLERY_REQUEST_CODE);  // 4
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_ACTION_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = Uri.parse(currentPhotoPath);
            openCropActivity(uri, uri);

        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            assert data != null;
            Uri uri = UCrop.getOutput(data);
            setVehicleImageFromUri(uri);

        } else if (requestCode == PICK_IMAGE_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri sourceUri = data.getData(); // 1
            File file = getImageFile(); // 2
            Uri destinationUri = Uri.fromFile(file);  // 3
            openCropActivity(sourceUri, destinationUri);  // 4

        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "Error" + UCrop.getError(data), Toast.LENGTH_SHORT).show();
            Log.d("TAG", "onActivityResult: " + UCrop.getError(data));
        } else Toast.makeText(this, "Error!!", Toast.LENGTH_SHORT).show();
    }

}
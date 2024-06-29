package com.s19.spotbuy.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.s19.spotbuy.BuildConfig;
import com.s19.spotbuy.Widgets.TestToast;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

public class ImagePickerActivity extends AppCompatActivity {
    public static final int IMAGE_REQUEST_CODE = 111;
    public static final int CAMERA_REQUEST = 100;
    public static final int STORAGE_REQUEST = 200;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_PICK = 2;

    public static final String RATIO_X = "ratio_x";
    public static final String RATIO_Y = "ratio_y";
    public static final String RESULT_WIDTH = "result_width";
    public static final String RESULT_HEIGHT = "result_height";


    private String[] cameraPermission;
    private String[] storagePermission;
    private Uri imageUri;

    private float x;
    private float y;
    private int width;
    private int height;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        x = intent.getFloatExtra(RATIO_X, 4f);
        y = intent.getFloatExtra(RATIO_Y, 3f);
        width = intent.getIntExtra(RESULT_WIDTH, 1050);
        height = intent.getIntExtra(RESULT_HEIGHT, 800);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        showImagePicDialog();

    }

    private void showImagePicDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    dispatchTakePictureIntent();

                }
            } else if (which == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    dispatchPickPictureIntent();

                }
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    // checking storage permissions
    private Boolean checkStoragePermission() {
        boolean b = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return b;
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
                        dispatchTakePictureIntent();
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
                        dispatchPickPictureIntent();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Handle error
            }
            if (photoFile != null) {

                imageUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void dispatchPickPictureIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    startCrop(imageUri);
                    break;
                case REQUEST_IMAGE_PICK:
                    imageUri = data.getData();
                    if (imageUri != null)
                        startCrop(imageUri);
                    else {
                        Intent temp = new Intent();
                        temp.setData(null);
                        setResult(RESULT_CANCELED, temp);
                        new TestToast(this, "Camera Image Failure");
                        finish();
                    }
                    break;
                case UCrop.REQUEST_CROP:
                    handleCropResult(data);
                    break;
                case UCrop.RESULT_ERROR:
                    handleCropError(data);
                    break;
            }
        }
    }

    private void startCrop(@NonNull Uri uri) {
        String destinationFileName = "CroppedImage_" + System.currentTimeMillis() + ".jpg";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(x, y);
        uCrop.withMaxResultSize(width, height);
        uCrop.start(this);
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            Intent data = new Intent();
            //data.putExtra("Bitmap",bitmap);
            data.setData(resultUri);
            setResult(RESULT_OK, data);
            finish();
        } else {
            // Handle error
            new TestToast(this, "Error getting resultUri" + result);
            Intent data = new Intent();
            data.setData(null);
            setResult(RESULT_CANCELED, data);
            finish();
        }
    }

    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            // Handle error
            new TestToast(this, "Error on Cropping Image" + cropError);
        }

        Intent data = new Intent();
        data.setData(null);
        setResult(RESULT_CANCELED, data);
        finish();
    }


}
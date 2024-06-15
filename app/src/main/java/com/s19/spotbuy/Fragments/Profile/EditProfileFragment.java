package com.s19.spotbuy.Fragments.Profile;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.s19.spotbuy.R;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;


public class EditProfileFragment extends Fragment implements View.OnClickListener {

    View Root;
    ImageButton editUserImage;
    ImageView userImage;
    ProgressBar imageProgressIndicator;
    Button updateProfile;
    EditText userName, userMobileNo, userAltMobile, userEmail, userAddress;
    LinearLayout linearBG;
    AutoCompleteTextView gender;
    Activity activity;
    LoadingDialog loadingDialog;
    SharedPrefs sharedPrefs;
    UserManager userManager;
    ImageManager imageManager;
    User user = new User();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String currentPhotoPath = "";
    String[] cameraPermission;
    String[] storagePermission;

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
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


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
        linearBG = Root.findViewById(R.id.linearBG);
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
            loadingDialog.show();
            getEnteredData();
            try {
                profileUpdate();
            } catch (Exception e) {
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
        if (userEmail.getText().toString().length() != 0 && !isValidEmail(userEmail.getText().toString().trim())) {
            isValid = false;
            userEmail.setError("Invalid");
        }

        if (userAltMobile.getText().toString().length() != 0 && userAltMobile.getText().toString().length() != 10) {
            isValid = false;
            userAltMobile.setError("Invalid");
        }


        return isValid;
    }

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void getEnteredData() {

        user.setId(sharedPrefs.getSharedUID());
        user.setName(userName.getText().toString().trim());
        user.setMobile(sharedPrefs.getSharedMobile());
        user.setAlt_mobile(userAltMobile.getText().toString().trim());
        user.setEmail(userEmail.getText().toString().trim());
        user.setAddress(userAddress.getText().toString().trim());


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

                Bitmap temp = imageManager.getImageByLink(user.getImage()).getImageBitmap();
                if (temp != null) {
                    userImage.setImageBitmap(temp);
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
        Bitmap selectedImageBitmap;
        try {
            selectedImageBitmap
                    = MediaStore.Images.Media.getBitmap(
                    requireActivity().getContentResolver(),
                    uri);
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

        } catch (IOException e) {
            e.printStackTrace();
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
        File fdelete = new File(getFilePath(imageUri));

        if (fdelete != null && fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :");
            } else {
                System.out.println("file not Deleted :");
            }
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
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
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
        boolean result = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    // Requesting  gallery permission
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    // checking camera permissions
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // Requesting camera permission
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageaccepted) {
                        openCamera();
                    } else {
                        Toast.makeText(this.requireActivity(), "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(this.requireActivity(), "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
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
            uri = FileProvider.getUriForFile(activity, "com.s19mobility.spotbuy.provider", file);
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
                .withMaxResultSize(200, 200)
                .withAspectRatio(1f, 1f)
                .withOptions(options)
                .start(activity, this, UCrop.REQUEST_CROP);
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

        if (requestCode == CAMERA_ACTION_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = Uri.parse(currentPhotoPath);
            openCropActivity(uri, uri);
            Toast.makeText(activity, "Camera Reult", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "onActivityResult: CAMERA_REQUEST");

        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            assert data != null;
            Log.d("TAG", "onActivityResult: OUTPUT" + UCrop.getOutputCropAspectRatio(data));
            Uri uri = UCrop.getOutput(data);
            setProfileImageFromUri(uri);

        } else if (requestCode == PICK_IMAGE_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri sourceUri = data.getData(); // 1
            File file = getImageFile(); // 2
            Uri destinationUri = Uri.fromFile(file);  // 3
            openCropActivity(sourceUri, destinationUri);  // 4
            Toast.makeText(activity, "Galary Reult", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "onActivityResult: GALLERY_REQUEST");
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(activity, "Error" + UCrop.getError(data), Toast.LENGTH_SHORT).show();
            Log.d("TAG", "onActivityResult: " + UCrop.getError(data));
        } else Toast.makeText(activity, "Error!!", Toast.LENGTH_SHORT).show();
    }


    private void deleteImageFromFirebaseAndLocalDB() {
        if (!Objects.equals(oldImage, user.getImage())) {
            imageManager.deleteByLink(oldImage);
            FirebaseStorage.getInstance().getReferenceFromUrl(oldImage).delete();

        }
    }

}
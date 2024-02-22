package com.s19.spotbuy.Others;

import static com.s19.spotbuy.Others.Constants.FuelTypeCollection;
import static com.s19.spotbuy.Others.Constants.UserCollection;
import static com.s19.spotbuy.Others.Constants.VehicleBrandCollection;
import static com.s19.spotbuy.Others.Constants.VehicleBrandModelCollection;
import static com.s19.spotbuy.Others.Constants.VehicleCategoryCollection;
import static com.s19.spotbuy.Others.Constants.VehiclePostCollection;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.s19.spotbuy.DataBase.ImageManager;
import com.s19.spotbuy.DataBase.SharedPrefs;
import com.s19.spotbuy.DataBase.UserManager;
import com.s19.spotbuy.DataBase.VehicleDetails.FuelTypeManager;
import com.s19.spotbuy.DataBase.VehicleDetails.VehicleBrandManager;
import com.s19.spotbuy.DataBase.VehicleDetails.VehicleBrandModelManager;
import com.s19.spotbuy.DataBase.VehicleDetails.VehicleCategoryManager;
import com.s19.spotbuy.DataBase.VehiclePostManager;
import com.s19.spotbuy.Models.FuelType;
import com.s19.spotbuy.Models.User;
import com.s19.spotbuy.Models.VehicleBrand;
import com.s19.spotbuy.Models.VehicleBrandModel;
import com.s19.spotbuy.Models.VehicleCategory;
import com.s19.spotbuy.Models.VehiclePost;

import java.util.Date;

public class ReadBasicFireBaseData {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FuelTypeManager fuelManager;
    VehicleBrandManager vehicleBrandManager;
    VehicleBrandModelManager vehicleBrandModelManager;
    VehicleCategoryManager vehicleCategoryManager;
    VehiclePostManager vehiclePostManager;
    ImageManager imageManager;
    UserManager userManager;
    Context context;
    SharedPrefs sharedPrefs;

    public ReadBasicFireBaseData(Context context) {
        this.context = context;
        fuelManager = new FuelTypeManager(context);
        vehicleBrandManager = new VehicleBrandManager(context);
        vehicleBrandModelManager = new VehicleBrandModelManager(context);
        vehicleCategoryManager = new VehicleCategoryManager(context);
        vehiclePostManager = new VehiclePostManager(context);
        imageManager = new ImageManager(context);
        sharedPrefs = new SharedPrefs(context);
        userManager = new UserManager(context);

        checkForUpdate(new OnGetDataListener() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Timestamp timestamp = (Timestamp) documentSnapshot.get("date");
                if(timestamp==null)
                    return;
                Date date =timestamp.toDate();
                if (sharedPrefs.getLastUpdateDate()==null ||sharedPrefs.getLastUpdateDate().getTime()<date.getTime()){
                    sharedPrefs.setLastUpdateDate(date);
                    Toast.makeText(context, "Update Available!", Toast.LENGTH_SHORT).show();
                    Log.e("Here","Update Available");
                    doOperations();
                }
                else {
                    Toast.makeText(context, "No Updates!", Toast.LENGTH_SHORT).show();
                    Log.e("Here","Update NOT Available");
                }

                Log.e("Here","Date Firebase"+date);
                Log.e("HERE","Local Dtae"+sharedPrefs.getLastUpdateDate());

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }
        });



    }

    private void doOperations(){
        imageManager.clearTable();
        readFirebaseFuelTypes();
        readFirebaseCategories();
        readFirebaseBrands();
        readFirebaseBrandsModels();
        readMyAdsFirebaseData();
        readMyDataFirebase();

    }

    private void checkForUpdate(OnGetDataListener listener){
        listener.onStart();
        db.collection("LatestUpdate")
                .document("DateTime")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        listener.onSuccess(documentSnapshot);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure();
                    }
                });

    }


    private void readFirebaseFuelTypes() {
        db.collection(FuelTypeCollection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            fuelManager.clearTable();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log.d("TAG", document.getId() + " => " + document.getData());
                                FuelType fuel = document.toObject(FuelType.class);
                                fuel.setId(document.getId());
                                if (fuel.isActive())
                                    fuelManager.insert(fuel);

                            }

                            //Log.i("Fuel", "Fuel set Correctly");
                        } else {
                            // Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void readFirebaseCategories() {
        db.collection(VehicleCategoryCollection)
                .orderBy("preference")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            vehicleCategoryManager.clearTable();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log.d("TAG", document.getId() + " => " + document.getData());
                                VehicleCategory category = document.toObject(VehicleCategory.class);
                                category.setId(document.getId());
                                if (category.isActive()) {
                                    vehicleCategoryManager.insert(category);
                                    new SaveImageByteToDatabase(context).execute(category.getImage());

                                }

                            }

                            //Log.i("Fuel", "category set Correctly");
                        } else {
                            //Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void readFirebaseBrands() {
        db.collection(VehicleBrandCollection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            vehicleBrandManager.clearTable();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("TAG", document.getId() + " => " + document.getData());
                                VehicleBrand vehicleBrand = document.toObject(VehicleBrand.class);
                                vehicleBrand.setId(document.getId());
                                if (vehicleBrand.isActive())
                                    vehicleBrandManager.insert(vehicleBrand);

                            }

                            //Log.i("Fuel", "vehicleBrand set Correctly");
                        } else {
                            // Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void readFirebaseBrandsModels() {
        db.collection(VehicleBrandModelCollection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            vehicleBrandModelManager.clearTable();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log.d("TAG", document.getId() + " => " + document.getData());
                                VehicleBrandModel vehicleBrandModel = document.toObject(VehicleBrandModel.class);
                                vehicleBrandModel.setId(document.getId());
                                if (vehicleBrandModel.isActive())
                                    vehicleBrandModelManager.insert(vehicleBrandModel);

                            }

                            //Log.i("Fuel", "vehicleBrandModel set Correctly");
                        } else {
                            //  Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void readMyAdsFirebaseData() {
        db.collection(VehiclePostCollection)
                .whereEqualTo("sellerId", sharedPrefs.getSharedUID())
                .orderBy("dateTime")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        vehiclePostManager.clearTable();
                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                            VehiclePost vehiclePost = snap.toObject(VehiclePost.class);
                            vehiclePostManager.insert(vehiclePost);
                            Log.e("TAG", "onSuccess: "+vehiclePost.getKmsRidden());
                            Log.e("TAG", "onSuccess: "+vehiclePost.getTitle());
                            for (String url : vehiclePost.getImageList())
                                new SaveImageByteToDatabase(context).execute(url);

                        }

                    }
                });


    }

    private void readMyDataFirebase() {
        db.collection(UserCollection)
                .document(sharedPrefs.getSharedUID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userManager.clearTable();
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            userManager.insert(user);
                            new SaveImageByteToDatabase(context).execute(user.getImage());
                        }

                    }
                });
    }

}

package com.s19mobility.spotbuy.Others;

import static com.s19mobility.spotbuy.Others.Constants.FuelTypeCollection;
import static com.s19mobility.spotbuy.Others.Constants.VehicleBrandCollection;
import static com.s19mobility.spotbuy.Others.Constants.VehicleBrandModelCollection;
import static com.s19mobility.spotbuy.Others.Constants.VehicleCategoryCollection;

import android.util.Log;

import androidx.annotation.NonNull;
import android.content.Context;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.s19mobility.spotbuy.DataBase.VehicleDetails.FuelTypeManager;
import com.s19mobility.spotbuy.DataBase.VehicleDetails.VehicleBrandManager;
import com.s19mobility.spotbuy.DataBase.VehicleDetails.VehicleBrandModelManager;
import com.s19mobility.spotbuy.DataBase.VehicleDetails.VehicleCategoryManager;
import com.s19mobility.spotbuy.Models.FuelType;
import com.s19mobility.spotbuy.Models.VehicleBrand;
import com.s19mobility.spotbuy.Models.VehicleBrandModel;
import com.s19mobility.spotbuy.Models.VehicleCategory;

public class ReadBasicFireBaseData {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FuelTypeManager fuelManager;
    VehicleBrandManager vehicleBrandManager;
    VehicleBrandModelManager vehicleBrandModelManager;
    VehicleCategoryManager vehicleCategoryManager;

    public  ReadBasicFireBaseData(Context context){
        fuelManager = new FuelTypeManager(context);
        vehicleBrandManager = new VehicleBrandManager(context);
        vehicleBrandModelManager = new VehicleBrandModelManager(context);
        vehicleCategoryManager = new VehicleCategoryManager(context);
        readFirebaseFuelTypes();
        readFirebaseCategories();
        readFirebaseBrands();
        readFirebaseBrandsModels();

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
                                if (category.isActive())
                                    vehicleCategoryManager.insert(category);

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
}

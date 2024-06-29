package com.s19.spotbuy.Dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.s19.spotbuy.DataBase.SharedPrefs;
import com.s19.spotbuy.DataBase.UserManager;
import com.s19.spotbuy.Fragments.main.HomeFragment;
import com.s19.spotbuy.Models.User;
import com.s19.spotbuy.Others.StateCityData;
import com.s19.spotbuy.R;

public class LocationPickerDialog {

    Activity activity;
    HomeFragment homeFragment;
    StateCityData stateCityData;
    UserManager userManager;
    User user;
    SharedPrefs sharedPrefs;
    AlertDialog dialog;
    AlertDialog.Builder builder;

    ImageView close;
    AutoCompleteTextView state, city;
    Button save;


    @SuppressLint("MissingInflatedId")
    public LocationPickerDialog(Activity activity, HomeFragment homeFragment) {
        this.activity = activity;
        this.homeFragment = homeFragment;
        stateCityData = new StateCityData(activity);
        sharedPrefs = new SharedPrefs(activity);
        userManager = new UserManager(activity);

        user = userManager.getUserById(sharedPrefs.getSharedUID());


        builder = new AlertDialog.Builder(activity);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_location_picker, null);
        builder.setView(dialogView);

        initView(dialogView);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

    }

    private void initView(View dialogView) {
        close = dialogView.findViewById(R.id.close);
        close.setOnClickListener(v -> dismiss());
        city = dialogView.findViewById(R.id.city);

        state = dialogView.findViewById(R.id.state);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_list_item_1, stateCityData.getStateList());
        state.setAdapter(adapter);
        state.setOnItemClickListener((adapterView, view, i, l) -> {
            user.setState(stateCityData.getStateList().get(i));
            sharedPrefs.setSharedState(stateCityData.getStateList().get(i));
            setCityAdapter(stateCityData.getStateList().get(i));
        });

        state.setText(activity.getString(R.string.select_your_state), false);
        city.setText(activity.getString(R.string.select_your_city), false);


        //Setting Default state Value
        if (user.getState() != null && !user.getState().equals(""))
            state.setText(user.getState(), false);
        else if (!sharedPrefs.getSharedState().equals(""))
            state.setText(sharedPrefs.getSharedState(), false);


        //Setting Default City Value
        if (user.getCity() != null && !user.getCity().equals(""))
            city.setText(user.getCity(), false);
        else if (!sharedPrefs.getSharedCity().equals("location"))
            city.setText(sharedPrefs.getSharedCity(), false);


        save = dialogView.findViewById(R.id.saveLocation);
        save.setOnClickListener(v -> saveLocation());
    }

    private void setCityAdapter(String stateName) {
        ArrayAdapter<String> adapterCity;
        adapterCity = new ArrayAdapter<>(activity,
                android.R.layout.simple_list_item_1, stateCityData.getCityList(stateName));
        city.setAdapter(adapterCity);
        city.setOnItemClickListener((adapterView, view, i, l) -> {
            user.setCity(stateCityData.getCityList(stateName).get(i));
            sharedPrefs.setSharedState(stateCityData.getCityList(stateName).get(i));
        });

    }

    public void show() {
        if (dialog != null)
            dialog.show();
    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

    public void saveLocation() {
        userManager.update(user);
        homeFragment.onLocationUpdate(city.getText().toString().trim(), state.getText().toString().trim());
        dismiss();
    }

}



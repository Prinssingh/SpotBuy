package com.s19mobility.spotbuy.Fragments.Login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.s19mobility.spotbuy.R;

public class ForgetPasswordFragment extends Fragment {
View Root;


    public ForgetPasswordFragment() {
        // Required empty public constructor
    }


    public static ForgetPasswordFragment newInstance() {

        return new ForgetPasswordFragment();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Root= inflater.inflate(R.layout.fragment_forget_password, container, false);



        return Root;
    }
}
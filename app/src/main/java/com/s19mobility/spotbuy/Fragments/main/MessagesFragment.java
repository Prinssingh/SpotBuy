package com.s19mobility.spotbuy.Fragments.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.s19mobility.spotbuy.R;


public class MessagesFragment extends Fragment {

    View Root;

    public MessagesFragment() {
        // Required empty public constructor
    }


    public static MessagesFragment newInstance() {


        return new MessagesFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Root = inflater.inflate(R.layout.fragment_messages, container, false);


        return Root;
    }
}
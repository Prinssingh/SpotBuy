package com.s19mobility.spotbuy.Fragments.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.s19mobility.spotbuy.R;


public class ShowProfileFragment extends Fragment implements View.OnClickListener {
    View Root;

    Button editProfile;

    public ShowProfileFragment() {
        // Required empty public constructor
    }


    public static ShowProfileFragment newInstance() {
        ShowProfileFragment fragment = new ShowProfileFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Root = inflater.inflate(R.layout.fragment_show_profile, container, false);

        initView();

        return Root;
    }

    private void initView(){
        editProfile = Root.findViewById(R.id.editProfile);
        editProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(editProfile ==view){
           // requireActivity().getSupportFragmentManager().popBackStack();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, EditProfileFragment.newInstance())
                    .commitNow();
        }

    }
}
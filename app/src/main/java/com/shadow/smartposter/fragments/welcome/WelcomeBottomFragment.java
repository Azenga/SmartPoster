package com.shadow.smartposter.fragments.welcome;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.shadow.smartposter.R;
import com.shadow.smartposter.WelcomeActivity;

public class WelcomeBottomFragment extends Fragment {


    public WelcomeBottomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome_bottom, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ImageButton goUpImageBtn = view.findViewById(R.id.go_up_img_btn);

        goUpImageBtn.setOnClickListener(v -> ((WelcomeActivity) getActivity()).switchFragment(new WelcomeTopFragment()));
    }
}

package com.shadow.smartposter.fragments.welcome;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.shadow.smartposter.BlogPostActivity;
import com.shadow.smartposter.LoginActivity;
import com.shadow.smartposter.R;
import com.shadow.smartposter.WelcomeActivity;

public class WelcomeTopFragment extends Fragment {


    public WelcomeTopFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome_top, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageButton goDownImageButton = view.findViewById(R.id.go_down_img_btn);
        goDownImageButton.setOnClickListener(v -> {
            ((WelcomeActivity) getActivity()).switchFragment(new WelcomeBottomFragment());
        });

        Button gotoPostsButton = view.findViewById(R.id.go_to_posts_btn);
        gotoPostsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BlogPostActivity.class);
            getActivity().startActivity(intent);
        });

        //Login Intent
        Button gotoLoginBtn = view.findViewById(R.id.go_to_login_btn);
        gotoLoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

    }
}

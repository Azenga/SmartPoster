package com.shadow.smartposter.fragments.publics;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shadow.smartposter.PublicDashboardActivity;
import com.shadow.smartposter.R;
import com.shadow.smartposter.models.Application;

public class RequestCandidatureFragment extends Fragment {

    private static final String TAG = "RequestCandidatureFragm";

    //Views/Widgets
    private EditText nameET, areaET;
    private Spinner positionSpinner;
    private Button sendRequestBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    public RequestCandidatureFragment() {
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_candidature, container, false);

        nameET = view.findViewById(R.id.name_et);
        areaET = view.findViewById(R.id.area_et);
        positionSpinner = view.findViewById(R.id.position_spinner);
        sendRequestBtn = view.findViewById(R.id.request_btn);

        return view;
    }

    private void sendApplication() {

        if (nameET.getText().toString().trim().isEmpty()) {
            nameET.setError("Need an Official Name");
            nameET.requestFocus();
            return;
        } else if (areaET.getText().toString().trim().isEmpty()) {
            areaET.setError("Geographical area you're vying");
            areaET.requestFocus();
            return;
        } else if (positionSpinner.getSelectedItem().toString().trim().equalsIgnoreCase("-- Position --")) {
            Toast.makeText(getActivity(), "Choose a position to vye", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = nameET.getText().toString().trim();
        String area = areaET.getText().toString().trim();
        String position = positionSpinner.getSelectedItem().toString().trim();
        String senderUid = mAuth.getCurrentUser().getUid();

        Application application = new Application(senderUid, name, position, area);

        mDb.collection("applications")
                .add(application)
                .addOnSuccessListener(
                        documentReference -> {
                            Toast.makeText(getActivity(), "Request Sent Successfully", Toast.LENGTH_SHORT).show();
                            ((PublicDashboardActivity) getActivity()).switchFragment(new ProfileFragment());
                        }
                )
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Application Failed try again later ...", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "sendApplication: Failed", e);
                });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        sendRequestBtn.setOnClickListener(v -> sendApplication());

    }
}

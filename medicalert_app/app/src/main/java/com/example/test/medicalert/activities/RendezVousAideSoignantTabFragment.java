package com.example.test.medicalert.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.test.medicalert.R;

public class RendezVousAideSoignantTabFragment extends Fragment {
    private static final String TAG =  "PharmacieTabFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_aide_soignant_rendez_vous, container, false);
        return view;
    }
}

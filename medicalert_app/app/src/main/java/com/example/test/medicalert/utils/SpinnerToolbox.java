package com.example.test.medicalert.utils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.test.medicalert.activities.AjouterTraitementActivity;

import java.util.ArrayList;

public final class SpinnerToolbox {
    private SpinnerToolbox(){}

    public static void disablePatientSpinner(Spinner spinner, Context context){
        spinner.setEnabled(false);
        spinner.setClickable(false);
        spinner.setAdapter(new ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_item,
                new ArrayList<String>()
        ));
    }
}

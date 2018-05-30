package com.example.test.medicalert.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.test.medicalert.Medicament;
import com.example.test.medicalert.Patient;
import com.example.test.medicalert.Traitement;
import com.example.test.medicalert.api_request.CategorieRequest;
import com.example.test.medicalert.api_request.MedicamentRequest;
import com.example.test.medicalert.api_request.PatientRequest;
import com.example.test.medicalert.utils.EditTextToolbox;
import com.example.test.medicalert.R;
import com.example.test.medicalert.utils.SpinnerToolbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AjouterMedicamentActivity extends Activity {
    private EditText cipEditText, newNomEditText;
    private Button addButton;
    private View activityView;
    private Context activityContext;
    private Spinner formePharmaSpinner;
    private List<String> formePharmas;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_medicament);

        cipEditText = (EditText) findViewById(R.id.cip_box);
        newNomEditText = (EditText) findViewById(R.id.nom_box);
        //newFormePharmaEditText = (EditText) findViewById(R.id.forme_pharma_box);
        formePharmaSpinner = (Spinner) findViewById(R.id.forme_pharma_spinner);
        addButton= (Button) findViewById(R.id.add_button);

        activityView = this.findViewById(android.R.id.content);
        activityContext = this;

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cip = cipEditText.getText().toString();
                String nom = newNomEditText.getText().toString();
                if (EditTextToolbox.areEmptyFields(R.id.ajouter_medicament_wrapper, activityView)) {
                    Toast.makeText(activityContext, "Veuillez compléter tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!EditTextToolbox.hasOnlyDigits(cip) || cip.length() != 13) {
                    Toast.makeText(activityContext, "Le code cip doit comporter 13 chiffres", Toast.LENGTH_SHORT).show();
                    return;
                }

                Object selectedItem = formePharmaSpinner.getSelectedItem();
                if(selectedItem == null){
                    Toast.makeText(AjouterMedicamentActivity.this, "Le service est indisponible", Toast.LENGTH_SHORT).show();
                } else {
                    int position = formePharmaSpinner.getSelectedItemPosition();

                    if (!MedicamentRequest.insertMedicament(new Medicament(cip, nom, formePharmas.get(position)))) {
                        Toast.makeText(activityContext, "Le médicament n'a pas pu être ajouté", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(activityContext, "Le médicament a été ajouté avec succès", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

        });

        formePharmas = CategorieRequest.getAllCategorieName();
        if(formePharmas == null){
            Toast.makeText(AjouterMedicamentActivity.this, "Le service est indisponible", Toast.LENGTH_SHORT).show();
            SpinnerToolbox.disableSpinner(formePharmaSpinner, this);
            return;
        }
        if(formePharmas.size() == 0){
            formePharmas.add("Pas de catégorie");
        }

        ArrayAdapter spinnerAdapter = new ArrayAdapter<String>(
                AjouterMedicamentActivity.this,
                R.layout.spinner_item,
                formePharmas
        );
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        formePharmaSpinner.setAdapter(spinnerAdapter);
    }
}

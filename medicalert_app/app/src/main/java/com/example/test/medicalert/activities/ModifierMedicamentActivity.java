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
import com.example.test.medicalert.R;
import com.example.test.medicalert.api_request.CategorieRequest;
import com.example.test.medicalert.api_request.MedicamentRequest;
import com.example.test.medicalert.utils.EditTextToolbox;
import com.example.test.medicalert.utils.SpinnerToolbox;

import java.util.HashMap;
import java.util.List;

public class ModifierMedicamentActivity extends Activity {
    private EditText cipEditText, newCipEditText, newNomEditText;
    private Button modifyButton;
    private Context activityContext;
    private Spinner formePharmaSpinner;
    private List<String> formePharmas;
    private String nePasModifier = "Ne pas modifier";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_medicament);

        cipEditText = (EditText) findViewById(R.id.cip_modif_box);
        newCipEditText = (EditText) findViewById(R.id.cip_box);
        newNomEditText = (EditText) findViewById(R.id.nom_box);
        formePharmaSpinner = (Spinner) findViewById(R.id.forme_pharma_spinner);
        modifyButton= (Button) findViewById(R.id.modifier_button);

        activityContext = this;

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();
                String cip = cipEditText.getText().toString();
                String newCip = newCipEditText.getText().toString();
                String newNom = newNomEditText.getText().toString();

                if (!EditTextToolbox.hasOnlyDigits(cip) || cip.length() != 13) {
                    Toast.makeText(activityContext, "Le code cip à modifier doit comporter 13 chiffres", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(newCip.length() > 0){
                    if (!EditTextToolbox.hasOnlyDigits(newCip) || newCip.length() != 13) {
                        Toast.makeText(activityContext, "Le nouveau code cip doit comporter 13 chiffres", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    map.put(Medicament.cip13Key, newCip);
                }

                if(newNom.length() > 0)
                    map.put(Medicament.nomKey, newNom);

                Object selectedFormePharma = formePharmaSpinner.getSelectedItem(); //"Ne pas modifier" par défaut même en hors ligne.

                if (!selectedFormePharma.equals(nePasModifier)) {
                    map.put(Medicament.formePharmaKey, (String) selectedFormePharma);
                }
                if (!MedicamentRequest.modifyMedicament(cip, map)) {
                    Toast.makeText(activityContext, "Le médicament n'a pas pu être modifié", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(activityContext, "Le médicament a été modifié avec succès", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        formePharmas = CategorieRequest.getAllCategorieName();
        if(formePharmas == null){
            Toast.makeText(ModifierMedicamentActivity.this, "Le service est indisponible", Toast.LENGTH_SHORT).show();
            SpinnerToolbox.disableSpinner(formePharmaSpinner, this);
            return;
        }

        formePharmas.add(nePasModifier);
        ArrayAdapter spinnerAdapter = new ArrayAdapter<String>(
                ModifierMedicamentActivity.this,
                R.layout.spinner_item,
                formePharmas
        );
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        formePharmaSpinner.setAdapter(spinnerAdapter);
        formePharmaSpinner.setSelection(spinnerAdapter.getPosition(nePasModifier));
    }

}

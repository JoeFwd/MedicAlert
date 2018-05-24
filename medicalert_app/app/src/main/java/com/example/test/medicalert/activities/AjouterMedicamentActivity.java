package com.example.test.medicalert.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test.medicalert.Medicament;
import com.example.test.medicalert.api_request.MedicamentRequest;
import com.example.test.medicalert.utils.EditTextToolbox;
import com.example.test.medicalert.R;

public class AjouterMedicamentActivity extends Activity {
    private EditText cipEditText, newNomEditText, newFormePharmaEditText;
    private Button addButton;
    private View activityView;
    private Context activityContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_medicament);

        cipEditText = (EditText) findViewById(R.id.cip_box);
        newNomEditText = (EditText) findViewById(R.id.nom_box);
        newFormePharmaEditText = (EditText) findViewById(R.id.forme_pharma_box);
        addButton= (Button) findViewById(R.id.add_button);

        activityView = this.findViewById(android.R.id.content);
        activityContext = this;

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cip = cipEditText.getText().toString();
                String nom = newNomEditText.getText().toString();
                String formePharma = newFormePharmaEditText.getText().toString();
                if (EditTextToolbox.areEmptyFields(R.id.ajouter_medicament_wrapper, activityView)) {
                    Toast.makeText(activityContext, "Veuillez compléter tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!EditTextToolbox.hasOnlyDigits(cip) || cip.length() != 13) {
                    Toast.makeText(activityContext, "Le code cip doit comporter 13 chiffres", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!MedicamentRequest.insertMedicament(new Medicament(cip, nom, formePharma))){
                    Toast.makeText(activityContext, "Le médicament n'a pas pu être ajouté", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(activityContext, "Le médicament a été ajouté avec succès", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activityContext, AideSoignantMenuActivity.class);
                    startActivity(intent);
                }
            }

        });

    }
}

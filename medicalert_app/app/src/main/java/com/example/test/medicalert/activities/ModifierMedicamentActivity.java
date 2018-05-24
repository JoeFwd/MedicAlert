package com.example.test.medicalert.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test.medicalert.R;
import com.example.test.medicalert.api_request.MedicamentRequest;
import com.example.test.medicalert.utils.EditTextToolbox;

import java.util.HashMap;

public class ModifierMedicamentActivity extends Activity {
    private EditText cipEditText, newCipEditText, newNomEditText, newFormePharmaEditText;
    private Button modifyButton;
    private Context activityContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_medicament);

        cipEditText = (EditText) findViewById(R.id.cip_modif_box);
        newCipEditText = (EditText) findViewById(R.id.cip_box);
        newNomEditText = (EditText) findViewById(R.id.nom_box);
        newFormePharmaEditText = (EditText) findViewById(R.id.forme_pharma_box);
        modifyButton= (Button) findViewById(R.id.modifier_button);

        activityContext = this;

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();
                String cip = cipEditText.getText().toString();
                String newCip = newCipEditText.getText().toString();
                String newNom = newNomEditText.getText().toString();
                String newFormePharma = newFormePharmaEditText.getText().toString();

                if (!EditTextToolbox.hasOnlyDigits(cip) || cip.length() != 13) {
                    Toast.makeText(activityContext, "Le code cip à modifier doit comporter 13 chiffres", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(newCip.length() > 0){
                    if (!EditTextToolbox.hasOnlyDigits(newCip) || newCip.length() != 13) {
                        Toast.makeText(activityContext, "Le nouveau code cip doit comporter 13 chiffres", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    map.put("cip13", newCip);
                }

                if(newNom.length() > 0)
                    map.put("nom", newNom);
                if(newFormePharma.length() > 0)
                    map.put("formePharma", newFormePharma);

                if(!MedicamentRequest.modifyMedicament(cip, map)){
                    Toast.makeText(activityContext, "Le médicament n'a pas pu être modifié", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(activityContext, "Le médicament a été modifié avec succès", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activityContext, AideSoignantMenuActivity.class);
                    startActivity(intent);
                }
            }

        });

    }

}

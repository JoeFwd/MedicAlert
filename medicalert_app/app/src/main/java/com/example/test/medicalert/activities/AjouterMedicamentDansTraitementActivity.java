package com.example.test.medicalert.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test.medicalert.R;
import com.example.test.medicalert.Traitement;
import com.example.test.medicalert.api_request.MedicamentRequest;
import com.example.test.medicalert.utils.DateValidator;
import com.example.test.medicalert.utils.EditTextToolbox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class AjouterMedicamentDansTraitementActivity extends Activity {

    private EditText cipEditText, dosageEditText, datePeremptionEditText;
    private View activityView;
    private Context activityContext;
    private ArrayList<HashMap<String, String>> medicamentList;
    private Traitement traitement;
    private GregorianCalendar scannedDatePeremption = null;
    private String scannedCip = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_medicament_dans_traitement);

        cipEditText = (EditText) findViewById(R.id.cip_box);
        dosageEditText = (EditText) findViewById(R.id.dosage_box);
        datePeremptionEditText = (EditText) findViewById(R.id.date_peremption_box);
        Button addButton = (Button) findViewById(R.id.add_button);

        activityView = this.findViewById(android.R.id.content);
        activityContext = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.containsKey("date"))
                scannedDatePeremption = (GregorianCalendar) extras.get("date");
            if(extras.containsKey("cip"))
                scannedCip = extras.getString("cip");
            medicamentList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("medicamentList");
            traitement = getIntent().getParcelableExtra(Traitement.CLASS_TAG);
        } else {
            Log.e("Error", "No bundle", new Exception("AjouterMedicamentDansTraitementActivity needs a Traitement object and a ArrayList<HashMap<String, String>> as Extras."));
        }

        if(scannedCip != null) {
            cipEditText.setText(scannedCip);
        }
        if(scannedDatePeremption != null){
            int day = scannedDatePeremption.get(Calendar.DAY_OF_MONTH);
            String stringifiedDay = day + "";
            if(day < 10) stringifiedDay = "0" + day;
            int month = scannedDatePeremption.get(Calendar.MONTH) + 1;
            String stringifiedMonth = month + "";
            if(month < 10) stringifiedMonth = "0" + month;
            datePeremptionEditText.setText(stringifiedDay + "/" + stringifiedMonth + "/" + scannedDatePeremption.get(Calendar.YEAR));
        }


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cip = cipEditText.getText().toString();
                String dosage = dosageEditText.getText().toString();
                String datePeremption = datePeremptionEditText.getText().toString();
                if (EditTextToolbox.areEmptyFields(R.id.ajouter_medicament_dans_traitment_wrapper, activityView)) {
                    Toast.makeText(activityContext, "Veuillez compléter tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!EditTextToolbox.hasOnlyDigits(cip) || cip.length() != 13) {
                    Toast.makeText(activityContext, "Le code cip doit comporter 13 chiffres", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!DateValidator.isDateValid(datePeremption, DateValidator.inputFormat)) {
                    Toast.makeText(activityContext, "La date n'est pas valide", Toast.LENGTH_SHORT).show();
                    EditTextToolbox.setEditTextToBlank(datePeremptionEditText);
                    return;
                }

                if (DateValidator.hasDatePassed(datePeremption)) {
                    Toast.makeText(activityContext, "Le médicament est périmé", Toast.LENGTH_SHORT).show();
                    EditTextToolbox.setEditTextToBlank(datePeremptionEditText);
                    return;
                }

                int medId = MedicamentRequest.getMedicamentIdByCip13(cip);
                if(medId != -1){
                    Intent intent = new Intent(activityContext, AjouterMedicamentTraitementMenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    HashMap<String, String> dernierMedicamentAjoute = new HashMap<>();
                    dernierMedicamentAjoute.put(Traitement.idMedicamentKey, medId + "");
                    dernierMedicamentAjoute.put(Traitement.datePeremptionKey, datePeremption);
                    dernierMedicamentAjoute.put(Traitement.dosageKey, dosage);
                    medicamentList.add(dernierMedicamentAjoute);

                    intent.putExtra(Traitement.medicamentListKey, medicamentList);
                    intent.putExtra(Traitement.CLASS_TAG, traitement);

                    startActivity(intent);
                    Toast.makeText(activityContext, "Le médicament a été ajouté", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(activityContext, "Le médicament n'a pas pu être ajouté", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

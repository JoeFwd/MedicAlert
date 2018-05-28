package com.example.test.medicalert.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.test.medicalert.R;
import com.example.test.medicalert.Traitement;
import com.example.test.medicalert.api_request.PatientRequest;
import com.example.test.medicalert.utils.DateValidator;
import com.example.test.medicalert.utils.EditTextToolbox;
import com.example.test.medicalert.utils.SpinnerToolbox;

import java.util.ArrayList;
import java.util.HashMap;

public class AjouterTraitementActivity extends Activity {
    private Spinner patientSpinner;
    private Button addbutton;
    private EditText nomEditText, dureeEditText, debutDateEditText;
    private CheckBox ouiMatin, nonMatin, ouiMidi, nonMidi, ouiSoir, nonSoir;
    private ArrayList<HashMap<String, String>> patients;
    private View activityView;
    private Context activityContext;
    private int userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_traitement);
        int default_id = -1;

        SharedPreferences p = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);
        userId = p.getInt(getString(R.string.userId), default_id);
        activityContext = this;
        activityView = this.findViewById(android.R.id.content);
        nomEditText = (EditText) findViewById(R.id.nom_traitement);
        debutDateEditText = (EditText) findViewById(R.id.date_debut_traitement);
        dureeEditText= (EditText) findViewById(R.id.duree_traitement);
        addbutton = (Button) findViewById(R.id.ajouter_button);
        patientSpinner = (Spinner) findViewById(R.id.patient_spinner);
        ouiMatin = (CheckBox) findViewById(R.id.oui_matin);
        ouiMatin.toggle();
        nonMatin = (CheckBox) findViewById(R.id.non_matin);
        ouiMidi = (CheckBox) findViewById(R.id.oui_apres_midi);
        ouiMidi.toggle();
        nonMidi = (CheckBox) findViewById(R.id.non_apres_midi);
        ouiSoir = (CheckBox) findViewById(R.id.oui_soir);
        ouiSoir.toggle();
        nonSoir = (CheckBox) findViewById(R.id.non_soir);

        ouiMatin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nonMatin.toggle();
            }
        });
        nonMatin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ouiMatin.toggle();
            }
        });
        ouiMidi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nonMidi.toggle();
            }
        });

        nonMidi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ouiMidi.toggle();
            }
        });
        ouiSoir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nonSoir.toggle();
            }
        });

        nonSoir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ouiSoir.toggle();
            }
        });

        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nom = nomEditText.getText().toString();
                String duree = dureeEditText.getText().toString();
                String dateDebut = debutDateEditText.getText().toString();

                if (EditTextToolbox.areEmptyFields(R.id.ajouter_traitement_wrapper, activityView)) {
                    Toast.makeText(activityContext, "Veuillez compléter tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!DateValidator.isDateValid(dateDebut, DateValidator.inputFormat)) {
                    Toast.makeText(activityContext, "La date n'est pas valide", Toast.LENGTH_SHORT).show();
                    EditTextToolbox.setEditTextToBlank(debutDateEditText);
                    return;
                }

                if (DateValidator.hasDatePassed(dateDebut)) {
                    Toast.makeText(activityContext, "La date est déjà passée", Toast.LENGTH_SHORT).show();
                    EditTextToolbox.setEditTextToBlank(debutDateEditText);
                    return;
                }

                if (!EditTextToolbox.hasOnlyDigits(duree)) {
                    Toast.makeText(activityContext, "La durée ne peut contenir que des chiffres", Toast.LENGTH_SHORT).show();
                    EditTextToolbox.setEditTextToBlank(dureeEditText);
                    return;
                }

                if(Integer.parseInt(duree) < 1){
                    Toast.makeText(activityContext, "La durée n'est pas correcte", Toast.LENGTH_SHORT).show();
                    EditTextToolbox.setEditTextToBlank(dureeEditText);
                    return;
                }

                Object selectedItem = patientSpinner.getSelectedItem();
                if(selectedItem == null){
                        Toast.makeText(AjouterTraitementActivity.this, "Le service est indisponible", Toast.LENGTH_SHORT).show();
                } else {
                    int position = patientSpinner.getSelectedItemPosition();
                    HashMap<String, String> patient = patients.get(position);
                    int patientId = Integer.parseInt(patient.get("id"));
                    Intent intent = new Intent(activityContext, AjouterMedicamentTraitementMenuActivity.class);

                    Traitement traitement = new Traitement(patientId, userId, Integer.parseInt(duree), nom, dateDebut, ouiMatin.isChecked(), ouiMidi.isChecked(), ouiSoir.isChecked(), null);
                    intent.putExtra("traitement", traitement);

                    startActivity(intent);

                }
            }
        });

        if(userId == default_id){
            SpinnerToolbox.disablePatientSpinner(patientSpinner, activityContext);
            Log.e("ERROR", "USER ID WAS NOT INITIALISED");
        } else {
            patients = PatientRequest.getAllPatientByAideSoignantId(userId);
            if(patients == null){
                Toast.makeText(AjouterTraitementActivity.this, "Le service est indisponible", Toast.LENGTH_SHORT).show();
                SpinnerToolbox.disablePatientSpinner(patientSpinner, activityContext);
            } else if(patients.size() == 0) {
                Toast.makeText(AjouterTraitementActivity.this, "Pas de patients disponibles", Toast.LENGTH_SHORT).show();
                SpinnerToolbox.disablePatientSpinner(patientSpinner, activityContext);
            } else {
                ArrayList<String> nomCompletPatients = new ArrayList<>();
                for (HashMap<String, String> patient : patients) {
                    String nomComplet = "";
                    nomComplet += patient.get("prenom") + " ";
                    nomComplet += patient.get("nom");
                    nomCompletPatients.add(nomComplet);
                }

                ArrayAdapter patientAdapter = new ArrayAdapter<>(
                        AjouterTraitementActivity.this,
                        R.layout.spinner_item,
                        nomCompletPatients
                );

                patientAdapter .setDropDownViewResource(R.layout.spinner_dropdown_item);
                patientSpinner.setAdapter(patientAdapter);
            }
        }
    }
}

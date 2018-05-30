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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.test.medicalert.R;
import com.example.test.medicalert.RendezVous;
import com.example.test.medicalert.api_request.FirebaseRequest;
import com.example.test.medicalert.api_request.PatientRequest;
import com.example.test.medicalert.api_request.PostRequest;
import com.example.test.medicalert.api_request.RendezVousRequest;
import com.example.test.medicalert.utils.DateValidator;
import com.example.test.medicalert.utils.EditTextToolbox;
import com.example.test.medicalert.utils.SpinnerToolbox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AjouterRendezVousActivity extends Activity {
    private final String TAG = "AjouterRendezVousActivi";
    private Spinner patientSpinner, heureSpinner, minuteSpinner;
    private EditText dateRendezVousEditText;
    private View activityView;
    private Context activityContext;
    int default_id = -1, userId;
    private ArrayList<HashMap<String, String>> patients;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_rendez_vous);
        activityContext = this;
        activityView = this.findViewById(android.R.id.content);

        patientSpinner = (Spinner) findViewById(R.id.patient_spinner);
        heureSpinner = (Spinner) findViewById(R.id.heure_spinner_box);
        initialiseSpinnerWithIntegers(heureSpinner, 0, 23);
        minuteSpinner = (Spinner) findViewById(R.id.minute_spinner_box);
        initialiseSpinnerWithIntegers(minuteSpinner, 0, 59);
        dateRendezVousEditText = (EditText) findViewById(R.id.date_rendez_vous_box);
        Button addbutton = (Button) findViewById(R.id.add_button);


        SharedPreferences p = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);
        userId = p.getInt(getString(R.string.userId), default_id);

        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateRendezVous = dateRendezVousEditText.getText().toString();

                if (EditTextToolbox.areEmptyFields(R.id.ajouter_rendez_vous_wrapper, activityView)) {
                    Toast.makeText(activityContext, "Veuillez compléter tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!DateValidator.isDateValid(dateRendezVous, DateValidator.inputFormat)) {
                    Toast.makeText(activityContext, "La date n'est pas valide", Toast.LENGTH_SHORT).show();
                    EditTextToolbox.setEditTextToBlank(dateRendezVousEditText);
                    return;
                }

                if (DateValidator.hasDatePassed(dateRendezVous)) {
                    Toast.makeText(activityContext, "La date est déjà passée", Toast.LENGTH_SHORT).show();
                    EditTextToolbox.setEditTextToBlank(dateRendezVousEditText);
                    return;
                }

                Object selectedPatient = patientSpinner.getSelectedItem();
                Object selectedHour = heureSpinner.getSelectedItem();
                Object selectedMinute = minuteSpinner.getSelectedItem();

                if(selectedPatient == null){
                    Toast.makeText(AjouterRendezVousActivity.this, "Le service est indisponible", Toast.LENGTH_SHORT).show();
                } else {
                    int position = patientSpinner.getSelectedItemPosition();
                    HashMap<String, String> patient = patients.get(position);
                    int patientId = Integer.parseInt(patient.get("id"));
                    //userId ->aidesoignant

                    Calendar dateRendezVousCalendar = Calendar.getInstance();
                    dateRendezVousCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateRendezVous.substring(0, 2)));
                    dateRendezVousCalendar.set(Calendar.MONTH, Integer.parseInt(dateRendezVous.substring(3, 5)) - 1);
                    dateRendezVousCalendar.set(Calendar.YEAR, Integer.parseInt(dateRendezVous.substring(6, 10)));
                    dateRendezVousCalendar.set(Calendar.HOUR_OF_DAY, (Integer) selectedHour);
                    dateRendezVousCalendar.set(Calendar.MINUTE, (Integer) selectedMinute);


                    if (RendezVousRequest.insertRendezVous(new RendezVous(patientId, userId, dateRendezVousCalendar))) {
                        Toast.makeText(activityContext, "Le rendez-vous a été ajouté", Toast.LENGTH_SHORT).show();

                        String firebaseToken = FirebaseRequest.getPatientToken(patientId);
                        if(firebaseToken != null){
                            if(!FirebaseRequest.sendNofiticationViaFCM (firebaseToken, "MedicAlert", "Vous avez un nouveau rendez-vous.")){
                                Log.v(TAG, "Notifier le patient a échoué");
                            }
                        }

                        Intent intent = new Intent(activityContext, AideSoignantMenuActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(activityContext, "Le rendez-vous n'a pas pu être ajouté", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if(userId == default_id){
            SpinnerToolbox.disableSpinner(patientSpinner, activityContext);
            Log.e(TAG, "UserId WAS NOT INITIALISED");
        } else {
            patients = PatientRequest.getAllPatientByAideSoignantId(userId);
            if(patients == null){
                Toast.makeText(activityContext, "Le service est indisponible", Toast.LENGTH_SHORT).show();
                SpinnerToolbox.disableSpinner(patientSpinner, activityContext);
            } else if(patients.size() == 0) {
                Toast.makeText(activityContext, "Pas de patients disponibles", Toast.LENGTH_SHORT).show();
                SpinnerToolbox.disableSpinner(patientSpinner, activityContext);
            } else {
                ArrayList<String> nomCompletPatients = new ArrayList<>();
                for (HashMap<String, String> patient : patients) {
                    String nomComplet = "";
                    nomComplet += patient.get("prenom") + " ";
                    nomComplet += patient.get("nom");
                    nomCompletPatients.add(nomComplet);
                }

                ArrayAdapter patientAdapter = new ArrayAdapter<>(
                        activityContext,
                        R.layout.spinner_item,
                        nomCompletPatients
                );

                patientAdapter .setDropDownViewResource(R.layout.spinner_dropdown_item);
                patientSpinner.setAdapter(patientAdapter);
            }
        }

    }

    private void initialiseSpinnerWithIntegers(Spinner spinner, int firstValue, int finalValue){
        if(firstValue > finalValue){
            Log.e(TAG, "firstValue must be equal or greater than the finalValue");
            return;
        }

        ArrayList<Integer> integers = new ArrayList<>();
        for(int i=firstValue; i<=finalValue; i++){
            integers.add(i);
        }

        ArrayAdapter adapter = new ArrayAdapter<Integer>(
                activityContext,
                R.layout.spinner_item,
                integers
        );

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}

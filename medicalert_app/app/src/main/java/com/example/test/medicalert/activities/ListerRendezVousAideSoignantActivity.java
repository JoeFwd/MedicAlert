package com.example.test.medicalert.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.medicalert.Patient;
import com.example.test.medicalert.R;
import com.example.test.medicalert.RendezVous;
import com.example.test.medicalert.api_request.RendezVousRequest;
import com.example.test.medicalert.utils.DateValidator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class ListerRendezVousAideSoignantActivity extends Activity{
    private TextView emptyListMessage;
    private String noRendezVousMessage = "\n\nVous n'avez pas de rendez-vous";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lister_rendez_vous);


        ListView rendezVousListView = (ListView) findViewById(R.id.liste_rendez_vous);
        emptyListMessage = (TextView) findViewById(R.id.empty_filler);
        int default_id = -1, userId;
        SharedPreferences p = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);
        userId = p.getInt(getString(R.string.userId), default_id);


        ArrayList<HashMap<String, String>> rendezVousList = RendezVousRequest.getAllRendezVousWithPatientFullName(userId);
        if(rendezVousList == null){
            handleRequestError();
        } else {
            ArrayList<String> displayedRdv = new ArrayList<>();
            for(HashMap<String, String> rdv : rendezVousList) {
                String date = rdv.get(RendezVous.dateRdvKey);
                try {
                    if(!DateValidator.hasDatePassed(DateValidator.formatDate(date.substring(0, 10), DateValidator.dbFormat, DateValidator.inputFormat))) {
                        String stringifiredRdv = "Patient : " + rdv.get(Patient.prenomKey) + " " + rdv.get(Patient.nomKey) + "\n"
                                + "Date : " + DateValidator.formatDate(date.substring(0, 10), DateValidator.dbFormat, DateValidator.inputFormat)
                                + " à " + date.substring(11, 16).replace(':', 'h');
                        displayedRdv.add(stringifiredRdv);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue; //Ignorer le rendez vous dont la date est mal formaté
                }
                if(displayedRdv.size() == 0) {
                    emptyListMessage.setText(noRendezVousMessage);
                } else {
                    ArrayAdapter<String>adapter = new ArrayAdapter<>(
                            ListerRendezVousAideSoignantActivity.this,
                            android.R.layout.simple_list_item_1,
                            displayedRdv
                    );
                    rendezVousListView.setAdapter(adapter);
                }
            }
        }
    }

    private void handleRequestError(){
        emptyListMessage.setText(noRendezVousMessage);
        Toast.makeText(ListerRendezVousAideSoignantActivity.this, "Le service est indisponible", Toast.LENGTH_SHORT).show();
    }
}

package com.example.test.medicalert.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.medicalert.Medicament;
import com.example.test.medicalert.R;
import com.example.test.medicalert.Traitement;
import com.example.test.medicalert.api_request.MedicamentRequest;

import java.util.ArrayList;
import java.util.HashMap;

public class ListerMedicamentTraitementActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lister_medicament_traitement);

        ListView listview = (ListView) findViewById(R.id.liste);
        TextView emptyFiller = (TextView) findViewById(R.id.empty_filler);
        ArrayList<HashMap<String, String>> medicamentList = null;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            medicamentList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("medicamentList");
        }

        if(medicamentList == null){
            Toast.makeText(ListerMedicamentTraitementActivity.this, "Pas de données à lister", Toast.LENGTH_SHORT).show();
            finish();
        } else if(medicamentList.size() == 0){
            emptyFiller.setText("\n\nPas de médicaments");
        }

        ArrayList<String> stringifiedList = new ArrayList<>();

        for(HashMap<String, String> medicament : medicamentList){
            String stringifiedMed = "Nom : ";
            if(medicament.containsKey(Medicament.nomKey)){
                stringifiedMed += medicament.get(Medicament.nomKey) + "\n";
            } else {
                Medicament med = MedicamentRequest.getMedicamentById(Integer.parseInt(medicament.get(Traitement.idMedicamentKey)));
                if (med == null) {
                    stringifiedMed += "introuvable\n";
                } else {
                    stringifiedMed += med.getNom() + "\n";
                }
            }
            stringifiedMed += "Dosage : " + medicament.get(Traitement.dosageKey) + "\n"
            + "Date de péremption : " + medicament.get(Traitement.datePeremptionKey);
            stringifiedList.add(stringifiedMed);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                ListerMedicamentTraitementActivity.this,
                android.R.layout.simple_list_item_1,
                stringifiedList
        );
        listview.setAdapter(adapter);

    }
}

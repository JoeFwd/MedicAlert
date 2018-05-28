package com.example.test.medicalert.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.test.medicalert.R;
import com.example.test.medicalert.Traitement;
import com.example.test.medicalert.activities.barcode_scanner.ScanCodeActivity;
import com.example.test.medicalert.api_request.TraitementRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AjouterMedicamentTraitementMenuActivity extends Activity {
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private Button ajouterButton;
    private ArrayList<HashMap<String, String>> medicamentList = new ArrayList<>();;
    private Traitement traitement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_medicament_traitement_menu);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.containsKey(Traitement.medicamentListKey)) {
                medicamentList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra(Traitement.medicamentListKey);
            }
        }
        traitement = getIntent().getParcelableExtra(Traitement.CLASS_TAG);
        traitement.setMedicamentList(medicamentList);

        List<String> recherches = new ArrayList<>();
        recherches.add("Voir la liste");
        recherches.add("Ajouter un médicament dans la liste");
        recherches.add("Scanner un médicament");
        recherches.add("Supprimer un médicament de la liste");

        listView = (ListView) findViewById(R.id.recherche_type);
        ajouterButton = (Button) findViewById(R.id.add_button);

        adapter = new ArrayAdapter<>(
                AjouterMedicamentTraitementMenuActivity.this,
                android.R.layout.simple_list_item_1,
                recherches
        );
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if(position == 0){
                    if(medicamentList.size() == 0) {
                        Toast.makeText(AjouterMedicamentTraitementMenuActivity.this, "Pas de médicaments dans la liste", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(AjouterMedicamentTraitementMenuActivity.this, ListerMedicamentTraitementActivity.class);
                        intent.putExtra(Traitement.medicamentListKey, medicamentList);
                        intent.putExtra(Traitement.CLASS_TAG, traitement);
                        startActivity(intent);
                    }
                }
                if(position == 1){
                    Intent intent = new Intent(AjouterMedicamentTraitementMenuActivity.this, AjouterMedicamentDansTraitementActivity.class);
                    intent.putExtra(Traitement.medicamentListKey, medicamentList);
                    intent.putExtra(Traitement.CLASS_TAG, traitement);
                    startActivity(intent);
                }
                if(position == 2){
                    Intent intent = new Intent(AjouterMedicamentTraitementMenuActivity.this, ScanCodeActivity.class);
                    intent.putExtra(Traitement.medicamentListKey, medicamentList);
                    intent.putExtra(Traitement.CLASS_TAG, traitement);
                    startActivity(intent);
                }
            }
        });

        ajouterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(medicamentList.size() == 0){
                    Toast.makeText(AjouterMedicamentTraitementMenuActivity.this, "Pas de médicaments dans la liste", Toast.LENGTH_SHORT).show();
                } else {
                    if(!TraitementRequest.insertTraitement(traitement)){
                        Toast.makeText(AjouterMedicamentTraitementMenuActivity.this, "Le traitement n'a pas pu être ajouté", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AjouterMedicamentTraitementMenuActivity.this, "Le traitement a été ajouté", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AjouterMedicamentTraitementMenuActivity.this, AideSoignantMenuActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
            }

        });


    }
}
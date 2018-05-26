package com.example.test.medicalert.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.test.medicalert.Medicament;
import com.example.test.medicalert.R;
import com.example.test.medicalert.api_request.CategorieRequest;
import com.example.test.medicalert.api_request.MedicamentRequest;

import java.util.ArrayList;
import java.util.List;

public class RechercheMedicamentParCategorieActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private ArrayAdapter<String> adapterSearchResult;
    private Spinner categorieSpinner;
    private ListView searchResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_medicament_par_categorie);

        categorieSpinner = (Spinner) findViewById(R.id.categorie_spinner);
        searchResult = (ListView) findViewById(R.id.search_results);

        List<String> categories = CategorieRequest.getAllCategorieName();
        if(categories == null){
            categories = new ArrayList<>(1);
            categories.add("Pas de catégorie");
        }

        if(categories.size() == 0){
            categories.add("Pas de catégorie");
        }

        ArrayAdapter spinnerAdapter = new ArrayAdapter<String>(
                RechercheMedicamentParCategorieActivity.this,
                android.R.layout.simple_spinner_item,
                categories
        );

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorieSpinner.setAdapter(spinnerAdapter);
        categorieSpinner.setOnItemSelectedListener(this);

        adapterSearchResult = new ArrayAdapter<>(
                RechercheMedicamentParCategorieActivity.this,
                android.R.layout.simple_list_item_1,
                new ArrayList<String>()
        );
        searchResult.setAdapter(adapterSearchResult);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String categorie = parent.getItemAtPosition(position).toString();
        ArrayList<Medicament> medicaments = MedicamentRequest.getMedicamentByFormePharma(categorie);
        ArrayList<String> medicamentsInfo = new ArrayList<>();

        adapterSearchResult.clear();
        if(medicaments != null) {
            for(Medicament medicament : medicaments){
                medicamentsInfo.add("Nom : " + medicament.getNom() + "\n" + "Cip13 : " + medicament.getCip13());
            }
            adapterSearchResult.addAll(medicamentsInfo);
        }
        adapterSearchResult.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

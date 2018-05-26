package com.example.test.medicalert.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.test.medicalert.Medicament;
import com.example.test.medicalert.R;
import com.example.test.medicalert.api_request.MedicamentRequest;

import java.util.ArrayList;
import java.util.List;

public class RechercheMedicamentActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_medicament);

        List<String> recherches = new ArrayList<>();
        recherches.add("Recherche par nom");
        recherches.add("Recherche par forme pharmaceutique");

        listView = (ListView) findViewById(R.id.recherche_type);

        adapter = new ArrayAdapter<>(
                RechercheMedicamentActivity.this,
                android.R.layout.simple_list_item_1,
                recherches
        );
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if(position == 0){
                    Intent intent = new Intent(RechercheMedicamentActivity.this, RechercheMedicamentParNomActivity.class);
                    startActivity(intent);
                }
                if(position == 1){
                    Intent intent = new Intent(RechercheMedicamentActivity.this, RechercheMedicamentParCategorieActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}

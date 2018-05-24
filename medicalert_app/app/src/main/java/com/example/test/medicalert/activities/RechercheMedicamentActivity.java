package com.example.test.medicalert.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.test.medicalert.Medicament;
import com.example.test.medicalert.R;
import com.example.test.medicalert.api_request.MedicamentRequest;

import java.util.ArrayList;
import java.util.List;

public class RechercheMedicamentActivity extends AppCompatActivity {

    private ArrayAdapter<Medicament> adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_medicament);
        listView = (ListView) findViewById(R.id.search_results);

        adapter = new ArrayAdapter<>(
                RechercheMedicamentActivity.this,
                android.R.layout.simple_list_item_1,
                new ArrayList<Medicament>()
        );
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_menu);
        SearchView searchView = (SearchView)  item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Medicament> medicaments = MedicamentRequest.getMedicamentContainingString(newText, 20);
                if(medicaments != null) {
                    adapter.clear();
                    adapter.addAll(medicaments);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}

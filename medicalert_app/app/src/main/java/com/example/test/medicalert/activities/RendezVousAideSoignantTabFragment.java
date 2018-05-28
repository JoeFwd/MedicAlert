package com.example.test.medicalert.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.test.medicalert.R;

public class RendezVousAideSoignantTabFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_list, container, false);

        String[] menuItems = {
                "Mes rendez-vous", "Planifier un rendez-vous"
        };
        ListView listView = (ListView) view.findViewById(R.id.mainMenu);
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                menuItems
        );
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if(position == 0){
                    Intent intent = new Intent(getActivity(), ListerRendezVousAideSoignantActivity.class);
                    startActivity(intent);
                }
                if(position == 1){
                    Intent intent = new Intent(getActivity(), AjouterRendezVousActivity.class);
                    startActivity(intent);
                }
            }
        });

        listView.setAdapter(listViewAdapter);

        return view;
    }
}

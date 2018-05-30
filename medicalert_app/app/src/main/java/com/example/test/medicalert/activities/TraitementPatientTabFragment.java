package com.example.test.medicalert.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.medicalert.AideSoignant;
import com.example.test.medicalert.R;
import com.example.test.medicalert.RendezVous;
import com.example.test.medicalert.Traitement;
import com.example.test.medicalert.api_request.RendezVousRequest;
import com.example.test.medicalert.api_request.TraitementRequest;
import com.example.test.medicalert.utils.DateValidator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class TraitementPatientTabFragment extends Fragment{
    private TextView emptyListMessage;
    private String noTraitementVousMessage = "\n\nVous n'avez pas de traitements";
    private ListView traitementListView;
    private ArrayList<Traitement> traitementList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_patient, container, false);

        traitementListView = (ListView) view.findViewById(R.id.liste);
        emptyListMessage = (TextView) view.findViewById(R.id.empty_filler);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        int default_id = -1, userId;
        SharedPreferences p = getActivity().getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);
        userId = p.getInt(getString(R.string.userId), default_id);

        traitementList = TraitementRequest.getAllTraitementByPatientId(userId);
        if (traitementList  == null) {
            handleRequestError();
        } else {
            ArrayList<String> displayedTraitements = new ArrayList<>();
            for (Traitement t : traitementList) {
                String displayedTraitement = "Traitement : " + t.getNom() + "\n"
                + "Commencé le " + t.getDateDebut() + " et à prendre pendant " + t.getDuree() + " "
                + (t.getDuree()>1?"jours":"jour") + (t.getMatin()?" le matin,":"")
                + (t.getMidi()?" l'après-midi,":"") + (t.getMatin()?" le soir.":"");
                if(displayedTraitement.charAt(displayedTraitement.length() - 1) == ','){
                    displayedTraitement = displayedTraitement.substring(0, displayedTraitement.length() - 1) + ".";
                }
                displayedTraitements.add(displayedTraitement);
            }
            if (displayedTraitements.size() == 0) {
                emptyListMessage.setText(noTraitementVousMessage);
            } else {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getActivity(),
                        android.R.layout.simple_list_item_1,
                        displayedTraitements
                );

                traitementListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int position, long arg3) {
                            Intent intent = new Intent(getActivity(), ListerMedicamentTraitementActivity.class);
                            intent.putExtra(Traitement.medicamentListKey, traitementList.get(position).getMedicamentList());
                            startActivity(intent);
                    }
                });



                traitementListView.setAdapter(adapter);
            }
        }
    }

    private void handleRequestError(){
        emptyListMessage.setText(noTraitementVousMessage);
        Toast.makeText(getActivity(), "Le service est indisponible", Toast.LENGTH_SHORT).show();
    }
}


package com.example.test.medicalert.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.medicalert.AideSoignant;
import com.example.test.medicalert.R;
import com.example.test.medicalert.RendezVous;
import com.example.test.medicalert.api_request.RendezVousRequest;
import com.example.test.medicalert.utils.DateValidator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class RendezVousPatientTabFragment extends Fragment {
    private TextView emptyListMessage;
    private String noRendezVousMessage = "\n\nVous n'avez pas de rendez-vous";
    private ListView rendezVousListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_patient, container, false);

        rendezVousListView = (ListView) view.findViewById(R.id.liste);
        emptyListMessage = (TextView) view.findViewById(R.id.empty_filler);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        int default_id = -1, userId;
        SharedPreferences p = getActivity().getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);
        userId = p.getInt(getString(R.string.userId), default_id);

        ArrayList<HashMap<String, String>> rendezVousList = RendezVousRequest.getAllRendezVousWithAideSoignantFullName(userId);
        if (rendezVousList == null) {
            handleRequestError();
        } else {
            ArrayList<String> displayedRdv = new ArrayList<>();
            for (HashMap<String, String> rdv : rendezVousList) {
                String date = rdv.get(RendezVous.dateRdvKey);
                try {
                    if(!DateValidator.hasDatePassed(DateValidator.formatDate(date.substring(0, 10), DateValidator.dbFormat, DateValidator.inputFormat))) {
                        String stringifiredRdv = "Vous avez un rendez-vous avec :\n" + rdv.get(AideSoignant.prenomKey) + " " + rdv.get(AideSoignant.nomKey)
                                + " le " + DateValidator.formatDate(date.substring(0, 10), DateValidator.dbFormat, DateValidator.inputFormat)
                                + " à " + date.substring(11, 16).replace(':', 'h');
                        displayedRdv.add(stringifiredRdv);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue; //Ignorer le rendez vous dont la date est mal formaté
                }
            }
            if (displayedRdv.size() == 0) {
                emptyListMessage.setText(noRendezVousMessage);
            } else {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getActivity(),
                        android.R.layout.simple_list_item_1,
                        displayedRdv
                );
                rendezVousListView.setAdapter(adapter);
            }
        }
    }

    private void handleRequestError(){
        emptyListMessage.setText(noRendezVousMessage);
        Toast.makeText(getActivity(), "Le service est indisponible", Toast.LENGTH_SHORT).show();
    }
}
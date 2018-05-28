package com.example.test.medicalert.api_request;

import android.util.Log;

import com.example.test.medicalert.AideSoignant;
import com.example.test.medicalert.Medicament;
import com.example.test.medicalert.Patient;
import com.example.test.medicalert.Traitement;
import com.example.test.medicalert.utils.DateValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class TraitementRequest {
    public static String TRAITEMENT_URL = RequestValues.SERVER_URL + "/traitements";

    public static boolean insertTraitement(Traitement t){
        HashMap<String, String> params = new HashMap<>();
        params.put(Traitement.idPatientKey, t.getId_patient() + "");
        params.put(Traitement.idAideSoignantKey, t.getId_aide_soignant() + "");
        params.put(Traitement.nomKey, t.getNom());
        try {
            params.put(Traitement.dateDebutKey, DateValidator.formatDate(t.getDateDebut(), DateValidator.inputFormat, DateValidator.dbFormat));
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        params.put(Traitement.dureeTraitementKey, t.getDuree() + "");
        int mySqlBoolean = t.getMatin()?1:0;
        params.put(Traitement.matinKey, mySqlBoolean + "");
        mySqlBoolean = t.getMidi()?1:0;
        params.put(Traitement.apresMiditKey, mySqlBoolean + "");
        mySqlBoolean = t.getSoir()?1:0;
        params.put(Traitement.soirKey, mySqlBoolean + "");

        JSONArray jsonMedicamentList = new JSONArray();
        ArrayList<HashMap<String, String>> medicamentList = t.getMedicamentList();
        for(HashMap<String, String> medicament : medicamentList){
            JSONObject obj = new JSONObject(medicament);
            try {
                obj.put(Traitement.datePeremptionKey, DateValidator.formatDate(obj.getString(Traitement.datePeremptionKey), DateValidator.inputFormat, DateValidator.dbFormat));
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
            jsonMedicamentList.put(obj);
        }


        params.put(Traitement.medicamentListKey, jsonMedicamentList.toString());

        PostRequest request;
        Integer requestCode;
        try {
            request = new PostRequest(params);
            requestCode = request.execute(TRAITEMENT_URL).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        return requestCode == HttpURLConnection.HTTP_CREATED;
    }

    public static ArrayList<Traitement> getAllTraitementByPatientId(int patientId){
        GetRequestJSONArray request = new GetRequestJSONArray();
        JSONArray response;
        ArrayList<Traitement> traitementList = new ArrayList<>();

        try {
            response = request.execute(TRAITEMENT_URL + "/patient/" + patientId).get();
        } catch(Exception e) {e.printStackTrace(); return null;}

        if(response == null) return null;
        Log.v("res", response +"");
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject t = response.getJSONObject(i);
                Traitement patientTraitement = new Traitement(t.getInt(Traitement.idPatientKey), t.getInt(Traitement.idAideSoignantKey), t.getInt(Traitement.dureeTraitementKey),
                        t.getString(Traitement.nomKey), DateValidator.formatDate(t.getString(Traitement.dateDebutKey), DateValidator.dbFormat, DateValidator.inputFormat),
                        t.getInt(Traitement.matinKey)==1, t.getInt(Traitement.apresMiditKey)==1, t.getInt(Traitement.soirKey)==1, null);

                JSONArray medicamentListJSONArray = t.getJSONArray(Traitement.medicamentListKey);
                ArrayList<HashMap<String, String>> medicamentList = new ArrayList<>();

                //Log.v("Test", "" + medicamentListJSONArray);

                for(int j=0; j<medicamentListJSONArray.length(); j++){

                    HashMap<String, String> medicamentAttributes = new HashMap<>();
                    JSONObject medicament = (JSONObject) medicamentListJSONArray.get(j);
                    medicamentAttributes.put(Traitement.dosageKey, medicament.getString(Traitement.dosageKey));
                    medicamentAttributes.put(Traitement.datePeremptionKey, DateValidator.formatDate(medicament.getString(Traitement.datePeremptionKey), DateValidator.dbFormat, DateValidator.inputFormat));
                    medicamentAttributes.put(Medicament.nomKey, medicament.getString(Medicament.nomKey));
                    medicamentList.add(medicamentAttributes);
                }
                patientTraitement.setMedicamentList(medicamentList);
                traitementList.add(patientTraitement);

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } catch (ParseException e) {
                e.printStackTrace();
                continue; //ignore traitement;
            }
        }
        return traitementList;
    }
}

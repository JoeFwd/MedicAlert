package com.example.test.medicalert.api_request;

import android.util.Log;

import com.example.test.medicalert.AideSoignant;
import com.example.test.medicalert.Patient;
import com.example.test.medicalert.RendezVous;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class RendezVousRequest {
    public final static String RENDEZ_VOUS_URL = RequestValues.SERVER_URL + "/rendez_vous";

    public static boolean insertRendezVous (RendezVous rdv){
        HashMap<String, String> map = new HashMap<>();
        map.put(RendezVous.idAideSoignantKey, rdv.getIdAideSoignant() + "");
        map.put(RendezVous.idPatientKey, rdv.getIdPatient() + "");

        Calendar rdvDate = rdv.getDateRendezVous();
        int day = rdvDate.get(Calendar.DAY_OF_MONTH), month = rdvDate.get(Calendar.MONTH)+1, hour = rdvDate.get(Calendar.HOUR_OF_DAY), minute = rdvDate.get(Calendar.MINUTE);
        String stringifiedDay = day + "";
        if(day < 10) stringifiedDay = "0" + day;
        String stringifiedMonth = month + "";
        if(month < 10) stringifiedMonth = "0" + month;
        String stringifiedHour = hour + "";
        if(hour < 10) stringifiedHour = "0" + hour;
        String stringifiedMinute = minute + "";
        if(minute < 10) stringifiedMinute = "0" + minute;

        String stringifiedRdvDate = rdvDate.get(Calendar.YEAR) + "-" + stringifiedMonth + "-" + stringifiedDay + " "
                + stringifiedHour + ":" + stringifiedMinute + ":00"  ;

        map.put(RendezVous.dateRdvKey, stringifiedRdvDate);
        PostRequest request;
        Integer requestCode;
        try {
            request = new PostRequest(map);
            requestCode = request.execute(RENDEZ_VOUS_URL).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        return requestCode == HttpURLConnection.HTTP_CREATED;
    }

    public static ArrayList<HashMap<String, String>> getAllRendezVousWithPatientFullName(int aideSoignantId){
        GetRequestJSONArray request = new GetRequestJSONArray();
        JSONArray response;
        ArrayList<HashMap<String, String>> rendezVous = new ArrayList<>();

        try {
            response = request.execute(RENDEZ_VOUS_URL + "/aide_soignant/" + aideSoignantId).get();
        } catch(Exception e) {e.printStackTrace(); return null;}

        if(response == null) return null;

        for (int i = 0; i < response.length(); i++) {
            HashMap<String, String> rdv = new HashMap<>();
            try {
                JSONObject rendezVousJsonObj = response.getJSONObject(i);
                rdv.put(Patient.nomKey, rendezVousJsonObj.getString(Patient.nomKey));
                rdv.put(Patient.prenomKey, rendezVousJsonObj.getString(Patient.prenomKey));
                rdv.put(RendezVous.dateRdvKey, rendezVousJsonObj.getString(RendezVous.dateRdvKey));
                rendezVous.add(rdv);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return rendezVous;
    }

    public static ArrayList<HashMap<String, String>> getAllRendezVousWithAideSoignantFullName(int patientId){
        GetRequestJSONArray request = new GetRequestJSONArray();
        JSONArray response;
        ArrayList<HashMap<String, String>> rendezVous = new ArrayList<>();

        try {
            response = request.execute(RENDEZ_VOUS_URL + "/patient/" + patientId).get();
        } catch(Exception e) {e.printStackTrace(); return null;}

        if(response == null) return null;

        for (int i = 0; i < response.length(); i++) {
            HashMap<String, String> rdv = new HashMap<>();
            try {
                JSONObject rendezVousJsonObj = response.getJSONObject(i);
                rdv.put(AideSoignant.nomKey, rendezVousJsonObj.getString(AideSoignant.nomKey));
                rdv.put(AideSoignant.prenomKey, rendezVousJsonObj.getString(AideSoignant.prenomKey));
                rdv.put(RendezVous.dateRdvKey, rendezVousJsonObj.getString(RendezVous.dateRdvKey));
                rendezVous.add(rdv);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return rendezVous;
    }

    public static ArrayList<RendezVous> getAllRendezVousByPatientId(int patientId){
        GetRequestJSONArray request = new GetRequestJSONArray();
        JSONArray response;
        ArrayList<RendezVous> rendezVous = new ArrayList<>();

        try {
            response = request.execute(RENDEZ_VOUS_URL + "/patient/" + patientId).get();
        } catch(Exception e) {e.printStackTrace(); return null;}

        if(response == null) return null;

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject rendezVousJsonObj = response.getJSONObject(i);
                int idPatient = rendezVousJsonObj.getInt(RendezVous.idPatientKey);
                int idAideSoignant = rendezVousJsonObj.getInt(RendezVous.idAideSoignantKey);
                String stringifiedDateRdv = rendezVousJsonObj.getString(RendezVous.dateRdvKey);

                Calendar dateRdv = Calendar.getInstance();
                dateRdv.set(Calendar.YEAR, Integer.parseInt(stringifiedDateRdv.substring(0, 4)));
                dateRdv.set(Calendar.MONTH, Integer.parseInt(stringifiedDateRdv.substring(5, 7)) - 1);
                dateRdv.set(Calendar.DAY_OF_MONTH, Integer.parseInt(stringifiedDateRdv.substring(8, 10)));

                dateRdv.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringifiedDateRdv.substring(11, 13)));
                dateRdv.set(Calendar.MINUTE, Integer.parseInt(stringifiedDateRdv.substring(14, 16)));

                rendezVous.add(new RendezVous(idPatient, idAideSoignant, dateRdv));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return rendezVous;
    }
}

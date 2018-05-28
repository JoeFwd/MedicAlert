package com.example.test.medicalert.api_request;

import android.util.Log;

import com.example.test.medicalert.AideSoignant;
import com.example.test.medicalert.utils.DateValidator;
import com.example.test.medicalert.Patient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public final class PatientRequest {
    public static String PATIENT_URL = RequestValues.SERVER_URL + "/patients";

    private PatientRequest(){}

    public static boolean insertPatient (Patient patient){
        HashMap<String, String> map = new HashMap<>();
        map.put(Patient.emailKey, patient.getEmail());
        map.put(Patient.passwordKey, patient.getPassword());
        map.put(Patient.prenomKey, patient.getPrenom());
        map.put(Patient.nomKey, patient.getNom());
        String dateNaissance = patient.getDateNaissance(), parsedDateNaissance;

        if(!DateValidator.isDateValid(dateNaissance, DateValidator.inputFormat)){
            Log.e("PatientRequest : ", "date_naissance not valid");
            return false;
        }

        try {
            parsedDateNaissance = DateValidator.formatDate(patient.getDateNaissance(), DateValidator.inputFormat, DateValidator.dbFormat);
        } catch (ParseException e) {
            e.printStackTrace();
            parsedDateNaissance = DateValidator.DEFAULT_DATE;
        }

        map.put(Patient.dateNaissanceKey, parsedDateNaissance);
        map.put(Patient.idAideSoignantKey, patient.getIdAideSoignant() + "");
        PostRequest request;
        Integer requestCode;
        try {
            request = new PostRequest(map);
            requestCode = request.execute(PATIENT_URL).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        return (requestCode == HttpURLConnection.HTTP_CREATED)?true:false;
    }

    public static ArrayList<HashMap<String, String>> getAllPatientByAideSoignantId(int aideSoignantId){
        GetRequestJSONArray request = new GetRequestJSONArray();
        JSONArray response;
        ArrayList<HashMap<String, String>> patients = new ArrayList<>();

        try {
            response = request.execute(PATIENT_URL+ "/id_aide_soignant/" + aideSoignantId).get();
        } catch(Exception e) {e.printStackTrace(); return null;}

        if(response == null) return null;

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject patientJsonObj = response.getJSONObject(i);
                HashMap<String, String> nameInfos = new HashMap<>();
                nameInfos.put(Patient.prenomKey, patientJsonObj.getString(AideSoignant.prenomKey));
                nameInfos.put(Patient.nomKey, patientJsonObj.getString(AideSoignant.nomKey));
                nameInfos.put("id", patientJsonObj.getInt("id") + "");
                patients.add(nameInfos);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return patients;
    }
}

package com.example.test.medicalert.api_request;

import android.util.Log;

import com.example.test.medicalert.utils.DateValidator;
import com.example.test.medicalert.Patient;

import java.net.HttpURLConnection;
import java.text.ParseException;
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

        if(!DateValidator.isDateValid(dateNaissance, "dd/MM/yyyy")){
            Log.e("PatientRequest : ", "date_naissance not valid");
            return false;
        }

        try {
            parsedDateNaissance = DateValidator.formatDate(patient.getDateNaissance(), "dd/MM/yyyy","yyyy-MM-dd");
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
}

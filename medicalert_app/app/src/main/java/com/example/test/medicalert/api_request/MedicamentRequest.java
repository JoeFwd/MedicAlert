package com.example.test.medicalert.api_request;

import com.example.test.medicalert.Medicament;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public final class MedicamentRequest {
    public static String MEDICAMENT_URL = RequestValues.SERVER_URL + "/medicaments";

    private MedicamentRequest(){}

    private static Medicament getMedicamentFromJSONObject(JSONObject jsonObj){
        if(jsonObj == null) return null;

        Medicament medicament;
        String cip13, nom, formePharma;
        try {
            cip13 = jsonObj.getString(Medicament.cip13Key);
            nom = jsonObj.getString(Medicament.nomKey);
            formePharma = jsonObj.getString(Medicament.formePharmaKey);
            medicament = new Medicament(cip13, nom, formePharma);
        } catch (JSONException e) {
            e.printStackTrace();
            medicament = null;
        }

        return medicament;
    }

    public static Medicament getMedicamentByCip13(String wantedCip13){
        JSONObject jsonObj;
        GetRequestJSONObject request = new GetRequestJSONObject();
        try {
            jsonObj = request.execute(MEDICAMENT_URL + "/" + Medicament.cip13Key + "/" + wantedCip13).get();
        } catch(Exception e) {e.printStackTrace(); return null;}

        if(jsonObj == null) return null;

        return getMedicamentFromJSONObject(jsonObj);
    }

    private static ArrayList<Medicament> getMedicamentContainingStringFromUrl(String stringUrl){
        ArrayList<Medicament> medicaments;
        GetRequestJSONArray request = new GetRequestJSONArray();
        JSONArray jsonArray;

        try {
            jsonArray = request.execute(stringUrl).get();
        } catch(Exception e) {e.printStackTrace(); return null;}

        if(jsonArray == null) return null;
        medicaments = new ArrayList<Medicament>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                medicaments.add(getMedicamentFromJSONObject(jsonArray.getJSONObject(i)));
            }
            catch(JSONException e){
                e.printStackTrace();
                return null;
            }
        }
        return medicaments;
    }

    public static ArrayList<Medicament> getMedicamentContainingString(String nom){
        return getMedicamentContainingStringFromUrl(MEDICAMENT_URL + "/" + Medicament.nomKey + "/" + nom);
    }

    public static ArrayList<Medicament> getMedicamentContainingString(String nom, int limit){
        return getMedicamentContainingStringFromUrl(MEDICAMENT_URL + "/" + Medicament.nomKey + "/" + nom + "/" + limit);
    }

    public static boolean insertMedicament (Medicament med){
        HashMap<String, String> map = new HashMap<>();
        map.put(Medicament.cip13Key, med.getCip13());
        map.put(Medicament.nomKey, med.getNom());
        map.put(Medicament.formePharmaKey, med.getFormePharma());
        PostRequest request;
        Integer requestCode;
        try {
            request = new PostRequest(map);
            requestCode = request.execute(MEDICAMENT_URL).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        return (requestCode == HttpURLConnection.HTTP_CREATED)?true:false;
    }

    public static boolean deleteMedicament (String cip13){
        DeleteRequest request;
        Integer requestCode;
        try {
            request = new DeleteRequest();
            requestCode = request.execute(MEDICAMENT_URL + "/" + cip13).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        return (requestCode == HttpURLConnection.HTTP_OK)?true:false;
    }

    public static boolean modifyMedicament (String cip13, HashMap<String, String> params){
        PatchRequest request;
        Integer requestCode;
        try {
            request = new PatchRequest(params);
            requestCode = request.execute(MEDICAMENT_URL + "/" + cip13).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        return (requestCode == HttpURLConnection.HTTP_OK)?true:false;
    }
}

package com.example.test.medicalert.api_request;

import com.example.test.medicalert.AideSoignant;
import com.example.test.medicalert.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AideSoignantRequest {
    public static String AIDE_SOIGNANT_URL = RequestValues.SERVER_URL + "/aide_soignants";

    public static ArrayList<HashMap<String, String>> getAllAideSoignantByFullName(){
        GetRequestJSONArray request = new GetRequestJSONArray();
        JSONArray response;
        ArrayList<HashMap<String, String>> aideSoignants = new ArrayList<>();

        try {
            response = request.execute(AIDE_SOIGNANT_URL + "/nom_complet").get();
        } catch(Exception e) {e.printStackTrace(); return null;}

        if(response == null) return null;

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject aideSoignantJsonObj = response.getJSONObject(i);
                HashMap<String, String> nameInfos = new HashMap<>();
                nameInfos.put(AideSoignant.prenomKey, aideSoignantJsonObj.getString(AideSoignant.prenomKey));
                nameInfos.put(AideSoignant.nomKey, aideSoignantJsonObj.getString(AideSoignant.nomKey));
                nameInfos.put("id", aideSoignantJsonObj.getInt("id") + "");
                aideSoignants.add(nameInfos);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return aideSoignants;
    }
}

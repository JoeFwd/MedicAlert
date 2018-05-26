package com.example.test.medicalert.api_request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategorieRequest {
    public static String CATEGORIE_URL = RequestValues.SERVER_URL + "/categorie";

    public static ArrayList<String> getAllCategorieName(){
        GetRequestJSONArray request = new GetRequestJSONArray();
        JSONArray response;
        ArrayList<String> categories = new ArrayList<>();

        try {
            response = request.execute(CATEGORIE_URL).get();
        } catch(Exception e) {e.printStackTrace(); return null;}

        if(response == null) return null;

        for (int i = 0; i < response.length(); ++i) {
            try {
                JSONObject categorieJsonObj = response.getJSONObject(i);
                categories.add(categorieJsonObj.getString("categorie"));
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return categories;
    }
}

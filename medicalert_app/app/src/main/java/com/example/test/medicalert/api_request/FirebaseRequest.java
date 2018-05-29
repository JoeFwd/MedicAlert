package com.example.test.medicalert.api_request;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class FirebaseRequest {
    public static String FIREBASE_TOKENS_URL = RequestValues.SERVER_URL + "/firebase_tokens";
    public static String tokenKey = "token";
    public static String idPatientKey = "id_patient";

    private FirebaseRequest(){}

    public static boolean insertToken (String token, int id_patient){
        HashMap<String, String> map = new HashMap<>();
        map.put(tokenKey, token);
        map.put(idPatientKey, id_patient + "");

        PostRequest request;
        Integer requestCode;
        try {
            request = new PostRequest(map);
            requestCode = request.execute(FIREBASE_TOKENS_URL).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        return (requestCode == HttpURLConnection.HTTP_CREATED)?true:false;
    }

    public static boolean updateToken (String token, int id_patient){
        HashMap<String, String> map = new HashMap<>();
        map.put(tokenKey, token);

        PatchRequest request;
        Integer requestCode;
        try {
            request = new PatchRequest(map);
            requestCode = request.execute(FIREBASE_TOKENS_URL + "/token/" + id_patient).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        return (requestCode == HttpURLConnection.HTTP_OK)?true:false;
    }

    public static String getPatientToken (int id_patient){
        GetRequestJSONObject request;
        JSONObject tokenJsonObj;
        try {
            request = new GetRequestJSONObject();
            tokenJsonObj = request.execute(FIREBASE_TOKENS_URL + "/token/" + id_patient).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }

        if(tokenJsonObj == null) return null;

        String token;
        try {
            token = tokenJsonObj.getString(tokenKey);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return token;
    }

    public static boolean sendNofiticationViaFCM (String token, String title, String message){
        ConnectFCMTask request;
        Boolean success;
        try {
            request = new ConnectFCMTask();
            success = request.execute("https://fcm.googleapis.com/fcm/send", token, title, message).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        return success;
    }
}

package com.example.test.medicalert.api_request;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public final class RequestValues {
    protected static String SERVER_URL = "http://192.168.0.46:8080";
    protected static String REQUEST_GET = "GET";
    protected static String REQUEST_POST = "POST";
    protected static String REQUEST_DELETE = "DELETE";
    protected static String REQUEST_PATCH = "PATCH";
    protected static int READ_TIMEOUT = 3000;
    protected static int CONNECTION_TIMEOUT = 3000;

    private RequestValues(){}

    protected static String getResponse(HttpURLConnection connection){
        String inputLine;
        try {
            InputStreamReader streamReader = new
                    InputStreamReader(connection.getInputStream(), "UTF-8");

            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }

            reader.close();
            streamReader.close();

            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected static HttpURLConnection createConnection(String stringUrl, String requestType){
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(requestType);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    protected static String getStringifiedGetRequestResult(String stringUrl){
        String result;
        try {
            HttpURLConnection connection = createConnection(stringUrl, REQUEST_GET);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                result = getResponse(connection);
            } else {
                result = null;
            }
        }
        catch(IOException e){
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    protected static JSONObject createJSONObject(String jsonString){
        JSONObject jsonResult;
        try {
            if(jsonString == null){
                jsonResult = null;
            } else {
                jsonResult = new JSONObject(jsonString);
            }
        }
        catch(JSONException e){
            e.printStackTrace();
            jsonResult = null;
        }
        return jsonResult;
    }

    protected static boolean sendParameters(HttpURLConnection connection, HashMap<String, String> parameters){
        connection.setDoOutput(true);

        String postParametersString = "";
        if(parameters.size() > 0){
            for (String key : parameters.keySet()) {
                postParametersString += key + "=" + parameters.get(key) + "&";
            }
            postParametersString = postParametersString.substring(0, postParametersString.length() - 1);
        }

        PrintWriter out = null;
        try {
            out = new PrintWriter(connection.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        out.print(postParametersString);
        out.close();
        return true;
    }
}


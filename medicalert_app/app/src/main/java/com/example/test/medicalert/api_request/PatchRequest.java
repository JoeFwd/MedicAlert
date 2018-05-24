package com.example.test.medicalert.api_request;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;

public class PatchRequest extends AsyncTask<String, Void, Integer> {
    private HashMap<String, String> parameters;

    public PatchRequest(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    protected Integer doInBackground(String... params) {
        try{
            HttpURLConnection connection = RequestValues.createConnection(params[0], RequestValues.REQUEST_PATCH);
            if(connection == null) return HttpURLConnection.HTTP_BAD_REQUEST;

            if(!RequestValues.sendParameters(connection, this.parameters)){
                return HttpURLConnection.HTTP_BAD_REQUEST;
            }
            return connection.getResponseCode();
        } catch (IOException e) {
            return HttpURLConnection.HTTP_CLIENT_TIMEOUT;
        }
    }

    @Override
    protected void onPostExecute(Integer result){
        super.onPostExecute(result);
    }
}

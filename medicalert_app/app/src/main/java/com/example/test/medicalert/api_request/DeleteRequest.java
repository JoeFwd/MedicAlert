package com.example.test.medicalert.api_request;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;

public class DeleteRequest extends AsyncTask<String, Void, Integer> {

    @Override
    protected Integer doInBackground(String... params) {
        try {
            HttpURLConnection connection = RequestValues.createConnection(params[0], RequestValues.REQUEST_DELETE);
            if(connection == null) return HttpURLConnection.HTTP_BAD_REQUEST;

            connection.setDoOutput(true);
            connection.connect();
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

package com.example.test.medicalert.api_request;

import android.os.AsyncTask;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.util.HashMap;

public class PostRequestWithResponse extends AsyncTask<String, Void, JSONObject> {
    private HashMap<String, String> parameters;

    public PostRequestWithResponse(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
            HttpURLConnection connection = RequestValues.createConnection(params[0], RequestValues.REQUEST_POST);
            if(connection == null) return null;
            if(!RequestValues.sendParameters(connection, this.parameters)){
                return null;
            }
            return RequestValues.createJSONObject(RequestValues.getResponse(connection));
    }

    @Override
    protected void onPostExecute(JSONObject result){
        super.onPostExecute(result);
    }
}

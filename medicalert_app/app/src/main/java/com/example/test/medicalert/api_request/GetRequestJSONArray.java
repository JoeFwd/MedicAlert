package com.example.test.medicalert.api_request;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONArray;

public class GetRequestJSONArray extends AsyncTask<String, Void, JSONArray> {

    @Override
    protected JSONArray doInBackground(String... params) {
        String result = RequestValues.getStringifiedGetRequestResult(params[0]);
        JSONArray jsonResult;
        try {
            if(result == null){
                jsonResult = null;
            } else {
                jsonResult = new JSONArray(result);
            }
        }
        catch(JSONException e){
            e.printStackTrace();
            jsonResult = null;
        }

        return jsonResult;
    }

    @Override
    protected void onPostExecute(JSONArray result){
        super.onPostExecute(result);
    }
}

package com.example.test.medicalert.api_request;

import android.os.AsyncTask;

import org.json.JSONObject;

public class GetRequestJSONObject extends AsyncTask<String, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(String... params) {
        return RequestValues.createJSONObject(RequestValues.getStringifiedGetRequestResult(params[0]));
    }

    @Override
    protected void onPostExecute(JSONObject result){
        super.onPostExecute(result);
    }
}

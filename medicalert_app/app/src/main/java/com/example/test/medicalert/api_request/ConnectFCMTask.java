package com.example.test.medicalert.api_request;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectFCMTask extends AsyncTask<String, Void, Boolean>{
        public ConnectFCMTask() {}

        @Override
        protected Boolean doInBackground(String... params) {
            try{
                URL url = new URL(params[0]);
                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();

                connection.setRequestMethod(RequestValues.REQUEST_POST);
                connection.setConnectTimeout(RequestValues.CONNECTION_TIMEOUT);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty ("Authorization", "key=AAAA8PyEOMo:APA91bEe7FsO3CL0rxOORDzPZbK8I6drpmAmR2dqYp_fh2JBpj9-C7F_5ufdGxfE1zlhA9ZSt7G6Cc5Axz6I8ncwV1N9FII15rPfG3Nm8KvKyAsFFMiCSvb1ddpdET4iwH6RqgLCZirH");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                if(connection == null) return false;

                JSONObject parent = new JSONObject();
                JSONObject notification = new JSONObject();
                String token = params[1];

                notification.put("title", params[2]);
                notification.put("text", params[3]);
                parent.put("to", token);
                parent.put("notification", notification);

                OutputStream os = connection.getOutputStream();
                os.write(parent.toString().getBytes("UTF-8"));
                os.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (connection.getInputStream())));

                String output;
                while ((output = br.readLine()) != null) {
                    Log.v("Google response", output);
                }
                return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean result){
            super.onPostExecute(result);
        }
    }


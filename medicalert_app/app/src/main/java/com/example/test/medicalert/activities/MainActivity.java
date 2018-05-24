package com.example.test.medicalert.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.test.medicalert.Medicament;
import com.example.test.medicalert.Patient;
import com.example.test.medicalert.R;
import com.example.test.medicalert.api_request.AuthorisationRequest;
import com.example.test.medicalert.api_request.MedicamentRequest;
import com.example.test.medicalert.api_request.PatientRequest;
import com.example.test.medicalert.api_request.PostRequestWithResponse;

import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //via offline USB 192.168.42.10
        //10.0.2.2 emulateur
        // 127.0.0.1
        //192.168.56.1

        //Log.v("Medicament", MedicamentRequest.getMedicamentByCip13("3400936975569")+"");
        //Log.v("Medicament", MedicamentRequest.getMedicamentByCip13("340093697556")+"");
        //Log.v("Medicament", MedicamentRequest.getMedicamentContainingString("3400936975569", 4)+"");
        //Log.v("Medicament", MedicamentRequest.getMedicamentContainingString("doliprane", 4)+"");

        /*HashMap<String, String> map = new HashMap<>();
        map.put("cip13", "1234567892");
        map.put("nom", "doliprane v2");
        map.put("formePharma", "solution");

        Log.v("Medicament", MedicamentRequest.insertMedicament(new Medicament("1234567891","doli", "comprimé"))+"");
        Log.v("Medicament", MedicamentRequest.modifyMedicament("1234567891", map)+"");
        Log.v("Medicament", MedicamentRequest.deleteMedicament("1234567892")+"");*/


        /*PostRequestWithResponse request = new PostRequestWithResponse(map);
        JSONObject result;
        try {
            result = request.execute("http://192.168.0.46:8080/medicaments").get();
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }

        TextView myAwesomeTextView = (TextView)findViewById(R.id.serverResponse);
        if(result == null){
            myAwesomeTextView.setText("Error");
        } else {
            myAwesomeTextView.setText(result.toString());
        }*/
        /*PatientRequest.insertPatient(new Patient("jforward@live.fr", "stratego2010", "Joël","Forward", "03/04/1996"));
        Log.v("token", AuthorisationRequest.login("jforward@live.fr", "stratego2010"));*/
    }
}
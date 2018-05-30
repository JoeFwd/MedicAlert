package com.example.test.medicalert.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.test.medicalert.R;
import com.example.test.medicalert.Traitement;
import com.example.test.medicalert.activities.PatientMenuActivity;
import com.example.test.medicalert.api_request.TraitementRequest;

import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "channel_id_1";
    private static final int NOTIFICATION_ID = 1100;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preferences), context.MODE_PRIVATE);

        int priseMedCounter = sharedPreferences.getInt(context.getString(R.string.prise_med_counter), -1);
        int patientId = sharedPreferences.getInt(context.getString(R.string.userId), -1);
        if(priseMedCounter == -1){
            priseMedCounter = 0;
        }

        String message = "N'oubliez pas de prendre vos médicaments !";
        ArrayList<Traitement> traitements = TraitementRequest.getAllTraitementByPatientId(patientId);
        if(traitements == null && traitements.size() == 0) return;
        if(priseMedCounter == 0){
            for(Traitement t : traitements){
                if(t.getMatin()){
                    Notification.createNotification(context, "MedicAlert", message, new Intent(context, PatientMenuActivity.class));
                    break;
                }
            }
        }
        if(priseMedCounter == 1){
            for(Traitement t : traitements){
                if(t.getMidi()){
                    Notification.createNotification(context, "MedicAlert", message, new Intent(context, PatientMenuActivity.class));
                    break;
                }
            }
        }
        if(priseMedCounter == 2){
            for(Traitement t : traitements){
                if(t.getSoir()){
                    Notification.createNotification(context, "MedicAlert", message, new Intent(context, PatientMenuActivity.class));
                    break;
                }
            }
        }

        priseMedCounter = (priseMedCounter+1)%3;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.prise_med_counter), priseMedCounter);
        editor.apply();
    }
}
package com.example.test.medicalert.notification;

import android.content.Intent;
import android.util.Log;

import com.example.test.medicalert.RendezVous;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingServic";

    public FirebaseMessagingService() {}

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();
        Log.d(TAG, "onMessageReceived: Message Received: \n" +
                "Title: " + title + "\n" +
                "Message: " + message);

        Intent intent = new Intent(this, RendezVous.class);
        Notification.createNotification(this, title, message, intent);
    }

    @Override
    public void onDeletedMessages() {
    }

}

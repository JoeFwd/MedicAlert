package com.example.test.medicalert.firebase;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.test.medicalert.R;
import com.example.test.medicalert.api_request.FirebaseRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
        private static final String TAG = "MyFirebaseIIDService";

        /**
         * Called if InstanceID token is updated. This may occur if the security of
         * the previous token had been compromised. Note that this is called when the InstanceID token
         * is initially generated so this is where you would retrieve the token.
         */
        // [START refresh_token]
        @Override
        public void onTokenRefresh() {
            // Get updated InstanceID token.
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "Refreshed token: " + refreshedToken);

            // If you want to send messages to this application instance or
            // manage this apps subscriptions on the server side, send the
            // Instance ID token to your app server.
            sendRegistrationToServer(refreshedToken);
        }
        // [END refresh_token]

        /**
         * Persist token to third-party servers.
         *
         * Modify this method to associate the user's FCM InstanceID token with any server-side account
         * maintained by your application.
         *
         * @param token The new token.
         */
        private void sendRegistrationToServer(String token) {
            SharedPreferences p = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);
            int userId = p.getInt(getString(R.string.userId), -1);
            if(userId == -1){
                Log.e(TAG, "Couldn't update firebase token");
                return;
            }
            if(!FirebaseRequest.updateToken(token, userId)){
                Log.e(TAG, "Couldn't update firebase token");
            }
        }
    }
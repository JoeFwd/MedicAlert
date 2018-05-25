package com.example.test.medicalert.api_request;

import com.example.test.medicalert.Patient;
import org.json.JSONObject;
import java.util.HashMap;

public final class AuthorisationRequest {
    public static String AUTHORISATION_URL = RequestValues.SERVER_URL;

    private AuthorisationRequest(){}

    /**
     * Checks if the email and password are matching the one's in the database. If yes, then a token is generated and sent to the user.
     * Otherwise this method returns null;
     */
    public static JSONObject login(String email, String password){
        JSONObject jsonToken=null;
        HashMap<String, String> map = new HashMap<>();
        map.put(Patient.emailKey, email);
        map.put(Patient.passwordKey, password);
        PostRequestWithResponse request = new PostRequestWithResponse(map);

        try {
            jsonToken = request.execute(AUTHORISATION_URL + "/login").get();
            if(jsonToken == null) return null;
            if(jsonToken.getBoolean("succes")){
                return jsonToken;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

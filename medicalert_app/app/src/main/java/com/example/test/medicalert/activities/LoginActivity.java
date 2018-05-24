package com.example.test.medicalert.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.medicalert.api_request.AuthorisationRequest;
import com.example.test.medicalert.utils.EditTextToolbox;
import com.example.test.medicalert.R;

public class LoginActivity extends Activity{

    private EditText emailEditText, passwordEditText;
    private View activityView;
    private Context activityContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = (EditText) findViewById(R.id.email_box);
        passwordEditText = (EditText) findViewById(R.id.password_box);
        Button loginButton = (Button) findViewById(R.id.login_button);
        TextView inscriptionTextView = findViewById(R.id.inscrisption_redirect);

        activityView = this.findViewById(android.R.id.content);
        activityContext = this;

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (EditTextToolbox.areEmptyFields(R.id.login_wrapper, activityView)) {
                    Toast.makeText(LoginActivity.this, "Veuillez compléter tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!EditTextToolbox.isEmailValid(email)) {
                    Toast.makeText(LoginActivity.this, "Votre email n'est pas valide", Toast.LENGTH_SHORT).show();
                    EditTextToolbox.setEditTextToBlank(emailEditText);
                    return;
                }

                String token = AuthorisationRequest.login(email.trim(), password.trim());
                if(token == null){
                    Toast.makeText(LoginActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                    EditTextToolbox.setEditTextToBlank(emailEditText);
                    EditTextToolbox.setEditTextToBlank(passwordEditText);
                    return;
                }

                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.tokenKey), token);
                editor.putString(getString(R.string.emailKey), email);
                editor.apply();

                Intent intent = new Intent(activityContext, AideSoignantMenuActivity.class);
                startActivity(intent);
            }

        });

        inscriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activityContext, SignUpActivity.class);
                startActivity(intent);
            }

        });
    }

}

package com.example.test.medicalert.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test.medicalert.utils.DateValidator;
import com.example.test.medicalert.utils.EditTextToolbox;
import com.example.test.medicalert.Patient;
import com.example.test.medicalert.R;
import com.example.test.medicalert.api_request.PatientRequest;

public class SignUpActivity extends Activity {
    private EditText emailEditText, passwordEditText, confirmPasswordEditText, prenewNomEditText, newNomEditText, dateNaissanceEditText;
    private Context activityContext;
    private View activityView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEditText = (EditText) findViewById(R.id.email_box);
        passwordEditText = (EditText) findViewById(R.id.password_box);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirm_password_box);
        prenewNomEditText = (EditText) findViewById(R.id.prenom_box);
        newNomEditText = (EditText) findViewById(R.id.nom_box);
        dateNaissanceEditText = (EditText) findViewById(R.id.date_naissance_box);

        dateNaissanceEditText.setHint("ex : 04/05/1986");

        /*SharedPreferences p = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);
        emailEditText.setText(p.getString(getString(R.string.emailKey), "noEmail"));
        passwordEditText.setText(p.getString(getString(R.string.tokenKey), "noPassword"));*/

        Button registerButton = (Button) findViewById(R.id.register_button);
        activityContext = this;

        activityView = this.findViewById(android.R.id.content);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                String prenom = prenewNomEditText.getText().toString();
                String nom = newNomEditText.getText().toString();
                String dateNaissance = dateNaissanceEditText.getText().toString();

                if (EditTextToolbox.areEmptyFields(R.id.register_wrapper, activityView)) {
                    Toast.makeText(SignUpActivity.this, "Veuillez compléter tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!EditTextToolbox.isEmailValid(email)) {
                    Toast.makeText(SignUpActivity.this, "Votre email n'est pas valide", Toast.LENGTH_SHORT).show();
                    EditTextToolbox.setEditTextToBlank(emailEditText);
                    return;
                }
                if (!isComfirmPasswordCorrect(password, confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Votre confirmation de mot de passe est fausse", Toast.LENGTH_SHORT).show();
                    EditTextToolbox.setEditTextToBlank(passwordEditText);
                    EditTextToolbox.setEditTextToBlank(confirmPasswordEditText);
                    return;
                }
                if (!DateValidator.isDateValid(dateNaissance, "dd/MM/yyyy")) {
                    Toast.makeText(SignUpActivity.this, "Votre date de naissance n'est pas valide", Toast.LENGTH_SHORT).show();
                    EditTextToolbox.setEditTextToBlank(dateNaissanceEditText);
                    return;
                }

                if (PatientRequest.insertPatient(new Patient(email.trim(), password.trim(), prenom.trim(), nom.trim(), dateNaissance.trim()))) {
                    Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activityContext, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignUpActivity.this, "Désolé, nous n'avons pas pu vous ajouter.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean isComfirmPasswordCorrect(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
}
package com.example.test.medicalert.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.test.medicalert.api_request.AideSoignantRequest;
import com.example.test.medicalert.utils.DateValidator;
import com.example.test.medicalert.utils.EditTextToolbox;
import com.example.test.medicalert.Patient;
import com.example.test.medicalert.R;
import com.example.test.medicalert.api_request.PatientRequest;

import java.util.ArrayList;
import java.util.HashMap;

public class SignUpActivity extends Activity {
    private EditText emailEditText, passwordEditText, confirmPasswordEditText, prenewNomEditText, newNomEditText, dateNaissanceEditText;
    private Spinner aideSoignantSpinner;
    private ArrayList<HashMap<String, String>> aideSoignants;
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
        aideSoignantSpinner = (Spinner) findViewById(R.id.aide_soignant_spinner);

        /*SharedPreferences p = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);
        emailEditText.setText(""+p.getInt(getString(R.string.userId), 25));
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
                if (!DateValidator.isDateValid(dateNaissance, DateValidator.inputFormat)) {
                    Toast.makeText(SignUpActivity.this, "Votre date de naissance n'est pas valide", Toast.LENGTH_SHORT).show();
                    EditTextToolbox.setEditTextToBlank(dateNaissanceEditText);
                    return;
                }

                Object selectedItem = aideSoignantSpinner.getSelectedItem();
                if(selectedItem == null){
                    if(aideSoignants != null){
                        if(aideSoignants.size() == 0){
                            Toast.makeText(SignUpActivity.this, "Pas d'aide-soignants disponibles", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Selectionnez un aide-soignant", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Le service est indisponible", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    int position = aideSoignantSpinner.getSelectedItemPosition();
                    HashMap<String, String> aideSoignant = aideSoignants.get(position);
                    String idAideSoignant = aideSoignant.get("id");
                    if (PatientRequest.insertPatient(new Patient(email.trim(), password.trim(), prenom.trim(), nom.trim(), dateNaissance.trim(), Integer.parseInt(idAideSoignant)))) {
                        Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activityContext, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Désolé, nous n'avons pas pu vous ajouter.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        aideSoignants = AideSoignantRequest.getAllAideSoignantByFullName();
        if(aideSoignants == null){
            Toast.makeText(SignUpActivity.this, "Le service est indisponible", Toast.LENGTH_SHORT).show();
            aideSoignantSpinner.setEnabled(false);
            aideSoignantSpinner.setClickable(false);
            aideSoignantSpinner.setAdapter(new ArrayAdapter<String>(
                            SignUpActivity.this,
                            android.R.layout.simple_spinner_item,
                            new ArrayList<String>()
            ));
        } else {
            ArrayList<String> nomCompletAideSoignants = new ArrayList<>();
            for(HashMap<String, String> aideSoignant : aideSoignants){
                String nomComplet = "";
                nomComplet += aideSoignant.get("prenom") + " ";
                nomComplet += aideSoignant.get("nom");
                nomCompletAideSoignants.add(nomComplet);
            }

            ArrayAdapter aideSoignantAdapter = new ArrayAdapter<String>(
                SignUpActivity.this,
                android.R.layout.simple_spinner_item,
                nomCompletAideSoignants
            );

            aideSoignantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            aideSoignantSpinner.setAdapter(aideSoignantAdapter);
        }
    }

    private boolean isComfirmPasswordCorrect(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

}
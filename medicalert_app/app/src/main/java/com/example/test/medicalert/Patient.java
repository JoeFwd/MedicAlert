package com.example.test.medicalert;

public class Patient {
    public static final String emailKey = "email";
    public static final String passwordKey = "password";
    public static final String prenomKey = "prenom";
    public static final String nomKey = "nom";
    public static final String dateNaissanceKey = "date_naissance";
    private String email;
    private String password;
    private String prenom;
    private String nom;
    private String dateNaissance;

    public Patient(String email, String password, String prenom, String nom, String dateNaissance){
        this.email = email;
        this.password = password;
        this.prenom = prenom;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNom() {
        return nom;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }
}


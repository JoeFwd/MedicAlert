package com.example.test.medicalert;

public class AideSoignant {
    public static final String emailKey = "email";
    public static final String passwordKey = "password";
    public static final String prenomKey = "prenom";
    public static final String nomKey = "nom";
    public static final String dateNaissanceKey = "date_naissance";
    public static final String adresseKey = "adresse";
    private String email;
    private String password;
    private String prenom;
    private String nom;
    private String dateNaissance;
    private String adresse;

    public AideSoignant(String email, String password, String prenom, String nom, String dateNaissance, String adresse){
        this.email = email;
        this.password = password;
        this.prenom = prenom;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
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

    public String getAdresse() { return adresse;}
}
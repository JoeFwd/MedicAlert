package com.example.test.medicalert;

import java.util.ArrayList;

public class Medicament {
    public static final String cip13Key = "cip13";
    public static final String nomKey = "nom";
    public static final String formePharmaKey = "formePharma";
    private String cip13;
    private String nom;
    private String formePharma;

    public Medicament(String cip13, String nom, String formaPharma){
        this.cip13 = cip13;
        this.nom = nom;
        this.formePharma = formaPharma;
    }

    public String getCip13() {
        return cip13;
    }

    public String getNom() {
        return nom;
    }

    public String getFormePharma() {
        return formePharma;
    }

    public static ArrayList<String> getAllKeys(){
        ArrayList<String> keys = new ArrayList<>();
        keys.add(cip13Key);
        keys.add(nomKey);
        keys.add(formePharmaKey);
        return keys;
    }

    @Override
    public String toString(){
        return "Nom : " + this.nom + "\n" + "cip13 : " + this.cip13+ "\n" + "Forme pharmaceutique : " + this.formePharma ;
    }
}
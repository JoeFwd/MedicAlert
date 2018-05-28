package com.example.test.medicalert;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * medicamentList is not Parcelable, you have to use the serializable methods.
 */
public class Traitement implements Parcelable{
    public final static String CLASS_TAG = "traitement";
    public final static String idMedicamentKey = "id_medicament";
    public final static String datePeremptionKey = "date_peremption";
    public final static String dosageKey = "dosage";
    public final static String idPatientKey = "id_patient";
    public final static String idAideSoignantKey = "id_aide_soignant";
    public final static String nomKey = "nom";
    public final static String dateDebutKey = "date_debut";
    public final static String dureeTraitementKey = "duree_traitement";
    public final static String matinKey = "matin";
    public final static String apresMiditKey = "apres_midi";
    public final static String soirKey = "soir";
    public final static String medicamentListKey = "medicamentList";

    private int id_patient, id_aide_soignant, duree;
    private String nom, dateDebut;
    private Boolean matin, midi, soir;
    private ArrayList<HashMap<String, String>> medicamentList;

    public Traitement(int id_patient, int id_aide_soignant, int duree, String nom, String dateDebut, Boolean matin, Boolean midi, Boolean soir, ArrayList<HashMap<String, String>> medicamentList) {
        this.id_patient = id_patient;
        this.id_aide_soignant = id_aide_soignant;
        this.duree = duree;
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.matin = matin;
        this.midi = midi;
        this.soir = soir;
        this.medicamentList = medicamentList;
    }

    protected Traitement(Parcel in) {
        id_patient = in.readInt();
        id_aide_soignant = in.readInt();
        duree = in.readInt();
        nom = in.readString();
        dateDebut = in.readString();
        byte tmpMatin = in.readByte();
        matin = tmpMatin == 0 ? null : tmpMatin == 1;
        byte tmpMidi = in.readByte();
        midi = tmpMidi == 0 ? null : tmpMidi == 1;
        byte tmpSoir = in.readByte();
        soir = tmpSoir == 0 ? null : tmpSoir == 1;
    }

    public static final Creator<Traitement> CREATOR = new Creator<Traitement>() {
        @Override
        public Traitement createFromParcel(Parcel in) {
            return new Traitement(in);
        }

        @Override
        public Traitement[] newArray(int size) {
            return new Traitement[size];
        }
    };

    public int getId_patient() {
        return id_patient;
    }

    public int getId_aide_soignant() {
        return id_aide_soignant;
    }

    public int getDuree() {
        return duree;
    }

    public String getNom() {
        return nom;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public Boolean getMatin() {
        return matin;
    }

    public Boolean getMidi() {
        return midi;
    }

    public Boolean getSoir() {
        return soir;
    }

    public ArrayList<HashMap<String, String>> getMedicamentList() {
        return medicamentList;
    }

    public void setMedicamentList(ArrayList<HashMap<String, String>> medicamentList) {
        this.medicamentList = medicamentList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_patient);
        dest.writeInt(id_aide_soignant);
        dest.writeInt(duree);
        dest.writeString(nom);
        dest.writeString(dateDebut);
        dest.writeByte((byte) (matin == null ? 0 : matin ? 1 : 2));
        dest.writeByte((byte) (midi == null ? 0 : midi ? 1 : 2));
        dest.writeByte((byte) (soir == null ? 0 : soir ? 1 : 2));
    }

    @Override
    public String toString(){
        return this.nom + " " + this.id_patient + " " + this.id_aide_soignant + " " + this.dateDebut + " " + this.duree + " " + this.matin + " " + this.midi + " " + this.soir + " " + this.medicamentList;
    }
}

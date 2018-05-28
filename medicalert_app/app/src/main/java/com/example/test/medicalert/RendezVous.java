package com.example.test.medicalert;

import java.util.Calendar;

public class RendezVous {
    public final static String idPatientKey = "id_patient";
    public final static String idAideSoignantKey = "id_aide_soignant";
    public final static String dateRdvKey = "date_rdv";

    private int idPatient, idAideSoignant;
    private Calendar dateRendezVous;

    public RendezVous(int idPatient, int idAideSoignant, Calendar dateRendezVous) {
        this.idPatient = idPatient;
        this.idAideSoignant = idAideSoignant;
        this.dateRendezVous = dateRendezVous;
    }

    public int getIdPatient() {
        return idPatient;
    }

    public int getIdAideSoignant() {
        return idAideSoignant;
    }

    public Calendar getDateRendezVous() {
        return dateRendezVous;
    }

    @Override
    public String toString(){
        return idPatient + " " + idAideSoignant + " " + dateRendezVous.get(Calendar.DAY_OF_MONTH) + "/" + (dateRendezVous.get(Calendar.MONTH)+1) + "/" + dateRendezVous.get(Calendar.YEAR) + " "
                + dateRendezVous.get(Calendar.HOUR_OF_DAY) + ":" + dateRendezVous.get(Calendar.MINUTE);
    }
}

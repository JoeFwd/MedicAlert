package com.example.test.medicalert;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.SQLException;

public class DbAdapter {
    public final static String KEY_ID = "ID";
    public final static String KEY_CIS = "CIS";
    public final static String KEY_NOM = "Nom";
    public final static String KEY_QUANTITE = "Quantité";
    public final static String KEY_DATE_PEREMPTION = "Date de péremption";
    public final static String KEY_CATEGORIE = "Catégorie";

    public final static String DATABASE_NAME = "MedicAlertDB";
    public final static String TABLE_MEDICAMENT = "Medicaments";
    public final static int DATABASE_VERSION = 1;

    private final Context context;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;


    public DbAdapter(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL("create table " + TABLE_MEDICAMENT + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "CIS INTEGER, NOM TEXT, QUANTITE INTEGER, DATE_PEREMPTION TEXT, CATEGORIE TEXT);");
            } catch(SQLException e){e.printStackTrace();}

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICAMENT);
            } catch(SQLException e){e.printStackTrace();}
            onCreate(db);
        }
    }

    public DbAdapter open(){
        try {
            db = dbHelper.getWritableDatabase();
        } catch(SQLException e){e.printStackTrace();}
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    /**
     * Insère un médicament dans la table TABLE_MEDICAMENT.
     * @param cis
     * @param nom
     * @param quantite
     * @param datePeremption
     * @param categrorie
     * @return Le numéro de la ligne nouvellement ajoutée ou -1 si une erreur est apparue.
     */
    public long insererMedicament(String cis, String nom, int quantite, String datePeremption, String categrorie){
        ContentValues content = new ContentValues();
        content.put(KEY_CIS, cis);
        content.put(KEY_NOM, nom);
        content.put(KEY_QUANTITE, quantite);
        content.put(KEY_DATE_PEREMPTION, datePeremption);
        content.put(KEY_CATEGORIE, categrorie);
        return db.insert(TABLE_MEDICAMENT, null, content);
    }

    /**
     * Supprime un médicament de la table TABLE_MEDICAMENT.
     * @param id
     * @return 1 si le médicament a été surippimé ou 0 si le médicament n'a pas été trouvé.
     */
    public int supprimerMedicament(int id){
        return db.delete(TABLE_MEDICAMENT, KEY_ID + " = " + id, null);
    }

    /**
     * Met à jour un médicament de la table TABLE_MEDICAMENT.
     * @param id
     * @param cis
     * @param nom
     * @param quantite
     * @param datePeremption
     * @param categrorie
     * @return le nombre de médicaments affecté.
     */
    public int modifierMedicament(int id, String cis, String nom, int quantite, String datePeremption, String categrorie){
        ContentValues updatedContent = new ContentValues();
        updatedContent.put(KEY_CIS, cis);
        updatedContent.put(KEY_NOM, nom);
        updatedContent.put(KEY_QUANTITE, quantite);
        updatedContent.put(KEY_DATE_PEREMPTION, datePeremption);
        updatedContent.put(KEY_CATEGORIE, categrorie);
        return db.update(TABLE_MEDICAMENT, updatedContent, KEY_ID + " = " + id, null);

    }
}

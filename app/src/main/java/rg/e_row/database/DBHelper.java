package rg.e_row.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    //Nom base de donnée
    private static final String DATABASE_NAME = "Ro11w1";

    //Nom table Sortie
    private static final String TABLE_SORTIE = "sortie";
    //Nom Colonnes de la table sortie
    private static final String KEY_SORTIE_ID = "id_sortie";
    private static final String KEY_SORTIE_DATE = "date_sortie";
    private static final String KEY_SORTIE_DISTANCE = "distance";
    private static final String KEY_SORTIE_DUREE= "duree";


    //Nom table Mesures
    private static final String TABLE_MESURE = "mesure";
    private static final String KEY_MESURE_ID = "id_mesure";
    private static final String KEY_MESURE_SORTIE_ID="id_sortie_mesure";
    private static final String KEY_MESURE_CADENCE = "cadence";
    private static final String KEY_MESURE_VITESSE = "vitesse";
    private static final String KEY_MESURE_LATITUDE = "latitude";
    private static final String KEY_MESURE_LONGITUDE = "longitude";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }


    ////////////////////
    ////////////////////
    //                //
    //   GESTION DB   //
    //                //
    ////////////////////
    ////////////////////



    @Override
    public void onCreate(SQLiteDatabase dbS) {
        try {
            //Création table sortie
            String CREATE_TABLE_SORTIE = "CREATE TABLE " + TABLE_SORTIE + " ("
                    + KEY_SORTIE_ID + " INTEGER PRIMARY KEY, "
                    + KEY_SORTIE_DATE + " TEXT, "
                    + KEY_SORTIE_DISTANCE+" TEXT,"
                    + KEY_SORTIE_DUREE + " TEXT" +")";
            dbS.execSQL(CREATE_TABLE_SORTIE);

            //Création table mesure
            String CREATE_TABLE_MESURE = "CREATE TABLE " + TABLE_MESURE + " ("
                    + KEY_MESURE_ID + " INTEGER PRIMARY KEY,"
                    + KEY_MESURE_SORTIE_ID + " TEXT, "
                    + KEY_MESURE_CADENCE + " TEXT, "
                    + KEY_MESURE_VITESSE+ " TEXT, "
                    + KEY_MESURE_LATITUDE + " TEXT, "
                    + KEY_MESURE_LONGITUDE + " TEXT"+")";
            dbS.execSQL(CREATE_TABLE_MESURE);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase dbS, int oldVersion, int newVersion) {
        dbS.execSQL("DROP TABLE IF EXISTS "+TABLE_SORTIE);
        dbS.execSQL("DROP TABLE IF EXISTS "+TABLE_MESURE);
        onCreate(dbS);
    }

    //Vérifie si une table est vide
    public Boolean isTableEmpty(String table_name){
        SQLiteDatabase dbS = this.getWritableDatabase();
        String selectQuery = "SELECT count(*) FROM "+ table_name;
        Cursor cursor = dbS.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if(cursor.getInt(0)>0){
            cursor.close();
            dbS.close();
            return Boolean.FALSE;
        }
        else {
            cursor.close();
            dbS.close();
            return Boolean.TRUE;
        }
    }



    ////////////////////
    ////////////////////
    //                //
    //    SORTIE      //
    //                //
    ////////////////////
    ////////////////////



    //      ADD       //


    //Inserer nouvelle sortie
    public void addSortie(Sortie sortie){
        SQLiteDatabase dbS = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SORTIE_DATE, sortie.getDate());
        values.put(KEY_SORTIE_DISTANCE, sortie.getDistance());
        values.put(KEY_SORTIE_DUREE, sortie.getDuree());

        //Ajout de la colonne
        dbS.insert(TABLE_SORTIE, null, values);

        dbS.close();
    }


    //      GET       //


    //Obtenir la liste des sorties
    public ArrayList<Sortie> getAllSorties() {
        ArrayList<Sortie> sortieList = new ArrayList<>();
        String selectQuery = "SELECT*FROM " + TABLE_SORTIE;

        SQLiteDatabase dbS = this.getWritableDatabase();
        Cursor cursor = dbS.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Sortie sortie = new Sortie();
                sortie.setDbId(cursor.getString(0));
                sortie.setDate(cursor.getString(1));
                sortie.setDistance(cursor.getString(2));
                sortie.setDuree(cursor.getString(3));
                //Ajout de l'appel à la liste
                sortieList.add(sortie);

            } while (cursor.moveToNext());
        }
        cursor.close();
        dbS.close();
        return sortieList;
    }

    //Obtenir la sortie i
    public Sortie getSortie(int indexdb) {
        Sortie sortie = new Sortie();
        String selectQuery = "SELECT*FROM " + TABLE_SORTIE
                + " WHERE "+KEY_SORTIE_ID+"='"+indexdb+"'";

        SQLiteDatabase dbS = this.getWritableDatabase();
        Cursor cursor = dbS.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            sortie.setDbId(cursor.getString(0));
            sortie.setDate(cursor.getString(1));
            sortie.setDistance(cursor.getString(2));
            sortie.setDuree(cursor.getString(3));
        }
        cursor.close();
        dbS.close();
        return sortie;
    }

    //Obtenir l'index de la sortie qui va être enregistrée
    public String getIndexNouvelleSortie() {
        ArrayList<Sortie> sortieList = new ArrayList<>();
        String selectQuery = "SELECT*FROM " + TABLE_SORTIE;

        if (isTableEmpty(TABLE_SORTIE))
        {
            return "1";
        }
        else
        {
            SQLiteDatabase dbS = this.getWritableDatabase();
            Cursor cursor = dbS.rawQuery(selectQuery, null);
            cursor.moveToLast();
            int sortieid = Integer.parseInt(cursor.getString(0));
            sortieid=sortieid+1;
            return ""+sortieid;

        }
    }

    //     UPDATE     //


    // Modifier un contact dans la base de donnée
    public void updateSortie(Sortie newSortie, Sortie oldSortie){

        SQLiteDatabase dbS = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SORTIE_DATE, newSortie.getDate());
        values.put(KEY_SORTIE_DISTANCE, newSortie.getDistance());
        values.put(KEY_SORTIE_DUREE, newSortie.getDuree());

        dbS.update(TABLE_SORTIE, values, KEY_SORTIE_ID + " = ?", new String[] {oldSortie.getDbId()} );
        dbS.close();
    }


    //     DELETE     //


    // Supprimer sortie
    public void deleteSortie(Sortie sortie){
        SQLiteDatabase dbS = this.getWritableDatabase();
        dbS.delete(TABLE_SORTIE,KEY_SORTIE_ID+"=?",
                new String[]{String.valueOf(sortie.getDbId())});
        dbS.close();
    }




    ////////////////////
    ////////////////////
    //                //
    //    Mesure      //
    //                //
    ////////////////////
    ////////////////////



    //      ADD       //


    //Inserer nouvelle mesure
    public void addMesure(Mesure mesure){
        SQLiteDatabase dbS = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MESURE_SORTIE_ID, mesure.getSortieId());
        values.put(KEY_MESURE_CADENCE, mesure.getCadence());
        values.put(KEY_MESURE_VITESSE, mesure.getVitesse());
        values.put(KEY_MESURE_LATITUDE, mesure.getLatitude());
        values.put(KEY_MESURE_LONGITUDE, mesure.getLongitude());

        //Ajout de la colonne
        dbS.insert(TABLE_MESURE, null, values);
        dbS.close();
    }


    //     DELETE     //


    //Supprimer les mesures d'une sortie
    private void deleteMesureSortie(Sortie sortie) {
        SQLiteDatabase dbS = this.getWritableDatabase();
        dbS.delete(TABLE_MESURE,KEY_MESURE_SORTIE_ID+"=?",
                new String[] {String.valueOf(sortie.getDbId())});
        dbS.close();
    }


    //      GET       //



    //Obtenir toute les mesures d'une sortie
    public ArrayList<Mesure> getAllSortieMesures(Sortie sortie) {
        ArrayList<Mesure> mesureList = new ArrayList<>();
        String selectQuery = "SELECT*FROM " + TABLE_MESURE
                + " WHERE "+KEY_MESURE_SORTIE_ID+"='"+
                sortie.getDbId()+"'";

        SQLiteDatabase dbS = this.getWritableDatabase();
        Cursor cursor = dbS.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Mesure mesure = new Mesure();
                mesure.setDbId(cursor.getString(0));
                mesure.setSortieId(cursor.getString(1));
                mesure.setCadence(cursor.getString(2));
                mesure.setVitesse(cursor.getString(3));
                mesure.setLatitude(cursor.getString(4));
                mesure.setLongitude(cursor.getString(5));
                //Ajout de la mesure à la liste
                mesureList.add(mesure);

            } while (cursor.moveToNext());
        }
        cursor.close();
        dbS.close();
        return mesureList;

    }

    //Obtenir toutes les mesures
    public ArrayList<Mesure> getAllMesures() {
        ArrayList<Mesure> mesureListe = new ArrayList<>();
        String selectQuery = "SELECT*FROM " + TABLE_MESURE
                +" ORDER BY "+ KEY_MESURE_ID;

        SQLiteDatabase dbS = this.getWritableDatabase();
        Cursor cursor = dbS.rawQuery(selectQuery, null);



        if (cursor.moveToFirst()) {
            do {
                Mesure mesure = new Mesure();
                mesure.setDbId(cursor.getString(0));
                mesure.setSortieId(cursor.getString(1));
                mesure.setCadence(cursor.getString(2));
                mesure.setVitesse(cursor.getString(3));
                mesure.setLatitude(cursor.getString(4));
                mesure.setLongitude(cursor.getString(5));
                //Ajout de la mesure à la liste
                mesureListe.add(mesure);

            } while (cursor.moveToNext());
        }
        cursor.close();
        dbS.close();
        return mesureListe;

    }

}

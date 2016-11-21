package rg.e_row;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import rg.e_row.database.DBHelper;
import rg.e_row.database.Mesure;
import rg.e_row.database.Sortie;

/**
 * Created by furmon on 21/11/2016.
 */

public class Capteurs extends Service implements LocationListener, SensorEventListener {

    private final IBinder mBinder = new LocalBinder();

    private DBHelper mydb;

    private LocationManager locationManager;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Location location;

    private String sortieId;
    private boolean recording = false;
    private Long tDebutSortie;

    //Variables calcul cadence
    private float accSeuil = 1000f;
    private float previousAcc;
    private Boolean nouveauCoup = false;
    private float cadence;
    private Long timeNouveauCoup;

    //Variables de localisation
    private float vitesse;
    private double longitude;
    private double latitude;
    private float distance;

    //Mise en forme des dates
    DecimalFormat df = new DecimalFormat("######.#");
    SimpleDateFormat daf = new SimpleDateFormat("dd-MM-yyyy");

    // Classe utilisée pour l'activité Acquisiton

    public class LocalBinder extends Binder {
        Capteurs getService() {
            return Capteurs.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mydb= new DBHelper(getApplicationContext());

        //Initialisation des capteurs
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Si l'autorisation de localisation est donnée
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onDestroy() {
        //Destruction des listeners accelerometres et gps
        sensorManager.unregisterListener(this,accelerometer);
        sensorManager=null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
        locationManager=null;

        super.onDestroy();
    }

    public void startRecording(){
        tDebutSortie=System.currentTimeMillis();
        this.recording=true;
        sortieId = mydb.getIndexNouvelleSortie();
    }

    public void stopRecording(){
        registerSortie();
        this.recording=false;
    }

    public void registerSortie(){
        Calendar d = Calendar.getInstance();
        Sortie sortie = new Sortie();
        sortie.setDate(daf.format(d.getTime()));
        sortie.setDistance(formatDistance(distance));
        sortie.setDuree(getFormatedDuree());
        mydb.addSortie(sortie);
    }

    public void registerMesure(){
        if (recording){
            Mesure mesure = new Mesure();
            mesure.setCadence(""+cadence);
            mesure.setLatitude(""+latitude);
            mesure.setLongitude(""+longitude);
            mesure.setSortieId(sortieId);
            mesure.setVitesse(""+vitesse);
            mydb.addMesure(mesure);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        vitesse=location.getSpeed();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    //Capteurs

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        float acc;

        if(mySensor.getType()==Sensor.TYPE_ACCELEROMETER){

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            acc = x*x+y*y+z*z;

            //Calcul seuil
            if (acc>0.7*accSeuil && !nouveauCoup)
            {
                nouveauCoup = true;
                Long difference_temps = System.currentTimeMillis()-timeNouveauCoup;
                cadence = 60000f/difference_temps;
                timeNouveauCoup = System.currentTimeMillis();
                previousAcc=acc;

                registerMesure();

                String textaffiche = ""+cadence;
            }

            if (nouveauCoup)
            {
                if (acc>previousAcc&& acc>0.7*accSeuil)
                {
                    accSeuil=acc;
                }
            }

            if (acc<0.4*accSeuil && nouveauCoup)
            {
                nouveauCoup = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public String formatDistance(float dist){
        if (dist>1000) {
            dist = dist/1000;
            return df.format(dist)+"km";
        }
        else{
            return df.format(dist)+"m";
        }
    }

    public String getFormatedDuree(){
        long totalTime=System.currentTimeMillis()-tDebutSortie;
        String result ="";
        if (totalTime/3600000>1){
            int nbH = (int)totalTime/3600000;
            totalTime=totalTime-nbH*3600000;
            result=nbH+"h";
        }
        if (totalTime/60000>=1){
            return result+totalTime/60000+"m";
        }
        else {
            return totalTime / 1000 + "s";
        }

    }
}


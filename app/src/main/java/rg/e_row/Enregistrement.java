package rg.e_row;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import rg.e_row.database.DBHelper;


/**
 * Created by raphaelgavache on 04/07/2016.
 */
public class Enregistrement extends Service implements LocationListener{

    private LocationManager lm;

    private float distance;
    private Location oldlocation;
    private float oldistance;
    private double  time;

    private long temps2;
    private Boolean rowing;
    private SensorManager sensorManager;


    private ArrayList<Double> stat;
    private String statstring;

    private Boolean recording;
    private Boolean localisationactualisee;


    DecimalFormat df = new DecimalFormat("######.#");
    SimpleDateFormat daf = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.

        lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Autoriser le GPS", Toast.LENGTH_LONG).show();
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);



        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        recording = false;


        //Transformation de l'array en string grace a gson
        Gson gson = new Gson();
        statstring = gson.toJson(stat);

        //Enregistrement de la sortie

        Toast.makeText(getApplicationContext(), "Sortie enregistée", Toast.LENGTH_SHORT).show();

    }



    public void onLocationChanged(Location location) {


        //Enregistrement des données


        //Calcul de la distance après initialisation


        if (localisationactualisee) {
            distance = distance + oldlocation.distanceTo(location);
            oldlocation = location;
            if (distance - oldistance > 10 & recording) {

                Double time50m = (double) (System.currentTimeMillis() - time) / 1000;
                double avgspeed = 10 / time50m;
                //Toast.makeText(this, ""+timelapsed, Toast.LENGTH_SHORT).show();
                stat.add(location.getLatitude());
                stat.add(location.getLongitude());
                //à changer
                stat.add((double) location.getSpeed());
                stat.add((double) 0);
                stat.add( time50m);

                oldistance = distance;
                time = System.currentTimeMillis();
            }
        }

        //Initialisation
        else {
            localisationactualisee = true;
            oldlocation = location;
            stat.add(location.getLatitude());
            stat.add(location.getLongitude());
            stat.add((double) location.getSpeed());
            stat.add((double) 0);
            stat.add((double) 0);

            oldistance = 0;

            time = System.currentTimeMillis();

        }


        //Affichage de la vitesse
/*TODO
        if (distance > 1000) {
            float distkm = distance / 1000;
              dist.setText(df.format(distkm) + "km");
        } else {
              dist.setText(df.format(distance) + "m");
        }


        TextView vit = (TextView) findViewById(R.id.vitesse);
        assert vit != null;
        vit.setText(df.format(location.getSpeed()) + " m/s");
        */
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}

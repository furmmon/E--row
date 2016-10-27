package rg.e_row;

import android.Manifest;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.vision.text.Text;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rg.e_row.Liste.Historique;
import rg.e_row.database.DBHelper;
import rg.e_row.database.Mesure;
import rg.e_row.database.Sortie;


public class Acquisition extends AppCompatActivity implements LocationListener, SensorEventListener {

    private DBHelper mydb;


    private LocationManager lm;
    private Chronometer chrono;
    private TextView dist;
    private TextView cad;


    private float distance;
    private Location oldlocation;
    private float oldistance;
    private double time;
    private String sortieId;

    private long temps2;


    private ArrayList<Double> stat;

    private Boolean recording;
    private Boolean localisationactualisee;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private float [] gravity = new float[3];

    private float accSeuil=1000f;
    private float acc;
    private float previousAcc;
    private Boolean nouveauCoup = false;
    private float cadence;
    private Long timeNouveauCoup;

    DecimalFormat df = new DecimalFormat("######.#");
    SimpleDateFormat daf = new SimpleDateFormat("dd-MM-yyyy");


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acquisition);

        mydb = new DBHelper(this);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        stat = new ArrayList<>();
        localisationactualisee = false;
        distance = 0f;

        timeNouveauCoup = System.currentTimeMillis();
        dist = (TextView) findViewById(R.id.distanceview);
        cad = (TextView) findViewById(R.id.cadence);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Capteurs
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this,senAccelerometer, senSensorManager.SENSOR_DELAY_NORMAL);



        //Localisation
        lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Autoriser le GPS", Toast.LENGTH_LONG).show();
            return;
        }
        else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
        }

        chrono = (Chronometer) findViewById(R.id.chronometer);
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);

        distance = 0f;
        recording = false;


        //Lancement aquisition

        assert toggle != null;
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Calendar d = Calendar.getInstance();
                if (isChecked) {
                    recording = true;

                    sortieId = mydb.getIndexNouvelleSortie();
                    chrono.setBase(SystemClock.elapsedRealtime());
                    chrono.start();
                    distance = 0f;
                    assert dist != null;
                    String textaffiche = distance+"m";
                    dist.setText(textaffiche);

                } else {
                    Sortie sortie = new Sortie();
                    chrono.stop();
                    sortie.setDuree(chrono.getFormat());
                    recording = false;

                    //Enregistrement de la sortie
                    sortie.setDistance(distance+"");
                    sortie.setDate(daf.format(d.getTime()));
                    mydb.addSortie(sortie);
                    Toast.makeText(getApplicationContext(), "Sortie enregistée", Toast.LENGTH_SHORT).show();
                }
            }


        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        lm.removeUpdates(this);
    }


    @Override
    public void onLocationChanged(Location location) {

        Toast.makeText(this, "nouvelle localisation", Toast.LENGTH_SHORT).show();
        //Enregistrement des données


        //Calcul de la distance après initialisation

        Mesure mesure = new Mesure();
        if (localisationactualisee) {
            distance = distance + oldlocation.distanceTo(location);
            oldlocation = location;

            Toast.makeText(this, "nouvelle localisation", Toast.LENGTH_SHORT).show();


            if (true) {

                mesure.setLatitude(""+location.getLatitude());
                mesure.setLongitude(""+location.getLongitude());
                mesure.setVitesse(""+location.getSpeed());
                mesure.setCadence(""+cad.getText());
                //TODO changer sortie id
                mesure.setSortieId(sortieId);
                mydb.addMesure(mesure);

                oldistance = distance;
                time = System.currentTimeMillis();
            }
        }

        //Initialisation
        else {
            localisationactualisee = true;
            oldlocation = location;

            //Toast.makeText(this, ""+timelapsed, Toast.LENGTH_SHORT).show();
            mesure.setLatitude(""+location.getLatitude());
            mesure.setLongitude(""+location.getLongitude());
            mesure.setVitesse(""+location.getSpeed());
            //TODO changer sortie id
            mesure.setSortieId(""+1);
            mesure.setCadence("2");
            mesure.setSortieId(sortieId);
            mydb.addMesure(mesure);


            oldistance = 0;

            time = System.currentTimeMillis();

        }


        //Affichage de la vitesse

        if (distance > 1000) {
            float distkm = distance / 1000;
            String textaffiche = df.format(distkm)+"km";
            dist.setText(textaffiche);
        } else {
            String textaffiche = df.format(distance)+"m";
            dist.setText(textaffiche);
        }


        TextView vit = (TextView) findViewById(R.id.vitesse);
        assert vit != null;
        vit.setText(df.format(location.getSpeed()) + " m/s");
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


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Acquisition Page",
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                Uri.parse("android-app://rg.e_row/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    public void Historique(View view) {
        Intent intent = new Intent(this, Historique.class);
        startActivity(intent);
    }



    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Acquisition Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://rg.e_row/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

//            Float alpha=0.99f;
//            gravity[0] = alpha * gravity[0] + (1 - alpha) * x;
//            gravity[1] = alpha * gravity[1] + (1 - alpha) * y;
//            gravity[2] = alpha * gravity[2] + (1 - alpha) * z;
//
//            x = x - gravity[0];
//            y = y - gravity[1];
//            z = z - gravity[2];
//
//
            acc = x*x+ y*y+ z*z;





            //Calcul seuil
            if (acc>0.7*accSeuil && !nouveauCoup)
            {
                nouveauCoup = true;
                Long difference_temps = System.currentTimeMillis()-timeNouveauCoup;
                cadence = 60000f/difference_temps;
                timeNouveauCoup = System.currentTimeMillis();
                previousAcc=acc;

                String textaffiche = ""+cadence;
                cad.setText(textaffiche);

                Mesure mesure = new Mesure();
                mesure.setVitesse(""+0);
                mesure.setCadence("2");
                mesure.setSortieId(sortieId);
                mydb.addMesure(mesure);
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
}
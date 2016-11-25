package rg.e_row;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
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
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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


public class Acquisition extends Activity /*implements LocationListener, SensorEventListener*/ {

    private DBHelper mydb;

    Capteurs mService;
    boolean mBound = false;
    boolean recording = false;

    private LocationManager lm;
    private Chronometer chrono;
    private TextView dist;
    private TextView cad;
    private TextView vit;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acquisition);

        dist = (TextView) findViewById(R.id.distanceview);
        cad = (TextView) findViewById(R.id.cadence);
        vit = (TextView) findViewById(R.id.vitesse);

        mydb = new DBHelper(this);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        //Connection des TextView au service Capteurs
        Intent intent = new Intent(this, Capteurs.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.v("create", "bip cadence");
                        String[] donneEnv = intent.getStringExtra("mesure").split("-");
                        switch (donneEnv[0]){
                            case "cadence" :
                                cad.setText(donneEnv[1]);
                                break;
                            case "vitesse" :
                                vit.setText(donneEnv[1]);
                                break;
                            case "distance" :
                                dist.setText(donneEnv[1]);
                                break;
                            default:
                                break;
                        }
                    }
                }, new IntentFilter("resultatMesure")
        );
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


    @Override
    protected void onResume() {
        super.onResume();
        chrono = (Chronometer) findViewById(R.id.chronometer);
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);

        //Lancement aquisition
        assert toggle != null;
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
                    recording = true;
                    if (mBound){
                        mService.startRecording();
                        Toast.makeText(getApplicationContext(),"connectionReussie",Toast.LENGTH_SHORT);

                    }
                    chrono.setBase(SystemClock.elapsedRealtime());
                    chrono.start();

                } else {
                    recording = false;
                    if (mBound){
                        mService.stopRecording();

                    }
                    else{
                        Log.v("serv","service not bound");
                    }
                    chrono.stop();
                }
            }


        });
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Capteurs.LocalBinder binder = (Capteurs.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    //Connection à l'historique
    public void Historique(View view) {
        Intent intent = new Intent(this, Historique.class);
        startActivity(intent);
    }



    @Override
    protected void onDestroy() {
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onDestroy();
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
}

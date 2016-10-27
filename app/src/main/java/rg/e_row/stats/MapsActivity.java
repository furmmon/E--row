package rg.e_row.stats;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rg.e_row.R;
import rg.e_row.database.DBHelper;
import rg.e_row.database.Mesure;
import rg.e_row.database.Sortie;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private DBHelper mydb;
    private GoogleMap mMap;
    private int Value;
    private ArrayList<Mesure> mesures;
    DecimalFormat df = new DecimalFormat("######.#");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mydb = new DBHelper(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Value = extras.getInt("id");
            Sortie sortie = mydb.getSortie(Value);

            mesures = mydb.getAllSortieMesures(sortie);
        }

        if (mesures.size()<1)
        {
            Mesure mesure0 = new Mesure();
            mesure0.setLatitude("37.11");
            mesure0.setLongitude("29.91");
            mesure0.setVitesse("1.1");
            mesure0.setCadence("12.1");
            mesures.add(mesure0);

            Mesure mesure1 = new Mesure();
            mesure1.setLatitude("31.11");
            mesure1.setLongitude("21.91");
            mesure1.setVitesse("1.1");
            mesure1.setCadence("12.1");
            mesures.add(mesure1);
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        PolylineOptions poption = new PolylineOptions().width(6).color(Color.RED);

        for (int i = 0; i < mesures.size(); i++) {
            Mesure mesure = mesures.get(i);
            LatLng loc = new LatLng(Double.parseDouble(mesure.getLatitude()),
                    Double.parseDouble(mesure.getLongitude()));
            poption.add(loc);
            mMap.addMarker(new MarkerOptions().position(loc).title("Vitesse "
                    + df.format(Float.parseFloat(mesure.getVitesse()))+"m/s ; Cadence "
                    + df.format(Float.parseFloat(mesure.getCadence()))));
         //   mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        }
        googleMap.addPolyline(poption);


        //ZOOOM
        List<LatLng> points = poption.getPoints(); // route is instance of PolylineOptions
        LatLngBounds.Builder bc = new LatLngBounds.Builder();
        for (LatLng item : points) {
            bc.include(item);
        }

        //googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));

    }

}




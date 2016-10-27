package rg.e_row.stats;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import rg.e_row.R;
import rg.e_row.database.DBHelper;
import rg.e_row.database.Mesure;
import rg.e_row.database.Sortie;

/**
 * Created by raphaelgavache on 29/06/2016.
 */
public class Graph extends AppCompatActivity {


    private DBHelper mydb;
    private int Value;
    private double temps;
    private double temps2;
    private ArrayList<Mesure> mesures;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graph);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mydb = new DBHelper(this);

        temps = 0;
        temps2 = 0;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Value = extras.getInt("id");
            //recuperation donnes
            Sortie sortie = mydb.getSortie(Value);
            mesures = mydb.getAllSortieMesures(sortie);

        }

        //graph

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> serie_vitesse = new LineGraphSeries<>(generateDataVitesse()) ;
        LineGraphSeries<DataPoint> serie_cadence = new LineGraphSeries<>(generateDataCadence()) ;



        graph.addSeries(serie_vitesse);
        graph.addSeries(serie_cadence);

        serie_cadence.setColor(Color.parseColor("#FF0000"));


        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);

        //Legende

        serie_cadence.setTitle("Cadence");
        serie_vitesse.setTitle("Vitesse");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

    }

    private DataPoint[] generateDataVitesse(){
        DataPoint[] values = new DataPoint [mesures.size()];
        for (int i = 0; i < mesures.size(); i++) {
            temps = temps+1;
            double x = temps;
            double y = Double.parseDouble(mesures.get(i).getVitesse());
            DataPoint v = new DataPoint (x, y);
            values[i]=v;
        }
        return values;


    }

    private DataPoint[] generateDataCadence(){
        DataPoint[] values = new DataPoint [mesures.size()];
        for (int i = 0; i < mesures.size(); i++) {
            temps2 = temps2+1;
            double x = temps2;
            double y = Double.parseDouble(mesures.get(i).getCadence());
            DataPoint v = new DataPoint (x, y);
            values[i]=v;
        }
        return values;



    }
}

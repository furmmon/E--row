package rg.e_row.stats;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import rg.e_row.Liste.Historique;
import rg.e_row.R;
import rg.e_row.database.DBHelper;
import rg.e_row.database.Sortie;

/**
 * Created by furmon on 12/10/2016.
 */

public class DisplaySortie extends Activity {

    private DBHelper mydb ;

    private int Value;


    TextView date ;
    TextView distance;
    TextView duree;
    Sortie sortie;

    int id_To_Update = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sortie);


        date = (TextView) findViewById(R.id.textdate);
        distance = (TextView) findViewById(R.id.textdistance);
        duree = (TextView) findViewById(R.id.textduree);

        mydb = new DBHelper(this);

        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
            Value = extras.getInt("id");

            sortie = mydb.getSortie(Value);

            id_To_Update = Value;

            String dat = sortie.getDate();
            String bateau = sortie.getDistance();
            String dur = sortie.getDuree();

            date.setText(dat);
            distance.setText(bateau);
            duree.setText(dur);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Bundle extras = getIntent().getExtras();

        if(extras !=null)
        {
            int Value = extras.getInt("id");
            if(Value>0){
                getMenuInflater().inflate(R.menu.display_contact, menu);
            }

            else{
                getMenuInflater().inflate(R.menu.main, menu);
            }
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        switch(item.getItemId())
        {
            case R.id.Delete_Contact:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deleteContact)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mydb.deleteSortie(sortie);
                                Toast.makeText(getApplicationContext(), "Sortie supprimée", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),Historique.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Suppression de la sortie");
                d.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }



    public void map(View view)
    {
        Bundle dataBundle = new Bundle();
        dataBundle.putInt("id", Value);

        Intent intent = new Intent(this,MapsActivity.class);

        intent.putExtras(dataBundle);
        startActivity(intent);


    }

    public void graph(View view)
    {
        Bundle dataBundle = new Bundle();
        dataBundle.putInt("id", Value);

        Intent intent = new Intent(this,Graph.class);

        intent.putExtras(dataBundle);
        startActivity(intent);


    }
}

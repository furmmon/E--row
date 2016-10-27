package rg.e_row.Liste;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import rg.e_row.Acquisition;
import rg.e_row.stats.DisplaySortie;
import rg.e_row.R;
import rg.e_row.database.DBHelper;
import rg.e_row.database.Sortie;
import rg.e_row.stats.DisplaySortie;

/**
 * Created by raphaelgavache on 28/06/2016.
 */
public class Historique extends AppCompatActivity{

    private ListView lv;
    DBHelper mydb;
    private ArrayList<Sortie> sorties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);

        //Récupération de toutes les sorties
        mydb = new DBHelper(this);
        sorties = mydb.getAllSorties();
        Collections.reverse(sorties);


        //mise en forme donnée
        String[] liste_sortie_string = new String[sorties.size()];
        for(int i=0;i<sorties.size();i++) {
            liste_sortie_string[i] = sorties.get(i).toString();
        }
        ArrayAdapter arrayAdapter=new ArrayAdapter(this, R.layout.mylist,R.id.Itemname, liste_sortie_string);



        lv = (ListView)findViewById(R.id.listView1);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {


                int id_To_Search = Integer.parseInt(sorties.get(arg2).getDbId());
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", id_To_Search);

                Intent intent = new Intent(getApplicationContext(),DisplaySortie.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        switch(item.getItemId())
        {
            case R.id.item1:Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", 0);

                Intent intent = new Intent(getApplicationContext(),DisplaySortie.class);
                intent.putExtras(dataBundle);

                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }


    public void Acquisition (View view) {
        Intent intent=new Intent(this, Acquisition.class);
        startActivity(intent);
    }
}



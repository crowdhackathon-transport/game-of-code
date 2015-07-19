package com.example.alberto.myapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Created by Alberto on 19/7/2015.
 */
public class Demo {
   // DBAdapter myDb;
    private Context context;

    //private void openDB() {
      //  myDb = new DBAdapter(context);
        //myDb.open();
    //}
   // private void closeDB() {
     //   myDb.close();
   // }
    public void add(DBAdapter myDb){


        myDb.insertRowDemo("Θεσσαλονίκης 15, Αθήνα",MainActivity.imageIDs[0],"Επικίνδυνα ξερά χόρτα στην περιοχή","37.976767,23.714622","19/07/2015 18:23","ΠΥΡΟΣΒΕΣΤΙΚΗ");
        myDb.insertRowDemo("Iera odos 21, Athens",MainActivity.imageIDs[0],"Ατύχημα ΙΧ με μηχανη. Κλειστή η δεξία λωρίδα","37.979732,23.714421","19/07/2015 18:23","ΤΡΟΧΑΙΑ");
    }
}

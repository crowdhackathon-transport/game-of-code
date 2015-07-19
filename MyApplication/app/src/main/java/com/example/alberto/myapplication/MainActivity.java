package com.example.alberto.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;

import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity {

    private Button bt;
    private ListView lv;
    private ArrayList<String> strArr;
    private ArrayAdapter<String> adapter;
    private EditText et;
    static Double lat;
    static Double lng;
    private Button btn;
    static int rating=0;

    AutoCompleteTextView auto_text;
    String[] places={"Palaio Faliro ","Voula","Athens","Alimos","Kifisia"};

    public void currentloc(){
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location) throws IOException {
                //Got the location!
                Toast.makeText(getApplicationContext(),
                        "Success!!", Toast.LENGTH_SHORT).show();

                // Creating a LatLng object for the current location
                lat = location.getLatitude();
                lng=location.getLongitude();

                MarkerOptions marker = new MarkerOptions().position( new LatLng(lat , lng)).flat(true).title("You are here! ").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                googleMap.addMarker(marker);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));

                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = gcd.getFromLocation(lat, lng,1);
                if (addresses.size() > 0)
                    System.out.println(addresses.get(0).getAddressLine(0));

                TextView txtloc=(TextView) findViewById(R.id.textViewLocation);
                txtloc.setText("@"+addresses.get(0).getAddressLine(0)+" "+addresses.get(0).getLocality());

            }
        };
        MyLocation myLocation = new MyLocation();
        try {
            myLocation.getLocation(this, locationResult);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

  static public int[] imageIDs = {
            R.drawable.caution,
            R.drawable.happy,
            R.drawable.sad
    };

    DBAdapter myDb;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }
    private void openDB() {
        myDb = new DBAdapter(this);
        myDb.open();
    }
    private void closeDB() {
        myDb.close();
    }

    /*
     * UI Button Callbacks
     */

    public void perform_action_location(long idInDB)
    {
        //TextView tv= (TextView) findViewById(R.id.textViewLocation);

        //alter text of textview widget
        //tv.setText("This text view is clicked");

        //assign the textview forecolor
        //tv.setTextColor(Color.GREEN);
        Cursor cursor = myDb.getRow(idInDB);
        if (cursor.moveToFirst()) {

            String coordinates = cursor.getString(DBAdapter.COL_COORDINATES);
           String[] s=coordinates.split(",");
            Double curlat=Double.parseDouble(s[0]);
            Double curlang=Double.parseDouble(s[1]);

            change_tab_focus();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curlat, curlang), 15));
        }
    }


    // click on the list for marker info
    public void change_tab_focus(){
        TabHost tabHost=(TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        tabHost.setCurrentTab(0);
}

    public void onClick_AddRecord(View v) {
        try {
            TextView tvLocation = (TextView) findViewById(R.id.textViewLocation);
            EditText et = (EditText) findViewById(R.id.commentTxtBox);
            int imageiD;

            imageiD = imageIDs[k];
            String latlng = "0,0";
            if (lat != 0 || lng != 0)
                latlng = Double.toString(lat) + "," + Double.toString(lng);
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date time = new Date();
            TextView foreastxt=(TextView) findViewById(R.id.foreastxt);
            String foreas= foreastxt.getText().toString();
            myDb.insertRow(tvLocation.getText().toString(), imageiD, et.getText().toString(), latlng, df.format(time).toString(),foreas);

        /*int imageId = imageIDs[nextImageIndex];
        nextImageIndex = (nextImageIndex + 1) % imageIDs.length;

        // Add it to the DB and re-draw the ListView
        myDb.insertRow("Jenny" + nextImageIndex, imageId, "Green");*/
            googleMap.clear();
            populateListViewFromDB();
            currentloc();
            addEventMarker();
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            et.setText("Add a comment");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            //catch exception
        }

    }

    public void onClick_ClearAll(View v) {
        myDb.deleteAll();
        googleMap.clear();
        populateListViewFromDB();
    }



    private void populateListViewFromDB() {
        Cursor cursor = myDb.getbyupvote();

        // Allow activity to manage lifetime of the cursor.
        // DEPRECATED! Runs on the UI thread, OK for small/short queries.
        startManagingCursor(cursor);

        // Setup mapping from cursor to view fields:
        String[] fromFieldNames = new String[]
                {DBAdapter.KEY_PLACE, DBAdapter.KEY_IMAGE, DBAdapter.KEY_COMMENT, DBAdapter.KEY_TIME, DBAdapter.KEY_FOREAS};

        int[] toViewIDs = new int[]
                {R.id.item_name,     R.id.item_icon,           R.id.commenttxt,     R.id.timetxt,   R.id.item_favcolour};

        String[] fromFieldNames2 = new String[]
                {DBAdapter.KEY_RATING};

        int[] toViewIDs2 = new int[]
                {R.id.textView};

        // Create adapter to may columns of the DB onto elemesnt in the UI.
        SimpleCursorAdapter myCursorAdapter =
                new SimpleCursorAdapter(
                        this,		// Context
                        R.layout.item_layout,	// Row layout template
                        cursor,					// cursor (set of DB records to map)
                        fromFieldNames,			// DB Column names
                        toViewIDs				// View IDs to put information in
                );
        SimpleCursorAdapter myCursorAdapter2 =
                new SimpleCursorAdapter(
                        this,		// Context
                        R.layout.itemlayout2,	// Row layout template
                        cursor,					// cursor (set of DB records to map)
                        fromFieldNames2,			// DB Column names
                        toViewIDs2				// View IDs to put information in
                );

        // Set the adapter for the list view
       ListView myList = (ListView) findViewById(R.id.listView1);
        ListView myList2 = (ListView) findViewById(R.id.listView2);
        myList.setAdapter(myCursorAdapter);
        myList2.setAdapter(myCursorAdapter2);


    }

    private void registerListClickCallback() {
        ListView myList = (ListView) findViewById(R.id.listView1);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View viewClicked,
                                   int position, long idInDB) {

             perform_action_location(idInDB);
           }
       });
    }
public void upvote() {
     ListView myList2 = (ListView) findViewById(R.id.listView2);
    myList2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
     public void onItemClick(AdapterView<?> parent, View viewClicked,
                            int position, long idInDB) {
          Toast.makeText(MainActivity.this,Long.toString(idInDB), Toast.LENGTH_LONG).show();
    }
    });
}
    private void updateItemForId(long idInDB) {
        Cursor cursor = myDb.getRow(idInDB);
        if (cursor.moveToFirst()) {
            long idDB = cursor.getLong(DBAdapter.COL_ROWID);
            String place = cursor.getString(DBAdapter.COL_PLACE);
            int image = cursor.getInt(DBAdapter.COL_IMAGE);
            String comment = cursor.getString(DBAdapter.COL_COMMENT);
            String coordinates = cursor.getString(DBAdapter.COL_COORDINATES);
            String time = cursor.getString(DBAdapter.COL_TIME);

            //comment += "!";


            myDb.updateRow(idInDB, place, image, comment, coordinates,time);

            cursor.close();
            populateListViewFromDB();

            perform_action_location(idInDB);

        }
        
    }


      //switch tabs



    private void displayToastForId(long idInDB) {
        Cursor cursor = myDb.getRow(idInDB);
        if (cursor.moveToFirst()) {
            long idDB = cursor.getLong(DBAdapter.COL_ROWID);
            String place = cursor.getString(DBAdapter.COL_PLACE);
            int image = cursor.getInt(DBAdapter.COL_IMAGE);
            String comment = cursor.getString(DBAdapter.COL_COMMENT);
            String coordinates = cursor.getString(DBAdapter.COL_COORDINATES);
            String time=cursor.getString(DBAdapter.COL_TIME);

            String message = //"ID: " + idDB + "\n"
                     "Place: " + place + "\n"
                    //+ "Std#: " + image + "\n"
                    + "Comment: " + comment+"\n"
                    + "Time: " + time;
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }
        cursor.close();
    }


    GoogleMap googleMap;

    static int i=0;

    //for the images to load
    public static int k=0;
    private void createMapView(){
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try {
            if(null == googleMap){
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                        R.id.mapView)).getMap();
               // googleMap.setMyLocationEnabled(true);
                // Getting LocationManager object from System Service LOCATION_SERVICE

                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                // Creating a criteria object to retrieve provider

                Criteria criteria = new Criteria();
                // Getting the name of the best provider

                String provider = locationManager.getBestProvider(criteria, true);

                // Getting Current Location
                Location location = locationManager.getLastKnownLocation(provider);


                /**
                 * If the map is still null after attempted initialisation,
                 * show an error to the user
                 */
                if(null == googleMap) {
                    Toast.makeText(getApplicationContext(),
                            "Error creating map", Toast.LENGTH_SHORT).show();
                }

            }
        } catch (NullPointerException exception){
            Log.e("mapApp", exception.toString());
        }
    }





    private void addEventMarker(){
        Cursor cursor = myDb.getAllRows();

        if (cursor.moveToFirst()){
            do{
                int image = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_IMAGE));
                String comment = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_COMMENT));
                String time = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_TIME));
                String coordinates = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_COORDINATES));
               String[]coo=coordinates.split((","));

                if(image==imageIDs[0])
                googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(coo[0]), Double.parseDouble(coo[1])))
                                .title(comment)
                                .snippet(time)
                                .draggable(true)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.caution))
                        );
                else if (image==imageIDs[1])
                    googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(coo[0]), Double.parseDouble(coo[1])))
                                    .title(comment)
                                    .snippet(time)
                                    .draggable(true)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.happy))
                    );
                else if (image==imageIDs[2])
                    googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(coo[0]), Double.parseDouble(coo[1])))
                                    .title(comment)
                                    .snippet(time)
                                    .draggable(true)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.sad))
                    );
                // do what ever you want here
            }while(cursor.moveToNext());
        }
        cursor.close();

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //DB
        openDB();

        Demo test=new Demo();
        test.add(myDb);
        populateListViewFromDB();
        registerListClickCallback();
        upvote();


        //search code
        auto_text=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);


        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,places);

        auto_text.setAdapter(adapter);
        auto_text.setThreshold(1);

        auto_text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView t1=(TextView) findViewById(R.id.autoCompleteTextView);
                Toast.makeText(getApplicationContext(),
                        t1.getText().toString(), Toast.LENGTH_SHORT).show();
                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addresses = gcd.getFromLocation(lat, lng,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));
            }
        });





                TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec=tabHost.newTabSpec("reporter");
        tabSpec.setContent(R.id.map);
        tabSpec.setIndicator("MAP");
        tabHost.addTab(tabSpec);

        tabSpec=tabHost.newTabSpec("info");
        tabSpec.setContent(R.id.report);
        tabSpec.setIndicator("REPORT");
        tabHost.addTab(tabSpec);

        tabSpec=tabHost.newTabSpec("info");
        tabSpec.setContent(R.id.Info);
        tabSpec.setIndicator("INFO");
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);


        final ImageButton imgbtn=(ImageButton) findViewById(R.id.imageButton);

        imgbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (k == 0) {
                    imgbtn.setBackgroundResource(R.drawable.happy);
                    k = 1;
                } else if (k == 1) {
                    imgbtn.setBackgroundResource(R.drawable.sad);
                    k = 2;
                } else if (k == 2) {
                    imgbtn.setBackgroundResource(R.drawable.caution);
                    k = 0;
                }
            }


        });




        createMapView();
currentloc();

        addEventMarker();
        final TextView foreastxt=(TextView) findViewById(R.id.foreastxt);
        btn = (Button) findViewById(R.id.popbtn);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this,btn);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                                foreastxt.setText(item.getTitle());
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        }); //closing the setOnClickListener method
    }





        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

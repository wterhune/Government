package com.wisaterhunep.government;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private final String TAG = "MAIN ACTIVITY";
    private RecyclerView recycler;
    private Adapter adapter;
    private ArrayList<GovernmentOfficial> governmentOfficialList = new ArrayList<>();

    private static int LOCATION_CODE_REQUEST = 111; //code for permission
    private TextView locationHeader; //this holds the location field at the top header
    private LocationManager locationManager;
    private Criteria criteria;
    public static String header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //using the activity_main layout
        locationHeader = findViewById(R.id.location); //set locationHeader field
        recycler = findViewById(R.id.recycler); //bind the recycler

        adapter = new Adapter(this, governmentOfficialList);
        recycler.setAdapter(adapter); //each object will appear in the recycler in a list format
        recycler.setLayoutManager(new LinearLayoutManager(this));

        //setting location network service
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); //Accuracy Medium gave errors...
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false); //don't need altitude
        criteria.setBearingRequired(false); //don't need direction of travel
        criteria.setSpeedRequired(false); //don't need speed

        //checking network and location permission
        if (hasNetwork() == true) {
            if (permissionCheck() == true) {
                Toast.makeText(this, "Has network with permission", Toast.LENGTH_LONG).show();
                readJSONData();
                locationHeader.setText(header);
            }
        } else {
            Toast.makeText(this, "No network", Toast.LENGTH_LONG).show();
            locationHeader.setText("No Location");
        }
    }

    //This method will check the location access permission
    private boolean permissionCheck() {
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_CODE_REQUEST); //ID = 111
            Toast.makeText(this, "No Permission", Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            setLocation();
            return true;
        }
    }

    //check if we have the right permission
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull
            String[] permissions, @NonNull
                    int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_CODE_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                setLocation();
                return;
            }
        }
        else{
            locationHeader.setText("No Permissionn");
            Toast.makeText(this, "No Permission", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void setLocation() {
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location currentLocation = null;
        Log.d(TAG, "setLocation: " + header);

        if (bestProvider != null) {
           currentLocation = locationManager.getLastKnownLocation(bestProvider);

           if (currentLocation != null) {
               if (header == null) { //if nothing is in the header, then we call Geocoder to get current address to call government downloader
                   Log.d(TAG, "setLocation: Latitude: " + currentLocation.getLatitude() +
                           " Longitude " + currentLocation.getLongitude()); //for debugging purposes
                   loadLocationData(currentLocation.getLatitude(), currentLocation.getLongitude());
               } else {
                   new Thread(new GovernmentOfficialDownloader(this, locationHeader.getText().toString())).start();
               }
           }
        } else {
            locationHeader.setText("No Data For Location");
        }
    }

    //This method takes the latitude and longitude/or location name and try to get an address
    public void loadLocationData(double latitude, double longitude){
        List<Address> addresses = new ArrayList<>();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 10); //getting a real location
            Address address = addresses.get(0);
            Log.d(TAG, "loadLocationData: Real Address from Latitude/Longitude: " + addresses.get(0)); //debugging purposes

            header = address.getPostalCode();
            Log.d(TAG, "loadDataFromLocation: Location ZipCode " + address.getPostalCode()); //debugging purposes

            new Thread(new GovernmentOfficialDownloader(this, header)).start(); //call downloader with zipcode

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //The thread calls this method when it is done parsing fields from GOOGLE
    public void updateData(ArrayList<Object> dataFromDownloader){
        governmentOfficialList.clear();
        if(!dataFromDownloader.isEmpty()){
            header = dataFromDownloader.get(0).toString(); //has searched the location information
            Log.d(TAG, "updateData: " + header );
            locationHeader.setText(header);

            ArrayList<GovernmentOfficial> list = (ArrayList<GovernmentOfficial>) dataFromDownloader.get(1); //has address, party, contact info, and social media fields
            for(int x = 0; x < list.size(); x++){
                GovernmentOfficial person = list.get(x);
                governmentOfficialList.add(person);
            }
            writeJSONData();
        }
        else {
            locationHeader.setText("No Location Data Returned");
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v){ //We will direct to the officer activity class when a position in the list has been selected
        Log.d(TAG, "onClick : Show data of official");
        int position = recycler.getChildLayoutPosition(v);
        Intent intent = new Intent(MainActivity.this, OfficerActivity.class);
        GovernmentOfficial official = governmentOfficialList.get(position);
        intent.putExtra("location", locationHeader.getText().toString());
        Bundle bundle = new Bundle();
        bundle.putSerializable("officer", official);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.about:
                Log.d(TAG, "onOptionsItemSelected: Selected AboutActivity Menu");
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.search:
                Log.d(TAG, "onOptionsItemSelected: Selected Search Menu");
                if(hasNetwork() == true){
                    getLocationDialog();
                }
                break;
        }
        return true;
    }

    private boolean hasNetwork() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            AlertDialog.Builder networkBuilder = new AlertDialog.Builder(this);
            networkBuilder.setMessage("Unable to retrieve data without network connection.");
            networkBuilder.setTitle("No Network Connection");
            AlertDialog dialog = networkBuilder.create();
            dialog.show();
            return false;
        }

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true; }
        else {
            AlertDialog.Builder networkBuilder = new AlertDialog.Builder(this);
            networkBuilder.setMessage("Unable to retrieve data without network connection.");
            networkBuilder.setTitle("No Network Connection");
            AlertDialog dialog = networkBuilder.create();
            dialog.show();
            return false;
        }
    }

    //Displays the initial dialog when the user enters a location preference
    private void getLocationDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(editText);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "findLocationDialog: CANCEL");
            }
        } );
        builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String locationInput = editText.getText().toString().trim();
                header = locationInput;
                Log.d(TAG, "findLocationDialog: OK");
                callDownloader(header);
            }
        } );
        builder.setTitle("Enter a City, State or a Zip Code");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //this method is to start the GOOGLE download thread
    public void callDownloader(String headerInput) {
        new Thread(new GovernmentOfficialDownloader(this, headerInput)).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //This error method is called by the GOOGLE thread class if the HTTP connect != 200
    public void showDownloaderError() {
        Toast.makeText(this, "Failed to connect to GOOGLE URL", Toast.LENGTH_LONG).show();
    }

    private void readJSONData() {
        try {
            FileInputStream fis = getApplicationContext().
                    openFileInput(getString(R.string.data_file));

            // Read string content from file
            byte[] data = new byte[fis.available()]; // this technique is good for small files
            int loaded = fis.read(data);
            Log.d(TAG, "readJSONData: Loaded " + loaded + " bytes");
            fis.close();
            String json = new String(data);

            // Create JSON Array from string file content
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                String role = obj.getString("role");
                String name = obj.getString("name");
                String politicalParty = obj.getString("politicalParty");
                String address = obj.getString("address");
                String phone = obj.getString("phone");
                String websiteURL = obj.getString("websiteURL");
                String email = obj.getString("email");
                String photo = obj.getString("photo");
                String facebook = obj.getString("facebook");
                String twitter = obj.getString("twitter");
                String youtube = obj.getString("youtube");

                // Create object and add to list
                GovernmentOfficial g = new GovernmentOfficial(role, name, politicalParty, address,
                        phone, websiteURL, email, photo, facebook, twitter, youtube);
                governmentOfficialList.add(g);
            }
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeJSONData() {

        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput(getString(R.string.data_file), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            writer.setIndent("  ");
            writer.beginArray();
            for (GovernmentOfficial g : governmentOfficialList) {
                writer.beginObject();
                writer.name("role").value(g.getRole());
                writer.name("name").value(g.getName());
                writer.name("politicalParty").value(g.getPoliticalParty());
                writer.name("address").value(g.getAddress());
                writer.name("phone").value(g.getPhone());
                writer.name("websiteURL").value(g.getWebsiteURL());
                writer.name("email").value(g.getEmail());
                writer.name("photo").value(g.getPhoto());
                writer.name("facebook").value(g.getFacebook());
                writer.name("twitter").value(g.getTwitter());
                writer.name("youtube").value(g.getYoutube());
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "writeJSONData: " + e.getMessage());
        }
    }
}
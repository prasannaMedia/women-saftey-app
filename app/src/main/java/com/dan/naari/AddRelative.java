package com.dan.naari;
import  java.lang.String;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.content.Context;
import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import android.os.Looper;
import android.provider.Settings;
import android.database.sqlite.SQLiteOpenHelper;
//import android.support.v7.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;
import android.telephony.SmsManager;
import android.app.PendingIntent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AddRelative extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;
    private static final int REQUEST_SMS = 1;
    double latitude;
    double longitude;
    DatabaseHelper myDB;
    Button btnAdd,btnView;
    EditText editText, editText2;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_relative);
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnView = (Button) findViewById(R.id.btnView);
        myDB = new DatabaseHelper(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEntry = editText.getText().toString();
                String newEntry1 = editText2.getText().toString();
                if(editText.length()!= 0){
                    AddData(newEntry);
                    AddData(newEntry1);
                    editText.setText("");
                }else{
                    Toast.makeText(AddRelative.this, "You must put something in the text field!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddRelative.this, ViewListContents.class);
                startActivity(intent);
            }
        });


    }
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                           latitude= location.getLatitude();
                           longitude=location.getLongitude();
                            System.out.println(latitude);
                            System.out.println(longitude);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            System.out.println(latitude);
            System.out.println(longitude);
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }


    public void AddData(String newEntry) {

        boolean insertData = myDB.addData(newEntry);

        if(insertData==true){
            Toast.makeText(this, "Data Successfully Inserted!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Something went wrong :(.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int action, keycode;

        action = event.getAction();
        String Number;
        int count=0;
        keycode = event.getKeyCode();
        switch (keycode)
        {
            case KeyEvent.KEYCODE_VOLUME_UP:
                {
                if(KeyEvent.ACTION_UP == action){

                    ArrayList<String> theList = new ArrayList<>();
                    Cursor data = myDB.getListContents();
                    if(data.getCount() == 0){
                        Toast.makeText(this, "There are no contents in this list!",Toast.LENGTH_LONG).show();
                    }else {
                        while (data.moveToNext()) {
                            theList.add(data.getString(1));

                        }
                    }
                    System.out.println(theList);
                    int counter=1;
                    while (counter<theList.size()){
                        System.out.println(counter);
                        Number=theList.get(counter);
                        System.out.println(Number);
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + Number.trim()));
                        if (ActivityCompat.checkSelfPermission(AddRelative.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(AddRelative.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                            startActivity(callIntent);
                        } else {
                            callIntent.setData(Uri.parse("tel:" + Number.trim()));
                            startActivity(callIntent);
                        }
                        if (ActivityCompat.checkSelfPermission(AddRelative.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(AddRelative.this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS);
                            Intent intent=new Intent(getApplicationContext(),AddRelative.class);
                            PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
                            SmsManager sms=SmsManager.getDefault();
                            sms.sendTextMessage(Number.trim(), null, "Hello i am in emergency situation my location is: https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude, pi,null);
                        } else {
                            Intent intent=new Intent(getApplicationContext(),AddRelative.class);
                            PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
                            SmsManager sms=SmsManager.getDefault();
                            sms.sendTextMessage(Number.trim(), null, "Hello i am in emergency situation my location is: https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude, pi,null);
                        }
                        startActivity(callIntent);
                        counter +=2;
                    }

                }
            }
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(KeyEvent.ACTION_DOWN == action){
                    //count = 0;
                    //String S2 = String.valueOf(count);
                    //Log.d("downButton", S2);
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + "9482090700"));
                    if (ActivityCompat.checkSelfPermission(AddRelative.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddRelative.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);

                    } else {
                        callIntent.setData(Uri.parse("tel:"));
                        startActivity(callIntent);
                    }
                    startActivity(callIntent);
                }
        }
        return super.dispatchKeyEvent(event);
    }
}


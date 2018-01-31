package com.example.kelvin.testinglocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    //Handle location updates
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    private Location currentLocation;//Device current location

    private TextView tvLocation;//Text on screen
    String androidId;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);//Get location client
        initGui();//Initializes GUI elements
        getStartLocation();
        createLocationRequest();//Creates object to handle location

        getCurrentLocationSettings();
        startLocationUpdates();
    }

    void initGui() {
        tvLocation = findViewById(R.id.tvLocation);

        //What to do on location update
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLastLocation();

                String ss = "Latitude: " + currentLocation.getLatitude() + "\n" +
                        "Longitude: " + currentLocation.getLongitude() + "\n" +
                        "Altitude: " + currentLocation.getAltitude() + " (meters from WGS Ellipsoid)\n" +
                        "Speed: " + currentLocation.getSpeed() + " (m/s)\n" +
                        "Time: " + new java.util.Date(currentLocation.getTime()).toString() + "\n" +
                        "Accuracy: " + currentLocation.getAccuracy() + " (meters of error)\n" +
                        "Your ID: " + androidId + " \n" +
                        "Update #: " + count++;
                tvLocation.setText(ss);
            }
        };
    }

    void createLocationRequest() {
        //Create an object to request location and set parameters
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);//Preferred update rate, milliseconds
        locationRequest.setFastestInterval(500);//Fastest update rate, milliseconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Prioritize accuracy over battery usage; Accurate to a few feet (in theory)
    }

    void getStartLocation() {
        //Check location permission and prompt if required
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //If location successfully found
                if (location != null) {
                    //Display current coordinates to user
                    currentLocation = location;
                    /*
                    String ss = "Latitude: " + currentLocation.getLatitude() + "\n" +
                            "Longitude: " + currentLocation.getLongitude() + "\n" +
                            "Altitude: " + currentLocation.getAltitude() + "\n" +
                            "Speed: " + currentLocation.getSpeed() + "\n" +
                            "Time: " + currentLocation.getTime() + "\n" +
                            "Accuracy: " + currentLocation.getAccuracy() + "\n";
                            */
                    String ss = "";
                    tvLocation.setText(ss);
                } else {
                    tvLocation.setText("Starting location not found");
                }
            }
        });
    }

    void getCurrentLocationSettings() {
        //Create location request with created location object
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
    }

    void startLocationUpdates() {
        //Check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

}

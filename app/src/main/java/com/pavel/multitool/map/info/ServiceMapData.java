package com.pavel.multitool.map.info;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class ServiceMapData extends Service {
    //private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback locationCallback;
    private LocationBreedcrumb locationBreedcrumb;
    private List<Location> savedLocations;
    private boolean locationPriority;
    //private List<Location> savedLocation;


    private final long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private final long FASTEST_INTERVAL = 2000; /* 2 sec */

    //текущее местоположение
    private Location currentLocation;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        init();
        Toast.makeText(this, "service on started", Toast.LENGTH_SHORT).show();
        startLocationUpdate();

    }

    private void init() {
        locationBreedcrumb = (LocationBreedcrumb) getApplicationContext();
        savedLocations = locationBreedcrumb.getMyLocations();
        locationPriority = locationBreedcrumb.isLocationPriority();

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        //callback
        locationCallback = new LocationCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // do work here
                onLocationChanged(locationResult.getLastLocation());
            }
        };

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, locationCallback,
                null);

    }

    public void onLocationChanged(Location location) {
        locationPriority = locationBreedcrumb.isLocationPriority();
        if (locationPriority) {
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            Toast.makeText(this, "GPS", Toast.LENGTH_SHORT).show();
        }
            else {
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            Toast.makeText(this, "GSM/WIFI", Toast.LENGTH_SHORT).show();
        }

        Intent i = new Intent("location_update");
        i.putExtra("latitude", location.getLatitude());
        i.putExtra("longitude", location.getLongitude());
        i.putExtra("accuracy",String.valueOf(location.getAccuracy()));

        if (location.hasAltitude()) {
            String alt = String.valueOf(Math.round(location.getAltitude()));
            i.putExtra("altitude", alt);
        } else
            i.putExtra("altitude",0);

        if (location.hasSpeed()) {
            String speed = String.valueOf(location.getSpeed());
            i.putExtra("speed", speed);
        } else
            i.putExtra("speed",0);

        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String ad = addresses.get(0).getAddressLine(0);
            i.putExtra("address",ad);
        } catch (Exception e) {
            i.putExtra("address","Данные не доступны");

        }


        sendBroadcast(i);
        // You can now create a LatLng Object for use with maps
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        savedLocations.add(location);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "service on destroyed", Toast.LENGTH_SHORT).show();
        getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
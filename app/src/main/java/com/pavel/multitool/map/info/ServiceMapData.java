package com.pavel.multitool.map.info;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.pavel.multitool.R;

import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class ServiceMapData extends Service {
    private static final String CHANNEL_ID = "com.pavel.multitool.map.info";
    //private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback locationCallback;
    private LocationBreedcrumb locationBreedcrumb;
    private List<Location> savedLocations;
    private boolean locationPriority;
    //private List<Location> savedLocation;


    private final long UPDATE_INTERVAL = 15 * 1000;  /* 10 secs */
    private final long FASTEST_INTERVAL = 3000; /* 2 sec */

    //текущее местоположение
    //private Location currentLocation;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();


        init();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
        // Toast.makeText(this, "service on started", Toast.LENGTH_SHORT).show();
        startLocationUpdate();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        //Главное, правилное уведомление состряпать
        String channelName = "Service GPS";
        NotificationChannel chan = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_baseline_coronavirus_24)
                .setContentTitle("Сервис геолокации включен.")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    //    START_NOT_STICKY – сервис не будет перезапущен после того, как был убит системой
//
//    START_STICKY – сервис будет перезапущен после того, как был убит системой
//
//    START_REDELIVER_INTENT – сервис будет перезапущен после того, как был убит системой. Кроме этого, сервис снова получит все вызовы startService, которые не были завершены методом stopSelf(startId).
//
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
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
            // Toast.makeText(this, "GPS", Toast.LENGTH_SHORT).show();
        } else {
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            //Toast.makeText(this, "GSM/WIFI", Toast.LENGTH_SHORT).show();
        }

        Intent i = new Intent("location_update");
        i.putExtra("latitude", location.getLatitude());
        i.putExtra("longitude", location.getLongitude());
        i.putExtra("accuracy", String.valueOf(location.getAccuracy()));

        if (location.hasAltitude()) {
            String alt = String.valueOf(Math.round(location.getAltitude()));
            i.putExtra("altitude", alt);
        } else
            i.putExtra("altitude", 0);

        if (location.hasSpeed()) {
            String speed = String.valueOf(location.getSpeed());
            i.putExtra("speed", speed);
        } else
            i.putExtra("speed", "0");

        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String ad = addresses.get(0).getAddressLine(0);
            i.putExtra("address", ad);
        } catch (Exception e) {
            i.putExtra("address", "Данные не доступны");

        }


        sendBroadcast(i);
        // You can now create a LatLng Object for use with maps
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (savedLocations.isEmpty()) {
            savedLocations.add(location);
        } else
            compareLatLongData(location);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "service on destroyed", Toast.LENGTH_SHORT).show();
        getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //@SuppressLint("SetTextI18n")
    //стравнение данных, чтобы не забивать список дубликатами одной точки
    private void compareLatLongData(Location location) {
        if (savedLocations.size() > 0) {
            int dotLatOld = Double.toString(savedLocations.get(savedLocations.size() - 1).getLatitude()).indexOf(".");
            int dotLatNew = Double.toString(location.getLatitude()).indexOf(".");
            int dotLonOld = Double.toString(savedLocations.get(savedLocations.size() - 1).getLongitude()).indexOf(".");
            int dotLonNew = Double.toString(location.getLongitude()).indexOf(".");

            String latitOld = Double.toString(savedLocations.get(savedLocations.size() - 1).getLatitude()).substring(0, dotLatOld + 3);
            String latitNew = Double.toString(location.getLatitude()).substring(0, dotLatNew + 3);
            String longitOld = Double.toString(savedLocations.get(savedLocations.size() - 1).getLongitude()).substring(0, dotLonOld + 3);
            String longitNew = Double.toString(location.getLongitude()).substring(0, dotLonNew + 3);

            if (!latitNew.equalsIgnoreCase(latitOld) || !longitNew.equalsIgnoreCase(longitOld)) {
                savedLocations.add(location);
                Toast.makeText(this, "Добавилась новая точка", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Точка не изменилась", Toast.LENGTH_LONG).show();
            }

        }
    }


}
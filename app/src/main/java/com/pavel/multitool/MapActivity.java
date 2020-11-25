package com.pavel.multitool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pavel.multitool.map.info.LocationBreedcrumb;
import com.pavel.multitool.map.info.ServiceMapData;
import com.pavel.multitool.map.info.ShowSavedLocationList;
import com.pavel.multitool.map.info.TrekerMapActivity;

import java.math.BigDecimal;
import java.util.List;


public class MapActivity extends AppCompatActivity {

    //    public static final int DEFAULT_UPDATE_INTERVAL = 30;
//    public static final int FAST_UPDATE_INTERVAL = 5;
    public static final int PRIORITY_HIGH_ACCURACY = 100;
    public static final int PRIORITY_BALANCED_POWER_ACCURACY = 102;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private TextView tvLat, tvLon, tvAltitude, tvAccuracy, tvSpeed, tvSensor, tvUpdates, tvAddress, tvWayPointCounts;
    private Button btnNewWayPoints, btnClearPointList, btnShowMap;
    private Switch swLocationUpdates, swGps;

    private double latitude, longitude;

    boolean locationPriority;
    private LocationBreedcrumb locationBreedcrumb;

    //список точек локации
    List<Location> savedLocations;
    //    private FusedLocationProviderClient fusedLocationProviderClient;
//    private LocationRequest locationRequest;
//    private LocationCallback locationCallback;
    private BroadcastReceiver broadcastReceiver;
    //текущее местоположение
//    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //tvLat

                    updateValues(intent);

                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));

    }

    private void updateValues(Intent intent) {
        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude", 0.0);
        tvLat.setText(String.valueOf(latitude));
        tvLon.setText(String.valueOf(longitude));
        tvAccuracy.setText(intent.getStringExtra("accuracy"));
        tvAltitude.setText(intent.getStringExtra("altitude"));
        tvSpeed.setText((String.valueOf(intent.getIntExtra("speed", 0))));
        tvAddress.setText(String.valueOf(intent.getStringExtra("address")));

        LocationBreedcrumb locationBreedcrumb = (LocationBreedcrumb) getApplicationContext();
        savedLocations = locationBreedcrumb.getMyLocations();
        if (savedLocations.size() > 0) {
            tvWayPointCounts.setText(String.valueOf(savedLocations.size()));
        }
    }

    private void init() {
        tvLat = findViewById(R.id.tv_lat);
        tvLon = findViewById(R.id.tv_lon);
        tvAltitude = findViewById(R.id.tv_altitude);
        tvAccuracy = findViewById(R.id.tv_accuracy);
        tvSpeed = findViewById(R.id.tv_speed);
        tvSensor = findViewById(R.id.tv_sensor);
        tvUpdates = findViewById(R.id.tv_updates);
        tvAddress = findViewById(R.id.tv_address);

        tvWayPointCounts = findViewById(R.id.tv_crumb_count);
        swLocationUpdates = findViewById(R.id.sw_locationsupdates);
        swGps = findViewById(R.id.sw_gps);

        btnNewWayPoints = findViewById((R.id.btn_new_way_point));
        btnClearPointList = findViewById(R.id.btn_sho_wayPoint_list);
        btnShowMap = findViewById(R.id.btn_show_map);

        locationBreedcrumb = (LocationBreedcrumb) getApplicationContext();
        locationPriority = locationBreedcrumb.isLocationPriority();

        if (!runtime_permissions())
            enable_buttons();

    }

//    @SuppressLint("SetTextI18n")
//    //стравнение данных, чтобы не забивать список дубликатами одной точки
//    private boolean compareLatLongData() {
//        if (savedLocation.size() > 0) {
//            int dotLatOld = Double.toString(savedLocation.get(savedLocation.size() - 1).getLatitude()).indexOf(".");
//            int dotLatNew = Double.toString(currentLocation.getLatitude()).indexOf(".");
//            int dotLonOld = Double.toString(savedLocation.get(savedLocation.size() - 1).getLongitude()).indexOf(".");
//            int dotLonNew = Double.toString(currentLocation.getLongitude()).indexOf(".");
//
//            String latitOld = Double.toString(savedLocation.get(savedLocation.size() - 1).getLatitude()).substring(0, dotLatOld + 3);
//            String latitNew = Double.toString(currentLocation.getLatitude()).substring(0, dotLatNew + 3);
//            String longitOld = Double.toString(savedLocation.get(savedLocation.size() - 1).getLongitude()).substring(0, dotLonOld + 3);
//            String longitNew = Double.toString(currentLocation.getLongitude()).substring(0, dotLonNew + 3);
//
//            Toast.makeText(MapActivity.this, longitNew + " " + longitOld, Toast.LENGTH_LONG).show();
//
//            if (!latitNew.equalsIgnoreCase(latitOld) || !longitNew.equalsIgnoreCase(longitOld)) {
//                savedLocation.add(currentLocation);
//                tvWayPointCounts.setText(Integer.toString(savedLocation.size()));
//                Toast.makeText(MapActivity.this, "Добавилась новая точка", Toast.LENGTH_LONG).show();
//                updateOn = true;
//            } else {
//                Toast.makeText(MapActivity.this, "Точка не изменилась", Toast.LENGTH_LONG).show();
//                updateOn = false;
//            }
//
//        } else {
//            //savedLocation.add(currentLocation);
//            updateOn = true;
//        }
//        return updateOn;
//    }


    private void locationNotAcces() {
        tvUpdates.setText("Геолокация отключена");
        tvLat.setText("Геолокация отключена");
        tvLon.setText("Геолокация отключена");
        tvSpeed.setText("Геолокация отключена");
        tvAltitude.setText("Геолокация отключена");
        tvAccuracy.setText("Геолокация отключена");
        tvSensor.setText("Геолокация отключена");
        tvAddress.setText("Геолокация отключена");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //                    updateGPS();  enable Buttons
                    enable_buttons();
                } else {
                    Toast.makeText(this, "Данное приложение требует соответствующего разрешения для продолжения работы",
                            Toast.LENGTH_SHORT).show();
                    runtime_permissions();
                }
                break;
        }
    }

    private void enable_buttons() {
        //чистка списка точек
        btnNewWayPoints.setOnClickListener(v -> {
            savedLocations.clear();
            tvWayPointCounts.setText(String.valueOf(savedLocations.size()));
        });

        swGps.setOnClickListener(v -> {
            if (swGps.isChecked()) {
                //приоритетно используется GPS
                locationPriority = true;
                locationBreedcrumb.setLocationPriority(locationPriority);
                tvSensor.setText(R.string.tvsensor_text_gps);
            } else {
                //приоритет gsm and wi-fi данным
                locationPriority = false;
                locationBreedcrumb.setLocationPriority(locationPriority);
                tvSensor.setText(R.string.tvsensor_text_gsm_wifi);
            }
        });

        swLocationUpdates.setOnClickListener(v -> {
            if (swLocationUpdates.isChecked()) {
                // вкл трекинг
                startLocationUpdates();
            } else {
                //отклык трекинг
                stopLocationUpdates();

            }
        });

        btnClearPointList.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, ShowSavedLocationList.class);
            startActivity(intent);
        });

        btnShowMap.setOnClickListener(v -> {
            Intent i = new Intent(MapActivity.this, TrekerMapActivity.class);
            startActivity(i);
        });
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        tvUpdates.setText("Геолокация включена");
        if (locationPriority) tvSensor.setText(R.string.tvsensor_text_gps);
            else tvSensor.setText(R.string.tvsensor_text_gsm_wifi);

        Intent i = new Intent(getApplicationContext(), ServiceMapData.class);
        startService(i);
    }

    private void stopLocationUpdates() {
        locationNotAcces();
        Intent i = new Intent(getApplicationContext(), ServiceMapData.class);
        stopService(i);
    }

    private boolean runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }
}
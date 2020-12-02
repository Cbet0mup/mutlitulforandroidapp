package com.pavel.multitool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.pavel.multitool.map.info.LocationBreedcrumb;
import com.pavel.multitool.map.info.ServiceMapData;
import com.pavel.multitool.map.info.ShowSavedLocationList;
import com.pavel.multitool.map.info.TrekerMapActivity;

import java.util.List;


public class MapActivity extends AppCompatActivity {

    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private TextView tvLat, tvLon, tvAltitude, tvAccuracy, tvSpeed, tvSensor, tvUpdates, tvAddress, tvWayPointCounts;
    private Button btnNewWayPoints, btnClearPointList, btnShowMap;
    private Switch swLocationUpdates, swGps;

    private double latitude, longitude;
    //gsm/gps
    boolean locationPriority;
    //singleton
    private LocationBreedcrumb locationBreedcrumb;

    //список точек локации
    List<Location> savedLocations;

    private BroadcastReceiver broadcastReceiver;

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        tvSpeed.setText((String.valueOf(intent.getStringExtra("speed"))));
        tvAddress.setText(String.valueOf(intent.getStringExtra("address")));

        LocationBreedcrumb locationBreedcrumb = (LocationBreedcrumb) getApplicationContext();
        savedLocations = locationBreedcrumb.getMyLocations();
        if (savedLocations.size() > 0) {
            tvWayPointCounts.setText(String.valueOf(savedLocations.size()));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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


    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        Intent i = new Intent(getApplicationContext(), ServiceMapData.class);
        stopService(i);
        super.onDestroy();
    }
}
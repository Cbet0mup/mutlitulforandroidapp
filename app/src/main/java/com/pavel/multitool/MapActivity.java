package com.pavel.multitool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pavel.multitool.map.info.LocationBreedcrumb;
import com.pavel.multitool.map.info.MapsActivity;
import com.pavel.multitool.map.info.ServiceMapData;
import com.pavel.multitool.map.info.ShowSavedLocationList;

import java.math.BigDecimal;
import java.util.List;


public class MapActivity extends AppCompatActivity {

    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private TextView tvLat, tvLon, tvAltitude, tvAccuracy, tvSpeed, tvSensor, tvUpdates, tvAddress, tvWayPointCounts;
    private Button btnNewWayPoints, btnClearPointList, btnShowMap;
    private Switch swLocationUpdates, swGps;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ServiceMapData data;
    //boolean updateOn = false;

                                                    //текущее местоположение
    private Location currentLocation;
                                                     //список точек локации
    List<Location> savedLocation;

    LocationRequest locationRequest;
    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        init();
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

        data = new ServiceMapData();

        btnNewWayPoints = findViewById((R.id.btn_new_way_point));
        btnClearPointList = findViewById(R.id.btn_sho_wayPoint_list);
        btnShowMap = findViewById(R.id.btn_show_map);

                                                                    //установки запроса локации
        locationRequest = new LocationRequest();
                                                                         //частота запроса
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallback = new LocationCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //int myData = data.getData();
                //Toast.makeText(MapActivity.this, "data = :" + myData, Toast.LENGTH_LONG).show();
                                                                            //сохраним лакацию
                currentLocation = locationResult.getLastLocation();
                updateUIValues(locationResult.getLastLocation());
                compareLatLongData();
            }
        };
                                                                            //чистка списка точек
        btnNewWayPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedLocation.clear();
            }
        });

        swGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swGps.isChecked()) {
                                                                     //приоритетно используется GPS
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tvSensor.setText(R.string.tvsensor_text_gps);
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tvSensor.setText(R.string.tvsensor_text_gsm_wifi);
                }
            }
        });

        swLocationUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swLocationUpdates.isChecked()) {
                    // вкл трекинг
                    startLocationUpdates();
                    startService( new Intent(MapActivity.this, ServiceMapData.class));
                } else {
                    //отклык трекинг
                    stopLocationUpdates();
                    stopService(new Intent(MapActivity.this, ServiceMapData.class));

                }
            }
        });

        btnClearPointList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, ShowSavedLocationList.class);
                startActivity(intent);
            }
        });

        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });
        updateGPS();
    }

    @SuppressLint("SetTextI18n")
    private void compareLatLongData() {
        if (savedLocation.size() > 0) {
            BigDecimal latitOld = new BigDecimal(Double.toString(savedLocation.get(savedLocation.size() - 1).getLatitude()));
            latitOld = latitOld.setScale(4, BigDecimal.ROUND_DOWN);

            BigDecimal latitNew = new BigDecimal(Double.toString(currentLocation.getLatitude()));
            latitNew = latitNew.setScale(4, BigDecimal.ROUND_DOWN);

            BigDecimal longitOld = new BigDecimal(Double.toString( savedLocation.get(savedLocation.size() - 1).getLongitude()));
            longitOld = longitOld.setScale(4, BigDecimal.ROUND_DOWN);

            BigDecimal longitNew = new BigDecimal(Double.toString(currentLocation.getLongitude()));
            longitNew = longitNew.setScale(4, BigDecimal.ROUND_DOWN);

            if (latitOld.compareTo(latitNew) < 0 || longitNew.compareTo(longitOld) < 0) {
                savedLocation.add(currentLocation);
                tvWayPointCounts.setText(Integer.toString(savedLocation.size()));
            } else
                Toast.makeText(MapActivity.this, "Точка не изменилась", Toast.LENGTH_LONG).show();

        } else
            savedLocation.add(currentLocation);
    }

    private void stopLocationUpdates() {
        locationNotAcces();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
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

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        tvUpdates.setText("Геолокация включена");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        updateGPS();
        //Toast.makeText(MapActivity.this, "Start Location Update", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
                    Toast.makeText(this, "Данное приложение требует соответствующего разрешения для продолжения работы",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    public void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //разрешения даны
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //получили разрешения, нужно получить данные и обновить Ui
                    updateUIValues(location);
                    currentLocation = location;             //сохраним текущую локацию
                }
            });
        } else {
            //разрешения не даны
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateUIValues(Location location) {
        //обновляем текстовые поля в UI
        if (location != null) {
            tvLat.setText(String.valueOf(location.getLatitude()));
            tvLon.setText(String.valueOf(location.getLongitude()));
            tvAccuracy.setText(String.valueOf(Math.round(location.getAccuracy())));

            if (location.hasAltitude()) {
                tvAltitude.setText(String.valueOf(Math.round(location.getAltitude())));
            } else
                tvAltitude.setText("Не доступно");

            if (location.hasSpeed()) {
                tvSpeed.setText(String.valueOf(Math.round(location.getSpeed() * 3.6)));
            } else
                tvSpeed.setText("Не доступно");
        }   else locationNotAcces();

                                                            //получаем адрес
        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tvAddress.setText(addresses.get(0).getAddressLine(0));
        } catch (Exception e) {
            tvAddress.setText("Данные не доступны");
        }

        LocationBreedcrumb locationBreedcrumb = (LocationBreedcrumb) getApplicationContext();
        savedLocation = locationBreedcrumb.getMyLocations();
        //колличество сохранённых путевых точек
        tvWayPointCounts.setText(Integer.toString(savedLocation.size()));


    }


}
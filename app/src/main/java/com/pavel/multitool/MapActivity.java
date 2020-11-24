package com.pavel.multitool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    boolean updateOn;

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

                   latitude = intent.getDoubleExtra("latitude", 0.0);
                   longitude = intent.getDoubleExtra("longitude", 0.0);
                   tvLat.setText(String.valueOf(latitude));
                   tvLon.setText(String.valueOf(longitude));

                    LocationBreedcrumb locationBreedcrumb = (LocationBreedcrumb) getApplicationContext();
                    savedLocations = locationBreedcrumb.getMyLocations();
                    if (savedLocations.size() > 0) {
                        tvWayPointCounts.setText(String.valueOf(savedLocations.size()));
                    }

                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));

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

        //чистка списка точек
        btnNewWayPoints.setOnClickListener(v -> savedLocations.clear());

        if (!runtime_permissions())
            enable_buttons();


        //установки запроса локации
//        locationRequest = new LocationRequest();
        //частота запроса
//        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
//        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
//        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

//        locationCallback = new LocationCallback() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//
//                Toast.makeText(MapActivity.this, "Location callback", Toast.LENGTH_LONG).show();
//                //сохраним лакацию
//                currentLocation = locationResult.getLastLocation();
//
//                if (compareLatLongData()) {
//                    updateUIValues(locationResult.getLastLocation());
//                }
//            }
//        };


//        swGps.setOnClickListener(v -> {
//            if (swGps.isChecked()) {
//                //приоритетно используется GPS
//                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//                tvSensor.setText(R.string.tvsensor_text_gps);
//            } else {                                            //приоритет gsm and wi-fi данным
//                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//                tvSensor.setText(R.string.tvsensor_text_gsm_wifi);
//            }
//        });
//
//        swLocationUpdates.setOnClickListener(v -> {
//            if (swLocationUpdates.isChecked()) {
//                // вкл трекинг
//                startLocationUpdates();
//                //startService(new Intent(MapActivity.this, ServiceMapData.class));
//            } else {
//                //отклык трекинг
//                stopLocationUpdates();
//               // stopService(new Intent(MapActivity.this, ServiceMapData.class));
//
//            }
//        });
//
//        btnClearPointList.setOnClickListener(v -> {
//            Intent intent = new Intent(MapActivity.this, ShowSavedLocationList.class);
//            startActivity(intent);
//        });
//
//        btnShowMap.setOnClickListener(v -> {
//            Intent i = new Intent(MapActivity.this, TrekerMapActivity.class);
//            startActivity(i);
//        });
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
        swGps.setOnClickListener(v -> {
            if (swGps.isChecked()) {
                //приоритетно используется GPS
                //locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                Intent i = new Intent("gpsPriority");
                i.putExtra("priority", PRIORITY_HIGH_ACCURACY);
                sendBroadcast(i);
                tvSensor.setText(R.string.tvsensor_text_gps);
            } else {                                            //приоритет gsm and wi-fi данным
                // locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                Intent i = new Intent("gpsPriority");
                i.putExtra("priority", PRIORITY_BALANCED_POWER_ACCURACY);
                sendBroadcast(i);

                tvSensor.setText(R.string.tvsensor_text_gsm_wifi);
            }
        });

        swLocationUpdates.setOnClickListener(v -> {
            if (swLocationUpdates.isChecked()) {
                // вкл трекинг
                startLocationUpdates();
                //startService(new Intent(MapActivity.this, ServiceMapData.class));
            } else {
                //отклык трекинг
                stopLocationUpdates();
                // stopService(new Intent(MapActivity.this, ServiceMapData.class));

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
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        Intent i = new Intent(getApplicationContext(), ServiceMapData.class);
        startService(i);

        //Toast.makeText(MapActivity.this, "StartLocationUpdate", Toast.LENGTH_LONG).show();

    }
    private void stopLocationUpdates() {
        locationNotAcces();
        Intent i = new Intent(getApplicationContext(), ServiceMapData.class);
        stopService(i);
        //Toast.makeText(MapActivity.this, "service stopped", Toast.LENGTH_SHORT).show();
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private boolean runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            return true;
        }
        return false;
    }

//    public void updateGPS() {
////        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            //разрешения даны
//
//            startLocationUpdates();
//
////            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
////                @Override
////                public void onSuccess(Location location) {
////                    //получили разрешения, нужно получить данные и обновить Ui
////                    Toast.makeText(MapActivity.this, "UpdateGPS", Toast.LENGTH_LONG).show();
////                    updateUIValues(location);
////                    currentLocation = location;             //сохраним текущую локацию
////                }
////            });
//        } else {
//            //разрешения не даны
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
//            }
//        }
//    }

    @SuppressLint("SetTextI18n")
    private void updateUIValues(Location location) {
        //обновляем текстовые поля в UI
        Toast.makeText(MapActivity.this, "UpdateUI ", Toast.LENGTH_LONG).show();

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
        } else locationNotAcces();

        //получаем адрес
        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tvAddress.setText(addresses.get(0).getAddressLine(0));
        } catch (Exception e) {
            tvAddress.setText("Данные не доступны");
        }
//колличество сохранённых путевых точек
        LocationBreedcrumb locationBreedcrumb = (LocationBreedcrumb) getApplicationContext();
        savedLocations = locationBreedcrumb.getMyLocations();
        //savedLocation.add(currentLocation);

        tvWayPointCounts.setText(savedLocations.size());
    }

    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }
}
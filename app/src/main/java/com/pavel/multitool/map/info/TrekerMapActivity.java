package com.pavel.multitool.map.info;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pavel.multitool.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrekerMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private UiSettings uiSettings;
    private List<Location> savedLocations;
    private List<LatLng> latLngs;
    private BroadcastReceiver broadcastReceiver;

    private static final int PATTERN_DASH_LENGTH_PX = 50;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final Dot DOT = new Dot();
    private static final Dash DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final Gap GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_DOTTED = Arrays.asList(DOT, GAP);
    private static final List<PatternItem> PATTERN_DASHED = Arrays.asList(DASH, GAP);
    private static final List<PatternItem> PATTERN_MIXED = Arrays.asList(DOT, GAP, DOT, DASH, GAP);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treker_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LocationBreedcrumb locationBreedcrumb = (LocationBreedcrumb) getApplicationContext();
        savedLocations = locationBreedcrumb.getMyLocations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {


                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        uiSettings = mMap.getUiSettings();
        latLngs = new ArrayList<>();

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        uiSettings.setZoomControlsEnabled(true);            // показывать кнопки зума

        LatLng lastLocationPlaced = sydney;


        for (Location l : savedLocations) {
            LatLng latLng = new LatLng(l.getLatitude(), l.getLongitude());
            //опции маркера
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Lat: " + l.getLatitude() + "  Lon: " + l.getLongitude());
            markerOptions.icon(bitmapDescriptorFromVector(this, R.drawable.ic_action_map_icon));
            markerOptions.anchor(0.5f, 0.5f);
            mMap.addMarker(markerOptions);

            latLngs.add(latLng);
            lastLocationPlaced = latLng;
        }
        //добавим линию связывающую маркеры
        mMap.addPolyline(new PolylineOptions()
                        .addAll(latLngs)
                .width(5)
                .color(Color.BLUE)
                .geodesic(false)
                .pattern(PATTERN_MIXED)
        );

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocationPlaced, 12.0f));
        mMap.setMyLocationEnabled(true);
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
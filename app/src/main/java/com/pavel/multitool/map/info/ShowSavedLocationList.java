package com.pavel.multitool.map.info;

import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pavel.multitool.R;

import java.util.List;

public class ShowSavedLocationList extends AppCompatActivity {

    ListView lv_savedLocations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_location_list);

        lv_savedLocations = findViewById(R.id.lv_waypoints);

        LocationBreedcrumb locationBreedcrumb = (LocationBreedcrumb) getApplicationContext();
        List<Location> locations = locationBreedcrumb.getMyLocations();

        lv_savedLocations.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, locations));
    }

}
package com.pavel.multitool.map.info;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;
// Singleton class
public class LocationBreedcrumb extends Application {
    private static LocationBreedcrumb singleton;
    private List<Location> myLocations;
    private boolean locationPriority;

    public void setMyLocations(List<Location> myLocations) {
        this.myLocations = myLocations;
    }

    public List<Location> getMyLocations() {
        return myLocations;
    }

    public boolean isLocationPriority() {
        return locationPriority;
    }

    public void setLocationPriority(boolean locationPriority) {
        this.locationPriority = locationPriority;
    }

    public LocationBreedcrumb getInstance(){
        return singleton;
    }
    public void onCreate(){
        super.onCreate();
        singleton = this;
        myLocations = new ArrayList<>();
        locationPriority = false;
    }
}

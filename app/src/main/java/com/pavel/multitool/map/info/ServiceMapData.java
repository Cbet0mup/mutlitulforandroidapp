package com.pavel.multitool.map.info;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.pavel.multitool.MapActivity;

import java.util.concurrent.TimeUnit;

public class ServiceMapData extends Service {

    public ServiceMapData() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        taskService();
        return super.onStartCommand(intent, flags, startId);
    }

    private void taskService() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Сервис остановлен", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
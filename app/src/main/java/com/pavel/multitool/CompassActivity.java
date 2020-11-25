package com.pavel.multitool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView ivDynamicRose;
    private TextView tvDegreeView;
    private float currentDegree = 0f;
    private SensorManager sensorManager;   //класс управления сенсорами

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();       //каждый раз при обращении к активити нужно сверяться с сенсорами
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), sensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this); //отключаем слушатель на время паузы
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]); //угол отклонения
        textInfoUpdate(degree);
        //анимация вращения ёлки
        //RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);
        ivDynamicRose.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //инит
    private void init(){
        ivDynamicRose = findViewById(R.id.iv_compass_dynamic_rose);
        tvDegreeView = findViewById(R.id.tvDegree);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    private void textInfoUpdate(float degree){
        if (degree >= 0.0 && degree <=180.0) {
            tvDegreeView.setText(getString(R.string.compass_text_turn_info_left) + (360.0 - degree) + getString(R.string.compass_text_degree));
        }
            else {
            tvDegreeView.setText(getString(R.string.compass_text_turn_info_right) + degree + getString(R.string.compass_text_degree));
        }

    }
}
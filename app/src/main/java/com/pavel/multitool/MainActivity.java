package com.pavel.multitool;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pavel.multitool.flashLightSupplement.FlashClass;

public class MainActivity extends AppCompatActivity {

    private FlashClass flashClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void noteLayout(View view) {
        Intent intent = new Intent(this, GetNoteActivity.class);
        startActivity(intent);

    }

    public void compassLayout(View view) {
        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }

    public void flashLight(View view) {
        if (flashClass.isFlashStatus()){
            flashClass.flashOff();
        }   else {
            flashClass.flashOn();
        }
    }
    private void init(){
        flashClass = new FlashClass(this);
    }

    public void getTracker(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}
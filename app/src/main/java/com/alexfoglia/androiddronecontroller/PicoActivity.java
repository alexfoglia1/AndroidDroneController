package com.alexfoglia.androiddronecontroller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public abstract class PicoActivity extends AppCompatActivity implements PicoClient {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PicoComm.instance().attachClient(this);

        findViewById(android.R.id.content).setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {

                Intent myIntent = new Intent(PicoActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
            public void onSwipeRight() {
                Intent myIntent = new Intent(PicoActivity.this, PidActivity.class);
                startActivity(myIntent);
            }
            public void onSwipeLeft() {
                Intent myIntent = new Intent(PicoActivity.this, MotorsActivity.class);
                startActivity(myIntent);
            }
            public void onSwipeBottom() {
                Intent myIntent = new Intent(PicoActivity.this, MainActivity.class);
                startActivity(myIntent);
            }

        });
    }

}

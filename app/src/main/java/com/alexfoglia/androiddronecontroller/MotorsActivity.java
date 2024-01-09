package com.alexfoglia.androiddronecontroller;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MotorsActivity extends PicoActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motors);
    }


    public void requestThrottleParams(View v) {
        if (!PicoComm.instance().getThrottleParams()) {
            Toast.makeText(getBaseContext(),"CANNOT SEND",
                    Toast.LENGTH_SHORT).show();
        };
    }

    public void requestMotorParams(View v) {
        if (!PicoComm.instance().getMotorParams()) {
            Toast.makeText(getBaseContext(),"CANNOT SEND",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onThrottleParams(short descend, short hovering, short climb) {
        EditText lineDescend = (EditText) findViewById(R.id.editDescend);
        EditText lineHovering = (EditText) findViewById(R.id.editHovering);
        EditText lineClimb = (EditText) findViewById(R.id.editClimb);

        runOnUiThread(()->{
            lineDescend.setText(String.format("%d", descend));
            lineHovering.setText(String.format("%d", hovering));
            lineClimb.setText(String.format("%d", climb));
        });

    }

    @Override
    public void onMotorParams(int motorNo, int minSignal, int maxSignal) {
        EditText[] minEditTexts = new EditText[]{findViewById(R.id.editMinM1),
                findViewById(R.id.editMinM2),
                findViewById(R.id.editMinM3),
                findViewById(R.id.editMinM4)};

        EditText[] maxEditTexts = new EditText[]{findViewById(R.id.editMaxM1),
                findViewById(R.id.editMaxM2),
                findViewById(R.id.editMaxM3),
                findViewById(R.id.editMaxM4)};
        if (motorNo < 0 || motorNo > 4) {
            return;
        }
        runOnUiThread(()->{
            minEditTexts[motorNo - 1].setText(String.format("%d", minSignal));
            maxEditTexts[motorNo - 1].setText(String.format("%d", maxSignal));
        });
    }
    @Override
    public void onWrongChecksum(int txStatus) {

    }

    @Override
    public void onSwVersion(byte major, byte minor, byte stage, byte rel_type) {

    }

    @Override
    public void onRollPid(float kp, float ki, float kt, float sat, float ad, float bd) {

    }

    @Override
    public void onPitchPid(float kp, float ki, float kt, float sat, float ad, float bd) {

    }

    @Override
    public void onYawPid(float kp, float ki, float kt, float sat, float ad, float bd) {

    }
}

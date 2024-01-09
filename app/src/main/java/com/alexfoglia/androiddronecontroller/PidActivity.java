package com.alexfoglia.androiddronecontroller;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PidActivity extends PicoActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pid);
    }

    public void requestPidParams(View v) {
        if (!PicoComm.instance().getPidParams()) {
            Toast.makeText(getBaseContext(),"CANNOT SEND",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void writeRollParams(View v) {
        EditText[] editTexts = new EditText[]{  findViewById(R.id.editRollKP),
                findViewById(R.id.editRollKI),
                findViewById(R.id.editRollKT),
                findViewById(R.id.editRollSAT),
                findViewById(R.id.editRollAD),
                findViewById(R.id.editRollBD)};

        if (editTexts[0].getText().length() == 0) {
            editTexts[0].setText("0.0");
        }
        if (editTexts[1].getText().length() == 0) {
            editTexts[1].setText("0.0");
        }
        if (editTexts[2].getText().length() == 0) {
            editTexts[2].setText("0.0");
        }
        if (editTexts[3].getText().length() == 0) {
            editTexts[3].setText("0.0");
        }
        if (editTexts[4].getText().length() == 0) {
            editTexts[4].setText("0.0");
        }
        if (editTexts[5].getText().length() == 0) {
            editTexts[5].setText("0.0");
        }

        if (!PicoComm.instance().writeRollParams(Float.parseFloat(editTexts[0].getText().toString()),
                Float.parseFloat(editTexts[1].getText().toString()),
                Float.parseFloat(editTexts[2].getText().toString()),
                Float.parseFloat(editTexts[3].getText().toString()),
                Float.parseFloat(editTexts[4].getText().toString()),
                Float.parseFloat(editTexts[5].getText().toString()))) {
            Toast.makeText(getBaseContext(),"CANNOT SEND",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void writePitchParams(View v) {
        EditText[] editTexts = new EditText[]{  findViewById(R.id.editPitchKP),
                findViewById(R.id.editPitchKI),
                findViewById(R.id.editPitchKT),
                findViewById(R.id.editPitchSAT),
                findViewById(R.id.editPitchAD),
                findViewById(R.id.editPitchBD)};

        if (editTexts[0].getText().length() == 0) {
            editTexts[0].setText("0.0");
        }
        if (editTexts[1].getText().length() == 0) {
            editTexts[1].setText("0.0");
        }
        if (editTexts[2].getText().length() == 0) {
            editTexts[2].setText("0.0");
        }
        if (editTexts[3].getText().length() == 0) {
            editTexts[3].setText("0.0");
        }
        if (editTexts[4].getText().length() == 0) {
            editTexts[4].setText("0.0");
        }
        if (editTexts[5].getText().length() == 0) {
            editTexts[5].setText("0.0");
        }

        if (!PicoComm.instance().writePitchParams(Float.parseFloat(editTexts[0].getText().toString()),
                Float.parseFloat(editTexts[1].getText().toString()),
                Float.parseFloat(editTexts[2].getText().toString()),
                Float.parseFloat(editTexts[3].getText().toString()),
                Float.parseFloat(editTexts[4].getText().toString()),
                Float.parseFloat(editTexts[5].getText().toString()))) {
            Toast.makeText(getBaseContext(),"CANNOT SEND",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void writeYawParams(View v) {
        EditText[] editTexts = new EditText[]{  findViewById(R.id.editYawKP),
                findViewById(R.id.editYawKI),
                findViewById(R.id.editYawKT),
                findViewById(R.id.editYawSAT),
                findViewById(R.id.editYawAD),
                findViewById(R.id.editYawBD)};

        if (editTexts[0].getText().length() == 0) {
            editTexts[0].setText("0.0");
        }
        if (editTexts[1].getText().length() == 0) {
            editTexts[1].setText("0.0");
        }
        if (editTexts[2].getText().length() == 0) {
            editTexts[2].setText("0.0");
        }
        if (editTexts[3].getText().length() == 0) {
            editTexts[3].setText("0.0");
        }
        if (editTexts[4].getText().length() == 0) {
            editTexts[4].setText("0.0");
        }
        if (editTexts[5].getText().length() == 0) {
            editTexts[5].setText("0.0");
        }

        if (!PicoComm.instance().writeYawParams(Float.parseFloat(editTexts[0].getText().toString()),
                Float.parseFloat(editTexts[1].getText().toString()),
                Float.parseFloat(editTexts[2].getText().toString()),
                Float.parseFloat(editTexts[3].getText().toString()),
                Float.parseFloat(editTexts[4].getText().toString()),
                Float.parseFloat(editTexts[5].getText().toString()))) {
            Toast.makeText(getBaseContext(),"CANNOT SEND",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWrongChecksum(int txStatus) {

    }

    @Override
    public void onSwVersion(byte major, byte minor, byte stage, byte rel_type) {

    }

    @Override
    public void onThrottleParams(short descend, short hovering, short climb) {

    }

    @Override
    public void onMotorParams(int motorNo, int minSignal, int maxSignal) {

    }

    private void updatePidEditTexts(float kp, float ki, float kt, float sat, float ad, float bd, EditText[] editTexts) {
        editTexts[0].setText(String.format("%.1f", kp).replace(',','.'));
        editTexts[1].setText(String.format("%.1f", ki).replace(',','.'));
        editTexts[2].setText(String.format("%.1f", kt).replace(',','.'));
        editTexts[3].setText(String.format("%.1f", sat).replace(',','.'));
        editTexts[4].setText(String.format("%.1f", ad).replace(',','.'));
        editTexts[5].setText(String.format("%.1f", bd).replace(',','.'));
    }


    @Override
    public void onRollPid(float kp, float ki, float kt, float sat, float ad, float bd) {
        EditText[] editTexts = new EditText[]{  findViewById(R.id.editRollKP),
                findViewById(R.id.editRollKI),
                findViewById(R.id.editRollKT),
                findViewById(R.id.editRollSAT),
                findViewById(R.id.editRollAD),
                findViewById(R.id.editRollBD)};

        runOnUiThread(()->{updatePidEditTexts(kp, ki, kt, sat, ad, bd, editTexts);});
    }


    @Override
    public void onPitchPid(float kp, float ki, float kt, float sat, float ad, float bd) {
        EditText[] editTexts = new EditText[]{  findViewById(R.id.editPitchKP),
                findViewById(R.id.editPitchKI),
                findViewById(R.id.editPitchKT),
                findViewById(R.id.editPitchSAT),
                findViewById(R.id.editPitchAD),
                findViewById(R.id.editPitchBD)};

        runOnUiThread(()->{updatePidEditTexts(kp, ki, kt, sat, ad, bd, editTexts);});
    }

    @Override
    public void onYawPid(float kp, float ki, float kt, float sat, float ad, float bd) {
        EditText[] editTexts = new EditText[]{  findViewById(R.id.editYawKP),
                findViewById(R.id.editYawKI),
                findViewById(R.id.editYawKT),
                findViewById(R.id.editYawSAT),
                findViewById(R.id.editYawAD),
                findViewById(R.id.editYawBD)};

        runOnUiThread(()->{updatePidEditTexts(kp, ki, kt, sat, ad, bd, editTexts);});
    }
}

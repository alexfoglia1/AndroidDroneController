package com.alexfoglia.androiddronecontroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Output;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements PicoClient {
    public static final String HC05_MAC_ADDR = "58:56:00:00:8C:2E";
    private PicoComm picoComm;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        socket = null;
        picoComm = null;

        initBluetooth();
    }

    public void connect(View v) {
        Button btnConnect = findViewById(R.id.btnConnect);
        TextView txtStatus = (TextView) findViewById(R.id.txtConnStatus);

        if (btnConnect.getText().toString().toLowerCase().equals("connect")) {
            if (!connectBluetooth()) {
                return;
            }

            txtStatus.setText("Connected");

            picoComm = new PicoComm();
            picoComm.setInputStream(inputStream);
            picoComm.setOutputStream(outputStream);
            picoComm.attachClient(this);
            picoComm.start();
            picoComm.getSwVersion();
            btnConnect.setText("Disconnect");
        }
        else {
            picoComm.stop();
            if (socket != null) {
                try {
                    socket.close();
                    btnConnect.setText("Connect");
                    txtStatus.setText("Disconnected");
                } catch (IOException e) {
                    Toast.makeText(getBaseContext(),"ERROR: CLOSE SOCKET",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    public void requestThrottleParams(View v) {
        picoComm.getThrottleParams();
    }

    public void requestMotorParams(View v) {
        picoComm.getMotorParams();
    }

    public void requestPidParams(View v) {
        picoComm.getPidParams();
    }

    private boolean initBluetooth() {
        boolean result = true;

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            if (!adapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                    startActivityForResult(enableBluetooth, 1);
                }
                else {
                    result = true;
                }
            }
        }
        else {
            result = false;
        }

        return result;
    }

    private boolean connectBluetooth() {
        boolean result = true;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //UUID for serial connection
        BluetoothDevice device = adapter.getRemoteDevice(HC05_MAC_ADDR); //get remote device by mac, we assume these two devices are already paired

        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                socket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
            }
            else {
                socket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
            }
        } catch (IOException e) {
            result = false;
        }

        if (socket != null) {
            try {
                socket.connect();
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                result = false;
            }
        }

        return result;
    }

    @Override
    public void onSwVersion(byte major, byte minor, byte stage, byte rel_type) {
        String sw_ver = String.format("%d.%d.%d-%c", major, minor, stage, rel_type == 0 ? 'b' : 'r');
        TextView txtSwVer = (TextView) findViewById(R.id.txtSwVer);
        runOnUiThread(()->{txtSwVer.setText(sw_ver);});
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
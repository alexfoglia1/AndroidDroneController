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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
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

public class MainActivity extends PicoActivity implements PicoClient {
    public static final String HC05_MAC_ADDR = "58:56:00:00:8C:2E";

    private PicoComm picoComm;

    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        picoComm = PicoComm.instance();
        socket = null;
        inputStream = null;
        outputStream = null;

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

            picoComm.setInputStream(inputStream);
            picoComm.setOutputStream(outputStream);
            picoComm.attachClient(this);

            picoComm.start();
            picoComm.getSwVersion();

            btnConnect.setText("Disconnect");
        }
        else {
            if (!picoComm.stop()) {
                Toast.makeText(getBaseContext(),"ERROR: STOP RX THREAD",
                        Toast.LENGTH_SHORT).show();
            }

            picoComm = PicoComm.reinstance();

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
    public void onWrongChecksum(int txStatus) {
        runOnUiThread(()->{
                Toast.makeText(getBaseContext(),"ERROR: WRONG CHECKSUM",
                Toast.LENGTH_SHORT).show();
            });
        switch (txStatus) {
            case PicoComm.TX_GET_SW_VER:
                picoComm.getSwVersion();
                break;
            case PicoComm.TX_GET_PID_PARAMS:
                picoComm.getPidParams();
                break;
            case PicoComm.TX_GET_THROTTLE_PARAMS:
                picoComm.getThrottleParams();
                break;
            case PicoComm.TX_GET_MOTOR_PARAMS:
                picoComm.getMotorParams();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSwVersion(byte major, byte minor, byte stage, byte rel_type) {
        String sw_ver = String.format("%d.%d.%d-%c", major, minor, stage, rel_type == 0 ? 'b' : 'r');
        TextView txtSwVer = (TextView) findViewById(R.id.txtSwVer);
        runOnUiThread(()->{txtSwVer.setText(sw_ver);});
    }

    @Override
    public void onThrottleParams(short descend, short hovering, short climb) {

    }

    @Override
    public void onMotorParams(int motorNo, int minSignal, int maxSignal) {

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
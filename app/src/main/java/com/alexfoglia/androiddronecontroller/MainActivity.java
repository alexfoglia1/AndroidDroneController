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

public class MainActivity extends AppCompatActivity implements PicoClient {
    public static final String HC05_MAC_ADDR = "58:56:00:00:8C:2E";
    private PicoComm picoComm;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    private Button btnTxPidRoll;
    private Button btnTxPidPitch;
    private Button btnTxPidYaw;
    private Button btnTxThrottleParams;
    private Button btnTxMotorParams;
    private Button btnTxFlashWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTxPidRoll = (Button) findViewById(R.id.btnUpdatePidRoll);
        btnTxPidPitch = (Button) findViewById(R.id.btnUpdatePidPitch);
        btnTxPidYaw = (Button) findViewById(R.id.btnUpdatePidYaw);
        btnTxThrottleParams = (Button) findViewById(R.id.btnUpdateThrottleParams);
        btnTxMotorParams = (Button) findViewById(R.id.btnUpdateMotorBounds);
        btnTxFlashWrite = (Button) findViewById(R.id.btnFlashWrite);

        socket = null;
        inputStream = null;
        outputStream = null;
        picoComm = new PicoComm();

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

            picoComm = new PicoComm();

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
        if (!picoComm.getThrottleParams()) {
            Toast.makeText(getBaseContext(),"CANNOT SEND",
                    Toast.LENGTH_SHORT).show();
        };
    }

    public void requestMotorParams(View v) {
        if (!picoComm.getMotorParams()) {
            Toast.makeText(getBaseContext(),"CANNOT SEND",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void requestPidParams(View v) {
        if (!picoComm.getPidParams()) {
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

        if (!picoComm.writeRollParams(Float.parseFloat(editTexts[0].getText().toString()),
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

        if (!picoComm.writePitchParams(Float.parseFloat(editTexts[0].getText().toString()),
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

        if (!picoComm.writeYawParams(Float.parseFloat(editTexts[0].getText().toString()),
                Float.parseFloat(editTexts[1].getText().toString()),
                Float.parseFloat(editTexts[2].getText().toString()),
                Float.parseFloat(editTexts[3].getText().toString()),
                Float.parseFloat(editTexts[4].getText().toString()),
                Float.parseFloat(editTexts[5].getText().toString()))) {
            Toast.makeText(getBaseContext(),"CANNOT SEND",
                    Toast.LENGTH_SHORT).show();
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
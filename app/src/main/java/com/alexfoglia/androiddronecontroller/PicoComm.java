package com.alexfoglia.androiddronecontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.UUID;

public class PicoComm {
    private static final int[] GET_SW_VERSION = new int[] {
            0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x40, 0x00, 0x00, 0xE8, 0x03, 0xDC, 0x05, 0xDC, 0x05, 0xE8, 0x03, 0x40
    };

    private static final int[] GET_THROTTLE_PARAMS = new int[] {
            0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0xE8, 0x03, 0xDC, 0x05, 0xDC, 0x05, 0xE8, 0x03, 0x01
    };

    private static final int[] GET_MOTOR_PARAMS = new int[] {
            0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0xE8, 0x03, 0xDC, 0x05, 0xDC, 0x05, 0xE8, 0x03, 0x01
    };

    private static final int[] GET_PID_PARAMS = new int[] {
            0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0xE8, 0x03, 0xDC, 0x05, 0xDC, 0x05, 0xE8, 0x03, 0x04
    };

    private static final int[] WRITE_ROLL_PARAMS = new int[] {
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1E
    };

    private static final int[] WRITE_PITCH_PARAMS = new int[] {
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x20
    };

    private static final int[] WRITE_YAW_PARAMS = new int[] {
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x22
    };

    private static final int WAIT_SYNC = 0;
    private static final int WAIT_HEADER = 1;
    private static final int WAIT_PAYLOAD = 2;
    private static final int WAIT_CHECKSUM = 3;

    public static final int TX_NONE = -1;
    public static final int TX_GET_SW_VER = 0;
    public static final int TX_GET_PID_PARAMS = 1;
    public static final int TX_GET_THROTTLE_PARAMS = 2;
    public static final int TX_GET_MOTOR_PARAMS = 3;

    private static final int[] txStatusToRxBufLength = new int[] {
            12, 80, 14 , 56
    };
    private LinkedList<PicoClient> clients = new LinkedList<>();
    private OutputStream outputStream;
    private InputStream inputStream;

    private Thread rxThread;
    private int rxStatus;
    private int txStatus;
    private byte[] rxBuf;
    private int llRxBuf;
    private boolean stopped;
    public PicoComm() {
        outputStream = null;
        inputStream = null;
        rxThread = null;
        stopped = false;
        txStatus = TX_NONE;
        rxStatus = WAIT_SYNC;
        rxBuf = new byte[1024];
        llRxBuf = 0;
    }

    public void attachClient(PicoClient e) {
        clients.add(e);
    }

    public boolean getSwVersion() {
        byte[] txMessage = castBytes(GET_SW_VERSION);
        txStatus = TX_GET_SW_VER;
        try {
            outputStream.write(txMessage);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean getThrottleParams() {
        byte[] txMessage = castBytes(GET_THROTTLE_PARAMS);
        txStatus = TX_GET_THROTTLE_PARAMS;
        try {
            outputStream.write(txMessage);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean getMotorParams() {
        byte[] txMessage = castBytes(GET_MOTOR_PARAMS);
        txStatus = TX_GET_MOTOR_PARAMS;
        try {
            outputStream.write(txMessage);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean getPidParams() {
        byte[] txMessage = castBytes(GET_PID_PARAMS);
        txStatus = TX_GET_PID_PARAMS;
        try {
            outputStream.write(txMessage);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean writeRollParams(float kp, float ki, float kt, float sat, float ad, float bd) {
        byte[] txHeader = castBytes(WRITE_ROLL_PARAMS);
        final int payloadSize = 24;
        final int remoteControlTagSize = 9;
        final int checksumSize = 1;
        ByteBuffer txMessage = ByteBuffer.allocate(txHeader.length + payloadSize + remoteControlTagSize + checksumSize);
        txMessage.order(ByteOrder.LITTLE_ENDIAN);
        txMessage.put(txHeader);
        txMessage.putFloat(kp);
        txMessage.putFloat(ki);
        txMessage.putFloat(kt);
        txMessage.putFloat(sat);
        txMessage.putFloat(ad);
        txMessage.putFloat(bd);
        txMessage.put((byte) 0);
        txMessage.putShort((short)1000);
        txMessage.putShort((short)1500);
        txMessage.putShort((short)1500);
        txMessage.putShort((short)1000);

        txMessage.put(calculateChecksum(txMessage.array()));
        byte[] txMessageBytes = txMessage.array();
        try {
            outputStream.write(-1);
            outputStream.write(txMessageBytes);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean writePitchParams(float kp, float ki, float kt, float sat, float ad, float bd) {
        byte[] txHeader = castBytes(WRITE_PITCH_PARAMS);
        final int payloadSize = 24;
        final int remoteControlTagSize = 9;
        final int checksumSize = 1;
        ByteBuffer txMessage = ByteBuffer.allocate(txHeader.length + payloadSize + remoteControlTagSize + checksumSize);
        txMessage.order(ByteOrder.LITTLE_ENDIAN);
        txMessage.put(txHeader);
        txMessage.putFloat(kp);
        txMessage.putFloat(ki);
        txMessage.putFloat(kt);
        txMessage.putFloat(sat);
        txMessage.putFloat(ad);
        txMessage.putFloat(bd);
        txMessage.put((byte) 0);
        txMessage.putShort((short)1000);
        txMessage.putShort((short)1500);
        txMessage.putShort((short)1500);
        txMessage.putShort((short)1000);

        txMessage.put(calculateChecksum(txMessage.array()));
        byte[] txMessageBytes = txMessage.array();
        try {
            outputStream.write(-1);
            outputStream.write(txMessageBytes);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean writeYawParams(float kp, float ki, float kt, float sat, float ad, float bd) {
        byte[] txHeader = castBytes(WRITE_YAW_PARAMS);
        final int payloadSize = 24;
        final int remoteControlTagSize = 9;
        final int checksumSize = 1;
        ByteBuffer txMessage = ByteBuffer.allocate(txHeader.length + payloadSize + remoteControlTagSize + checksumSize);
        txMessage.order(ByteOrder.LITTLE_ENDIAN);
        txMessage.put(txHeader);
        txMessage.putFloat(kp);
        txMessage.putFloat(ki);
        txMessage.putFloat(kt);
        txMessage.putFloat(sat);
        txMessage.putFloat(ad);
        txMessage.putFloat(bd);
        txMessage.put((byte) 0);
        txMessage.putShort((short)1000);
        txMessage.putShort((short)1500);
        txMessage.putShort((short)1500);
        txMessage.putShort((short)1000);

        txMessage.put(calculateChecksum(txMessage.array()));
        byte[] txMessageBytes = txMessage.array();
        try {
            outputStream.write(-1);
            outputStream.write(txMessageBytes);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void start() {
        if (rxThread == null)
        {
            rxThread = new Thread(()->{rxLoop();});
            rxThread.start();
        }
    }

    public boolean stop() {
        stopped = true;
        try {
            inputStream.close();
            outputStream.close();
            rxThread.join(100);
        } catch (IOException | InterruptedException e) {
            return false;
        }

        return true;
    }

    public void setInputStream(InputStream is) {
        this.inputStream = is;
    }

    public void setOutputStream(OutputStream os) {
        this.outputStream = os;
    }


    private byte calculateChecksum(byte[] buf) {
        byte cks = 0;
        for (int i = 0; i < buf.length; i++) {
            cks ^= buf[i];
        }

        return cks;
    }


    private void dataIngest(byte checksum) {
        //if (checksum != calculateChecksum(rxBuf)) {
        //    for (PicoClient cl : clients) {
        //        cl.onWrongChecksum(txStatus);
        //    }
        //}
        //else {
            for (PicoClient cl : clients) {
                switch (txStatus) {
                    case TX_GET_SW_VER:
                        cl.onSwVersion(rxBuf[8], rxBuf[9], rxBuf[10], rxBuf[11]);
                        break;
                    case TX_GET_THROTTLE_PARAMS:
                        byte[] descend = new byte[]{rxBuf[9], rxBuf[8]};
                        byte[] hovering = new byte[]{rxBuf[11], rxBuf[10]};
                        byte[] climb = new byte[]{rxBuf[13], rxBuf[12]};
                        cl.onThrottleParams(ByteBuffer.wrap(descend).getShort(0),
                                ByteBuffer.wrap(hovering).getShort(0),
                                ByteBuffer.wrap(climb).getShort(0));
                        break;
                    case TX_GET_MOTOR_PARAMS:
                        byte[] m1Min = new byte[]{rxBuf[15], rxBuf[14], rxBuf[13], rxBuf[12]};
                        byte[] m1Max = new byte[]{rxBuf[19], rxBuf[18], rxBuf[17], rxBuf[16]};

                        byte[] m2Min = new byte[]{rxBuf[27], rxBuf[26], rxBuf[25], rxBuf[24]};
                        byte[] m2Max = new byte[]{rxBuf[31], rxBuf[30], rxBuf[29], rxBuf[28]};

                        byte[] m3Min = new byte[]{rxBuf[39], rxBuf[38], rxBuf[37], rxBuf[36]};
                        byte[] m3Max = new byte[]{rxBuf[43], rxBuf[42], rxBuf[41], rxBuf[40]};

                        byte[] m4Min = new byte[]{rxBuf[51], rxBuf[50], rxBuf[49], rxBuf[48]};
                        byte[] m4Max = new byte[]{rxBuf[55], rxBuf[54], rxBuf[53], rxBuf[52]};

                        cl.onMotorParams(1, ByteBuffer.wrap(m1Min).getInt(0), ByteBuffer.wrap(m1Max).getInt(0));
                        cl.onMotorParams(2, ByteBuffer.wrap(m2Min).getInt(0), ByteBuffer.wrap(m2Max).getInt(0));
                        cl.onMotorParams(3, ByteBuffer.wrap(m3Min).getInt(0), ByteBuffer.wrap(m3Max).getInt(0));
                        cl.onMotorParams(4, ByteBuffer.wrap(m4Min).getInt(0), ByteBuffer.wrap(m4Max).getInt(0));
                        break;
                    case TX_GET_PID_PARAMS:
                        byte[] roll_kp = new byte[]{rxBuf[11], rxBuf[10], rxBuf[9], rxBuf[8]};
                        byte[] roll_ki = new byte[]{rxBuf[15], rxBuf[14], rxBuf[13], rxBuf[12]};
                        byte[] roll_kt = new byte[]{rxBuf[19], rxBuf[18], rxBuf[17], rxBuf[16]};
                        byte[] roll_sat = new byte[]{rxBuf[23], rxBuf[22], rxBuf[21], rxBuf[20]};
                        byte[] roll_ad = new byte[]{rxBuf[27], rxBuf[26], rxBuf[25], rxBuf[24]};
                        byte[] roll_bd = new byte[]{rxBuf[31], rxBuf[30], rxBuf[29], rxBuf[28]};

                        byte[] pitch_kp = new byte[]{rxBuf[35], rxBuf[34], rxBuf[33], rxBuf[32]};
                        byte[] pitch_ki = new byte[]{rxBuf[39], rxBuf[38], rxBuf[37], rxBuf[36]};
                        byte[] pitch_kt = new byte[]{rxBuf[43], rxBuf[42], rxBuf[41], rxBuf[40]};
                        byte[] pitch_sat = new byte[]{rxBuf[47], rxBuf[46], rxBuf[45], rxBuf[44]};
                        byte[] pitch_ad = new byte[]{rxBuf[51], rxBuf[50], rxBuf[49], rxBuf[48]};
                        byte[] pitch_bd = new byte[]{rxBuf[55], rxBuf[54], rxBuf[53], rxBuf[52]};

                        byte[] yaw_kp = new byte[]{rxBuf[59], rxBuf[58], rxBuf[57], rxBuf[56]};
                        byte[] yaw_ki = new byte[]{rxBuf[63], rxBuf[62], rxBuf[61], rxBuf[60]};
                        byte[] yaw_kt = new byte[]{rxBuf[67], rxBuf[66], rxBuf[65], rxBuf[64]};
                        byte[] yaw_sat = new byte[]{rxBuf[71], rxBuf[70], rxBuf[69], rxBuf[68]};
                        byte[] yaw_ad = new byte[]{rxBuf[75], rxBuf[74], rxBuf[73], rxBuf[72]};
                        byte[] yaw_bd = new byte[]{rxBuf[79], rxBuf[78], rxBuf[77], rxBuf[76]};

                        cl.onRollPid(ByteBuffer.wrap(roll_kp).getFloat(0),
                                ByteBuffer.wrap(roll_ki).getFloat(0),
                                ByteBuffer.wrap(roll_kt).getFloat(0),
                                ByteBuffer.wrap(roll_sat).getFloat(0),
                                ByteBuffer.wrap(roll_ad).getFloat(0),
                                ByteBuffer.wrap(roll_bd).getFloat(0));

                        cl.onPitchPid(ByteBuffer.wrap(pitch_kp).getFloat(0),
                                ByteBuffer.wrap(pitch_ki).getFloat(0),
                                ByteBuffer.wrap(pitch_kt).getFloat(0),
                                ByteBuffer.wrap(pitch_sat).getFloat(0),
                                ByteBuffer.wrap(pitch_ad).getFloat(0),
                                ByteBuffer.wrap(pitch_bd).getFloat(0));

                        cl.onYawPid(ByteBuffer.wrap(yaw_kp).getFloat(0),
                                ByteBuffer.wrap(yaw_ki).getFloat(0),
                                ByteBuffer.wrap(yaw_kt).getFloat(0),
                                ByteBuffer.wrap(yaw_sat).getFloat(0),
                                ByteBuffer.wrap(yaw_ad).getFloat(0),
                                ByteBuffer.wrap(yaw_bd).getFloat(0));
                        break;
                    default:
                        break;
                }
            }
        //}
    }

    private void updateFsm(byte byteIn) {
        switch (rxStatus) {
            case WAIT_SYNC:
                if (byteIn == (byte) 0xFF) {
                    rxStatus = WAIT_HEADER;
                    llRxBuf = 0;
                }
                break;
            case WAIT_HEADER:
                rxBuf[llRxBuf] = byteIn;
                llRxBuf++;
                if (llRxBuf == 8) {
                    rxStatus = WAIT_PAYLOAD;
                }
                break;
            case WAIT_PAYLOAD:
                rxBuf[llRxBuf] = byteIn;
                llRxBuf++;
                if (llRxBuf == txStatusToRxBufLength[txStatus]) {
                    rxStatus = WAIT_CHECKSUM;
                }
                break;
            case WAIT_CHECKSUM:
                dataIngest(byteIn);
                rxStatus = WAIT_SYNC;
                break;
        }
    }

    private void rxLoop() {
        while (!stopped) {
            byte[] rx = new byte[512];
            try {
                int nByteRead = inputStream.read(rx);
                for (int i = 0; i < nByteRead; i++) {
                    updateFsm(rx[i]);
                }
            } catch (IOException e) {
                stopped = true;
            }
        }
    }

    private byte[] castBytes(int[] message) {
        byte[] txMessage = new byte[message.length];
        for (int i = 0; i < txMessage.length; i++) {
            txMessage[i] = (byte) message[i];
        }

        return txMessage;
    }

}

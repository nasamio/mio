package com.hzz.serial;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.io.IOException;
import java.util.List;

/**
 * 封装服务 后台串口
 */
public class SerialService extends Service {
    private static final String TAG = "SerialService";
    private boolean permissionGranted;
    private SerialManager.SerialCallback callback;

    public void setCallback(SerialManager.SerialCallback callback) {
        this.callback = callback;
    }

    public class SerialBinder extends Binder {


        public SerialService getService() {
            return SerialService.this;
        }
    }

    private final IBinder binder;
    private boolean connected;

    /**
     * Lifecylce
     */
    public SerialService() {
        binder = new SerialBinder();
    }

    @Override
    public void onDestroy() {
        disconnect();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void disconnect() {
        connected = false;
        try {
            usbSerialPort.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SerialSizeReader ioManager;
    private UsbSerialPort usbSerialPort;

    public void connect(SerialItem item) {
        UsbSerialDriver driver = item.getDriver();
        List<UsbSerialPort> ports = driver.getPorts();
        int portNum = item.getPortNum();
        if (portNum < ports.size()) {

            try {
                usbSerialPort = ports.get(portNum);
                UsbDevice device = null;
                UsbManager usbManager = (UsbManager) getApplicationContext().getSystemService(Context.USB_SERVICE);
                UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());

                Log.e(TAG, "connect: serial port : " + usbSerialPort);
                usbSerialPort.open(usbConnection);
                usbSerialPort.setParameters(item.baudRate, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

                usbSerialPort.setDTR(true); // for arduino, ...
                usbSerialPort.setRTS(false);
                UsbEndpoint readEndpoint = usbSerialPort.getReadEndpoint();
                Log.d(TAG, "connect: " + readEndpoint.toString());

                ioManager = new SerialSizeReader(usbSerialPort, new SerialSizeReader.Listener() {
                    @Override
                    public void onNewData(byte[] data) {
                        // Log.e(TAG, "onNewData: " + new String(data) + " len : " + data.length);
                        if (callback != null) callback.onRead(data);
                    }

                    @Override
                    public void onRunError(Exception e) {
                        Log.e(TAG, "onRunError: " + e.toString());
                        if (callback != null) callback.onDisconnected(e.toString());
                    }
                });
                ioManager.setReadSize(SerialManager.getInstance().getReadSize());
                ioManager.start();

                // 走到这里 默认为连接成功
                if (callback != null) callback.onConnected();
                Log.e(TAG, "connect: success...");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "connect: port num : " + portNum + " size : " + ports.size());
        }
    }

    private static final int TIME_OUT = 2_000;

    public void write(byte[] data) {
        try {
            usbSerialPort.write(data, TIME_OUT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

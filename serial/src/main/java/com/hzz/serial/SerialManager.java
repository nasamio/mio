package com.hzz.serial;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装接口 对外暴露
 */
public class SerialManager {
    private static final String TAG = "SerialManager";
    private static SerialManager instance;
    private SerialService service;
    private SerialCallback callback;
    private int readSize = 32; // 满多少size固定回调
    private UsbManager usbManager;
    private Handler handler;

    private SerialManager() {
    }

    public static SerialManager getInstance() {
        if (instance == null) {
            instance = new SerialManager();
        }
        return instance;
    }

    private Context mContext;

    public SerialManager init(@NotNull Context context, @NotNull SerialCallback callback) {
        mContext = context;
        usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        handler = new Handler();

        mContext.bindService(new Intent(mContext, SerialService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder binder) {
                Log.d(TAG, "onServiceConnected: ");
                service = ((SerialService.SerialBinder) binder).getService();
                service.setCallback(callback);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.e(TAG, "onServiceDisconnected: " + componentName);
            }
        }, Context.BIND_AUTO_CREATE);


        this.callback = callback;
        return this;
    }

    /**
     * 扫描获取当前设备的串口
     */
    public List<SerialItem> scanPorts() {
        List<SerialItem> res = new ArrayList<>();
        UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
        UsbSerialProber usbCustomProber = CustomProber.getCustomProber();
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            UsbSerialDriver driver = usbDefaultProber.probeDevice(device);
            if (driver == null) {
                driver = usbCustomProber.probeDevice(device);
            }
            if (driver != null) {
                for (int port = 0; port < driver.getPorts().size(); port++)
                    res.add(new SerialItem(device, port, driver));
            }
        }
        return res;
    }

    /**
     * 连接串口
     * 如果没有权限 会重复请求 间隔3s
     * 如果service未初始化 会重试 间隔1s
     */
    public void connect(SerialItem item) {
        if (service != null) {
            UsbSerialDriver driver = item.getDriver();
            if (!usbManager.hasPermission(driver.getDevice())) {
                requestPermission(driver);
                handler.postDelayed(() ->
                        connect(item), 3_000);
            } else {
                service.connect(item);
            }
        } else {
            Log.e(TAG, "connect: service null...");
            handler.postDelayed(() ->
                    connect(item), 1_000);
        }
    }

    private void requestPermission(UsbSerialDriver driver) {
        Log.d(TAG, "requestPermission: ");
        int flags = /*Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE :*/ 0;
        PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(mContext,
                0, new Intent(Constants.INTENT_ACTION_GRANT_USB), flags);
        usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
    }

    public void write(byte[] data) {
        if (service != null) {
            service.write(data);
        } else {
            Log.e(TAG, "connect: service null...");
        }
    }

    public void write(String data) {
        write(data.getBytes());
    }

    public interface SerialCallback {
        void onConnected();

        void onDisconnected(String msg);

        void onRead(byte[] data);

    }

    public int getReadSize() {
        return readSize;
    }

    public void setReadSize(int readSize) {
        this.readSize = readSize;
    }
}

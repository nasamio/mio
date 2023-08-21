package com.hzz.serial;

import android.hardware.usb.UsbDevice;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

/**
 * 串口对象
 */
public class SerialItem {
    UsbDevice device;
    int portNum;
    UsbSerialDriver driver;
    int baudRate;

    public SerialItem(UsbDevice device, int portNum, UsbSerialDriver driver) {
        this.device = device;
        this.portNum = portNum;
        this.driver = driver;
        baudRate = 9600;
    }

    public UsbDevice getDevice() {
        return device;
    }

    public void setDevice(UsbDevice device) {
        this.device = device;
    }

    public int getPortNum() {
        return portNum;
    }

    public void setPortNum(int portNum) {
        this.portNum = portNum;
    }

    public UsbSerialDriver getDriver() {
        return driver;
    }

    public void setDriver(UsbSerialDriver driver) {
        this.driver = driver;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    @Override
    public String toString() {
        return "SerialItem{" +
                "device=" + device.getDeviceName() +
                ", port=" + portNum +
                ", driver=" + driver +
                '}';
    }
}

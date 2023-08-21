package com.hzz.serial;

import android.os.Process;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 循环读取数据 固定大小
 */
public class SerialSizeReader implements Runnable {

    public enum State {
        STOPPED,
        RUNNING,
        STOPPING
    }

    private static final String TAG = SerialSizeReader.class.getSimpleName();
    private static final int BUFSIZ = 4096;

    private int mReadTimeout = 0;
    private int mWriteTimeout = 0;

    private final Object mReadBufferLock = new Object();
    private final Object mWriteBufferLock = new Object();

    private ByteBuffer mReadBuffer; // default size = getReadEndpoint().getMaxPacketSize()
    private ByteBuffer mWriteBuffer = ByteBuffer.allocate(BUFSIZ);

    private int mThreadPriority = Process.THREAD_PRIORITY_URGENT_AUDIO;
    private State mState = State.STOPPED; // Synchronized by 'this'
    private Listener mListener; // Synchronized by 'this'
    private final UsbSerialPort mSerialPort;

    public interface Listener {
        void onNewData(byte[] data);

        void onRunError(Exception e);
    }

    public SerialSizeReader(UsbSerialPort serialPort, Listener listener) {
        mSerialPort = serialPort;
        mListener = listener;
        mReadBuffer = ByteBuffer.allocate(serialPort.getReadEndpoint().getMaxPacketSize());
    }

    public synchronized void setListener(Listener listener) {
        mListener = listener;
    }

    public synchronized Listener getListener() {
        return mListener;
    }

    public void setReadTimeout(int timeout) {
        // when set if already running, read already blocks and the new value will not become effective now
        if (mReadTimeout == 0 && timeout != 0 && mState != State.STOPPED)
            throw new IllegalStateException("readTimeout only configurable before SerialInputOutputManager is started");
        mReadTimeout = timeout;
    }

    public int getReadTimeout() {
        return mReadTimeout;
    }

    public void setWriteTimeout(int timeout) {
        mWriteTimeout = timeout;
    }

    public int getWriteTimeout() {
        return mWriteTimeout;
    }

    public void setReadBufferSize(int bufferSize) {
        if (getReadBufferSize() == bufferSize)
            return;
        synchronized (mReadBufferLock) {
            mReadBuffer = ByteBuffer.allocate(bufferSize);
        }
    }

    public int getReadBufferSize() {
        return mReadBuffer.capacity();
    }

    public void setWriteBufferSize(int bufferSize) {
        if (getWriteBufferSize() == bufferSize)
            return;
        synchronized (mWriteBufferLock) {
            ByteBuffer newWriteBuffer = ByteBuffer.allocate(bufferSize);
            if (mWriteBuffer.position() > 0)
                newWriteBuffer.put(mWriteBuffer.array(), 0, mWriteBuffer.position());
            mWriteBuffer = newWriteBuffer;
        }
    }

    public int getWriteBufferSize() {
        return mWriteBuffer.capacity();
    }

    /**
     * start SerialInputOutputManager in separate thread
     */
    public void start() {
        if (mState != State.STOPPED)
            throw new IllegalStateException("already started");
        new Thread(this, this.getClass().getSimpleName()).start();
    }

    public synchronized void stop() {
        if (getState() == State.RUNNING) {
            Log.i(TAG, "Stop requested");
            mState = State.STOPPING;
        }
    }

    public synchronized State getState() {
        return mState;
    }

    @Override
    public void run() {
        synchronized (this) {
            if (getState() != State.STOPPED) {
                throw new IllegalStateException("Already running");
            }
            mState = State.RUNNING;
        }
        Log.i(TAG, "Running ...");
        try {
            if (mThreadPriority != Process.THREAD_PRIORITY_DEFAULT)
                Process.setThreadPriority(mThreadPriority);
            while (true) {
                if (getState() != State.RUNNING) {
                    Log.i(TAG, "Stopping mState=" + getState());
                    break;
                }
                step();
            }
        } catch (Exception e) {
            Log.w(TAG, "Run ending due to exception: " + e.getMessage(), e);
            final Listener listener = getListener();
            if (listener != null) {
                listener.onRunError(e);
            }
        } finally {
            synchronized (this) {
                mState = State.STOPPED;
                Log.i(TAG, "Stopped");
            }
        }
    }

    private int readSize = 16; // read的数据有这么大才会回调

    private byte[] accumulatedData; // 存储累积数据的缓冲区
    private int accumulatedDataLength = 0; // 缓冲区中已累积的数据长度

    private void step() throws IOException {

        // Handle incoming data.
        byte[] buffer;
        synchronized (mReadBufferLock) {
            buffer = mReadBuffer.array();
            int len = mSerialPort.read(buffer, mReadTimeout);
            if (len > 0) {
                // Log.d(TAG, "Read data len=" + len);
                final Listener listener = getListener();
                if (listener != null) {
                    handleRead(listener, buffer, len);
                }
            }
        }
    }

    private void handleRead(Listener listener, byte[] buffer, int len) {
        // Log.e(TAG, "handleRead: buffer : " + new String(buffer) + " buffer len : " + buffer.length + ",len : " + len);
        if (accumulatedData == null) {
            accumulatedData = new byte[readSize];
        }
        if (accumulatedDataLength + len >= readSize) {
            // 如果累积数据长度达到 1024 或以上，进行回调并清空累积数据
            System.arraycopy(buffer, 0, accumulatedData, accumulatedDataLength, readSize - accumulatedDataLength);
            listener.onNewData(accumulatedData);
            int delta = accumulatedDataLength + len - readSize;
            if (delta > 0) {
                // 说明这一次读的数据多了 把多的数据存到accumulatedData的开头
                System.arraycopy(buffer, readSize - accumulatedDataLength, accumulatedData, 0, delta);
            }
            accumulatedDataLength = delta;
        } else {
            // 将数据存储到累积数据缓冲区中
            System.arraycopy(buffer, 0, accumulatedData, accumulatedDataLength, len);
            accumulatedDataLength += len;
        }
    }

    public int getReadSize() {
        return readSize;
    }

    public void setReadSize(int readSize) {
        this.readSize = readSize;
    }
}

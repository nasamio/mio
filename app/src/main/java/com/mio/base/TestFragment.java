package com.mio.base;

import android.annotation.SuppressLint;
import android.util.Log;

import com.hzz.serial.SerialItem;
import com.hzz.serial.SerialManager;
import com.mio.base.databinding.FragmentTestBinding;
import com.mio.basic.BaseFragment;

import java.util.List;

public class TestFragment extends BaseFragment<FragmentTestBinding> {
    private static final String TAG = "TestFragment";

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        mDataBinding.tvContent.setText("我是测试fragment1的内容...");

        SerialManager manager = SerialManager.getInstance().init(mContext, new SerialManager.SerialCallback() {
            @Override
            public void onConnected() {
                Log.d(TAG, "onConnected: ");
                mDataBinding.getRoot().postDelayed(() ->
                        SerialManager.getInstance().write("hello,I'm mio."), 1_000);
            }

            @Override
            public void onDisconnected(String msg) {
                Log.d(TAG, "onDisconnected: ");
            }

            @Override
            public void onRead(byte[] data) {
                Log.d(TAG, "onRead: " + new String(data));
            }
        });

        List<SerialItem> serialItems = manager.scanPorts();
        Log.d(TAG, "initView: serial size : " + serialItems.size());
        if (serialItems != null && serialItems.size() > 0) {
            mDataBinding.getRoot().postDelayed(new Runnable() {
                @Override
                public void run() {
                    manager.connect(serialItems.get(0));
                }
            }, 10);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test;
    }
}

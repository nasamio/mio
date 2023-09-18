package com.mio.base;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.util.Log;

import com.hzz.serial.SerialItem;
import com.hzz.serial.SerialManager;
import com.mio.base.databinding.FragmentTestBinding;
import com.mio.basic.BaseFragment;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class TestFragment extends BaseFragment<FragmentTestBinding> {
    private static final String TAG = "TestFragment";

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        // mDataBinding.tvContent.setText("我是测试fragment1的内容...");

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

        initIot();
//        testIot();
    }

    private void testIot() {
        String serverUrl = "ssl://host=xfc0b7fa.ala.cn-hangzhou.emqxsl.cn:8084";
        MqttConnectOptions options = new MqttConnectOptions();
        AssetManager assetManager = mContext.getAssets();
        try {
            options.setSocketFactory(SSLUtils.getSocketFactory(assetManager.open("ca.crt"), null, null, "aaa"));
            mqttAndroidClient = new MqttAndroidClient(mContext, serverUrl, "clientId");

            mqttAndroidClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.i(TAG, "连接断开");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.i(TAG, "收到消息:" + message.toString());

                    //建议使用队列接收
                    MyMessage myMessage = new MyMessage();
                    myMessage.setData(message.getPayload());
//                    boolean offer = SERVER_QUEUE.offer(aMessage);
//                    if (!offer) {
//                        Log.e(TAG, "队列已满，无法接受消息!");
//                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.i(TAG, "deliveryComplete: " + token.toString());

                }
            });

            //建立连接规则
            options.setUserName(mqttUsername);
            options.setPassword(mqttPassword.toCharArray());
            options.setCleanSession(true);
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1); //MQTT版本
            options.setConnectionTimeout(10); //连接超时时间
            options.setKeepAliveInterval(180); //心跳间隔时间
            options.setMaxInflight(100); //最大请求数，默认10，高流量场景可以增大该值
            options.setAutomaticReconnect(true); //设置自动重新连接

            mqttAndroidClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "连接成功");
                    //这里订阅消息
                    subscribe();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "连接失败" + exception);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test;
    }

    private static MqttAndroidClient mqttAndroidClient;
    private static String mqttUsername = "aaa"; //服务端创建的用户名
    private static String mqttPassword = "aaa"; //服务端吧创建的用户名密码
    private static String clientId = "123"; //唯一标识不可重复
    //接受消息的队列
    public static final LinkedBlockingQueue<MyMessage> SERVER_QUEUE = new LinkedBlockingQueue<>(
            200);

    //消息订阅的topic,可以自定义
    private static final String topic = "/" + mqttUsername + "/" + clientId + "/user/get";


    public void initIot() {

        String serverUrl = "ssl://host=xfc0b7fa.ala.cn-hangzhou.emqxsl.cn:8084";

        try {
            mqttAndroidClient = new MqttAndroidClient(mContext, serverUrl, "clientId");

            mqttAndroidClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.i(TAG, "连接断开");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.i(TAG, "收到消息:" + message.toString());

                    //建议使用队列接收
                    MyMessage myMessage = new MyMessage();
                    myMessage.setData(message.getPayload());
//                    boolean offer = SERVER_QUEUE.offer(aMessage);
//                    if (!offer) {
//                        Log.e(TAG, "队列已满，无法接受消息!");
//                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.i(TAG, "deliveryComplete: " + token.toString());

                }
            });

            //建立连接规则
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(mqttUsername);
            options.setPassword(mqttPassword.toCharArray());
            options.setCleanSession(true);
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1); //MQTT版本
            options.setConnectionTimeout(10); //连接超时时间
            options.setKeepAliveInterval(180); //心跳间隔时间
            options.setMaxInflight(100); //最大请求数，默认10，高流量场景可以增大该值
            options.setAutomaticReconnect(true); //设置自动重新连接
            options.setSocketFactory(SSLUtils.getSingleSocketFactory(mContext.getResources().getAssets().open("ca.crt")));

            mqttAndroidClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "连接成功");
                    //这里订阅消息
                    subscribe();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "连接失败" + exception);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "INIT IOT ERROR!" + e.toString());
        }
    }

    private static void subscribe() {
        try {
            mqttAndroidClient.subscribe(topic, 1, null,
                    new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Log.i(TAG,
                                    "订阅成功 topic: "
                                            + topic);
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken,
                                              Throwable exception) {
                            Log.e(TAG, "订阅失败!" + exception.getMessage());
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "订阅失败!" + e.getMessage());
        }
    }


    public class MyMessage {

        public Object data;

        public MyMessage() {
        }

        public MyMessage(Object data) {
            this.data = data;
        }

        public Object getData() {
            return this.data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

}

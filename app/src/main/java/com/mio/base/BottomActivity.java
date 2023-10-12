package com.mio.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.InputConfirmPopupView;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.mio.base.bean.User;
import com.mio.base.manager.RetrofitServiceManager;
import com.mio.basic.BaseBottomActivity;
//import com.yanzhenjie.andserver.AndServer;
//import com.yanzhenjie.andserver.Server;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import rx.Observer;
import rx.observers.TestObserver;

public class BottomActivity extends BaseBottomActivity {
    private static final String TAG = "BottomActivity";
//    private Server server;

    @Override
    protected void initFragmentList() {
        addFragment(R.id.item1, new TestFragment());
        addFragment(R.id.item2, new Test2Fragment());
        addFragment(R.id.item3, new Test3Fragment());
        addFragment(R.id.item4, new Test4Fragment());


//        mDataBinding.vp
//                .setPageTransformer(true, new CubeOutTransformer());

//        checkPer();

        // testLocal();
        // testWeb();
        // 观察者模式
        testObserver();
        // 责任链模式
        testHandler();
        // 单例模式
        testSingleTon();
        // 工厂模式
        testFactory();
        // 策略模式
        testStrategy();
        // 代理模式
        testProxy();
        // 外观模式
        testWaiguan();

    }

    class TObservable extends Observable {

        public void setChanged2() {
            setChanged();
        }


    }

    private void testObserver() {
        Log.d(TAG, "testObserver: ");
        // 被观察者
        TObservable observable = new TObservable();
        // 观察者1
        java.util.Observer observer1 = (o, arg) ->
                Log.d(TAG, "观察者1接到消息: " + arg.toString());    // 观察者2
        java.util.Observer observer2 = (o, arg) ->
                Log.d(TAG, "观察者2接到消息: " + arg.toString());

        // 被观察者注册观察
        observable.addObserver(observer1);
        observable.addObserver(observer2);

        // 被观察者发生改变
        mDataBinding.getRoot().postDelayed(() -> {
            String str = "我是消息...";
            Log.d(TAG, "被观察者变化,发送消息:" + str);
            observable.setChanged2();
            observable.notifyObservers(str);
        }, 1_000);
    }

    public abstract class TestHandler {

        public TestHandler(TestHandler next) {
            this.next = next;
        }

        TestHandler next; // 表示链式的下一个 不需要知道上一个

        abstract void handle(int num); // 处理任务


    }

    private void testHandler() {

        TestHandler handler3 = new TestHandler(null) {
            @Override
            void handle(int num) {
                Log.d(TAG, "handle3: " + num);
                if (next != null) next.handle(num);
            }
        };
        TestHandler handler2 = new TestHandler(handler3) {
            @Override
            void handle(int num) {
                Log.d(TAG, "handle2: " + num);
                if (next != null) next.handle(num);
            }
        };
        TestHandler handler1 = new TestHandler(handler2) {
            @Override
            void handle(int num) {
                Log.d(TAG, "handle1: " + num);
                if (next != null) next.handle(num);// 可以在这里加是否处理的逻辑 没处理的话就给下一级
            }
        };

        mDataBinding.getRoot().postDelayed(() ->
                handler1.handle(3), 1_000);
    }


    private void testSingleTon() {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setIcon(0)
                .create();


    }


    private void testFactory() {

    }

    private void testStrategy() {
        Animal person = new Animal(new PersonAction());
        person.hello();

        Animal dog = new Animal(new DogAction());
        dog.hello();
    }

    private void testProxy() {
        SourceObj sourceObj = new SourceObj();
        // 静态代理
        ProxyObj proxyObj = new ProxyObj(sourceObj);
        proxyObj.methodA();
        // 动态代理 使用InvocationHandler
        IObj proxyInstance = (IObj) Proxy.newProxyInstance(
                IObj.class.getClassLoader(), // 基类的类加载器
                new Class[]{IObj.class}, // 目标接口
                new DProxyObj(sourceObj));// 替换代理
        proxyInstance.methodA();
    }

    private void testWaiguan() {
        Iphone iphone = new Iphone(new Music(), new Camera());
        iphone.playMusic();
        iphone.takePhoto();
    }

    private void checkPer() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getPackageName(),
                Context.MODE_PRIVATE);
        boolean isActive = sharedPreferences.getBoolean("isActive", false);
        if (!isActive) {
            InputConfirmPopupView inputConfirmPopupView = new XPopup.Builder(mContext)
                    .asInputConfirm("请输入激活码", "你的设备需要激活", null, "激活码", new OnInputConfirmListener() {
                        @Override
                        public void onConfirm(String text) {

                        }
                    });
            inputConfirmPopupView.popupInfo.isDismissOnTouchOutside = false;
            inputConfirmPopupView.show();
        }

    }


    private SharedPreferences.Editor getEditor() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getPackageName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        return edit;
    }

    @Override
    protected int getMenuRes() {
        return R.menu.menu_bottom_navigation;
    }

    private void testWeb() {
//        server = AndServer.webServer(this)
//                .port(8080)
//                .timeout(1, TimeUnit.SECONDS)
//                .build();
//        server.startup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        server.shutdown();
    }


    private void testLocal() {
        RetrofitServiceManager.getInstance().getAllUser(new Observer<List<User>>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.toString());
            }

            @Override
            public void onNext(List<User> users) {
                for (User user : users) {
                    Log.e(TAG, "onNext: " + user.toString());
                }
            }
        });
    }

}
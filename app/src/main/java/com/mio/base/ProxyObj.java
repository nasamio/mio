package com.mio.base;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 静态代理
 */
public class ProxyObj implements IObj {
    private SourceObj obj;

    public ProxyObj(SourceObj obj) {
        this.obj = obj;
    }

    @Override
    public void methodA() {
        // 添加逻辑...
        obj.methodA();
        // 添加逻辑...
    }

    @Override
    public void methodB() {
        obj.methodB();
    }

    @Override
    public void methodC() {
        obj.methodC();
    }
}

class SourceObj implements IObj {
    private static final String TAG = "SourceObj";

    @Override
    public void methodA() {
        Log.d(TAG, "methodA: ");
    }

    @Override
    public void methodB() {
        Log.d(TAG, "methodB: ");
    }

    @Override
    public void methodC() {
        Log.d(TAG, "methodC: ");
    }
}

/**
 * 使用 InvocationHandler 实现动态代理
 */
class DProxyObj implements InvocationHandler {
    private SourceObj obj;

    public DProxyObj(SourceObj obj) {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(obj, args);
    }
}

interface IObj {
    void methodA();

    void methodB();

    void methodC();
}

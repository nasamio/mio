package com.mio.base;

/**
 * 线程安全的懒汉式单例
 */
public class SingleTon {
    private volatile static SingleTon sInstance;

    private SingleTon() {
    }

    public static SingleTon getsInstance() {
        if (sInstance == null) {
            synchronized (SingleTon.class) {
                if (sInstance == null) {
                    sInstance = new SingleTon();
                }
            }
        }
        return sInstance;
    }
}
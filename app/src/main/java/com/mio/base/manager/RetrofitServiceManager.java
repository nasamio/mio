package com.mio.base.manager;

import com.mio.base.bean.User;

import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.NonNull;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 管理类
 * 依赖：
 * // 测试 Retrofit2+OkHttp3+RxJava
 * //RxJava
 * compile 'io.reactivex:rxjava:1.1.3'
 * //RxAndroid
 * compile 'io.reactivex:rxandroid:1.1.0'
 * //retrofit
 * compile 'com.squareup.retrofit2:retrofit:2.0.0'
 * //retrofit依赖Gson
 * compile 'com.squareup.retrofit2:converter-gson:2.0.0'
 * //OkHttp
 * compile 'com.squareup.okhttp3:okhttp:3.2.0'
 * //retrofit依赖RxJava
 * compile 'com.squareup.retrofit2:adapter-rxjava:2.0.0'
 * https://juejin.cn/post/6844903913469001736
 */
public class RetrofitServiceManager {
    private static final int DEFAULT_CONNECT_TIME = 10;
    private static final int DEFAULT_WRITE_TIME = 30;
    private static final int DEFAULT_READ_TIME = 30;
    private final OkHttpClient okHttpClient;
    private static final String REQUEST_PATH = "http://192.168.1.200:8080/";
    private final Retrofit retrofit;

    private RetrofitServiceManager() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIME, TimeUnit.SECONDS)//连接超时时间
                .writeTimeout(DEFAULT_WRITE_TIME, TimeUnit.SECONDS)//设置写操作超时时间
                .readTimeout(DEFAULT_READ_TIME, TimeUnit.SECONDS)//设置读操作超时时间
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)//设置使用okhttp网络请求
                .baseUrl(REQUEST_PATH)//设置服务器路径
                .addConverterFactory(GsonConverterFactory.create())//添加转化库，默认是Gson
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//添加回调库，采用RxJava
                .build();
    }

    private static class SingletonHolder {
        private static final RetrofitServiceManager INSTANCE = new RetrofitServiceManager();
    }

    public static RetrofitServiceManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

    private static <T> void setSubscribe(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())//子线程访问网络
                .observeOn(AndroidSchedulers.mainThread())//回调到主线程
                .subscribe(observer);
    }

    private static ApiService apiService = getInstance().create(ApiService.class);

    public void getAllUser(@NonNull Observer<List<User>> observer) {
        setSubscribe(apiService.getAllUser(), observer);
    }

    interface ApiService {
        @GET("user/getAll")
        Observable<List<User>> getAllUser();
    }
}
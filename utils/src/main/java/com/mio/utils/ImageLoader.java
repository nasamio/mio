package com.mio.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.LruCache;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private static final String TAG = "ImageLoader";

    // 图片缓存
    private LruCache<String, Bitmap> mImageCache = null;
    // 下载线程池
    private ExecutorService mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private Handler mHandler;
    private static volatile ImageLoader sInstance;
    private Context mContext;

    public ImageLoader(Context mContext) {
        this.mContext = mContext;

        mHandler = new Handler(mContext.getMainLooper());
        initLruCache();
    }

    private void initLruCache() {
        // 缓存区域是最大内存的四分之一
        int memorySize = (int) (Runtime.getRuntime().maxMemory() / 1024);
        mImageCache = new LruCache<String, Bitmap>(memorySize / 4) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // 每张图片占用的内存大小
                return value.getAllocationByteCount() / 1024;
            }
        };
    }

    public static ImageLoader getsInstance(@NonNull Context context) {
        if (sInstance == null) {
            synchronized (ImageLoader.class) {
                sInstance = new ImageLoader(context);
            }
        }
        return sInstance;
    }

    public void load(ImageView imageView, String url) {
        if (imageView == null || TextUtils.isEmpty(url)) {
            return;
        }

        Bitmap bitmap = mImageCache.get(url);
        if (bitmap != null) {
            // 说明缓存有 直接加载
            imageView.setImageBitmap(bitmap);
        } else {
            // 没有 下载好了会自动加载上图 并且加到缓存中
            downloadImage(imageView, url);
        }
    }

    private void downloadImage(ImageView imageView, String url) {
        // 子线程下载
        mExecutor.execute(() -> {
            Bitmap bitmap = downloadBitmapByUrl(url);

            if (bitmap != null) {
                mHandler.post(() ->
                        imageView.setImageBitmap(bitmap));
            }

            // 添加到缓存
            mImageCache.put(url, bitmap);
        });
    }

    private Bitmap downloadBitmapByUrl(String urlStr) {
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        Bitmap bitmap = null;
        try {
            final URL url = new URL(urlStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            bitmap = BitmapFactory.decodeStream(in);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bytes = bos.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }
}

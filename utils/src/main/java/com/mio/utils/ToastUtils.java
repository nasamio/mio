package com.mio.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class ToastUtils {
    public static void showShortToast(@NonNull Context context, @NonNull String content) {
        showToast(context, content, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(@NonNull Context context, @NonNull String content) {
        showToast(context, content, Toast.LENGTH_LONG);
    }

    public static void showToast(@NonNull Context context, @NonNull String content, int duration) {
        Toast.makeText(context, content, duration).show();
    }
}

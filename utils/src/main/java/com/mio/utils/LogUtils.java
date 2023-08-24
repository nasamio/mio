package com.mio.utils;

import android.util.Log;

/**
 * @author mio..
 * @description: log类，推荐使用这个.
 * 能够根据堆栈信息 获取log具体所在的代码位置
 */
public class LogUtils {
    public static boolean DEBUG = true;
    // 关键词，只需要过滤该词，只会出现该项目的的log，不会有别人的log，可以修改该值.
    private static final String TAG = "mio_";

    public static void d() {
        if (DEBUG) {
            Log.d(TAG + getAutoJumpLogInfos()[0], getAutoJumpLogInfos()[1] + ":" + getAutoJumpLogInfos()[2]);
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(TAG + getAutoJumpLogInfos()[0], getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void v(String msg) {
        if (DEBUG) {
            Log.v(TAG + getAutoJumpLogInfos()[0], getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(TAG + getAutoJumpLogInfos()[0], getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void d(String msg) {
        if (DEBUG) {
            Log.d(TAG + getAutoJumpLogInfos()[0], getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(TAG + getAutoJumpLogInfos()[0], getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void i(String msg) {
        if (DEBUG) {
            Log.i(TAG + getAutoJumpLogInfos()[0], getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(TAG + getAutoJumpLogInfos()[0], getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void w(String msg) {
        if (DEBUG) {
            Log.w(TAG + getAutoJumpLogInfos()[0], getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(TAG + getAutoJumpLogInfos()[0], getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void e(String msg) {
        if (DEBUG) {
            Log.e(TAG + getAutoJumpLogInfos()[0], getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    private static String[] getAutoJumpLogInfos() {
        String[] infos = new String[]{"", "", ""};
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if (elements.length < 5) {
            Log.e("MyLogger", "Stack is too shallow!!!");
            return infos;
        } else {
            infos[0] = elements[4].getClassName().substring(
                    elements[4].getClassName().lastIndexOf(".") + 1);
            infos[1] = elements[4].getMethodName() + "()";
            infos[2] = " at(" + elements[4].getClassName() + ".java:"
                    + elements[4].getLineNumber() + ")";
            return infos;
        }
    }
}

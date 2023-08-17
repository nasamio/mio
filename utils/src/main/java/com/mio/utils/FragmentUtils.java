package com.mio.utils;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentUtils {
    private static int enterAni = -1, exitAni = -1;

    public static void setEnterAni(int enterAni) {
        FragmentUtils.enterAni = enterAni;
    }

    public static void setExitAni(int exitAni) {
        FragmentUtils.exitAni = exitAni;
    }

    /**
     * 直接替换掉原来的fragment 会销毁
     *
     * @param activity    容器activity
     * @param fragment    新fragment
     * @param containerId 容器组件id
     */
    public static void replaceFragment(@NonNull AppCompatActivity activity,
                                       @NonNull Fragment fragment,
                                       @IdRes int containerId) {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * 使用show hide方式显示隐藏fragment 来回切换不会销毁 开销会更大一些
     *
     * @param activity     容器activity
     * @param showFragment 要显示的fragment
     * @param hideFragment 旧fragment
     * @param containerId  容器组件id
     */
    public static void showFragment(@NonNull AppCompatActivity activity,
                                    @NonNull Fragment showFragment,
                                    Fragment hideFragment,
                                    @IdRes int containerId) {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (enterAni != -1 && exitAni != -1) {
            transaction.setCustomAnimations(enterAni, exitAni);
        }


        //没有添加则先完成添加再显示
        if (!showFragment.isAdded()) {
            if (hideFragment != null) {
                transaction.hide(hideFragment);
            }
            transaction
                    .add(containerId, showFragment)
                    .commit();
        } else {//都添加了就直接隐藏当前fragment，显示目标fragment
            transaction
                    .hide(hideFragment)
                    .show(showFragment)
                    .commit();
        }
    }


}

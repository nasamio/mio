package com.mio.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;

public class Utils {
    public static boolean areArraysEqual(int[][] array1, int[][] array2) {
        if (array1.length != array2.length) {
            return false;
        }

        for (int i = 0; i < array1.length; i++) {
            if (array1[i].length != array2[i].length) {
                return false;
            }

            for (int j = 0; j < array1[i].length; j++) {
                if (array1[i][j] != array2[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 的判断列表中是否有相同的元素
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <T> boolean hasDuplicateContent(List<T> list) {
        return list.stream()
                .distinct() // 去重
                .count() < list.size();
    }

    /**
     * 从 list1中去除list2中的所有元素
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <T> void removeElementsFromList1(List<T> list1, List<T> list2) {
        list1.removeIf(list2::contains);
    }
}

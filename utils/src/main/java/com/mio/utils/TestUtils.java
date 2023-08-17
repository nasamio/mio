package com.mio.utils;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {
    public static List<String> getTestStringList(int size) {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            strings.add("我是第" + i + "个item");
        }
        return strings;
    }
}

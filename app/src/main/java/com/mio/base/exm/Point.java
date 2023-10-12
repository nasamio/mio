package com.mio.base.exm;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Point {
    private int x, y;
    private int type;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static final int NORMAL = 0; // 默认无东西的情况
    public static final int DATA = 1; // 有格子的情况下
    public static final int DO = 2; // 可以操作的格子
}

package com.mio.game.bean;

import java.util.Objects;

import lombok.Data;

@Data
public class Point {
    private int x, y;
    private int type;

    public  Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static final int NORMAL = 0; // 默认无东西的情况
    public static final int DATA = 1; // 有格子的情况下
    public static final int DO = 2; // 可以操作的格子

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x &&
                y == point.y &&
                type == point.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, type);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", type=" + type +
                '}';
    }
}

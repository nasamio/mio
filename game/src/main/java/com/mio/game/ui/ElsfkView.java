package com.mio.game.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;

import com.mio.game.bean.Direction;
import com.mio.game.bean.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.mio.game.bean.Direction.BOTTOM;
import static com.mio.game.bean.Direction.TOP;
import static com.mio.game.ui.ElsfkType.*;

public class ElsfkView extends BaseGameView {
    private static final String TAG = "ElsfkView";
    private static final long DURATION_BETWEEN = 400;

    public ElsfkView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private List<Point> brick;

    @Override
    protected void onStart() {
        updateDuration = 200;
        // 方向和初始化的数据需要一致 头必须是面朝的方向
        direction = TOP;

        generateNewBrick();

        title = "俄罗斯方块";
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        marginOne = 1.f;
    }

    private boolean isFrozen = false;

    @Override
    protected void onUpdate() {
        if (isFrozen) return;
//        Log.d(TAG, "onUpdate: ");
        if (brick == null) return;

        // Log.d(TAG, "onUpdate: can move bottom : " + canBrickMove(BOTTOM));


        if (canBrickMove(BOTTOM)) {
            brick.forEach(point -> point.setY(point.getY() + 1));
        } else { // 现在这个砖块不能移动了
            // brick.forEach(point -> point.setType(Point.DATA));

            int top = vel, bottom = 0;
            for (Point p : brick) {
                top = Math.min(top, p.getY());
                bottom = Math.max(bottom, p.getY());
            }


            for (int j = top; j <= bottom; j++) {
                boolean canBreak = true;
                for (int i = 0; i < hor; i++) {
                    Point point = getPoint(i, j);
                    if (point.getType() != Point.DATA
                            && point.getType() != Point.DO) {
                        canBreak = false;
                        break;
                    }
                }

                if (canBreak) {
                    // todo 处理一行或几行砖块 j 销毁逻辑
                }
            }


           /* // 检查是否可以消除
            if (canBreakBrick()) {
                isFrozen = true;
                for (int i = 0; i < breakList.size(); i++) {
                    Log.d(TAG, "onUpdate: break index : " + breakList.get(i));
                    breakLine(breakList.get(i));
                }
                postInvalidate();

                breakList.clear();
                brick.clear();
                isFrozen = false;
                return;
                // return;
            } else {
                notifyData(brick, Point.DATA);
                brick.clear();
            }

            postDelayed(() -> generateNewBrick(), DURATION_BETWEEN);
        }

        if (brick.size() > 0) {
            notifyData(brick, brick.get(0).getType());
        }*/
    }

    /**
     * 消除一行
     */
    private void breakLine(int index) {
        for (int j = index; j < vel; j++) {
//            if (j < vel - 1) {
            // 把上一行的数据填到现在这一行
            for (int i = 0; i < hor; i++) {
                getPoint(i, j).setType(getPoint(i, j - 1).getType());
            }
//            } else {
//
//            }
        }

        plusScore(10);
    }

    private List<Integer> breakList = new ArrayList<>();

    private boolean canBreakBrick() {
        if (breakList.size() > 0) return true;
        for (int j = 0; j < vel; j++) {

            boolean canBreak = true;
            for (int i = 0; i < hor; i++) {
                Point point = getPoint(i, j);
                if (point.getType() != Point.DATA
                        && point.getType() != Point.DO) {
                    canBreak = false;
                    break;
                }
            }

            if (canBreak) {
                breakList.add(j);
            }
        }
        return breakList.size() > 0;
    }

    /**
     * 生成新的砖块
     * 上方四行用于生成砖块
     */
    private void generateNewBrick() {
        int elsfkType = random.nextInt(TYPE_T);

        elsfkType = TYPE_O;

        switch (elsfkType) {
            case TYPE_O:
                brick = generateBrick(new int[]{1, 1, 1, 2, 2, 1, 2, 2});
                break;
            case TYPE_I:
                brick = generateBrick(new int[]{1, 0, 1, 1, 1, 2, 1, 3});
                break;
            case TYPE_S:
                brick = generateBrick(new int[]{1, 1, 2, 1, 0, 2, 1, 2});
                break;
            case TYPE_Z:
                brick = generateBrick(new int[]{0, 1, 1, 1, 1, 2, 2, 2});
                break;
            case TYPE_L:
                brick = generateBrick(new int[]{1, 1, 1, 2, 1, 3, 2, 3});
                break;
            case TYPE_J:
                brick = generateBrick(new int[]{1, 1, 1, 2, 1, 3, 0, 3});
                break;
            case TYPE_T:
                brick = generateBrick(new int[]{1, 0, 1, 1, 1, 2, 0, 1});
                break;
        }

        plusScore(1);
    }

    /**
     * 4 * 4的格子中生成俄罗斯方块的初始格子 尽量居中 左上角坐标是0，0 右下角是 3，3
     */
    private List<Point> generateBrick(int[] ints) {
        ArrayList<Point> points = new ArrayList<>();
        int dx = hor / 2 - 2;
        for (int i = 0; i < ints.length / 2; i++) {
            points.add(new Point(ints[2 * i] + dx, ints[2 * i + 1], Point.DO));
        }
        return points;
    }

    @Override
    public void handleKey(Direction toDir) {
        if (brick != null) {
            if (toDir == TOP && canBrickRotate()) {
                rotateBrick();
            }

            if (canBrickMove(toDir))
                switch (toDir) {
                    case LEFT:
                        brick.forEach(point -> point.setX(point.getX() - 1));
                        break;
                    case RIGHT:
                        brick.forEach(point -> point.setX(point.getX() + 1));
                        break;
                    case BOTTOM:
                        brick.forEach(point -> point.setY(point.getY() + 1));
                        break;
                }

            invalidate();
        }
    }

    private void rotateBrick() {
        // 创建一个自定义的Comparator，首先按y从小到大，然后x从小到大排序
        Comparator<Point> customComparator = Comparator
                .comparing(Point::getY)
                .thenComparing(Point::getX);

        // 使用Collections.sort来排序列表
        Collections.sort(brick, customComparator);

        // 物块就四个格子 排序后取第二个来旋转
        // 找到中心点的坐标
        float rx = brick.get(1).getX();
        float ry = brick.get(1).getY();

        // 旋转每个点
        for (Point point : brick) {
            int x1 = point.getX();
            int y1 = point.getY();

            float dx = x1 - rx;
            float dy = y1 - ry;

            // 计算新坐标
            int newX = Math.round(rx + dy);
            int newY = Math.round(ry - dx);

            // 更新点的坐标
            point.setX(newX);
            point.setY(newY);
        }

        postInvalidate();
    }

    private boolean canBrickRotate() {
        return true;
    }

    private boolean canBrickMove(Direction toDir) {
        for (Point p : brick) {
            switch (toDir) {
                case LEFT:
                    if (p.getX() <= 0) return false;
                    break;
                case RIGHT:
                    if (p.getX() >= hor - 1) return false;
                    break;
                case BOTTOM:
                    if (p.getY() >= vel - 1) return false; // 有到最下面一行的
                    break;
            }

            int type = Point.UNKNOWN;
            switch (toDir) {
                case LEFT:
                    type = getPoint(p.getX() - 1, p.getY()).getType();
                    break;
                case RIGHT:
                    type = getPoint(p.getX() + 1, p.getY()).getType();
                    break;
                case BOTTOM:
                    type = getPoint(p.getX(), p.getY() + 1).getType();
                    break;
            }

            if (type == Point.DO || type == Point.UNKNOWN) continue;
            if (type != Point.NORMAL) return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {

    }

    @Override
    protected void notifyDataPaintColor(int data, float left, float top, float right, float bottom) {
        dataPaint.setShader(null);

        switch (data) {
            case Point.DATA:
                dataPaint.setShader(new LinearGradient(
                        (left + right) / 2.f, top, (left + right) / 2.f, bottom,
                        new int[]{
                                Color.parseColor("#08AEEA"),
                                Color.parseColor("#2AF598"),
                        }, new float[]{0, 1.f}, Shader.TileMode.CLAMP));
                break;
            case Point.DO:
                dataPaint.setShader(new LinearGradient(
                        (left + right) / 2.f, top, (left + right) / 2.f, bottom,
                        new int[]{
                                Color.parseColor("#21D4FD"),
                                Color.parseColor("#B721FF"),
                        }, new float[]{0, 1.f}, Shader.TileMode.CLAMP));
                break;
            default:
                dataPaint.setShader(new LinearGradient(
                        (left + right) / 2.f, top, (left + right) / 2.f, bottom,
                        new int[]{
                                Color.parseColor("#FFDEE9"),
                                Color.parseColor("#B5FFFC"),
                        }, new float[]{0, 1.f}, Shader.TileMode.CLAMP));
                break;
        }
    }

    @Override
    protected void drawPoint(Canvas canvas, float left, float top, float right, float bottom, Paint paint) {
        float roundRadius = 2;
        canvas.drawRoundRect(left, top, right, bottom, roundRadius, roundRadius, paint);
    }
}

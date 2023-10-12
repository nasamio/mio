package com.mio.game.ui;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;

import com.mio.game.bean.Direction;
import com.mio.game.bean.Point;
import com.mio.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.mio.game.bean.Direction.BOTTOM;
import static com.mio.game.bean.Direction.LEFT;
import static com.mio.game.bean.Direction.RIGHT;
import static com.mio.game.bean.Direction.TOP;

public class TcsView extends BaseGameView {
    private static final String TAG = "TcsView";

    public TcsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private List<Point> snake;

    private Point scorePoint; // 分数

    private int toAddCount = 0; // 需要添加到尾部的个数

    @Override
    protected void onStart() {
        updateDuration = 200;
        // 方向和初始化的数据需要一致 头必须是面朝的方向
        direction = TOP;
        snake = new ArrayList<Point>() {{
            add(new Point(4, 5));
            add(new Point(4, 6));
            add(new Point(4, 7));
            add(new Point(4, 8));
            add(new Point(4, 9));
            add(new Point(4, 10));
        }};
        notifySnake();
        title = "贪吃蛇";
    }

    @Override
    protected void onUpdate() {
        Log.d(TAG, "onUpdate: ");
        if (snake == null) return;

        Point next = judgeIfDead(snake.get(0));
        if (next == null) {
            return;
        }


        if (scorePoint == null) {
            randomScore();
        } else {
            getPoint(scorePoint.getX(), scorePoint.getY()).setType(Point.DATA);
        }


        toNext(next);


    }

    private boolean snakeContainsScore() {
        if (scorePoint == null || snake == null) return false;
        for (Point p : snake) {
            if (p.getX() == scorePoint.getX() && p.getY() == scorePoint.getY()) {
                return true;
            }
        }
        return false;
    }

    private void randomScore() {
        ArrayList<Point> points = new ArrayList<>(data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Utils.removeElementsFromList1(points, snake);
            scorePoint = points.get(random.nextInt(points.size()));
        }
    }

    private void toNext(Point next) {
        Point teal = snake.get(snake.size() - 1);

        if (toAddCount > 0) {
            toAddCount--;
            snake.add(teal);
        } else {
            getPoint(teal.getX(), teal.getY()).setType(Point.NORMAL);
        }
        for (int i = snake.size() - 1; i >= 0; i--) {
            if (i > 0) {
                snake.set(i, snake.get(i - 1));
            } else {
                snake.set(0, next);
            }
        }

        Point head = snake.get(0);
        // 在计算出下一帧的头部 并且判断下一帧的头部和奖励一致的时候 就直接在界面刷新前 把分加上
        if (head.getX() == scorePoint.getX()
                && head.getY() == scorePoint.getY()) {
            toAddCount++;
            scorePoint = null;
            setScore(score + 1);
        }
        notifySnake();
    }

    @Override
    protected void onDestroy() {

    }

    private void notifySnake() {
        for (int i = 0; i < snake.size(); i++) {
            Point point = snake.get(i);
            getPoint(point.getX(), point.getY()).setType(Point.DO);
        }

        postInvalidate();
    }

    /**
     * 是否死亡了 如果不是 返回下一个格子
     */
    private Point judgeIfDead(Point zero) {
        Log.d(TAG, "judgeIfDead: ");
        // 检查现在snake是否有一样的 如果有说明死了
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (Utils.hasDuplicateContent(snake)) {
                gameOver("撞自己死了...");
                return null;
            }
        }

        Point next = null;
        switch (direction) {
            case BOTTOM:
                next = new Point(zero.getX(), zero.getY() + 1);
                break;
            case TOP:
                next = new Point(zero.getX(), zero.getY() - 1);
                break;
            case LEFT:
                next = new Point(zero.getX() - 1, zero.getY());
                break;
            case RIGHT:
                next = new Point(zero.getX() + 1, zero.getY());
                break;
        }

        if (next == null
                || next.getX() < 0
                || next.getX() > hor - 1
                || next.getY() < 0
                || next.getY() > vel - 1
        ) {
            // Log.e(TAG, "judgeIfDead: game over...");
            gameOver("撞墙死了...");
            return null;
        }
        return next;
    }


    /**
     * 按键
     */
    public void handleKeyDown(Direction toDir) {
        if ((direction == TOP && toDir == BOTTOM)
                || (direction == BOTTOM && toDir == TOP)
                || (direction == LEFT && toDir == RIGHT)
                || (direction == RIGHT && toDir == LEFT)) {

        } else {
            direction = toDir;
        }
    }

    @Override
    public void restart() {
        randomScore();
        super.restart();
    }
}

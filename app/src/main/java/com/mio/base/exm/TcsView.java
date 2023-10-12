package com.mio.base.exm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.mio.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class TcsView extends BaseGameView {
    private static final String TAG = "TcsView";

    public TcsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private List<Point> snake;

    @Override
    protected void onStart() {
        updateDuration = 500;
        snake = new ArrayList<Point>() {{
            add(new Point(4, 5));
            add(new Point(4, 6));
            add(new Point(4, 7));
            add(new Point(4, 8));
            add(new Point(4, 9));
            add(new Point(4, 10));
        }};
        notifySnake();

    }

    private void notifySnake() {
        for (int i = 0; i < snake.size(); i++) {
            Point point = snake.get(i);
            getPoint(point.getX(), point.getY()).setType(Point.DO);
        }

        postInvalidate();
    }

    @Override
    protected void onUpdate() {
        Log.d(TAG, "onUpdate: ");
        if (snake == null) return;
        Point next = judgeIfDead(snake.get(0));
        if (next == null) {
            return;
        }
        Point teal = snake.get(snake.size() - 1);
        getPoint(teal.getX(), teal.getY()).setType(Point.NORMAL);
        for (int i = snake.size() - 1; i >= 0; i--) {
            if (i > 0) {
                snake.set(i, snake.get(i - 1));
            } else {
                snake.set(0, next);
            }
        }

        notifySnake();
    }

    /**
     * 是否死亡了 如果不是 返回下一个格子
     */
    private Point judgeIfDead(Point zero) {
        Log.d(TAG, "judgeIfDead: ");
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
        if (next != null) {
            Log.d(TAG, "judgeIfDead: x :" + next.getX() + " , y : " + next.getY());
        }

        if (next == null
                || next.getType() == Point.DATA
                || next.getType() == Point.DO
                || next.getX() < 0
                || next.getX() > hor - 1
                || next.getY() < 0
                || next.getY() > vel - 1
        ) {
            Log.e(TAG, "judgeIfDead: game over...");
            post(() -> ToastUtils.showShortToast(mContext, "game over..."));
            isRunning = false;
            return null;
        }
        return next;
    }

    @Override
    protected void onDestroy() {

    }

    @SuppressLint("ClickableViewAccessibility")

    @Override
    protected void bindBtn(Button btn, DIRECTION dir) {
        btn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 反向操作
//                    if ((direction == DIRECTION.TOP && dir == DIRECTION.BOTTOM)
//                            || (direction == DIRECTION.BOTTOM && dir == DIRECTION.TOP)
//                            || (direction == DIRECTION.LEFT && dir == DIRECTION.RIGHT)
//                            || (direction == DIRECTION.RIGHT && dir == DIRECTION.LEFT)) {
//                        return false;
//                    } else {
                    direction = dir;
//                    }
                    break;
            }
            return true;
        });

        btn.setOnClickListener(v -> direction = dir);
    }
}

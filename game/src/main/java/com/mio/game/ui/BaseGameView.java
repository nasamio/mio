package com.mio.game.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.mio.game.bean.Direction;
import com.mio.game.bean.Point;
import com.mio.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseGameView extends View {
    private static final String TAG = "BaseGameView";

    // 线程池
    protected ExecutorService gameThreadPool;

    public BaseGameView(Context context) {
        super(context);
        init(context);
    }

    public BaseGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected List<Point> data; // 格子数据

    protected boolean isRunning = true;
    protected int hor, vel; // 横竖格子数

    protected float oneWidth; // 每个格子的宽度

    protected float marginOne; // 格子之间的宽度

    protected float marginHorizon; // 格子区域外的左右间距
    protected float marginTop; // 上间距
    protected Paint dataPaint; // 画格子的画笔
    protected long updateDuration = 16;
    protected Direction direction = Direction.BOTTOM;

    protected Random random;

    private GameCallback callback;

    protected String title;
    protected int score;

    private void init(Context context) {
        hor = 10;
        vel = 20;
        data = new ArrayList<>(hor * vel);
        for (int j = 0; j < vel; j++) {
            for (int i = 0; i < hor; i++) {
                Point point = new Point(i, j);
                data.add(point);
            }
        }
        random = new Random();


        dataPaint = new Paint();
        dataPaint.setStyle(Paint.Style.FILL);
        dataPaint.setAntiAlias(true);


        setBackgroundColor(Color.BLACK);

        gameRunnableInit();

        initBtn();

        setScore(0);
        onStart();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initBtn() {
//        bindBtn(mDataBinding.btnLeft, DIRECTION.LEFT);
//        bindBtn(mDataBinding.btnRight, DIRECTION.RIGHT);
//        bindBtn(mDataBinding.btnTop, DIRECTION.TOP);
//        bindBtn(mDataBinding.btnBottom, DIRECTION.BOTTOM);

    }

    protected void gameRunnableInit() {
        if (gameThreadPool == null) {
            gameThreadPool = Executors.newFixedThreadPool(5);
            gameThreadPool.execute(() -> {
                while (true) {
                    // Log.d(TAG, "gameRunnableInit is running : " + isRunning);
                    if (!isRunning) return;


                    onUpdate();
                    postInvalidate();

                    try {
                        Thread.sleep(updateDuration);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (callback != null) {
                        callback.onGameUpdate();

                        if (title != null) {
                            callback.onTitle(title);
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        marginTop = 100;
        marginHorizon = 200;
        oneWidth = (getWidth() - 2.f * marginHorizon) / (hor * 1.f);
        marginOne = 2.f;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < data.size(); i++) {
            Point point = data.get(i);
            float left = marginHorizon + point.getX() * (oneWidth + marginOne);
            float top = marginTop + point.getY() * (oneWidth + marginOne);
            notifyDataPaintColor(point.getType(), left, top, left + oneWidth, top + oneWidth);
            drawPoint(canvas, left, top, left + oneWidth, top + oneWidth, dataPaint);
        }
    }

    protected void drawPoint(Canvas canvas, float left, float top, float right, float bottom, Paint paint) {
        canvas.drawRect(left, top, right, bottom, paint);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    /**
     * 可以重写此方法来达到自定义颜色的效果
     */
    protected void notifyDataPaintColor(int data, float left, float top, float right, float bottom) {
        // Log.d(TAG, "notifyDataPaintColor: " + data);
        switch (data) {
            case Point.DATA:
                dataPaint.setColor(Color.YELLOW);
                break;
            case Point.DO:
                dataPaint.setColor(Color.WHITE);
                break;
            default:
                dataPaint.setColor(Color.GRAY);
                break;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDestroy();
    }

    public Point getPoint(int x, int y) {
        int index = x + (y * hor);
        if (index < data.size() && index >= 0) {
            return data.get(index);
        } else {
            return new Point(0, 0); // 返回一个不在data中的数据 操作不会影响界面
        }
    }

    protected void gameOver(String msg) {
        post(() -> ToastUtils.showShortToast(getContext(), msg));
        isRunning = false;
        if (callback != null) callback.onGameOver();
    }

    public void handleKey(Direction toDir) {

    }

    protected void setScore(int s) {
        score = s;
        if (callback != null) callback.onScore(score);
    }

    public void setCallback(GameCallback callback) {
        this.callback = callback;
    }

    public void restart() {
        isRunning = true;
        init(getContext());
    }

    protected void notifyData(List<Point> newData, int type) {
        // 清除原有do的数据
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data.forEach(point -> {
                if (point.getType() == Point.DO) {
                    point.setType(Point.NORMAL);
                }
            });
        }

        for (int i = 0; i < newData.size(); i++) {
            Point point = newData.get(i);
            getPoint(point.getX(), point.getY()).setType(type);
        }

        postInvalidate();
    }

    protected void plusScore(int delta) {
        setScore(score + delta);
    }

    /**
     * 开始时调用
     */
    protected abstract void onStart();

    /**
     * 每一帧调用 16ms
     */
    protected abstract void onUpdate();

    /**
     * 销毁
     */
    protected abstract void onDestroy();
}

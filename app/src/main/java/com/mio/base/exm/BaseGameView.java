package com.mio.base.exm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.mio.base.R;
import com.mio.base.databinding.LayoutGameBinding;
import com.mio.basic.BaseView;
import com.mio.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.mio.base.exm.Point.*;

public abstract class BaseGameView extends BaseView<LayoutGameBinding> {
    private static final String TAG = "BaseGameView";
    protected List<Point> data; // 格子数据
    protected boolean isRunning = true;

    protected int hor, vel; // 横竖格子数

    protected float oneWidth; // 每个格子的宽度

    protected float marginOne; // 格子之间的宽度
    protected float marginHorizon; // 格子区域外的左右间距
    protected float marginTop; // 上间距
    protected Paint dataPaint; // 画格子的画笔
    protected long updateDuration = 16;

    enum DIRECTION {
        UNKNOWN,
        LEFT,
        TOP,
        RIGHT,
        BOTTOM,
    }

    protected DIRECTION direction = DIRECTION.BOTTOM;


    public BaseGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(AttributeSet attrs) {
        hor = 10;
        vel = 20;
        data = new ArrayList<>(hor * vel);
        for (int j = 0; j < vel; j++) {
            for (int i = 0; i < hor; i++) {
                Point point = new Point(i, j);
                data.add(point);
            }
        }


        dataPaint = new Paint();
        dataPaint.setStyle(Paint.Style.FILL);
        dataPaint.setAntiAlias(true);


        setBackgroundColor(Color.BLACK);

        gameRunnableInit();

        initBtn();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initBtn() {
        bindBtn(mDataBinding.btnLeft, DIRECTION.LEFT);
        bindBtn(mDataBinding.btnRight, DIRECTION.RIGHT);
        bindBtn(mDataBinding.btnTop, DIRECTION.TOP);
        bindBtn(mDataBinding.btnBottom, DIRECTION.BOTTOM);

    }

    protected abstract void bindBtn(Button btn, DIRECTION dir);


    protected void gameRunnableInit() {
        new Thread(() -> {
            while (isRunning) {
                onUpdate();
                postInvalidate();

                try {
                    Thread.sleep(updateDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

//        postDelayed(() -> genData(), 1000);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        marginTop = 100;
        marginHorizon = 200;
        oneWidth = (getWidth() - 2.f * marginHorizon) / (hor * 1.f);
        marginOne = 2.f;

        onStart();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        for (int i = 0; i < data.size(); i++) {
            Point point = data.get(i);
            float left = marginHorizon + point.getX() * (oneWidth + marginOne);
            float top = marginTop + point.getY() * (oneWidth + marginOne);
            notifyDataPaintColor(point.getType());
            canvas.drawRect(left, top, left + oneWidth, top + oneWidth, dataPaint);
        }
    }

    protected void notifyDataPaintColor(int data) {
        // Log.d(TAG, "notifyDataPaintColor: " + data);
        switch (data) {
            case DATA:
                dataPaint.setColor(Color.YELLOW);
                break;
            case DO:
                dataPaint.setColor(Color.WHITE);
                break;
            default:
                dataPaint.setColor(Color.GRAY);
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_game;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDestroy();
    }

    public Point getPoint(int x, int y) {
        return data.get(x + (y * hor));
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

package com.mio.base.exm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import com.mio.base.R;
import com.mio.base.databinding.LayoutElsBinding;
import com.mio.basic.BaseView;
import com.mio.utils.Utils;

import java.util.Random;

import static com.mio.base.exm.ELS_TYPE.*;

/**
 * 俄罗斯方块
 */
public class ElsView extends BaseView<LayoutElsBinding> {
    private static final String TAG = "ElsView";
    private int[][] data; // 格子数据
    private int[][] temp; // 复制数据
    private int sameCount = 0;

    public static final int NORMAL = 0; // 默认无东西的情况
    public static final int DATA = 1; // 有格子的情况下
    public static final int DO = 2; // 可以操作的格子


    private boolean isRunning = true;
    private long lastDownTime = -1; // 上一次下降的时间
    public static final long DOWN_DURATION = 200; // 多久下降一次
    private long lastGenerateTime = -1; // 上一次生成的时间
    public static final long GENERATION_DURATION = 3000; // 多久生成一个新的

    private int hor, vel; // 横竖格子数

    private float oneWidth; // 每个格子的宽度

    private float marginOne; // 格子之间的宽度
    private float marginHorizon; // 格子区域外的左右间距
    private float marginTop; // 上间距
    private Paint dataPaint; // 画格子的画笔
    private Random random;
    private int[][] todoData; // 即将加入界面的内容 一格4*4的格子
    private int todoIndex = 3; // 扫描 todoData 的时候 从下往上数的第几行

    public ElsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(AttributeSet attrs) {
        hor = 10;
        vel = 20;
        data = new int[hor][vel];


        dataPaint = new Paint();
        dataPaint.setStyle(Paint.Style.FILL);
        dataPaint.setAntiAlias(true);


        setBackgroundColor(Color.BLACK);


        gameRunnableInit();
    }

    private void gameRunnableInit() {
        new Thread(() -> {
            while (isRunning) {
//                 Log.d(TAG, "run: game running...");

                if (lastDownTime == -1) {
                    lastDownTime = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - lastDownTime >= DOWN_DURATION) {
                    downData();
                    lastDownTime = System.currentTimeMillis();
                }

                if (lastGenerateTime == -1) {
                    lastGenerateTime = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - lastGenerateTime >= GENERATION_DURATION) {
                     genData();
//                    lastGenerateTime = System.currentTimeMillis();
                }

                try {
                    Thread.sleep(16);
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
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                float left = marginHorizon + i * (oneWidth + marginOne);
                float top = marginTop + j * (oneWidth + marginOne);
//                Log.d(TAG, "dispatchDraw: i : " + i + " , left : " + left + " \n"
//                        + " j :" + j + " , top : " + top);
                notifyDataPaintColor(data[i][j]);
                canvas.drawRect(
                        left, top, left + oneWidth, top + oneWidth, dataPaint
                );
            }
        }
    }

    private void notifyDataPaintColor(int data) {
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

    /**
     * 把数据都往下移动一格 如果到头了就不能下移
     */
    private void downData() {
        Log.d(TAG, "downData: ");
        for (int j = data[0].length - 1; j >= 0; j--) {
            for (int i = 0; i < data.length; i++) {
                // 如果是首行 检测即将增加的
                // 如果是非首行：当前的有 就不移动上一个 否则把上一个移动到现在的一格 并把上一个置灰
                if (j == 0) {
                    if ((i >= hor / 2 - 2 && i < hor / 2 + 2) && todoIndex >= 0) {
                        // 检测是否有待出现的
                        if (todoData != null) {
                            data[i][j] = todoData[i - (hor / 2 - 2)][todoIndex];
                        }
                    } else {
                        data[i][j] = NORMAL;
                    }
                } else {
                    if (data[i][j] == DATA || data[i][j] == DO) {
                        continue;
                    }
                    data[i][j] = data[i][j - 1];
                    data[i][j - 1] = NORMAL;
                }
                // Log.d(TAG, "downData: i : " + i + " , j : " + j + " ,can down : " + canDown);
            }
        }

        if (todoData != null) {
            if (todoIndex-- < 0) {
                todoData = null;
                todoIndex = 3;
            }
        }

        if (temp == null) temp = data;
        if (Utils.areArraysEqual(temp, data)) {
            sameCount++;
            Log.d(TAG, "downData: same count : " + sameCount);
            if (sameCount == 6) {
                genData();
                sameCount = 0;
            }
        } else {
            temp = data;
        }

        postInvalidate();
    }


    /**
     * 生成一个新的对象 到todo中
     */
    private void genData() {
        if (random == null)
            random = new Random();

        if (todoData != null) return;

        int type = random.nextInt(TYPE_T);
        switch (type) {
            case TYPE_O:
                todoData = new int[4][4];
                todoData[1][1]
                        = todoData[1][2]
                        = todoData[2][1]
                        = todoData[2][2]
                        = DO;
                break;
            case TYPE_I:
                todoData = new int[4][4];
                todoData[0][2]
                        = todoData[1][2]
                        = todoData[2][2]
                        = todoData[3][2]
                        = DO;
                break;
            case TYPE_S:
                todoData = new int[4][4];
                todoData[1][2]
                        = todoData[2][1]
                        = todoData[2][2]
                        = todoData[3][1]
                        = DO;
                break;
            case TYPE_Z:
                todoData = new int[4][4];
                todoData[1][1]
                        = todoData[2][1]
                        = todoData[2][2]
                        = todoData[3][2]
                        = DO;
                break;
            case TYPE_L:
                todoData = new int[4][4];
                todoData[1][1]
                        = todoData[1][2]
                        = todoData[1][3]
                        = todoData[2][3]
                        = DO;
                break;
            case TYPE_J:
                todoData = new int[4][4];
                todoData[1][3]
                        = todoData[2][1]
                        = todoData[2][2]
                        = todoData[2][3]
                        = DO;
                break;
            case TYPE_T:
                todoData = new int[4][4];
                todoData[1][1]
                        = todoData[1][2]
                        = todoData[1][3]
                        = todoData[2][2]
                        = DO;
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_els;
    }
}

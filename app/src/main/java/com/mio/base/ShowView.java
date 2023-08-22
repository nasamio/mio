package com.mio.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShowView extends View {
    public ShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private int margin; // 四周边距
    private int count = 5; // 划分格子数
    private int innerWidth; // 内部item宽度
    private int xDelta, yDelta;
    private List<ImgItem> list = new ArrayList<>();

    private void init() {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int width = getWidth();
        int height = getHeight();
        margin = (int) (Math.max(width, height) * .1f);
        innerWidth = (int) (Math.min(width, height) * .1f);
        xDelta = (int) ((width - 2 * margin) * 1.f / count);
        yDelta = (int) ((height - 2 * margin) * 1.f / count);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < list.size(); i++) {
            ImgItem item = list.get(i);
            float cx = margin + item.x * xDelta + innerWidth / 2.f;
            float cy = margin + item.y * yDelta + innerWidth / 2.f;
            drawImg(canvas, cx, cy, item.res);
        }
    }

    /**
     * 在cx cy为中心点绘制一个边长是innerWidth的图片 图片资源是 res
     *
     * @param canvas 画布
     * @param cx     中心点x坐标
     * @param cy     中心点y坐标
     * @param res    图片资源ID
     */
    private void drawImg(Canvas canvas, float cx, float cy, int res) {
        // 加载图片资源
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), res);

        // 设置绘制区域
        float left = cx - innerWidth / 2;
        float top = cy - innerWidth / 2;
        RectF dstRect = new RectF(left, top, left + innerWidth, top + innerWidth);

        // 绘制图片
        canvas.drawBitmap(imageBitmap, null, dstRect, null);

        // 记得释放图片资源
        imageBitmap.recycle();
    }

    /**
     * 设置列表 主线程调用
     */
    public void setList(@NotNull List<ImgItem> data) {
        list.clear();
        list.addAll(data);
        invalidate();
    }

    /**
     * 设置动画列表
     */
    public void setAniList(@NotNull List<ImgItem> data, @NotNull Callback callback, long speed) {
        ArrayList<ImgItem> imgItems = new ArrayList<>();
        if (data.isEmpty() || getVisibility() != VISIBLE) {
            callback.onAniEnd();
            setList(imgItems);
            return;
        }

        imgItems.add(data.get(0));
        post(() -> setList(imgItems));

        data.remove(0);

        postDelayed(() -> setAniList(data, callback, speed), speed);
    }

    public interface Callback {
        void onAniEnd();
    }

    public static class ImgItem {
        public int x;
        public int y;
        public int res;

        public ImgItem(int x, int y, int res) {
            this.x = x;
            this.y = y;
            this.res = res;
        }
    }
}

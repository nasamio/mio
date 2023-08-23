package com.mio.base;

import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mio.base.databinding.Fragment4Binding;
import com.mio.base.databinding.ItemRvF4Binding;
import com.mio.basic.BaseFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test4Fragment extends BaseFragment<Fragment4Binding> {
    private static final String TAG = "Test4Fragment";

    private BaseQuickAdapter<Item, BaseViewHolder> adapter;
    private int one;
    private float deltaX;
    private float deltaY;

    @Override
    protected void initView() {
        Log.d(TAG, "initView: ");
        mDataBinding.rvLeft.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        adapter = new BaseQuickAdapter<Item, BaseViewHolder>(R.layout.item_rv_f4) {
            @Override
            protected void convert(BaseViewHolder helper, Item item) {
                // Log.d(TAG, "convert: " + helper.getAdapterPosition());
                ItemRvF4Binding binding = DataBindingUtil.bind(helper.itemView);
                binding.tv.setText(item.name);

                binding.getRoot().setBackgroundColor(item.isChild ? Color.YELLOW : Color.GREEN);
                binding.tv.setTextAlignment(item.isChild ? TextView.TEXT_ALIGNMENT_TEXT_START : View.TEXT_ALIGNMENT_TEXT_END);

                helper.itemView.setOnClickListener(v -> {
                    int position = helper.getAdapterPosition();

                    if (item.children != null) {
                        if (item.isExpand) {
                            // 收起
                            for (int i = 0; i < item.children.size(); i++) {
                                adapter.remove(position + 1);
                            }
                        } else {
                            // 展开
                            adapter.addData(position + 1, item.children);
                        }
                        item.isExpand = !item.isExpand;
                    }
                    if (item.isChild) {
                        Log.d(TAG, "convert: on item click : " + position);
                        // todo 处理子view点击
                    }

                    Log.d(TAG, "onClick: pos : " + helper.getAdapterPosition()
                            + " item : " + item);
                });
            }
        };

        int n = 10;
        List<Item> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Item item = new Item("name : " + i);
            if (i % 4 == 0) {
                item.children = getChildren();
            }
            list.add(item);
        }
        mDataBinding.rvLeft.setAdapter(adapter);
        adapter.setNewData(list);




        initPos();
    }

    private void initPos() {
        mDataBinding.getRoot().postDelayed(() -> {
            ArrayList<ShowView.ImgItem> imgItems = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    ShowView.ImgItem imgItem = new ShowView.ImgItem(i, j, R.drawable.img_1);
                    imgItems.add(imgItem);
                }
            }




            mDataBinding.sv.setAniList(imgItems, () -> Log.d(TAG, "onAniEnd: "), 500);

//                one = Math.min(mDataBinding.getRoot().getWidth(), mDataBinding.getRoot().getHeight()) / 10;
//
//                deltaX = (mDataBinding.getRoot().getWidth() - 2 * one) / 5.f;
//                deltaY = (mDataBinding.getRoot().getHeight() - 2 * one) / 5.f;
//                Point point = getPoint(0, 0);
//                Log.d(TAG, "run: " + point.toString());
//
//                ImageView imageView = new ImageView(mContext);
//                imageView.setBackgroundColor(Color.BLUE);
//                imageView.setImageResource(R.drawable.ic_app);
//                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(one, one);
//                layoutParams.width = one;
//                layoutParams.height = one;
//                imageView.setLayoutParams(layoutParams);
//                mDataBinding.root.addView(imageView);
//                showView(imageView, point.x, point.y);
        }, 200);
    }

    /**
     * 设置一个view在父view中的位置
     *
     * @param x 中心点坐标
     * @param y 中心点坐标
     */
    private void showView(@NotNull View view, int x, int y) {
        Log.d(TAG, "showView: " + view.getWidth());
        view.setLeft(x - view.getWidth() / 2);
        view.setRight(x + view.getWidth() / 2);
        view.setTop(y - view.getHeight() / 2);
        view.setBottom(y + view.getHeight() / 2);
    }

    private Point getPoint(int x, int y) {
        return new Point((int) (one + x * deltaX), (int) (one + y * deltaY));
    }

    private List<Item> getChildren() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("child name : 0", true));
        items.add(new Item("child name : 1", true));
        items.add(new Item("child name : 2", true));
        return items;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_4;
    }

    class Item {
        String name;
        List<Item> children;
        boolean isExpand;
        boolean isChild;

        public Item(String name) {
            this.name = name;
        }

        public Item(String name, boolean isChild) {
            this.name = name;
            this.isChild = isChild;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "name='" + name + '\'' +
                    ", child=" + children +
                    ", isExpand=" + isExpand +
                    '}';
        }
    }
}

package com.mio.basic;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 多item绑定 不带data binding 0817 暂不可用ww
 */
public abstract class BaseMultiRecyclerViewAdapter<O extends Object> extends RecyclerView.Adapter<BaseRecyclerViewAdapter.BaseViewHolder> {
    private static final String TAG = "BaseMultiRecyclerViewAd";
    protected List<O> mData = new ArrayList<>();

    protected List<Item> itemList = new ArrayList<>();

    public BaseMultiRecyclerViewAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override

    public BaseRecyclerViewAdapter.BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        for (Item item : itemList) {
            if (item.type == viewType) {
                ViewDataBinding inflate = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), item.layoutId, parent, false);
                return new BaseRecyclerViewAdapter.BaseViewHolder(inflate.getRoot());
            }
        }
        Log.e(TAG, "onCreateViewHolder: 请检查你的view type是否初始化了...");
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerViewAdapter.BaseViewHolder holder, int position) {
        bind(holder.itemView, mData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public static class Item {
        @LayoutRes
        private int layoutId;
        private int type;

        public Item(int layoutId, int type) {
            this.layoutId = layoutId;
            this.type = type;
        }

    }

    public void setData(@NonNull List<O> data) {
        if (mData != null) {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    protected abstract void bind(View itemView, O bean, int position);
}

package com.mio.basic;

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
 * 带有data binding的快速绑定
 * 仅支持一种item
 * 如果要多种item 需要自己重写onCreateViewHolder及bind方法 不推荐
 */
public abstract class BaseRecyclerViewAdapter<T extends ViewDataBinding, O extends Object>
        extends RecyclerView.Adapter<BaseRecyclerViewAdapter.BaseViewHolder> {

    protected List<O> mData = new ArrayList<>();
    @LayoutRes
    private int itemResId;

    public BaseRecyclerViewAdapter(int itemResId) {
        this.itemResId = itemResId;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        T binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), itemResId, parent, false);
        return new BaseViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        T binding = DataBindingUtil.findBinding(holder.itemView);
        bind(binding, mData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder {
        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setData(@NonNull List<O> data) {
        if (mData != null) {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    protected abstract void bind(T binding, O bean, int position);
}

package com.mio.basic;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import java.util.Objects;

/**
 * 默认的base fragment 仅带有data binding
 */
public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment {
    protected T mDataBinding;
    protected Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mContext = requireContext();
        defaultInit();
        initView();
        return mDataBinding.getRoot();
    }

    /**
     * 默认初始化的一些东西
     */
    private void defaultInit() {

    }

    /**
     * 在这里初始化你的布局
     * 界面的内容直接使用mDataBinding.tv_name的方式引用
     */
    protected abstract void initView();

    /**
     * 返回你的布局id，记得要按alt+回车转换成data binding layout
     *
     * @return 布局id··
     */
    @LayoutRes
    protected abstract int getLayoutId();
}

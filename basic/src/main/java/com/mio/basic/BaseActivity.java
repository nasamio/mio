package com.mio.basic;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import java.util.Objects;

/**
 * 默认的base activity 仅带有data binding
 * 选择 AppCompatActivity 继承的原因是因为自带lifecycle owner
 */
public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {
    protected T mDataBinding;
    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        mContext = this;
        defaultInit();
        initView();
    }

    /**
     * 默认初始化的一些东西
     */
    protected void defaultInit() {
        // 隐藏标题栏
        Objects.requireNonNull(getSupportActionBar()).hide();
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

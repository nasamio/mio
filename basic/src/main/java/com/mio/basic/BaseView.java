package com.mio.basic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

/**
 * 依托于布局绑定 带有lifecycle owner
 */
public abstract class BaseView<T extends ViewDataBinding> extends FrameLayout implements LifecycleOwner {
    protected T mDataBinding;
    protected Context mContext;
    private LifecycleRegistry lifecycle;

    public BaseView(@NonNull Context context) {
        this(context, null);
    }

    /**
     * 默认 xml 布局走这个构造器
     */
    public BaseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), getLayoutId(), this, false);
        addView(mDataBinding.getRoot());
        init(attrs);
        lifecycle = new LifecycleRegistry(this);
        lifecycle.setCurrentState(Lifecycle.State.CREATED);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        lifecycle.setCurrentState(Lifecycle.State.RESUMED);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        lifecycle.setCurrentState(Lifecycle.State.STARTED);
    }

    @NonNull
    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycle;
    }

    protected abstract void init(AttributeSet attrs);

    protected abstract int getLayoutId();
}

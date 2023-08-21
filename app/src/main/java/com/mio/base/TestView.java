package com.mio.base;

import android.content.Context;
import android.util.AttributeSet;

import com.mio.base.databinding.ViewTestBinding;
import com.mio.basic.BaseView;

import org.jetbrains.annotations.NotNull;

public class TestView extends BaseView<ViewTestBinding> {
    public TestView(@NotNull Context context, @NotNull AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(AttributeSet attrs) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_test;
    }
}

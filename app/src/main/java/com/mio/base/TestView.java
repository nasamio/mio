package com.mio.base;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mio.base.databinding.ViewTestBinding;
import com.mio.basic.BaseView;

public class TestView extends BaseView<ViewTestBinding> {
    public TestView(@NonNull Context context, @Nullable AttributeSet attrs) {
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

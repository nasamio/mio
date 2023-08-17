package com.mio.base;

import android.annotation.SuppressLint;

import com.mio.base.databinding.FragmentTestBinding;
import com.mio.basic.BaseFragment;

public class Test2Fragment extends BaseFragment<FragmentTestBinding> {
    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        mDataBinding.tvContent.setText("我是测试fragment2的内容...");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test;
    }
}

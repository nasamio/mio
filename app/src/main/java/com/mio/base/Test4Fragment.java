package com.mio.base;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mio.base.databinding.Fragment4Binding;
import com.mio.basic.BaseFragment;

public class Test4Fragment extends BaseFragment<Fragment4Binding> {
    @Override
    protected void initView() {
        mDataBinding.rvLeft.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_4;
    }
}

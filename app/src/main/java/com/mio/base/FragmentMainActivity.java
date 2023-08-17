package com.mio.base;

import com.mio.base.databinding.ActivityFragmentMainBinding;
import com.mio.basic.BaseFragmentActivity;

public class FragmentMainActivity extends BaseFragmentActivity<ActivityFragmentMainBinding> {
    @Override
    protected void initFragment() {
        addFragment(new TestFragment());
        addFragment(new Test2Fragment());
    }

    @Override
    protected void initView() {
        toFragment(new TestFragment());

        mDataBinding.btn1.setOnClickListener(v ->
                toFragmentWithTransition(new TestFragment(),
                        new FragmentAnimation().setTransition(FragmentAnimation.LEFT))
        );
        mDataBinding.btn2.setOnClickListener(v ->
                toFragmentWithTransition(new Test2Fragment(),
                        new FragmentAnimation().setTransition(FragmentAnimation.RIGHT))
        );
        mDataBinding.btn3.setOnClickListener(v ->
                toFragmentWithTransition(new Test3Fragment(),
                        new FragmentAnimation().setTransition(FragmentAnimation.RIGHT))
        );

    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fragment_main;
    }
}

package com.mio.basic;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.mio.utils.FragmentUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class BaseFragmentActivity<T extends ViewDataBinding> extends AppCompatActivity {

    private int containerId;
    private Map<String, Fragment> fragmentMap = new HashMap<>();
    private Fragment currentFragment;

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

    protected void defaultInit() {
        getSupportActionBar().hide();

        containerId = getFragmentContainerId();

        initFragment();
    }

    /**
     * fragment跳转
     *
     * @param toFragment 要跳转的fragment
     */
    protected void toFragment(@NonNull Fragment toFragment) {
        toFragment(Objects.requireNonNull(toFragment.getClass().getCanonicalName()), toFragment);
    }

    /**
     * 带有动画的跳转
     *
     * @param fragment 目标fragment
     * @param enterAni 进入的fragment的动画
     * @param exitAni  出去的fragment的动画
     */
    protected void toFragmentWithAnimation(@NonNull Fragment fragment, int enterAni, int exitAni) {
        FragmentUtils.setEnterAni(enterAni);
        FragmentUtils.setExitAni(exitAni);

        toFragment(fragment);

        FragmentUtils.setEnterAni(-1);
        FragmentUtils.setExitAni(-1);
    }

    /**
     * 带有动画的跳转
     *
     * @param fragment  目标fragment
     * @param animation 动画
     */
    protected void toFragmentWithTransition(@NonNull Fragment fragment, FragmentAnimation animation) {
        // todo 补充其他类型的 0817 只考虑平移
        switch (animation.transition) {
            case FragmentAnimation.LEFT:
                toFragmentWithAnimation(fragment, R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case FragmentAnimation.RIGHT:
                toFragmentWithAnimation(fragment, R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case FragmentAnimation.TOP:
                toFragmentWithAnimation(fragment, R.anim.slide_in_top, R.anim.slide_out_bottom);
                break;
            case FragmentAnimation.BOTTOM:
                toFragmentWithAnimation(fragment, R.anim.slide_in_bottom, R.anim.slide_out_top);
                break;
        }
    }

    protected void toFragment(@NonNull String tag, @NonNull Fragment toFragment) {
        if (currentFragment == toFragment) return;

        if (fragmentMap.containsKey(tag)) {
            toFragment(tag);
        } else {
            FragmentUtils.showFragment(this, toFragment, currentFragment, containerId);
            addFragment(tag, toFragment);
            currentFragment = toFragment;
        }
    }

    private void toFragment(@NonNull String tag) {
        Fragment toFragment = fragmentMap.get(tag);
        if (toFragment != null && toFragment != currentFragment) {
            FragmentUtils.showFragment(this, toFragment, currentFragment, containerId);
            currentFragment = toFragment;
        }
    }

    /**
     * 插入fragment 自动生成tag
     */
    protected void addFragment(@NonNull Fragment fragment) {
        addFragment(Objects.requireNonNull(fragment.getClass().getCanonicalName()), fragment);
    }

    /**
     * 自定义 tag 插入
     *
     * @param tag      fragment唯一tag·
     * @param fragment fragment
     */
    protected void addFragment(@NonNull String tag, @NonNull Fragment fragment) {
        fragmentMap.put(tag, fragment);
    }

    /**
     * 静态初始化fragment 调用addFragment方法 会自动进行fragment管理
     */
    protected abstract void initFragment();

    /**
     * 在这里初始化你的布局
     * 界面的内容直接使用mDataBinding.tv_name的方式引用
     */
    protected abstract void initView();

    /**
     * @return fragment容器id
     */
    protected abstract int getFragmentContainerId();

    /**
     * 返回你的布局id，记得要按alt+回车转换成data binding layout
     *
     * @return 布局id··
     */
    @LayoutRes
    protected abstract int getLayoutId();

    public static class FragmentAnimation {
        public static final int INLAID = -1;

        public static final int LEFT = 0;
        public static final int TOP = 1;
        public static final int RIGHT = 2;
        public static final int BOTTOM = 3;

        private int transition = INLAID;

        /**
         * @param transition 目标fragment在现在的什么位置
         */
        public FragmentAnimation setTransition(int transition) {
            this.transition = transition;
            return this;
        }
    }
}

package com.mio.basic;

import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mio.basic.databinding.ActivityBaseBottomBinding;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseBottomActivity extends BaseActivity<ActivityBaseBottomBinding>
        implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {
    private static final String TAG = "BottomActivity";
    private List<BottomItem> list = new ArrayList<>();
    private BottomAdapter adapter;

    @Override
    protected void initView() {
        Log.d(TAG, "initView: ");

        adapter = new BottomAdapter(getSupportFragmentManager());
        initFragmentList();
        mDataBinding.vp.setAdapter(adapter);
        mDataBinding.vp.addOnPageChangeListener(this);
        mDataBinding.bnv.inflateMenu(getMenuRes());
        mDataBinding.bnv.setOnNavigationItemSelectedListener(this);
    }

    public void addFragment(int id, @NonNull Fragment fragment) {
        list.add(new BottomItem(id, fragment));
    }

    /**
     * 调用 addFragment 方法来增加fragment按照从左往右的顺序
     */
    protected abstract void initFragmentList();

    /**
     * 返回底部菜单
     */
    protected abstract int getMenuRes();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected: " + item.getItemId());
        for (int i = 0; i < list.size(); i++) {
            BottomItem bottomItem = list.get(i);
            if (bottomItem.id == item.getItemId()) {
                onSwiftFragment(i);
                break;
            }
        }
        return true;
    }

    /**
     * 可以通过重写该方法 来改变vp的切换是否带有动画
     */
    protected void onSwiftFragment(int index) {
        mDataBinding.vp.setCurrentItem(index);
    }

    @Override
    public void onPageSelected(int position) {
        mDataBinding.bnv.getMenu().getItem(position).setChecked(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base_bottom;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class BottomItem {
        int id;
        Fragment fragment;

        public BottomItem(int id, Fragment fragment) {
            this.id = id;
            this.fragment = fragment;
        }
    }

    class BottomAdapter extends FragmentStatePagerAdapter {
        public BottomAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position).fragment;
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }

}

package com.mio.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ToxicBakery.viewpager.transforms.CubeInTransformer;
import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.InputConfirmPopupView;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.mio.base.databinding.ActivityBottomBinding;
import com.mio.basic.BaseActivity;
import com.mio.basic.BaseBottomActivity;

import java.util.ArrayList;
import java.util.List;

public class BottomActivity extends BaseBottomActivity {
    private static final String TAG = "BottomActivity";

    @Override
    protected void initFragmentList() {
        addFragment(R.id.item1, new TestFragment());
        addFragment(R.id.item2, new Test2Fragment());
        addFragment(R.id.item3, new Test3Fragment());
        addFragment(R.id.item4, new Test4Fragment());


        mDataBinding.vp
                .setPageTransformer(true, new CubeOutTransformer());

        checkPer();
    }

    private void checkPer() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getPackageName(),
                Context.MODE_PRIVATE);
        boolean isActive = sharedPreferences.getBoolean("isActive", false);
        if (!isActive) {
            InputConfirmPopupView inputConfirmPopupView = new XPopup.Builder(mContext)
                    .asInputConfirm("请输入激活码", "你的设备需要激活", null, "激活码", new OnInputConfirmListener() {
                        @Override
                        public void onConfirm(String text) {

                        }
                    });
            inputConfirmPopupView.popupInfo.isDismissOnTouchOutside = false;
            inputConfirmPopupView.show();
        }

    }


    private SharedPreferences.Editor getEditor() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getPackageName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        return edit;
    }

    @Override
    protected int getMenuRes() {
        return R.menu.menu_bottom_navigation;
    }
}
package com.mio.base;

import android.annotation.SuppressLint;
import android.util.Log;

import com.mio.base.databinding.FragmentTestBinding;
import com.mio.basic.BaseFragment;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

public class Test2Fragment extends BaseFragment<FragmentTestBinding> {
    private static final String TAG = "Test2Fragment";

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        Log.d(TAG, "initView: " + mDataBinding.bmb.getButtonPlaceEnum().buttonNumber());

//        mDataBinding.bmb.setButtonEnum(ButtonEnum.SimpleCircle);
//        mDataBinding.bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_1);
//        mDataBinding.bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_3_3);
//        for (int i = 0; i < mDataBinding.bmb.getButtonPlaceEnum().buttonNumber(); i++) {
//            mDataBinding.bmb.addBuilder(new SimpleCircleButton.Builder()
//                    .normalImageRes(R.drawable.img_1)
//            );
//        }

        BoomMenuButton bmb = mDataBinding.bmb;
        bmb.setButtonEnum(ButtonEnum.SimpleCircle);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_1);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_1);
        SimpleCircleButton.Builder builder = new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.img_1);
        bmb.addBuilder(builder);

        mDataBinding.getRoot().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: enu : " + mDataBinding.bmb.getButtonEnum());


            }
        }, 100);
        Log.d(TAG, "initView: builder size : " + mDataBinding.bmb.getBuilders().size());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test;
    }
}

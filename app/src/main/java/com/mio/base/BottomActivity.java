package com.mio.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.InputConfirmPopupView;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.mio.base.bean.User;
import com.mio.base.manager.RetrofitServiceManager;
import com.mio.basic.BaseBottomActivity;
//import com.yanzhenjie.andserver.AndServer;
//import com.yanzhenjie.andserver.Server;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observer;

public class BottomActivity extends BaseBottomActivity {
    private static final String TAG = "BottomActivity";
//    private Server server;

    @Override
    protected void initFragmentList() {
        addFragment(R.id.item1, new TestFragment());
        addFragment(R.id.item2, new Test2Fragment());
        addFragment(R.id.item3, new Test3Fragment());
        addFragment(R.id.item4, new Test4Fragment());


//        mDataBinding.vp
//                .setPageTransformer(true, new CubeOutTransformer());

//        checkPer();

        testLocal();
        testWeb();
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

    private void testWeb() {
//        server = AndServer.webServer(this)
//                .port(8080)
//                .timeout(1, TimeUnit.SECONDS)
//                .build();
//        server.startup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        server.shutdown();
    }


    private void testLocal() {
        RetrofitServiceManager.getInstance().getAllUser(new Observer<List<User>>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.toString());
            }

            @Override
            public void onNext(List<User> users) {
                for (User user : users) {
                    Log.e(TAG, "onNext: " + user.toString());
                }
            }
        });
    }

}
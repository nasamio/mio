package com.mio.game.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.mio.basic.BaseView;
import com.mio.game.R;
import com.mio.game.bean.Direction;
import com.mio.game.databinding.LayoutElsfkGameHolderBinding;
import com.mio.game.databinding.LayoutGameHolderBinding;

public class ElsfkGameHolder extends BaseView<LayoutElsfkGameHolderBinding> {
    private static final String TAG = "GameHolder";

    public ElsfkGameHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(AttributeSet attrs) {
        mDataBinding.btnTop.setOnClickListener(
                v -> mDataBinding.tcs.handleKey(Direction.TOP));
        mDataBinding.btnLeft.setOnClickListener(
                v -> mDataBinding.tcs.handleKey(Direction.LEFT));
        mDataBinding.btnRight.setOnClickListener(
                v -> mDataBinding.tcs.handleKey(Direction.RIGHT));
        mDataBinding.btnBottom.setOnClickListener(
                v -> mDataBinding.tcs.handleKey(Direction.BOTTOM));
        mDataBinding.btnRestart.setOnClickListener(
                v -> mDataBinding.tcs.restart());

        mDataBinding.tcs.setCallback(new GameCallback() {
            @Override
            public void onTitle(String text) {
                post(() -> mDataBinding.title.setText(text));
            }

            @Override
            public void onScore(int score) {
                post(() -> mDataBinding.score.setText("分数 : " + score));
            }

            @Override
            public void onGameStart() {
                Log.d(TAG, "onGameStart: ");
            }

            @Override
            public void onGameUpdate() {
                Log.d(TAG, "onGameUpdate: ");
            }

            @Override
            public void onGameOver() {
                Log.d(TAG, "onGameOver: ");
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_elsfk_game_holder;
    }
}

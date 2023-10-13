package com.mio.game.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mio.basic.BaseView;
import com.mio.game.R;
import com.mio.game.bean.Direction;
import com.mio.game.databinding.LayoutGameHolderBinding;

public class TcsGameHolder extends BaseView<LayoutGameHolderBinding> {
    private static final String TAG = "GameHolder";

    public TcsGameHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(AttributeSet attrs) {
        mDataBinding.btnTop.setOnClickListener(
                v -> mDataBinding.tcs.handleKeyDown(Direction.TOP));
        mDataBinding.btnLeft.setOnClickListener(
                v -> mDataBinding.tcs.handleKeyDown(Direction.LEFT));
        mDataBinding.btnRight.setOnClickListener(
                v -> mDataBinding.tcs.handleKeyDown(Direction.RIGHT));
        mDataBinding.btnBottom.setOnClickListener(
                v -> mDataBinding.tcs.handleKeyDown(Direction.BOTTOM));
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
        return R.layout.layout_game_holder;
    }
}

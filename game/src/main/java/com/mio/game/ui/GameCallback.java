package com.mio.game.ui;

public interface GameCallback {
    void onTitle(String text);

    void onScore(int score);

    default void onGameStart() {
    }

    default void onGameUpdate() {
    }

    default void onGameOver() {
    }
}

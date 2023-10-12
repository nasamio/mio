package com.mio.base;

import android.util.Log;

public class Iphone {
    private IMusic music;
    private ICamera camera;

    public Iphone(IMusic music, ICamera camera) {
        this.music = music;
        this.camera = camera;
    }

    void playMusic() {
        music.play();
    }

    void takePhoto() {
        camera.takePhoto();
        camera.takeVideo();
    }
}

class Music implements IMusic {
    private static final String TAG = "Music";

    @Override
    public void play() {
        Log.d(TAG, "play: ");

    }
}

class Camera implements ICamera {
    static final String TAG = "Camera";

    @Override
    public void takePhoto() {
        Log.d(TAG, "takePhoto: ");
    }

//    @Override
//    public void takeVideo() {
//        Log.d(TAG, "takeVideo2: ");
//    }
}

interface IMusic {
    void play();
}

interface ICamera {
    void takePhoto();

    default void takeVideo() {
        Log.d(Camera.TAG, "takeVideo1: ");
    }
}

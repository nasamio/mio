package com.mio.base;

import android.util.Log;

public abstract class Action {
    abstract String sayHello();
}

class PersonAction extends Action {

    @Override
    String sayHello() {
        return "hello";
    }
}

class DogAction extends Action {

    @Override
    String sayHello() {
        return "汪汪";
    }
}

class Animal {
    private static final String TAG = "Animal";
    private Action action;

    public Animal(Action action) {
        this.action = action;
    }

    public void hello() {
        if (action == null) {
            action = new PersonAction();
        }
        Log.d(TAG, "hello: " + action.sayHello());
    }
}

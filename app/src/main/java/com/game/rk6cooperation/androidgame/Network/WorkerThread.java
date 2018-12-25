package com.game.rk6cooperation.androidgame.Network;

import android.os.Handler;
import android.os.HandlerThread;

class WorkerThread extends HandlerThread {

    private Handler mWorkerHandler;

    public WorkerThread(String name) {
        super(name);
    }

    public void postTask(Runnable task) {
        mWorkerHandler.post(task);
    }

    public void prepareHandler() {
        mWorkerHandler = new Handler(getLooper());
    }
}
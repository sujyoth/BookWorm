package com.ash.bookworm.Utilities;

import android.os.Handler;

/*
private Debounce mDebounce = new Debounce(250);
...
mDebounce.attempt(this::someMethod);
*/

public class Debounce {

    private Handler mHandler = new Handler();
    private long mInterval;

    public Debounce(long interval) {
        mInterval = interval;
    }

    public void attempt(Runnable runnable) {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(runnable, mInterval);
    }

}

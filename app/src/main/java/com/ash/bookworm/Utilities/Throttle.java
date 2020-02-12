package com.ash.bookworm.Utilities;

import android.view.animation.AnimationUtils;

/*
private Throttle mThrottle = new Throttle(250);
...
mThrottle.attempt(this::someMethod);
*/

public class Throttle {

    private long mLastFiredTimestamp;
    private long mInterval;

    public Throttle(long interval) {
        mInterval = interval;
    }

    public void attempt(Runnable runnable) {
        if (hasSatisfiedInterval()) {
            runnable.run();
            mLastFiredTimestamp = getNow();
        }
    }

    private boolean hasSatisfiedInterval() {
        long elapsed = getNow() - mLastFiredTimestamp;
        return elapsed >= mInterval;
    }

    private long getNow() {
        return AnimationUtils.currentAnimationTimeMillis();
    }

}

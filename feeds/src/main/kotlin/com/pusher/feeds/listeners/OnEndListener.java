package com.pusher.feeds.listeners;

import android.support.annotation.Nullable;

import elements.EOSEvent;

public interface OnEndListener {
    void onEnd(@Nullable EOSEvent endEvent);
}

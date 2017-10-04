package com.pusher.feeds.listeners;

import android.support.annotation.NonNull;

import com.pusher.feeds.FeedItem;

public interface OnItemListener {
    void onItem(@NonNull FeedItem item);
}

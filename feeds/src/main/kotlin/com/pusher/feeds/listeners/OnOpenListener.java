package com.pusher.feeds.listeners;

import android.support.annotation.NonNull;
import java.util.List;
import java.util.Map;

public interface OnOpenListener {
    void onOpen(@NonNull Map<String, List<String>> headers);
}


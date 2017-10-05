package com.pusher.feeds.listeners;

import com.pusher.feeds.Feeds;

import java.util.List;

public interface FeedsReceivedListener {
    void onFeedsReceived(List<Feeds.FeedsListItem> feeds);
}

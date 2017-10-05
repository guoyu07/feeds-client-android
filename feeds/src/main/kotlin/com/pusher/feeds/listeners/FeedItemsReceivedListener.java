package com.pusher.feeds.listeners;

import com.pusher.feeds.FeedPaginationResponse;

public interface FeedItemsReceivedListener {
    void onItems(FeedPaginationResponse items);
}

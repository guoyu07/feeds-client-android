# Feeds Client Android 

🚨 Note - this project is in early preview stage. Use at your own risk. 🚨 

The Android client for Pusher Feeds. If you aren't already here, you can
find the source [on Github](https://github.com/pusher/feeds-client-android).

For more information on the Feeds service, [see
here](https://pusher.com/feeds). For full documentation, [see
here](https://docs.pusher.com/feeds)

The SDK is written in Kotlin, but aimed to be as Java-friendly as possible. Most code sample are written in Kotlin for clarity.

## Features & Usage

- subscribe
- list feeds
- get history

### Installation & Setup

Currently released as a snapshot. 
Add the client from the Sonatype Snapshots repository.
 
In your project-level `build.gradle` add this as 
 
```groovy
allprojects {
    repositories {
        google()
        jcenter()
        maven{
            url "https://oss.sonatype.org/content/repositories/snapshots"
        }
    }
}
```

Then add the library to your module's dependencies.

```groovy
implementation 'com.pusher:feeds-client-android:0.0.1-SNAPSHOT'
```

- Ensure you have these permissions in your app

```xml
  <uses-permission android:name="android.permission.INTERNET"/>
  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

### Instantiate a Feeds object

- In Kotlin - using named parameters in the `Feeds` constructor

```kotlin
    val feeds = Feeds(
                instanceLocater = INSTANCE_LOCATOR,
                context = applicationContext,
                authEndpoint = AUTH_ENDPONT,
                logLevel = LogLevel.VERBOSE
        )
```

- Java - using Feeds.Builder

```java
    Feeds feeds = new Feeds.Builder()
        .setInstanceLocator(INSTANCE_LOCATOR)
        .setContext(getApplicationContext())
        .setAuthEndpoint(AUTH_ENDPONT) 
        .setLogLevel(LogLevel.VERBOSE)
        .build();
```

The mandatory parameters are the Instance Locater - you get it from the Feeds Dashboard, and the Application Context.
Instance Locater configures the SDK, and the context hooks into the broadcasts to re-establish a subscription.

Auth Endpoint is the location of your authenticator service - it is used for `list`, and subscribing to private feeds. 
For more information on private feeds and authentication visit https://docs.pusher.com/feeds/concepts/private-feeds/
 

### Get a reference to a feed

```kotlin
val feed: Feed = feeds.feed("my-feed")
```

Feeds are public by default if their name doesn't start with `private-`.

### Subscribe to the feed

```kotlin

publicFeed.subscribe(
                    FeedSubscriptionListeners(
                            onOpen = OnOpenListener{ Log.d(TAG, "onOpen") },
                            onItem = OnItemListener {  feedItem -> Log.d(TAG, "onItem ${feedItem.data}") },
                            onError = OnErrorListener { error -> Log.d(TAG, "onError $error") },
                            onEnd = OnEndListener { endEvent -> Log.d(TAG, "onEnd") },
                            onRetrying = OnRetryingListener { Log.d(TAG, "onRetrying") },
                            onSubscribed = OnSubscribedListener { Log.d(TAG, "onSubscribed") }
                    )
            )

```

The only mandatory listeners are `onOpen` and `onItem`. Others are optional.

A `FeedItem` that is received by the `OnItemListener` is of this format:

```kotlin
data class FeedItem(
    val id: String, 
    val created: Long, 
    val data: JsonElement)
```

Where `data` is your published payload, `id` is unique for each item.

### Paginate though historic items in the feed

```kotlin
            paginatePublicButton.setOnClickListener {
                        publicFeed.paginate(
                                onSuccess = FeedItemsReceivedListener { items -> Log.d(TAG, "onItems $items") },
                                onError = OnErrorListener { error -> Log.d(TAG, "Error $error") },
                                limit = 20, //Number of items to return
                                cursor = "MOST_RECENT_ITEM_ID" //Overrides the limit 
                        )
                    }
```

The response is in the format of `FeedPaginationResponse`:

```kotlin
data class FeedPaginationResponse(
        val items: List<FeedItem>,
        val nextCursor: String?,
        val remaining: Int
)
```

Returned `items` length can be anywhere from 0 to `limit` as you specified above. 
`nextCursor` points to the next known item in the feed, if it exists, and `remaining` tells you 
how many items are remaining in this feed - older than the earliest received by this call.

### List all feeds

```kotlin
feeds.list(
                    onSuccess = FeedsReceivedListener { feeds -> Log.d(TAG, "Feeds received $feeds") },
                    onFailure = OnErrorListener { error -> Log.d(TAG, "Error $error") },
                    prefix = "private" //pass the optional prefix to filter the feeds. Optional
            )
```

The response is a `List` of `FeedListItem` objects:

```kotlin
data class FeedsListItem(
    val feedId: String, //unique feed identifier
    val length: Int) //number of items in the feed
```


# License

This SDK is released under the MIT license. See LICENSE for details.

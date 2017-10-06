# Feeds Client Android 

🚨 Note - this project is in early preview stage. Use at your own risk. 🚨 

The Android client for Pusher Feeds. If you aren't already here, you can
find the source [on Github](https://github.com/pusher/feeds-client-android).

For more information on the Feeds service, [see
here](https://pusher.com/feeds). For full documentation, [see
here](https://docs.pusher.com/feeds)

The SDK is written in Kotlin, but aimed to be as Java-friendly as possible. 

## Features & Usage

- subscribe
- list feeds
- get history

### Installation & Setup

- Add the client from Maven Central 

```groovy
implementation 'com.pusher:feeds-client-android:0.0.1'
```

Or clone this repository and include in your project.

- Ensure you have these permissions in your app

```xml
  <uses-permission android:name="android.permission.INTERNET"/>
  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

### Instantiate a Feeds object

- In Kotlin - using named parameters in the `Feeds` constructor

```kotlin
    val feeds = Feeds(
                instanceId = INSTANCE_ID,
                context = applicationContext,
                authEndpoint = AUTH_ENDPONT,
                logLevel = LogLevel.VERBOSE
        )
```

- Java - using Feeds.Builder

```java
    Feeds feeds = new Feeds.Builder()
        .setInstanceId(INSTANCE_ID)
        .setContext(getApplicationContext())
        .setAuthEndpoint(AUTH_ENDPONT)
        .setLogLevel(LogLevel.VERBOSE)
        .build();
```

The mandatory parameters are the Instance ID - you get it from the Feeds Dashboard, and the Application Context. 
Instance ID configures the SDK, and the context hooks into the broadcasts to re-establish a subscription.

Auth Endpoint is the location of your authenticator service - it is used for `list`, and subscribing to private Feeds.

### Get a reference to a feed

```kotlin
val feed: Feed = feeds.feed("my-feed")
```

Feeds are public by default if their name doesn't start with `private-`.

### Subscribe to the feed

```kotlin

publicFeed.subscribe(
                    FeedSubscriptionListeners(
                            onOpen = OnOpenListener{ Timber.d("onOpen") },
                            onItem = OnItemListener {  feedItem -> Timber.d("onItem ${feedItem.data}") },
                            onError = OnErrorListener { error -> Timber.d("onError $error") },
                            onEnd = OnEndListener { endEvent -> Timber.d("onEnd") },
                            onRetrying = OnRetryingListener { Timber.d("onRetrying") },
                            onSubscribed = OnSubscribedListener { Timber.d("onSubscribed") }
                    )
            )

```

The only mandatory listeners are `onOpen` and `onItem`.

### Paginate though historic items in the feed

```kotlin
            paginatePublicButton.setOnClickListener {
                        publicFeed.paginate(
                                onSuccess = FeedItemsReceivedListener { items -> Timber.d("onItems $items") },
                                onError = OnErrorListener { error -> Timber.d("Error $error") },
                                limit = 20, //Number of items to return
                                cursor = "MOST_RECENT_ITEM_ID" //Overrides the limit 
                        )
                    }
```

### List all feeds

```kotlin
feeds.list(
                    onSuccess = FeedsReceivedListener { feeds -> Timber.d("Feeds received $feeds") },
                    onFailure = OnErrorListener { error -> Timber.d("Error $error") },
                    prefix = "private" //pass the optional prefix to filter the feeds. Optional
            )
```

# License

This SDK is released under the MIT license. See LICENSE for details.

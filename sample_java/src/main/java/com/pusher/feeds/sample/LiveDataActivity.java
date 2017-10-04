package com.pusher.feeds.sample;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LiveDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_data);

        final TextView newsTitle = findViewById(R.id.newsTitle_text);
        final TextView newsStory = findViewById(R.id.newsStory_text);

        final FeedLiveModel viewModel = ViewModelProviders.of(this).get(FeedLiveModel.class);

        final Observer<NewsItem> newsItemObserver = new Observer<NewsItem>() {
            @Override
            public void onChanged(@Nullable NewsItem newsItem) {

                newsTitle.setText(newsItem.title);
                newsStory.setText(newsItem.story);
            }
        };

        viewModel.getCurrentNewsItem().observe(this, newsItemObserver);
    }
}

class FeedLiveModel extends ViewModel {

    private MutableLiveData<NewsItem> currentNewsItem;

    public MutableLiveData<NewsItem> getCurrentNewsItem(){
        if(currentNewsItem == null){
            currentNewsItem = new MutableLiveData<>();
        }
        return currentNewsItem;
    }



}

class NewsLiveData extends LiveData<NewsItem> {


    @Override
    protected void postValue(NewsItem value) {
        super.postValue(value);
    }

    @Override
    protected void setValue(NewsItem value) {
        super.setValue(value);
    }

    @Override
    protected void onActive() {
        super.onActive();
    }

    @Override
    protected void onInactive() {
        super.onInactive();
    }
}

class NewsItem {
    public final String title;
    public final String story;

    NewsItem(String title, String story){
        this.title = title;
        this.story = story;
    }
}

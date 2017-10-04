package com.pusher.feeds.sample;

import android.arch.lifecycle.ViewModel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LiveDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_data);
    }
}

class FeedLiveModel extends ViewModel {


}

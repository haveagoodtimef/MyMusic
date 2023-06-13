package com.fhz.music_home;

import android.content.ComponentName;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fhz.music_lib_player.service.MyMusicMediaService;

import java.util.List;

@Deprecated
/**
 * MediaBrowser 用于链接服务
 * 创建MediaController
 * 添加回调
 */
public class MainActivity2 extends AppCompatActivity {

    private MediaBrowser mMediaBrowser;

    private MediaController mController;
    private static final String TAG = "MainActivity2";
    private Button mPlay;
    private Button mPause;
    private Button mRestart;
    private Button mStop;
    private Button mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();


        mMediaBrowser = new MediaBrowser(this,
                new ComponentName(this, MyMusicMediaService.class),
                mConnectionCallback,
                null
        );
        mMediaBrowser.connect();

        mPlay.setOnClickListener(v -> {
            mController.getTransportControls().play();
        });
    }

    private MediaBrowser.ConnectionCallback mConnectionCallback = new MediaBrowser.ConnectionCallback() {
        @Override
        public void onConnected() {
            super.onConnected();
            Log.d(TAG, "onConnected: 链接成功");
            if (mMediaBrowser.isConnected()) {
                String root = mMediaBrowser.getRoot();
                mMediaBrowser.unsubscribe(root);
                mMediaBrowser.subscribe(root, subScripintCallBack);
                mController = new MediaController(MainActivity2.this, mMediaBrowser.getSessionToken());
                mController.registerCallback(new MediaController.Callback() {
                    @Override
                    public void onPlaybackStateChanged(@Nullable PlaybackState state) {
                        super.onPlaybackStateChanged(state);
                        //根据状态的改变.来修改ui界面
                        switch (state.getState()) {
                            case PlaybackState.STATE_PAUSED:
                                Log.d(TAG, "onPlaybackStateChanged: 暂停了");
                                break;
                        }
                    }
                });
            }
        }

        private MediaBrowser.SubscriptionCallback subScripintCallBack = new MediaBrowser.SubscriptionCallback() {
            @Override
            public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowser.MediaItem> children) {
                super.onChildrenLoaded(parentId, children);
                Log.d(TAG, "onChildrenLoaded: " + children.size());
                for (int i = 0; i < children.size(); i++) {
                    Log.d(TAG, "onChildrenLoaded: " + children.get(i).getDescription());
                }
            }


            @Override
            public void onError(@NonNull String parentId) {
                super.onError(parentId);
            }
        };

        @Override
        public void onConnectionFailed() {
            super.onConnectionFailed();
            Log.d(TAG, "onConnectionFailed: 链接失败");
        }

        @Override
        public void onConnectionSuspended() {
            super.onConnectionSuspended();
            Log.d(TAG, "onConnectionSuspended: 链接断开");
        }
    };

    private void initView() {
        mPlay = (Button) findViewById(R.id.play);
        mPause = (Button) findViewById(R.id.pause);
        mRestart = (Button) findViewById(R.id.restart);
        mStop = (Button) findViewById(R.id.stop);
        mNext = (Button) findViewById(R.id.next);
    }
}
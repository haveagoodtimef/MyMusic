/*
 * Copyright 2018 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fhz.music_lib_player.connect

import android.content.ComponentName
import android.content.Context
import android.media.session.PlaybackState
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.media.MediaBrowserServiceCompat
import javax.inject.Inject

/**
 * 提供给各个组件,用来控制播放的逻辑的.
 * 此类是一个单例类,每一个fragment都可以得到.
 * 在viewmodel中控制业务逻辑,在fragment中更新界面
 */
private const val TAG = "MusicServiceConnection"
@Deprecated("没有用上")
class MusicServiceConnection @Inject constructor (context: Context, serviceComponent: ComponentName) {
    val isConnected = MutableLiveData<Boolean>()
        .apply { postValue(false) }
    val networkFailure = MutableLiveData<Boolean>()
        .apply { postValue(false) }

    val mediaItemList :ArrayList<MediaBrowserCompat.MediaItem> = ArrayList()

    val rootMediaId: String get() = mediaBrowser.root

    val playbackState = MutableLiveData<PlaybackStateCompat>()
        .apply { postValue(EMPTY_PLAYBACK_STATE) }
    val nowPlaying = MutableLiveData<MediaMetadataCompat>()
        .apply { postValue(NOTHING_PLAYING) }

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(
        context,
        serviceComponent,
        mediaBrowserConnectionCallback, null
    ).apply { connect() }
    private lateinit var mediaController: MediaControllerCompat

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }

    fun sendCommand(command: String, parameters: Bundle?) =
        sendCommand(command, parameters) { _, _ -> }

    fun sendCommand(
        command: String,
        parameters: Bundle?,
        resultCallback: ((Int, Bundle?) -> Unit)
    ) = if (mediaBrowser.isConnected) {
        mediaController.sendCommand(command, parameters, object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                resultCallback(resultCode, resultData)
            }
        })
        true
    } else {
        false
    }

    private inner class MediaBrowserConnectionCallback(private val context: Context) :
        MediaBrowserCompat.ConnectionCallback() {
        /**
         * Invoked after [MediaBrowserCompat.connect] when the request has successfully
         * completed.
         */
        override fun onConnected() {
            // Get a MediaController for the MediaSession.
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            Log.i(TAG, "onConnected: 链接成功")

            //必须在确保连接成功的前提下执行订阅的操作
            if (mediaBrowser.isConnected) {
                /**
                 * mediaId即为MediaBrowserService.onGetRoot的返回值
                 * 若Service允许客户端连接，则返回结果不为null，其值为数据内容层次结构的根ID
                 * 若拒绝连接，则返回null
                 */
                val mediaId = mediaBrowser.root

                /**
                 * Browser通过订阅的方式向Service请求数据，发起订阅请求需要两个参数，其一为mediaId
                 * 而如果该mediaId已经被其他Browser实例订阅，则需要在订阅之前取消mediaId的订阅者
                 * 虽然订阅一个 已被订阅的mediaId 时会取代原Browser的订阅回调，但却无法触发onChildrenLoaded回调
                 */
                mediaBrowser.unsubscribe(mediaId)

                /**
                 * 之前说到订阅的方法还需要一个参数，即设置订阅回调SubscriptionCallback
                 * 当Service获取数据后会将数据发送回来，此时会触发SubscriptionCallback.onChildrenLoaded回调
                 */
                mediaBrowser.subscribe(mediaId, browserSubscriptionCallback)
                mediaBrowser.getItem(mediaId,itemCallback)

                /**
                 * 到Controller类
                 */
                mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken)
                mediaController.registerCallback(controllerCallback())
            }
//            isConnected.postValue(true)
        }
        /**
         * 媒体浏览器订阅以后的回调
         * 也就是服务端返回数据的地方
         * 服务器返回的数据是MediaBrowser.MediaItem
         * 需要将MediaBrowser.MediaItem 转化成bean 放入到适配中.
         */
        private val browserSubscriptionCallback: MediaBrowserCompat.SubscriptionCallback = (object :
            MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                //children 即为Service的onLoadChildren发送回来的媒体数据集合
                Log.e(TAG,"onChildrenLoaded------")
                for (media:MediaBrowserCompat.MediaItem in children){
                    Log.e(TAG,media.description.title.toString())
                    Log.i(TAG, "onChildrenLoaded: ${media.mediaId}")
                    mediaItemList.add(media)
//                    mediaItemList.add(media)
//                    mAdapter.notifyDataSetChanged()
//                adapter.submitList(it.result.data)
                }
            }
        })

        /**
         * MediaBrowser,链接后的回调
         */
        private var browserConnectionCallback: MediaBrowserCompat.ConnectionCallback = (object:
            MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                super.onConnected()
                //链接成功自动回调
                Log.i(TAG, "onConnected: ")

            }

            override fun onConnectionSuspended() {
                super.onConnectionSuspended()
                Log.i(TAG, "onConnectionSuspended: ")
            }

            override fun onConnectionFailed() {
                super.onConnectionFailed()
                Log.i(TAG, "onConnectionFailed: ")
            }
        })

        private val itemCallback = object : MediaBrowserCompat.ItemCallback(){

            override fun onItemLoaded(item: MediaBrowserCompat.MediaItem?) {
                super.onItemLoaded(item)
                Log.i(TAG, "onItemLoaded: ${item?.description}")
            }
            override fun onError(mediaId: String) {
                super.onError(mediaId)
                Log.i(TAG, "onError: $mediaId")
            }

        }

        /**
         * Invoked when the client is disconnected from the media browser.
         */
        override fun onConnectionSuspended() {
            Log.i(TAG, "onConnectionSuspended: ")
//            isConnected.postValue(false)
        }

        /**
         * Invoked when the connection to the media browser failed.
         */
        override fun onConnectionFailed() {
//            isConnected.postValue(false)
            Log.i(TAG, "onConnectionFailed: ")
        }
    }


    /**
     * 媒体控制器控制播放过程中的回调接口，可以用来根据播放状态更新UI
     */
    fun controllerCallback() = (object : MediaControllerCompat.Callback() {
        /**
         * 音乐播放状态改变的回调
         * @param state
         */
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            Log.i(TAG, "onPlaybackStateChanged: ")
            when (state?.state){
                //初始状态
                PlaybackState.STATE_NONE -> {
                    //处理ui
//                    textTitle.setText("");
//                    btnPlay.setText("开始")
                    Log.d(TAG, "onPlaybackStateChanged: STATE_NONE")
                }
                PlaybackState.STATE_PAUSED -> {
//                    textTitle.setText("")
//                    btnPlay.setText("开始");
                    Log.d(TAG, "onPlaybackStateChanged: STATE_PAUSED")

                }
//
                PlaybackState.STATE_PLAYING -> {
                    Log.d(TAG, "onPlaybackStateChanged: STATE_PLAYING")

                }
            }
        }

        /**
         * 播放音乐改变的回调
         * @param metadata
         */
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
//            textTitle.setText(metadata.getDescription().getTitle());

        }

        //  //音频信息，音量
        override fun onAudioInfoChanged(info: MediaControllerCompat.PlaybackInfo?) {
            super.onAudioInfoChanged(info)
            Log.i(TAG, "onAudioInfoChanged: ")
        }

    })

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
//            playbackState.postValue(state ?: EMPTY_PLAYBACK_STATE)
            Log.i(TAG, "onPlaybackStateChanged: ")
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            Log.i(TAG, "onMetadataChanged: ")
            // When ExoPlayer stops we will receive a callback with "empty" metadata. This is a
            // metadata object which has been instantiated with default values. The default value
            // for media ID is null so we assume that if this value is null we are not playing
            // anything.
//            nowPlaying.postValue(
//                if (metadata?.id == null) {
//                    NOTHING_PLAYING
//                } else {
//                    metadata
//                }
//            )
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            Log.i(TAG, "onSessionEvent: ")
            when (event) {
//                NETWORK_FAILURE -> networkFailure.postValue(true)
            }
        }

        /**
         * Normally if a [MediaBrowserServiceCompat] drops its connection the callback comes via
         * [MediaControllerCompat.Callback] (here). But since other connection status events
         * are sent to [MediaBrowserCompat.ConnectionCallback], we catch the disconnect here and
         * send it on to the other callback.
         */
        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }

//    companion object {
//        // For Singleton instantiation.
//        @Volatile
//        private var instance: MusicServiceConnection? = null
//
//        fun getInstance(context: Context, serviceComponent: ComponentName) =
//            instance ?: synchronized(this) {
//                instance ?: MusicServiceConnection(context, serviceComponent)
//                    .also { instance = it }
//            }
//    }
}

@Suppress("PropertyName")
val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()

@Suppress("PropertyName")
val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()

package com.fhz.music_lib_player.service

import android.app.Service
import android.content.Intent
import android.media.MediaDescription
import android.media.MediaMetadata
import android.media.MediaSession2
import android.media.browse.MediaBrowser
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.media.session.PlaybackState.STATE_NONE
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.DocumentsContract.Root
import android.service.media.MediaBrowserService
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.fhz.music_lib_common.entity.Song
import com.fhz.music_lib_player.R
import com.fhz.mvvm.dataSource.local.HomeMusicSdCardDataSource1

@Deprecated("换成Compat")
/**
 * 浏览器服务，提供onGetRoot（控制客户端媒体浏览器的连接请求，
 * 通过返回值决定是否允许该客户端连接服务）和onLoadChildren（媒体浏览器向Service发送数据订阅时调用，
 * 一般在这执行异步获取数据的操作，最后将数据发送至媒体浏览器的回调接口中）这两个抽象方法
 *
 * 同时MediaBrowserService还作为承载媒体播放器（如MediaPlayer、ExoPlayer等）和MediaSession的容器
 */
class MyMusicMediaService : MediaBrowserService() {
    private val TAG = "MyMusicMediaService"

    /**
     * 媒体会话，即受控端，通过设置MediaSession.Callback回调来接收媒体控制器MediaController发送的指令，
     * 当收到指令时会触发Callback中各个指令对应的回调方法（回调方法中会执行播放器相应的操作，
     * 如播放、暂停等）。Session一般在Service.onCreate方法中创建，
     * 最后需调用setSessionToken方法设置用于和控制器配对的令牌并通知浏览器连接服务成功
     */
    private lateinit var mediaSession: MediaSession

    private  val FOLDERS_ID = "__FOLDERS__"
     val ARTISTS_ID = "__ARTISTS__"
     val ALBUMS_ID = "__ALBUMS__"
     val GENRES_ID = "__GENRES__"
     val ROOT_ID = "__ROOT__"
    /**
     * 播放器的状态
     */
    private lateinit var playbackState: PlaybackState

    //播放器
    private lateinit var exoPlayer: ExoPlayer

    private val callback = object : MediaSession.Callback() {

        override fun onPlay() {
            super.onPlay()
            // 处理 播放器 的播放逻辑。
            // 车载应用的话，别忘了处理音频焦点
            Log.i(TAG, "onPlay: ")
            if (!mediaSession.isActive) {
                mediaSession.isActive = true
            }

            //更新播放状态.
            val state = PlaybackState.Builder()
                .setState(
                    PlaybackState.STATE_PLAYING,1,1f
                )
                .build()
            mediaSession.setPlaybackState(state)
            exoPlayer.addMediaItem(MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }

        override fun onPause() {
            super.onPause()
            Log.i(TAG, "onPause: ")
            exoPlayer.pause()
        }

        override fun onPrepare() {
            super.onPrepare()
            Log.i(TAG, "onPrepare: ")
        }

        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
            super.onPlayFromUri(uri, extras)
            Log.i(TAG, "onPlayFromUri: ")
        }
    }

    override fun onCreate() {
        super.onCreate()
        //初始化播放器
        exoPlayer = ExoPlayer.Builder(this).build()
        //初始化session
        mediaSession = MediaSession(this,"music")
        playbackState = PlaybackState.Builder()
                            .setState(STATE_NONE,0,1.0f)
                            .build()
        mediaSession.setCallback(callback)
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession.setPlaybackState(playbackState)
//设置token后会触发MediaBrowser.ConnectionCallback的回调方法
        //表示MediaBrowser与MediaBrowserService连接成功
        sessionToken = mediaSession.sessionToken

    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        // 由 MediaBrowser.connect 触发，可以通过返回 null 拒绝客户端的连接。
        return BrowserRoot(ROOT_ID,null)
    }

        override fun onLoadChildren(
            parentId: String,
            result: Result<MutableList<MediaBrowser.MediaItem>>
        ) {
        //将信息从当前线程中移除，允许后续调用sendResult方法
        result.detach()

        //先取到取到歌曲,然后封装成MediaMetadata 在然后变成MediaItem

        val musicFromSD = HomeMusicSdCardDataSource1().getMusicFromSD(this)
        /**
         *  MediaMetadata 是具体的媒体信息
         *  比如从sd读来的音乐,那么音乐的专辑,歌手,时长,大小等数据.都可以有,相当于"游标"以前的用法
         *  封装以后变成了MediaItem
         *  val metadata:MediaMetadata  =  MediaMetadata.Builder()
         * .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, ""+ R.raw.night)
         * .putString(MediaMetadata.METADATA_KEY_TITLE, "黑夜")
         * .putLong(MediaMetadata.METADATA_KEY_DURATION,20000)
         * .putString(MediaMetadata.METADATA_KEY_ARTIST,"小明")
         * .build()
         */
        var mediaItems = ArrayList<MediaBrowser.MediaItem>()

        musicFromSD.forEach {
            val metadata:MediaMetadata  =  MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, it.id.toString())
                .putString(MediaMetadata.METADATA_KEY_TITLE, it.title)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, it.duration.toLong())
                .putString(MediaMetadata.METADATA_KEY_ARTIST,it.artist)
                .putString(MediaMetadata.METADATA_KEY_ALBUM,it.album.toString())
                .putString(MediaMetadata.METADATA_KEY_MEDIA_URI,it.path)
                .build()
//            mediaSession.setMetadata(metadata)
            mediaItems.add(createMediaItem(metadata))
        }

        result.sendResult(mediaItems)

       /** 备忘方法属性
       val build = MediaDescription.Builder()
            .setMediaId(MEDIA_ID_MUSICS_BY_GENRE)
            .setTitle(getString(R.string.browse_genres))
            .setIconUri(
                Uri.parse(
                    "android.resource://" +
                            "com.example.android.uamp/drawable/ic_by_genre"
                )
            )
            .setSubtitle(getString(R.string.browse_genre_subtitle))
            .build()*/



        //向Browser发送数据
//        result.sendResult(mediaItems)
//        result.sendResult(mediaItems)
        //由 MediaBrowser.subscribe 触发
//        when (parentId) {
//            ROOT_ID -> {
//                // 查询本地媒体库
//                result.sendResult(mediaItems)
//           }
//            FOLDERS_ID -> {
//            }
//            ALBUMS_ID -> {
//            }
//            ARTISTS_ID -> {
//            }
//            GENRES_ID -> {
//            }
//            else -> {
//            }
//        }
    }


    /**
     * 创建MediaItem
     */
    private fun createMediaItem(metadata: MediaMetadata): MediaBrowser.MediaItem {
        return MediaBrowser.MediaItem(metadata.description, MediaBrowser.MediaItem.FLAG_PLAYABLE)
    }

    override fun onLoadItem(itemId: String?, result: Result<MediaBrowser.MediaItem>?) {
        super.onLoadItem(itemId, result)
        Log.e("TAG", "onLoadItem: $itemId")
    }


}
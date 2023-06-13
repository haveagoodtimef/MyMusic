package com.fhz.music_lib_player.service

import android.app.Notification
import android.app.Notification.PRIORITY_MAX
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_FROM_URI
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.fhz.music_lib_player.R
import com.fhz.mvvm.dataSource.local.HomeMusicSdCardDataSource1
import kotlinx.coroutines.*


/**
 * 时间:2023/6/1
 * @author Mr.Feng
 * 简述: 最新的service
 */
private const val MY_MEDIA_ROOT_ID = "media_root_id"
const val NOTIFICATION_LARGE_ICON_SIZE = 144 // px

private const val MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id"
private const val TAG = "MyMediaPlaybackService"
const val NOW_PLAYING_CHANNEL_ID = "com.example.android.uamp.media.NOW_PLAYING"
const val NOW_PLAYING_NOTIFICATION_ID = 0xb339 // Arbitrary number used to identify our notification


class MyMediaPlaybackService : MediaBrowserServiceCompat() {
    val mediaMetadatas = ArrayList<MediaMetadataCompat>()
    var currentPosition: Long = 0
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var playbackState: PlaybackStateCompat
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var notificationManager : NotificationManager

    private fun mySessionCallback() = object : MediaSessionCompat.Callback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onPlay() {
            super.onPlay()
            Log.i(TAG, "onPlay: ")
            // 处理 播放器 的播放逻辑。
            // 车载应用的话，别忘了处理音频焦点
            if (!mediaSession.isActive) {
                mediaSession.isActive = true
            }
            //更新播放状态.
            playbackState = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1f)
                .setActions(PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PLAY_PAUSE)
                .build()
            mediaSession.setPlaybackState(playbackState)
            /**
             * 可以用着两种加载数据
             * exoPlayer.addMediaItem(MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"))
             * exoPlayer.addMediaItem(MediaItem.fromUri("/storage/emulated/0/Jam - 七月上.mp3"))
             */
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true

            openNotification()
        }

        override fun onSkipToQueueItem(id: Long) {
            Log.i(TAG, "onSkipToQueueItem: ")
            super.onSkipToQueueItem(id)
            if(!exoPlayer.isPlaying){
                playbackState = PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1f)
                    .build()
                mediaSession.setPlaybackState(playbackState)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
            }
            exoPlayer.seekToDefaultPosition(id.toInt())
            //删除队列会不精准
            mediaSession.setMetadata(mediaMetadatas[id.toInt()])
            currentPosition = id
            openNotification()
        }

        override fun onPause() {
            super.onPause()
            exoPlayer.pause()
            playbackState = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PAUSED, 0, 1f)
                .build()
            mediaSession.setPlaybackState(playbackState)
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            Log.i(TAG, "onSkipToNext: ")
            exoPlayer.seekToNext()
            if(currentPosition < mediaMetadatas.size-1 ){
                currentPosition = currentPosition.plus(1)
            }
            mediaSession.setMetadata(mediaMetadatas[currentPosition.toInt()])
            openNotification()
        }

        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
            super.onPlayFromUri(uri, extras)
            playbackState = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, 1f)
                .setActions(ACTION_PLAY_FROM_URI)
                .build()
            mediaSession.setPlaybackState(playbackState)
            uri?.let { MediaItem.fromUri(it) }?.let { exoPlayer.addMediaItem(it) }
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    Log.i(TAG, "onPlaybackStateChanged__service: $playbackState")
                    if (playbackState == Player.STATE_ENDED) {
                        // 当音乐播放结束时的处理
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    Log.i(TAG, "onMediaItemTransition: ")
                }

                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)
                    Log.i(TAG, "onEvents: ")
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    super.onMediaMetadataChanged(mediaMetadata)
                    Log.i(TAG, "onMediaMetadataChanged: ")
//                    mediaSession.setMetadata(mediaMetadata as MediaMetadataCompat)
                }
            })
        }
    }

    private inner class DescriptionAdapter(private val controller: MediaControllerCompat) :
        PlayerNotificationManager.MediaDescriptionAdapter {

        var currentIconUri: Uri? = null
        var currentBitmap: Bitmap? = null

        override fun createCurrentContentIntent(player: Player): PendingIntent? =
            controller.sessionActivity

        override fun getCurrentContentText(player: Player) =
            controller.metadata.description.subtitle.toString()

        override fun getCurrentContentTitle(player: Player) =
            controller.metadata.description.title.toString()

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            val iconUri = controller.metadata.description.iconUri
            return if (currentIconUri != iconUri || currentBitmap == null) {

                // Cache the bitmap for the current song so that successive calls to
                // `getCurrentLargeIcon` don't cause the bitmap to be recreated.
                currentIconUri = iconUri
                serviceScope.launch {
                    currentBitmap = iconUri?.let {
                        resolveUriAsBitmap(it)
                    }
                    currentBitmap?.let { callback.onBitmap(it) }
                }
                null
            } else {
                currentBitmap
            }
        }

        private suspend fun resolveUriAsBitmap(uri: Uri): Bitmap? {
            return withContext(Dispatchers.IO) {
                // Block on downloading artwork.
                Glide.with(this@MyMediaPlaybackService).applyDefaultRequestOptions(glideOptions)
                    .asBitmap()
                    .load(uri)
                    .submit(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE)
                    .get()
            }
        }
    }

    private val glideOptions = RequestOptions()
        .fallback(R.drawable.kawayi)
        .diskCacheStrategy(DiskCacheStrategy.DATA)

    /**
     *  开启一个通知栏
     */
    private fun openNotification() {
        val controller = mediaSession.controller
        val mediaMetadata = controller.metadata
        val description = mediaMetadata.description

        //使用系统提供的通知栏
        val buil = PlayerNotificationManager.Builder(
            this,
            NOW_PLAYING_NOTIFICATION_ID,
            NOW_PLAYING_CHANNEL_ID
        )
        with(buil){
            setMediaDescriptionAdapter(DescriptionAdapter(controller))
            setNotificationListener(object : PlayerNotificationManager.NotificationListener{
                //发送通知后回调.有重复打印,代表多次发送了
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    super.onNotificationPosted(notificationId, notification, ongoing)
                    Log.i(TAG, "onNotificationPosted: ")
                }

                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    super.onNotificationCancelled(notificationId, dismissedByUser)
                    Log.i(TAG, "onNotificationCancelled: ")
                }
            })
            setChannelNameResourceId(R.string.notification_channel)
            setChannelDescriptionResourceId(R.string.notification_channel_description)
        }
        playerNotificationManager = buil.build().apply {
            setPlayer(exoPlayer)
            setColor(com.fhz.music_lib_common.R.color.white)
            setMediaSessionToken(mediaSession.sessionToken)
            setUseNextAction(true)
            setUsePlayPauseActions(true)
            setUsePreviousAction(true)
            setSmallIcon(R.drawable.kawayi)
            setUseRewindAction(false)
            setUseFastForwardAction(false)
            setPriority(NotificationCompat.PRIORITY_MAX)

        }

        //开启通知栏
        val builder = NotificationCompat.Builder(this@MyMediaPlaybackService, NOW_PLAYING_CHANNEL_ID).apply {
            // Add the metadata for the currently playing track
            setContentTitle(description.title)
            setContentText(description.subtitle)
            setSubText(description.description)
            setLargeIcon(description.iconBitmap)

            // Enable launching the player by clicking the notification
            setContentIntent(controller.sessionActivity)

//            // Stop the service when the notification is swiped away
//            setDeleteIntent(
//                MediaButtonReceiver.buildMediaButtonPendingIntent(
//                    this@MyMediaPlaybackService,
//                    PlaybackStateCompat.ACTION_STOP
//                )
//            )

            // Make the transport controls visible on the lockscreen
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            // Add an app icon and set its accent color
            // Be careful about the color
//            setSmallIcon(com.fhz.music_lib_common.R.mipmap.ic_launcher)
            //背景色
            color = ContextCompat.getColor(this@MyMediaPlaybackService, com.fhz.music_lib_common.R.color.white)

            // Add a pause button
//            addAction(
//                NotificationCompat.Action(
//                    R.drawable.home_list_play_all,
//                    getString(R.string.mingzi),
//                    MediaButtonReceiver.buildMediaButtonPendingIntent(
//                        this@MyMediaPlaybackService,
//                        PlaybackStateCompat.ACTION_PLAY_PAUSE
//                    )
//                )
//            )
//            addAction(
//                NotificationCompat.Action(
//                    R.drawable.home_list_play_all,
//                    getString(R.string.mingzi),
//                    MediaButtonReceiver.buildMediaButtonPendingIntent(
//                        this@MyMediaPlaybackService,
//                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT
//                    )
//                )
//            )

            priority = NotificationCompat.PRIORITY_MAX
            // Take advantage of MediaStyle features
//            setStyle(
//                androidx.media.app.NotificationCompat.MediaStyle()
//                    .setMediaSession(mediaSession.sessionToken)
//                    .setShowActionsInCompactView(0)
//
//                    // Add a cancel button
//                    .setShowCancelButton(true)
//                    .setCancelButtonIntent(
//                        MediaButtonReceiver.buildMediaButtonPendingIntent(
//                            this@MyMediaPlaybackService,
//                            PlaybackStateCompat.ACTION_PAUSE
//                        )
//                    )
//            )
        }
        startForeground(110, builder.build())
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build()
        //会话的创建
        mediaSession = MediaSessionCompat(this, "music")
        playbackState = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
            .build()
        mediaSession.setCallback(mySessionCallback())
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or
        MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS)
        // Enable callbacks from MediaButtons and TransportControls
        mediaSession.setPlaybackState(playbackState)
        //表示MediaBrowser与MediaBrowserService连接成功,如果不设置,则不能连接
        sessionToken = mediaSession.sessionToken

        //开启前台服务
        startService(Intent(this,MyMediaPlaybackService::class.java))
        //前台通知服务
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            NOW_PLAYING_CHANNEL_ID,
            "notification_name",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

//    private inner class PlayerNotificationListener : PlayerNotificationManager.NotificationListener {
//        override fun onNotificationPosted(
//            notificationId: Int,
//            notification: Notification,
//            ongoing: Boolean
//        ) {
////            if (ongoing && !isForegroundService) {
////                ContextCompat.startForegroundService(
////                    applicationContext,
////                    Intent(applicationContext, this@MusicService.javaClass)
////                )
////
////                startForeground(notificationId, notification)
////                isForegroundService = true
////            }
//        }
//
//        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
//            stopForeground(true)
////            isForegroundService = false
//            stopSelf()
//        }
//    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(MY_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        Log.i(TAG, "onLoadChildren: ")
        //将信息从当前线程中移除，允许后续调用sendResult方法
        result.detach()
//        if (MY_EMPTY_MEDIA_ROOT_ID == parentId) {
//            result.sendResult(null)
//            return
//        }
        //先取到取到歌曲,然后封装成MediaMetadata 在然后变成MediaItem
        val musicFromSD = HomeMusicSdCardDataSource1().getMusicFromSD(this)
        var mediaItems = ArrayList<MediaBrowserCompat.MediaItem>()
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
        musicFromSD.forEach {
            val metadata = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, it.id.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, it.title)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, it.duration.toLong())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, it.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, it.album)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, it.path)
                .putString(MediaMetadataCompat.METADATA_KEY_AUTHOR, it.artist)
                .build()
            mediaMetadatas.add(metadata)
            val mediaItem = createMediaItem(metadata)
            exoPlayer.addMediaItem(MediaItem.fromUri(it.path))
            mediaItems.add(mediaItem)
        }
        Log.i(TAG, "onLoadChildren: ${mediaItems.size}")
        result.sendResult(mediaItems)
    }

    private fun createMediaItem(metadata: MediaMetadataCompat): MediaBrowserCompat.MediaItem {
        return MediaBrowserCompat.MediaItem(
            metadata.description,
            MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
        stopForeground(false)
        stopSelf()
    }
}
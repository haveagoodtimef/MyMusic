package com.fhz.music_home.intent

import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat

/**
 * 时间:2023/5/23
 * @author Mr.Feng
 * 简述: Home界面上Music的意图
 */
sealed class HomeMusicIntent {
    data class GetMusicList(var page: Int, var size: Int) : HomeMusicIntent()
    object GetMusicListFormSD : HomeMusicIntent()
    data class PlayMusicAsPosition(var position: Int) : HomeMusicIntent()
    data class PlayForUri(var item: MediaBrowserCompat.MediaItem) : HomeMusicIntent()

    data class PlayAll(var item: List<MediaBrowserCompat.MediaItem>) : HomeMusicIntent()
}

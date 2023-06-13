package com.fhz.music_home.ui.state

import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import com.fhz.music_lib_common.entity.Song
import com.fhz.mvitest.entity.ResponseEntity

/**
 * 时间:2023/5/23
 * @author Mr.Feng
 * 简述: 首页的音乐播放状态
 */
sealed class HomeMusicUIState {
    data class Success(var result:ResponseEntity<List<Song>>):HomeMusicUIState()
    data class Fail(var result:ResponseEntity<List<Song>>):HomeMusicUIState()
    object Init:HomeMusicUIState()
    object Play:HomeMusicUIState()
    data class PlayFormUri(val uri: Uri):HomeMusicUIState()


}
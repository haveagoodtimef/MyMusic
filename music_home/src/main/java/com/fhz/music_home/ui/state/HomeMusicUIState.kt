package com.fhz.music_home.ui.state

import com.fhz.music_home.entity.Song
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
}